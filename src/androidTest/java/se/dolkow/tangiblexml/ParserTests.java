package se.dolkow.tangiblexml;

import android.util.Xml;

import junit.framework.TestCase;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;

import se.dolkow.tangiblexml.testobj.Student;
import se.dolkow.tangiblexml.testobj.StudentResults;

public class ParserTests extends TestCase {

    private <E> E parse(Class<E> clazz, String data) throws Exception {
        XmlPullParser xml = Xml.newPullParser();
        xml.setInput(new StringReader(data));

        Parser<E> parser = new Parser<>(clazz);
        return parser.parse(xml);
    }

    private static final String fullXml = "<?xml version='1.0' encoding='utf-8'?>"
            + "<StudentRegistry>"
            + "  <StudentList>"
            + "    <Student>"
            + "      <CourseGrade>"
            + "        <Name>C Programming</Name>"
            + "        <Grade>Pretty okay, I guess</Grade>"
            + "        <StuffThatWeDontCareAbout />"
            + "      </CourseGrade>"
            + "      <Name>Snild</Name>"
            + "      <CourseGrade>"
            + "        <Name>Calculus</Name>"
            + "        <Grade>Not that great</Grade>"
            + "      </CourseGrade>"
            + "      <Id>73</Id>"
            + "      <CourseGrade>"
            + "        <Name>Some other stuff</Name>"
            + "        <!-- this is a comment -->"
            + "      </CourseGrade>"
            + "    </Student>"
            + "    <NotAStudent>"
            + "      <Junk />"
            + "    </NotAStudent>"
            + "    <Student>"
            + "      <Name>Someone Else</Name>"
            + "      <CourseGrade>"
            + "        <Name>Some other stuff</Name>"
            + "      </CourseGrade>"
            + "      <CourseGrade>"
            + "        <Name />"
            + "      </CourseGrade>"
            + "    </Student>"
            + "  </StudentList>"
            + "</StudentRegistry>";

    /**
     * Checks the result of the "fullXml" string.
     */
    private void checkFullResult(StudentResults res) {
        assertNotNull(res);
        assertNotNull(res.students);
        assertEquals(2, res.students.size());

        final Student snild = res.students.get(0);
        assertEquals("Snild", snild.name);
        assertNotNull(snild.id);
        assertEquals(73, (int)snild.id);
        assertNotNull(snild.courses);
        assertNotNull(snild.grades);
        assertEquals(3, snild.courses.size());
        assertEquals(2, snild.grades.size());
        assertEquals("C Programming", snild.courses.get(0));
        assertEquals("Calculus", snild.courses.get(1));
        assertEquals("Some other stuff", snild.courses.get(2));
        assertEquals("Pretty okay, I guess", snild.grades.get(0));
        assertEquals("Not that great", snild.grades.get(1));


        final Student other = res.students.get(1);
        assertEquals("Someone Else", other.name);
        assertNull(other.id);
        assertNotNull(other.courses);
        assertNotNull(other.grades);
        assertEquals(2, other.courses.size());
        assertEquals(0, other.grades.size());
    }

    public void testParseRegistry() throws Exception {
        StudentResults res = parse(StudentResults.class, fullXml);
        checkFullResult(res);
    }

    private static final class ConcurrentParser extends Thread {
        private static final int REPEATS = 100;

        public final ArrayList<StudentResults> results = new ArrayList<>();
        public final Parser<StudentResults> parser;
        public Throwable err = null;

        private ConcurrentParser(Parser<StudentResults> parser) {
            this.parser = parser;
        }

        @Override
        public void run() {
            try {
                for (int j=0; j<REPEATS; ++j) {
                    XmlPullParser xml = Xml.newPullParser();
                    xml.setInput(new StringReader(fullXml));
                    results.add(parser.parse(xml));
                }
            } catch (Throwable t) {
                err = t;
            }
        }
    }

    public void testParseConcurrent() throws Exception {
        final int N = 5;
        final Parser<StudentResults> parser = new Parser<>(StudentResults.class);

        final ConcurrentParser[] threads = new ConcurrentParser[N];
        for (int i=0; i<N; ++i) {
            threads[i] = new ConcurrentParser(parser);
        }

        for (ConcurrentParser t : threads) {
            t.start();
        }

        for (ConcurrentParser t : threads) {
            t.join();
            assertNull(t.err);
            assertEquals(ConcurrentParser.REPEATS, t.results.size());
        }

        for (int i=0; i<N; ++i) {
            for (int j=i+1; j<N; ++j) {
                for (StudentResults r1 : threads[i].results) {
                    checkFullResult(r1);
                    for (StudentResults r2 : threads[j].results) {
                        assertNotSame(r1, r2);
                    }
                }
            }
        }
    }

    public void testOnlyRoot() throws Exception {
        final String xml = "<?xml version='1.0' encoding='utf-8'?>"
                + "<StudentRegistry>"
                + "</StudentRegistry>";
        try {
            parse(StudentResults.class, xml);
            fail("Expected an exception");
        } catch (ValueCountException e) {
            // ok! We never found a /StudentRegistry/StudentList node!
        }
    }

    public void testEmptyList() throws Exception {
        final String xml = "<?xml version='1.0' encoding='utf-8'?>"
                + "<StudentRegistry>"
                + "  <StudentList>"
                + "  </StudentList>"
                + "</StudentRegistry>";
        StudentResults res = parse(StudentResults.class, xml);
        assertNotNull(res);
        assertNotNull(res.students);
        assertEquals(0, res.students.size());
    }

    public void testMultipleLists() throws Exception {
        final String xml = "<?xml version='1.0' encoding='utf-8'?>"
                + "<StudentRegistry>"
                + "  <StudentList>"
                + "  </StudentList>"
                + "</StudentRegistry>";
        parse(StudentResults.class, xml);
    }

    public void testMissingRequiredField() throws Exception {
        final String xml = "<?xml version='1.0' encoding='utf-8'?>"
                + "<StudentRegistry>"
                + "  <StudentList>"
                + "    <Student>"
                + "      <CourseGrade>"
                + "        <Name>C Programming</Name>"
                + "        <Grade>Pretty okay, I guess</Grade>"
                + "        <StuffThatWeDontCareAbout />"
                + "      </CourseGrade>"
                + "    </Student>"
                + "  </StudentList>"
                + "</StudentRegistry>";
        try {
            parse(StudentResults.class, xml);
            fail("Expected an exception");
        } catch (ValueCountException e) {
            // ok!
        }
    }

    public void testMissingRequiredListElem() throws Exception {
        final String xml = "<?xml version='1.0' encoding='utf-8'?>"
                + "<StudentRegistry>"
                + "  <StudentList>"
                + "    <Student>"
                + "      <Name>Snild</Name>"
                + "    </Student>"
                + "  </StudentList>"
                + "</StudentRegistry>";
        try {
            parse(StudentResults.class, xml);
            fail("Expected an exception");
        } catch (ValueCountException e) {
            // ok!
        }
    }

    public void testDoubleField() throws Exception {
        final String xml = "<?xml version='1.0' encoding='utf-8'?>"
                + "<StudentRegistry>"
                + "  <StudentList>"
                + "    <Student>"
                + "      <Name>Snild</Name>"
                + "      <Name>Someone Else</Name>"
                + "    </Student>"
                + "  </StudentList>"
                + "</StudentRegistry>";
        try {
            parse(StudentResults.class, xml);
            fail("Expected an exception");
        } catch (ValueCountException e) {
            // ok!
        }
    }

}

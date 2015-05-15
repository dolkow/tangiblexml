package se.dolkow.tangiblexml;

import junit.framework.TestCase;

public class FieldPutterTests extends TestCase {
    private FieldPutter<Holder,Number> putter;
    private Holder holder;

    private static class Holder {
        @SuppressWarnings("unused")
        public Integer value;
    }

    @Override
    protected void setUp() throws Exception {
        holder = new Holder();
        putter = new FieldPutter<>(holder.getClass().getField("value"));
    }

    public void testGet() throws Exception {
        assertNull(putter.get(holder));
    }

    public void testPut() throws Exception {
        putter.put(holder, 27);
        assertEquals(27, (int) holder.value);
    }

    public void testPutAndGet() throws Exception {
        putter.put(holder, 39710291);
        assertEquals(39710291, putter.get(holder));
    }

    public void testDoublePut() throws Exception {
        putter.put(holder, 12345);
        try {
            putter.put(holder, 67890);
            fail("Expected an exception");
        } catch (ValueCountException e) {
            // ok!
        }
        assertEquals(12345, putter.get(holder));
    }
}

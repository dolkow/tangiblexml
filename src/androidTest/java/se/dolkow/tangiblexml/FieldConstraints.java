package se.dolkow.tangiblexml;

import junit.framework.TestCase;

public class FieldConstraints extends TestCase{

    @SuppressWarnings("unused")
    @TangibleRoot("/A")
    public static class Primitive { @TangibleField("B") public int i; }
    public void testPrimitiveField() throws Exception {
        try {
            new Parser<>(Primitive.class);
            fail("Expected an exception");
        } catch (InvalidFieldException e) {
            // ok!
        }
    }

    @SuppressWarnings("unused")
    @TangibleRoot("/A")
    public static class Private { @TangibleField("B") private Object o; }
    public void testPrivateField() throws Exception {
        try {
            new Parser<>(Private.class);
            fail("Expected an exception");
        } catch (InvalidFieldException e) {
            // ok!
        }
    }

    @SuppressWarnings("unused")
    @TangibleRoot("/A")
    public static class Package { @TangibleField("B") Object o; }
    public void testPackageField() throws Exception {
        try {
            new Parser<>(Package.class);
            fail("Expected an exception");
        } catch (InvalidFieldException e) {
            // ok!
        }
    }

    @SuppressWarnings("unused")
    @TangibleRoot("/A")
    public static class Protected { @TangibleField("B") protected Object o; }
    public void testProtectedField() throws Exception {
        try {
            new Parser<>(Protected.class);
            fail("Expected an exception");
        } catch (InvalidFieldException e) {
            // ok!
        }
    }

    @SuppressWarnings("unused")
    @TangibleRoot("/A")
    public static class Final {@TangibleField("B") public final Object o=null; }
    public void testFinalField() throws Exception {
        try {
            new Parser<>(Final.class);
            fail("Expected an exception");
        } catch (InvalidFieldException e) {
            // ok!
        }
    }

    @SuppressWarnings("unused")
    @TangibleRoot("/A")
    public static class Static { @TangibleField("B") public static Object o; }
    public void testStaticField() throws Exception {
        try {
            new Parser<>(Static.class);
            fail("Expected an exception");
        } catch (InvalidFieldException e) {
            // ok!
        }
    }
}

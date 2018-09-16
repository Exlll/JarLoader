package de.exlll.jarloader;

public final class DigestMismatchException extends RuntimeException {
    private final String expected;
    private final String actual;

    public DigestMismatchException(String expected, String actual) {
        super(exceptionMessage(expected, actual));
        this.expected = expected;
        this.actual = actual;
    }

    private static String exceptionMessage(String expected, String actual) {
        return "Digest mismatch exception:\n" +
                "Expected: '" + expected + "'\n" +
                "Actual:   '" + actual + "'";
    }

    public String getExpected() {
        return expected;
    }

    public String getActual() {
        return actual;
    }
}

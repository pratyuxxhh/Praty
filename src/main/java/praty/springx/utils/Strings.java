package praty.springx.utils;

/**
 * Shared string helpers for springx.
 */
public final class Strings {

    private Strings() {
    }

    public static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    public static String requireNonBlank(String s, String name) {
        if (isBlank(s)) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return s.trim();
    }
}

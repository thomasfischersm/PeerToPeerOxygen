package com.playposse.peertopeeroxygen.android;

/**
 * Helpful class for dealing with math.
 */
public class MathUtil {

    /**
     * Tries to parse the string into an integer. If it fails, the defaultValue is returned.
     */
    public static int tryParseInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}

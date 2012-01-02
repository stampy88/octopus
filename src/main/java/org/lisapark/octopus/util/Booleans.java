package org.lisapark.octopus.util;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public abstract class Booleans {
    public static boolean parseBoolean(String value) {
        return parseBoolean(value, false);
    }

    public static boolean parseBoolean(String value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }

        return parseBoolean(value, Boolean.valueOf(defaultValue));
    }

    public static Boolean parseBoolean(String value, Boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }

        String lowerCasedValue = value.toLowerCase();

        return (lowerCasedValue.equals("true") || lowerCasedValue.equals("1") ||
                lowerCasedValue.equals("on") || lowerCasedValue.equals("yes") || lowerCasedValue.equals("y"));
    }
}
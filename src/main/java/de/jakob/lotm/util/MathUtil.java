package de.jakob.lotm.util;

public class MathUtil {
    public static boolean isInteger(String s) {
        try {
            int ignored = Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isBoolean(String s) {
        return (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false"));
    }
}

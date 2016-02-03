package com.oakonell.dndcharacter.utils;

/**
 * Created by Rob on 1/27/2016.
 */
public class NumberUtils {
    public static String formatNumber(long number) {
        return String.format("%,d", number);
    }

    public static String formatLength(int totalInches) {
        String result = (totalInches / 12) + "'";
        int inches = totalInches % 12;
        if (inches > 0) {
            result += " " + inches + "\"";
        }
        return result;
    }
}

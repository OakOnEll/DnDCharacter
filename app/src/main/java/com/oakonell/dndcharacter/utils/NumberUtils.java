package com.oakonell.dndcharacter.utils;

import android.support.annotation.NonNull;

/**
 * Created by Rob on 1/27/2016.
 */
public class NumberUtils {
    public static String formatNumber(long number) {
        return String.format("%,d", number);
    }

    @NonNull
    public static String formatLength(int totalInches) {
        String result = (totalInches / 12) + "'";
        int inches = totalInches % 12;
        if (inches > 0) {
            result += " " + inches + "\"";
        }
        return result;
    }
}

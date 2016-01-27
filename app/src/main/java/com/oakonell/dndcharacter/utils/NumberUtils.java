package com.oakonell.dndcharacter.utils;

/**
 * Created by Rob on 1/27/2016.
 */
public class NumberUtils {
    public static String formatNumber(long number) {
        return String.format("%,d", number);
    }
}

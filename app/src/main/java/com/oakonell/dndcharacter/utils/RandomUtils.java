package com.oakonell.dndcharacter.utils;

import java.util.Random;

/**
 * Created by Rob on 12/15/2015.
 */
public class RandomUtils {
    private static final Random random = new Random();


    public static int random(@SuppressWarnings("SameParameterValue") int low, int high) {
        return random.nextInt((high - low) + 1) + low;
    }
}

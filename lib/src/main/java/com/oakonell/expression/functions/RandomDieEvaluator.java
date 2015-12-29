package com.oakonell.expression.functions;

import com.oakonell.expression.DieEvaluator;

import java.util.Random;

/**
 * Created by Rob on 12/29/2015.
 */
public class RandomDieEvaluator implements DieEvaluator {
    private final Random rand;

    public RandomDieEvaluator(int seed) {
        rand = new Random(seed);
    }

    public RandomDieEvaluator() {
        rand = new Random();
    }

    public int random(int low, int high) {
        return rand.nextInt((high - low) + 1) + low;
    }


    @Override
    public int evaluate(int die) {
        return random(1, die);
    }
}

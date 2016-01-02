package com.oakonell.dndcharacter.model;

/**
 * Created by Rob on 10/23/2015.
 */
public enum Proficient {
    NONE(0), HALF(0.5), PROFICIENT(1), EXPERT(2);
    private final double multiplier;

    Proficient(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}

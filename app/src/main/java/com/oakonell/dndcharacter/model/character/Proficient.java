package com.oakonell.dndcharacter.model.character;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 10/23/2015.
 */
public enum Proficient {
    NONE(0, R.string.no_proficiency), HALF(0.5, R.string.half_proficiency), PROFICIENT(1, R.string.proficient), EXPERT(2, R.string.expert);
    private final double multiplier;
    private final int stringResId;

    public int getStringResId() {
        return stringResId;
    }


    Proficient(double multiplier, int stringResId) {
        this.multiplier = multiplier;
        this.stringResId = stringResId;
    }

    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

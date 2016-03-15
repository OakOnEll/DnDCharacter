package com.oakonell.dndcharacter.model.character;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 3/15/2016.
 */
public enum Alignment {
    LAWFUL_GOOD(R.string.lawful_good), NEUTRAL_GOOD(R.string.neutral_good), CHAOTIC_GOOD(R.string.chaotic_good),
    LAWFUL_NEUTRAL(R.string.lawful_neutral), TRUE_NEUTRAL(R.string.true_neutral), CHATOIC_NEUTRAL(R.string.chaotic_neutral),
    LAWFUL_EVIL(R.string.lawful_evil), NEUTRAL_EVIL(R.string.neutral_evil), CHAOTIC_EVIL(R.string.chaotic_evil);

    private final int stringResId;

    Alignment(int stringResId) {
        this.stringResId = stringResId;
    }

    public int getStringResId() {
        return stringResId;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}

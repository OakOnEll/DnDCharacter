package com.oakonell.dndcharacter.model.character.stats;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 10/21/2015.
 */
public enum StatType {
    STRENGTH(R.string.strength), DEXTERITY(R.string.dexterity), CONSTITUTION(R.string.constitution),
    INTELLIGENCE(R.string.intelligence), WISDOM(R.string.wisdom), CHARISMA(R.string.charisma);


    private final int stringResId;

    StatType(int stringResId) {
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

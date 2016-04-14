package com.oakonell.dndcharacter.model.character.spell;

/**
 * Created by Rob on 1/14/2016.
 */
public enum SpellDurationType {
    ROUND(true), MINUTE(true), HOUR(true), DAY(true), INSTANTANEOUS(false), SPECIAL(false), UNTIL_DISPELLED(false), UNTIL_DISPELLED_OR_TRIGGERED(false);

    private final boolean hasValue;

    SpellDurationType(boolean hasValue) {
        this.hasValue = hasValue;
    }

    public boolean hasValue() {
        return hasValue;
    }
}

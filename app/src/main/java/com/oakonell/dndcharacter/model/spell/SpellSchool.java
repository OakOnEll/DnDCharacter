package com.oakonell.dndcharacter.model.spell;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 2/20/2016.
 */
public enum SpellSchool {
    ABJURATION(R.string.abjuration), EVOCATION(R.string.evocation), CONJURATION(R.string.conjuration),
    DIVINATION(R.string.divination), ENCHANTMENT(R.string.enchantment), ILLUSION(R.string.illusion),
    NECROMANCY(R.string.necromancy), TRANSMUTATION(R.string.transmutation);

    private final int stringResId;

    SpellSchool(int stringResId) {
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

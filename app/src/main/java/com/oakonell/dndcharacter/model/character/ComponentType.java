package com.oakonell.dndcharacter.model.character;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 10/26/2015.
 */
public enum ComponentType {
    RACE(R.string.race_component), BACKGROUND(R.string.background_component), CLASS(R.string.class_component),
    ITEM(R.string.item_component), EFFECT(R.string.effect_component), FEATURE(R.string.feature_component),
    SPELL(R.string.spell_component), CUSTOM_ADJUSTMENT(R.string.custom);

    private final int stringResId;

    ComponentType(int stringResId) {
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


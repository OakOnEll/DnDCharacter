package com.oakonell.dndcharacter.model.character.stats;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.CustomAdjustmentType;

/**
 * Created by Rob on 10/21/2015.
 */
public enum StatType {
    STRENGTH(R.string.strength, CustomAdjustmentType.STAT_STRENGTH), DEXTERITY(R.string.dexterity, CustomAdjustmentType.STAT_DEXTERITY),
    CONSTITUTION(R.string.constitution, CustomAdjustmentType.STAT_CONSTITUTION), INTELLIGENCE(R.string.intelligence, CustomAdjustmentType.STAT_INTELLIGENCE),
    WISDOM(R.string.wisdom, CustomAdjustmentType.STAT_WISDOM), CHARISMA(R.string.charisma, CustomAdjustmentType.STAT_CHARISMA);


    private final int stringResId;
    private final CustomAdjustmentType customType;

    StatType(int stringResId, CustomAdjustmentType type) {
        this.stringResId = stringResId;
        this.customType = type;
    }

    public int getStringResId() {
        return stringResId;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public CustomAdjustmentType getCustomType() {
        return customType;
    }
}

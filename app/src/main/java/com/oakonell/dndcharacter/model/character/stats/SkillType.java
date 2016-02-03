package com.oakonell.dndcharacter.model.character.stats;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 10/21/2015.
 */
public enum SkillType {
    ACROBATICS(StatType.DEXTERITY, R.string.acrobatics), ANIMAL_HANDLING(StatType.WISDOM, R.string.animal_handling), ARCANA(StatType.INTELLIGENCE, R.string.arcana),
    ATHLETICS(StatType.STRENGTH, R.string.athletics),DECEPTION(StatType.CHARISMA, R.string.deception), HISTORY(StatType.INTELLIGENCE, R.string.history),
    INSIGHT(StatType.WISDOM, R.string.insight), INTIMIDATION(StatType.CHARISMA, R.string.intimidation),INVESTIGATION(StatType.INTELLIGENCE, R.string.investigation),
    MEDICINE(StatType.WISDOM, R.string.medicine), NATURE(StatType.INTELLIGENCE, R.string.nature), PERCEPTION(StatType.WISDOM, R.string.perception),
    PERFORMANCE(StatType.CHARISMA, R.string.performance), PERSUASION(StatType.CHARISMA, R.string.persuasion), RELIGION(StatType.INTELLIGENCE, R.string.religion),
    SLEIGHT_OF_HAND(StatType.DEXTERITY, R.string.sleight_of_hand),STEALTH(StatType.DEXTERITY, R.string.stealth), SURVIVAL(StatType.WISDOM, R.string.survival);

    private final StatType statType;

    SkillType(StatType statType, int stringResId) {
        this.statType = statType;
        this.stringResId = stringResId;
    }

    public StatType getStatType() {
        return statType;
    }


    private final int stringResId;

    public int getStringResId() {
        return stringResId;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}

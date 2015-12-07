package com.oakonell.dndcharacter.model;

/**
 * Created by Rob on 10/21/2015.
 */
public enum SkillType {
    ACROBATICS(StatType.DEXTERITY), ANIMAL_HANDLING(StatType.WISDOM), ARCANA(StatType.INTELLIGENCE), ATHLETICS(StatType.STRENGTH),
    DECEPTION(StatType.CHARISMA), HISTORY(StatType.INTELLIGENCE), INSIGHT(StatType.WISDOM), INTIMIDATION(StatType.CHARISMA),
    INVESTIGATION(StatType.INTELLIGENCE), MEDICINE(StatType.WISDOM), NATURE(StatType.INTELLIGENCE), PERCEPTION(StatType.WISDOM),
    PERFORMANCE(StatType.CHARISMA), PERSUASION(StatType.CHARISMA), RELIGION(StatType.INTELLIGENCE), SLEIGHT_OF_HAND(StatType.DEXTERITY),
    STEALTH(StatType.DEXTERITY), SURVIVAL(StatType.WISDOM);

    private final StatType statType;

    private SkillType(StatType statType) {
        this.statType = statType;
    }

    public StatType getStatType() {
        return statType;
    }
}

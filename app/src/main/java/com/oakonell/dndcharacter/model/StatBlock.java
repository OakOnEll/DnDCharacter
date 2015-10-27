package com.oakonell.dndcharacter.model;

/**
 * Created by Rob on 10/21/2015.
 */
public class StatBlock {
    private final Character character;
    private final StatType type;

    public StatBlock(Character character,StatType type) {
        this.character = character;
        this.type = type;
    }

    public int getValue() {
        return character.deriveStatValue(type);
    }

    public int getSaveModifier() {
        Proficient proficient = getSaveProficiency();
        int statMod = character.getStatBlock(type).getModifier();
        int proficiency = character.getProficiency();
        return ((int) (proficiency * proficient.getMultiplier())) + statMod;
    }

    public Proficient getSaveProficiency() {
        return character.deriveSaveProciency(type);
    }
    public int getModifier() {
        int value = getValue();
        return (int) Math.floor((value - 10)/ 2.0);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Stat: " + type.name() + " = " + getValue() + "(initial)");
/*
        for (StatAdjustment each : adjustments) {
            builder.append(" + ");
            builder.append(each.getAdjustment());
            builder.append("(");
            builder.append(each.getReason());
            builder.append(")");
        }
*/        builder.append(" => ");
        builder.append(getValue());
        builder.append("[");
        builder.append(getModifier());
        builder.append("]");
        return builder.toString();
    }
}

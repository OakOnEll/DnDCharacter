package com.oakonell.dndcharacter.model;

import java.util.List;

/**
 * Created by Rob on 10/21/2015.
 */
public class StatBlock {
    private final Character character;
    private final StatType type;

    public StatBlock(Character character, StatType type) {
        this.character = character;
        this.type = type;
    }

    public int getValue() {
        return character.deriveStatValue(type);
    }

    public StatType getType() {
        return type;
    }

    public int getSaveModifier() {
        Proficient proficient = getSaveProficiency();
        int statMod = getModifier();
        int proficiency = character.getProficiency();
        return ((int) (proficiency * proficient.getMultiplier())) + statMod;
    }

    public List<Character.ModifierWithSource> getModifiers() {
        return character.deriveStat(type);
    }

    public List<Character.ProficientWithSource> getSaveProficiencies() {
        return character.deriveSaveProficiencies(type);
    }

    public Proficient getSaveProficiency() {
        return character.deriveSaveProciency(type);
    }

    public int getModifier() {
        int value = getValue();
        return (int) Math.floor((value - 10) / 2.0);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Stat: " + type.name() + " = " + getValue() + "(initial)");
        builder.append(" => ");
        builder.append(getValue());
        builder.append("[");
        builder.append(getModifier());
        builder.append("]");
        return builder.toString();
    }

    public Character getCharacter() {
        return character;
    }

}

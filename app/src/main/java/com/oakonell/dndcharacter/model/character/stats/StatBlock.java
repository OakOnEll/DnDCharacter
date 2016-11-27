package com.oakonell.dndcharacter.model.character.stats;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.Proficient;

import java.util.List;

/**
 * Created by Rob on 10/21/2015.
 */
public class StatBlock {
    private final com.oakonell.dndcharacter.model.character.AbstractCharacter character;
    private final StatType type;

    public StatBlock(AbstractCharacter character, StatType type) {
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

    @NonNull
    public List<Character.ModifierWithSource> getModifiers() {
        return character.deriveStat(type);
    }

    @NonNull
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

    @NonNull
    public String toString() {
        StringBuilder builder = new StringBuilder("Stat: " + type.name() + " = " + getValue() + "(initial)");
        builder.append(" => ");
        builder.append(getValue());
        builder.append("[");
        builder.append(getModifier());
        builder.append("]");
        return builder.toString();
    }

    public AbstractCharacter getCharacter() {
        return character;
    }

}

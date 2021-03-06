package com.oakonell.dndcharacter.model.character.stats;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.Proficient;

import java.util.List;

/**
 * Created by Rob on 10/22/2015.
 */
public class SkillBlock {
    private final com.oakonell.dndcharacter.model.character.AbstractCharacter character;
    private final SkillType type;

    public SkillBlock(AbstractCharacter character, SkillType type) {
        this.character = character;
        this.type = type;
    }

    public SkillType getType() {
        return type;
    }


    public int getBonus() {
        Proficient proficient = getProficiency();
        int statMod = character.getStatBlock(type.getStatType()).getModifier();
        int proficiency = character.getProficiency();
        return ((int) (proficiency * proficient.getMultiplier())) + statMod;
    }

    public Proficient getProficiency() {
        return character.deriveSkillProciency(type);
    }

    @NonNull
    public List<Character.ProficientWithSource> getProficiencies() {
        return character.deriveSkillProciencies(type);
    }

    public int getStatModifier() {
        return character.getStatBlock(type.getStatType()).getModifier();
    }

    public AbstractCharacter getCharacter() {
        return character;
    }
}
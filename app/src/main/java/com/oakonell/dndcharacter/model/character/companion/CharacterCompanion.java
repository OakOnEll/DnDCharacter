package com.oakonell.dndcharacter.model.character.companion;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.AbstractCharacterAbilityDeriver;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterAbilityDeriver;
import com.oakonell.dndcharacter.model.character.CharacterClass;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.model.character.ComponentVisitor;
import com.oakonell.dndcharacter.model.character.item.CharacterArmor;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import org.simpleframework.xml.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 10/31/2016.
 */

public class CharacterCompanion extends AbstractCharacter {

    @Element(required = false)
    private CompanionRace race;

    @Element(required = false)
    private boolean deleting;

    @Element(required = false)
    private String typeString;

    @Element(required = false)
    private boolean isActive;

    @Element(required = false)
    private int rootAc;

    @Element(required = false)
    private CompanionTypeComponent softType;

    // not stored
    @Nullable
    private AbstractCompanionType type;
    private Character character;

    public CharacterCompanion() {

    }

    @Override
    public int getMaxHP() {
        return getType().getMaxHp(character, this);
    }

    public int getRawMaxHP() {
        // TODO
        return 10;
    }

    @Override
    public int getProficiency() {
        return 0;
    }

    public CompanionRace getRace() {
        return race;
    }

    public void setRace(CompanionRace race) {
        this.race = race;
    }

    public boolean isDeleting() {
        return deleting;
    }

    public void setDeleting(boolean deleting) {
        this.deleting = deleting;
    }

    public AbstractCompanionType getType() {
        if (type != null) return type;
        if (typeString != null && typeString.trim().length() > 0) {
            type = AbstractHardCompanionType.fromString(typeString);
            return type;
        }
        if (softType == null) {
            throw new RuntimeException("Invalid companion type");
        }
        type = softType;
        return type;
    }

    public void setType(AbstractCompanionType type) {
        if (type == null) {
            this.type = null;
            this.typeString = null;
            this.softType = null;
            return;
        }
        this.type = type;
        if (type instanceof AbstractHardCompanionType) {
            typeString = type.getType();
            return;
        }
        if (type instanceof CompanionTypeComponent) {
            softType = (CompanionTypeComponent) type;
            return;
        }
        throw new RuntimeException("Invalid companion type");
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean anyContextFeats(@SuppressWarnings("SameParameterValue") FeatureContext context) {
        return false;
    }

    public boolean isProficientWith(CharacterArmor characterArmor) {
        return true;
    }

    public void removeEffect(CharacterEffect effect) {
        // no op..
    }

    public void setBaseAC(int ac) {
        this.rootAc = ac;
    }

    @Override
    public CompanionAbilityDeriver getAbilityDeriver(ComponentVisitor visitor, boolean skipFeatures) {
        return new CompanionAbilityDeriver(visitor, skipFeatures);
    }

    @NonNull
    protected String getBaseACString() {
        return rootAc + "";
    }

    @NonNull
    @Override
    public List<Character.HitDieRow> getHitDiceCounts() {
        Map<Integer, Integer> dice = new LinkedHashMap<>();
        // TODO

        List<Character.HitDieRow> result = new ArrayList<>();

        Character.HitDieRow aRow = new Character.HitDieRow();
        aRow.dieSides = 8;
        aRow.numDiceRemaining = 3;
        aRow.totalDice = 3;
        result.add(aRow);

        for (Map.Entry<Integer, Integer> entry : dice.entrySet()) {
            Character.HitDieRow row = new Character.HitDieRow();
            row.dieSides = entry.getKey();
            row.totalDice = entry.getValue();
            // TODO
            Integer uses = getHitDieUses(row.dieSides);
            if (uses != null) {
                row.numDiceRemaining = row.totalDice - uses;
            } else {
                row.numDiceRemaining = row.totalDice;
            }
            result.add(row);
        }
        return result;    }
}

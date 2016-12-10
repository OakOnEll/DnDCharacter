package com.oakonell.dndcharacter.model.character.companion;

import com.oakonell.dndcharacter.model.character.Character;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 11/27/2016.
 */

public abstract class AbstractCompanionType {
    public abstract String getType();

    public boolean effectsSelf() {
        return false;
    }

    public boolean onlyOneActiveAllowed() {
        return effectsSelf();
    }

    public int getMaxHp(Character character) {
        return 10;
    }

    public abstract int getStringResId();

    public abstract int getDescriptionResId();

    public abstract int getShortDescriptionResId();

    public String getCrLimit(Character character) {
        return null;
    }

    public boolean usesLimitedRaces() {
        return false;
    }

    public boolean applies(Character character) {
        return true;
    }

    // object management
    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(this.getClass());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    // ---------------- all types, as classes for now

    static List<AbstractCompanionType> types = new ArrayList<>();

    static {
        types.add(new CompanionTypeFamiliar());
        types.add(new CompanionTypeMount());
        types.add(new CompanionTypePet());
        types.add(new CompanionTypePolymorph());
        types.add(new CompanionTypeUndead());
        types.add(new CompanionTypeWildShape());
        types.add(new CompanionTypeRangerCompanion());
    }

    public static List<AbstractCompanionType> values() {
        return types;
    }


    public static AbstractCompanionType fromString(String typeString) {
        for (AbstractCompanionType each : values()) {
            if (each.getType().equals(typeString)) return each;
        }
        throw new RuntimeException("Unknown companion type: " + typeString);
    }

}

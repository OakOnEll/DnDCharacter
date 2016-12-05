package com.oakonell.dndcharacter.model.character.companion;

import com.oakonell.dndcharacter.model.character.Character;

/**
 * Created by Rob on 11/27/2016.
 */

public abstract class AbstractCompanionType {
    abstract String getType();

    public static AbstractCompanionType fromString(String typeString) {
        switch (typeString) {
            case "familiar":
                return new CompanionTypeFamiliar();
            case "mount":
                return new CompanionTypeMount();
            case "polymorph":
                return new CompanionTypeFamiliar();
            case "wildShape":
                return new CompanionTypeWildShape();
            case "rangerCompanion":
                return new CompanionTypeRangerCompanion();
            default:
                throw new RuntimeException("Unknown companion type: " + typeString);
        }
    }

    public boolean effectsSelf() {
        return false;
    }

    public boolean onlyOneActiveAllowed() {
        return effectsSelf();
    }

    public int getMaxHp(Character character) {
        return 10;
    }
}

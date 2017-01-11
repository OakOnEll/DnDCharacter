package com.oakonell.dndcharacter.model.character.companion;

import android.content.res.Resources;

import com.oakonell.dndcharacter.model.character.Character;

import java.util.Collection;

/**
 * Created by Rob on 11/27/2016.
 */

public abstract class AbstractCompanionType {
    public abstract String getType();

    public abstract String getName(Resources resources);

    public abstract String getDescription(Resources resources);

    public abstract String getShortDescription(Resources resources);

    public boolean effectsSelf() {
        return false;
    }

    public boolean onlyOneActiveAllowed() {
        return effectsSelf();
    }

    public int getMaxHp(Character character, CharacterCompanion companion) {
        return companion.getRawMaxHP();
    }


    public String getCrLimit(Character character) {
        return null;
    }

    public boolean usesLimitedRaces() {
        return false;
    }


    // object management
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractCompanionType)) return false;
        return ((AbstractCompanionType) obj).getType().equals(this.getType());
    }

    @Override
    public int hashCode() {
        return getType().hashCode();
    }


    public Collection<String> getLimitedRaces() {
        return null;
    }
}

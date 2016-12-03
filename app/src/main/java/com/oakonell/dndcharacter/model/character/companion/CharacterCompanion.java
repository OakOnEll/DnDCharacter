package com.oakonell.dndcharacter.model.character.companion;

import android.support.annotation.Nullable;

import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 10/31/2016.
 */

public class CharacterCompanion extends AbstractCharacter {

    @Element(required = false)
    private String race;

    @Element(required = false)
    private boolean deleting;

    @Element(required = false)
    private String typeString;

    @Element(required = false)
    private boolean isActive;


    // not stored
    @Nullable
    private AbstractCompanionType type;


    @Override
    public int getMaxHP() {
        return 0;
    }

    @Override
    public int getProficiency() {
        return 0;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
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
        if (typeString == null || typeString.trim().length() == 0) {
            // dev upgrade safe
            type = new CompanionTypePolymorph();
        } else {
            type = AbstractCompanionType.fromString(typeString);
        }
        return type;
    }

    public void setType(AbstractCompanionType type) {
        if (type == null) {
            this.type = null;
            this.typeString = null;
            return;
        }
        this.type = type;
        typeString = type.getType();
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

}

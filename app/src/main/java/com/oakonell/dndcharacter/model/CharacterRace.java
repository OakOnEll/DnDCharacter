package com.oakonell.dndcharacter.model;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 10/24/2015.
 */
public class CharacterRace extends BaseCharacterComponent {
    @Element(required = false)
    private String subraceName;

    @Element(required = false)
    private SavedChoices subRaceChoices;

    @Override
    public ComponentType getType() {
        return ComponentType.RACE;
    }

    public void setSubraceName(String subraceName) {
        this.subraceName = subraceName;
    }

    public String getSubraceName() {
        return subraceName;
    }

    public void setSubRaceChoices(SavedChoices subRaceChoices) {
        this.subRaceChoices = subRaceChoices;
    }

    public SavedChoices getSubRaceChoices() {
        return subRaceChoices;
    }
}
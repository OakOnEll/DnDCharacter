package com.oakonell.dndcharacter.model.character.companion;

import com.oakonell.dndcharacter.model.character.AbstractCharacter;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 10/31/2016.
 */

public class Companion extends AbstractCharacter {

    @Element(required = false)
    private String race;


    @Element(required = false)
    private boolean deleting;


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

}

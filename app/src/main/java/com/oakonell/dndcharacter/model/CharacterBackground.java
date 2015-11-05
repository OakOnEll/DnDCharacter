package com.oakonell.dndcharacter.model;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 10/24/2015.
 */
public class CharacterBackground extends BaseCharacterComponent{
    @Element
    private String name;


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public ComponentType getType() {
        return ComponentType.BACKGROUND;
    }
}

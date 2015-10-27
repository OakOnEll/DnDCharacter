package com.oakonell.dndcharacter.model;

/**
 * Created by Rob on 10/24/2015.
 */
public class CharacterBackground extends BaseCharacterComponent{
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

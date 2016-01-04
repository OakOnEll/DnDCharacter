package com.oakonell.dndcharacter.model;

/**
 * Created by Rob on 1/3/2016.
 */
public class CharacterEffect extends BaseCharacterComponent {
    @Override
    public ComponentType getType() {
        return ComponentType.ITEM;
    }


    public String toString() {
        return "Effect: " + getName();
    }

}

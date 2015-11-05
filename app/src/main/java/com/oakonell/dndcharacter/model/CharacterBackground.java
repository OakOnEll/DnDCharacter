package com.oakonell.dndcharacter.model;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 10/24/2015.
 */
public class CharacterBackground extends BaseCharacterComponent{


    @Override
    public ComponentType getType() {
        return ComponentType.BACKGROUND;
    }
}

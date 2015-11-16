package com.oakonell.dndcharacter.model;

import com.oakonell.dndcharacter.model.background.SavedChoices;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 10/24/2015.
 */
public class CharacterBackground extends BaseCharacterComponent {
    @Element(required = false)
    String personalityTrait;
    @Element(required = false)
    String ideal;
    @Element(required = false)
    String bond;
    @Element(required = false)
    String flaw;



    @Override
    public ComponentType getType() {
        return ComponentType.BACKGROUND;
    }
}

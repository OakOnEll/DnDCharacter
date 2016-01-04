package com.oakonell.dndcharacter.model;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 1/3/2016.
 */
public class CharacterEffect extends AbstractContextualComponent {
    @Element(required = false)
    private String description;

    @Override
    public ComponentType getType() {
        return ComponentType.EFFECT;
    }

    public String toString() {
        return "Effect: " + getName();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceString() {
        // TODO link an effect to a possible eg feature source
        return "";
    }


}

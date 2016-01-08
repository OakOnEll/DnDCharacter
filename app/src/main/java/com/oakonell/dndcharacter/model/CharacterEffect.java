package com.oakonell.dndcharacter.model;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 1/3/2016.
 */
public class CharacterEffect extends AbstractContextualComponent {
    @Element(required = false)
    private String description;

    @Element(required = false)
    private String origin;

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

    public String getSource() {
        return origin;
    }

    public void setSource(String source) {
        this.origin = source;
    }


}

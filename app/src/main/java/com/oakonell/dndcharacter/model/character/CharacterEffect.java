package com.oakonell.dndcharacter.model.character;

import android.support.annotation.NonNull;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 1/3/2016.
 */
public class CharacterEffect extends AbstractContextualComponent {
    @Element(required = false)
    private String origin;
    @Element(required = false)
    private String id;

    @NonNull
    @Override
    public ComponentType getType() {
        return ComponentType.EFFECT;
    }

    @NonNull
    public String toString() {
        return "Effect: " + getName();
    }

    public String getSource() {
        return origin;
    }

    public void setSource(String source) {
        this.origin = source;
    }


    @Override
    public void setName(String name) {
        super.setName(name);
        id = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

package com.oakonell.dndcharacter.model;

/**
 * Created by Rob on 10/24/2015.
 */
public class CharacterClass extends BaseCharacterComponent {
    private String name;
    // derivable from order in character...
    private int level;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.CLASS;
    }

    public String getSourceString() {
        return getType().toString() + ": " + getName() + " " + level;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}

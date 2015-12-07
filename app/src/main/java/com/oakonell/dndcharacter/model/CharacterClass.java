package com.oakonell.dndcharacter.model;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 10/24/2015.
 */
public class CharacterClass extends BaseCharacterComponent {
    // derivable from order in character...
    private int level;

    @Element(required = false)
    private int hpRoll;
    @Element(required = false)
    private int hitDie;


    @Override
    public ComponentType getType() {
        return ComponentType.CLASS;
    }

    public String getSourceString() {
        return getType().toString() + ": " + getName() + " " + level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setHitDie(int hitDie) {
        this.hitDie = hitDie;
    }

    public int getHitDie() {
        return hitDie;
    }

    public int getHpRoll() {
        return hpRoll;
    }

    public void setHpRoll(int hpRoll) {
        this.hpRoll = hpRoll;
    }


}

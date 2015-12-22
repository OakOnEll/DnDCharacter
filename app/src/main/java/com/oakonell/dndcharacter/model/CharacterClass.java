package com.oakonell.dndcharacter.model;

import org.simpleframework.xml.Element;

import java.util.Map;

/**
 * Created by Rob on 10/24/2015.
 */
public class CharacterClass extends BaseCharacterComponent {
    // TODO derivable from order in character...? This is redundant currently
    @Element(required = false)
    private int level;

    @Element(required = false)
    private int hpRoll;
    @Element(required = false)
    private int hitDie;


    @Override
    public ComponentType getType() {
        return ComponentType.CLASS;
    }

    @Override
    public String getSourceString() {
        return getType().toString() + ": " + getName() + " " + level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHitDie() {
        return hitDie;
    }

    public void setHitDie(int hitDie) {
        this.hitDie = hitDie;
    }

    public int getHpRoll() {
        return hpRoll;
    }

    public void setHpRoll(int hpRoll) {
        this.hpRoll = hpRoll;
    }

    @Override
    public void addExtraFormulaVariables(Map<String, Integer> extraVariables) {
        super.addExtraFormulaVariables(extraVariables);
        extraVariables.put("classLevel", getLevel());
    }
}

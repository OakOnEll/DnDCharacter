package com.oakonell.dndcharacter.model.character;

import com.oakonell.expression.context.SimpleVariableContext;

import org.simpleframework.xml.Element;

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
    @Element(required = false)
    private String subclassName;
    @Element(required = false)
    private SavedChoices subClassChoices;


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
    public void addExtraFormulaVariables(SimpleVariableContext variableContext) {
        super.addExtraFormulaVariables(variableContext);
        variableContext.setNumber("classLevel", getLevel());
    }

    public String getSubclassName() {
        return subclassName;
    }

    public void setSubclassName(String subclassName) {
        this.subclassName = subclassName;
    }

    public SavedChoices getSubClassChoices() {
        return subClassChoices;
    }

    public void setSubClassChoices(SavedChoices subClassChoices) {
        this.subClassChoices = subClassChoices;
    }
}

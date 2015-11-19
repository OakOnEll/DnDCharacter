package com.oakonell.dndcharacter.model;

import org.simpleframework.xml.Element;

import java.util.List;

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


    public void setPersonalityTrait(String personalityTrait) {
        this.personalityTrait = personalityTrait;
    }

    public void setIdeal(String ideals) {
        this.ideal = ideals;
    }

    public void setBond(String bonds) {
        this.bond = bonds;
    }

    public void setFlaw(String flaws) {
        this.flaw = flaws;
    }

    public void setTraitSavedChoiceToCustom(String trait) {
        List<String> selections = savedChoices.getChoicesFor(trait);
        selections.clear();
        selections.add("custom");
    }


    public String getPersonalityTrait() {
        return personalityTrait;
    }

    public String getIdeal() {
        return ideal;
    }

    public String getBonds() {
        return bond;
    }

    public String getFlaw() {
        return flaw;
    }

}

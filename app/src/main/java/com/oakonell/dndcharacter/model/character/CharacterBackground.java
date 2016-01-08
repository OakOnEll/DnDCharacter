package com.oakonell.dndcharacter.model.character;

import org.simpleframework.xml.Element;

import java.util.List;

/**
 * Created by Rob on 10/24/2015.
 */
public class CharacterBackground extends BaseCharacterComponent {
    @Element(required = false)
    private String personalityTrait;
    @Element(required = false)
    private String ideal;
    @Element(required = false)
    private String bond;
    @Element(required = false)
    private String flaw;
    @Element(required = false)
    private String specialtyTitle;
    @Element(required = false)
    private String specialty;


    @Override
    public ComponentType getType() {
        return ComponentType.BACKGROUND;
    }

    public void setBond(String bonds) {
        this.bond = bonds;
    }

    public void setTraitSavedChoiceToCustom(String trait) {
        List<String> selections = getSavedChoices().getChoicesFor(trait);
        selections.clear();
        selections.add("custom");
    }

    public String getPersonalityTrait() {
        return personalityTrait;
    }

    public void setPersonalityTrait(String personalityTrait) {
        this.personalityTrait = personalityTrait;
    }

    public String getIdeal() {
        return ideal;
    }

    public void setIdeal(String ideals) {
        this.ideal = ideals;
    }

    public String getBonds() {
        return bond;
    }

    public String getFlaw() {
        return flaw;
    }

    public void setFlaw(String flaws) {
        this.flaw = flaws;
    }

    public String getSpecialtyTitle() {
        return specialtyTitle;
    }

    public void setSpecialtyTitle(String specialtyTitle) {
        this.specialtyTitle = specialtyTitle;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}

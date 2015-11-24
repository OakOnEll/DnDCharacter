package com.oakonell.dndcharacter.model.components;

import com.oakonell.dndcharacter.model.Proficient;
import com.oakonell.dndcharacter.model.SkillType;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 10/24/2015.
 */
public class Proficiency {
    @Element(required = false)
    private ProficiencyType type;

    @Element(required = false)
    private String name;

    @Element(required = false)
    private String category;

    @Element(required = false)
    private Proficient proficient;

    // for Simple XML
    public Proficiency() {

    }

    public Proficiency(ProficiencyType type, String name, String category, Proficient proficient) {
        this.type = type;
        this.name = name;
        this.category = category;
        this.proficient = proficient;
    }

    public ProficiencyType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public Proficient getProficient() {
        return proficient;
    }
}

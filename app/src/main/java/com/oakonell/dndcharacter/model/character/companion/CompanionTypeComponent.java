package com.oakonell.dndcharacter.model.character.companion;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.expression.context.SimpleVariableContext;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Rob on 12/10/2016.
 */

public class CompanionTypeComponent extends AbstractCompanionType {
    @Element(required = false)
    private String type;

    @Element(required = false)
    private String name;

    @Element(required = false)
    private String description;

    @Element(required = false)
    private String shortDescription;


    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName(Resources resources) {
        return name;
    }

    @Override
    public String getDescription(Resources resources) {
        return description;
    }

    @Override
    public String getShortDescription(Resources resources) {
        return shortDescription;
    }


    // Customization
    @Element(required = false)
    private boolean effectsSelf;

    @Element(required = false)
    private boolean onlyOneActiveAllowed;

    @Element(required = false)
    private String maxHpFormula;

    @Element(required = false)
    private String crLimit;


    public boolean effectsSelf() {
        return effectsSelf;
    }

    public boolean onlyOneActiveAllowed() {
        return effectsSelf() || onlyOneActiveAllowed;
    }

    public int getMaxHp(Character character, CharacterCompanion companion) {
        if (maxHpFormula == null || maxHpFormula.trim().length() == 0) {
            return companion.getRawMaxHP();
        }
        SimpleVariableContext context = new SimpleVariableContext();
        context.setNumber("level", character.getCharacterLevel());
        context.setNumber("companionMaxHP", companion.getRawMaxHP());
        return character.evaluateFormula(maxHpFormula, context);
    }


    public String getCrLimit(Character character) {
        if (crLimit == null || crLimit.trim().length() == 0) {
            return null;
        }
        SimpleVariableContext context = new SimpleVariableContext();
        context.setNumber("level", character.getCharacterLevel());
        return character.evaluateStringFormula(crLimit, context);
    }

    public boolean usesLimitedRaces() {
        return limitedRaces.size() > 0;
    }


    // setters


    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setEffectsSelf(boolean effectsSelf) {
        this.effectsSelf = effectsSelf;
    }

    public void setOnlyOneActiveAllowed(boolean onlyOneActiveAllowed) {
        this.onlyOneActiveAllowed = onlyOneActiveAllowed;
    }

    public void setMaxHpFormula(String maxHpFormula) {
        this.maxHpFormula = maxHpFormula;
    }

    public void setCrLimit(String crLimit) {
        this.crLimit = crLimit;
    }

    public Collection<String> getLimitedRaces() {
        return limitedRaces;
    }

    @NonNull
    @ElementList(required = false)
    private List<String> limitedRaces = new ArrayList<>();

}

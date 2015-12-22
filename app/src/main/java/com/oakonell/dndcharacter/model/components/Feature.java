package com.oakonell.dndcharacter.model.components;

import com.oakonell.dndcharacter.model.*;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 10/24/2015.
 */
public class Feature extends BaseCharacterComponent {
    @Element(required = false)
    private String description;
    @Element(required = false)
    private String usesFormula;
    @Element(required = false)
    private RefreshType refreshType;

    @Element(required = false)
    private UseType useType;


    @Override
    public ComponentType getType() {
        return ComponentType.FEATURE;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UseType getUseType() {
        return useType;
    }

    public void setUseType(UseType useType) {
        this.useType = useType;
    }

    public String getUsesFormula() {
        return usesFormula;
    }


    public void setFormula(String formula) {
        usesFormula = formula;
    }

    public RefreshType getRefreshesOn() {
        return refreshType;
    }

    public void setRefreshesOn(RefreshType type) {
        this.refreshType = type;
    }
}

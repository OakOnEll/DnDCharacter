package com.oakonell.dndcharacter.model.components;

/**
 * Created by Rob on 10/24/2015.
 */
public class Feature {
    private String name;
    private String description;
    private String usesFormula;
    private RefreshType type;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUsesFormula() {
        return usesFormula;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFormula(String formula) {
        usesFormula = formula;
    }

    public void setRefreshesOn(RefreshType type) {
        this.type = type;
    }

    public RefreshType getRefreshesOn() {
        return type;
    }
}

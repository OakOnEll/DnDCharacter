package com.oakonell.dndcharacter.model.components;

import com.oakonell.dndcharacter.model.character.*;
import com.oakonell.dndcharacter.model.character.Character;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 10/24/2015.
 */
public class Feature extends AbstractContextualComponent {
    @Element(required = false)
    private String description;
    @Element(required = false)
    private String usesFormula;
    @Element(required = false)
    private RefreshType refreshType;
    @Element(required = false)
    private UseType useType;

    @ElementList(required = false)
    private List<FeatureCharacterEffect> effects = new ArrayList<>();
    @ElementList(required = false)
    private List<FeatureAction> actions = new ArrayList<>();


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

    public void addEffect(FeatureCharacterEffect characterEffect) {
        effects.add(characterEffect);
    }

    public List<FeatureCharacterEffect> getEffects() {
        return effects;
    }

    public void addAction(FeatureAction action) {
        actions.add(action);
    }

    public List<FeatureAction> getActions() {
        return actions;
    }

    public List<IFeatureAction> getActionsAndEffects() {
        List<IFeatureAction> result = new ArrayList<>();
        result.addAll(getActions());
        result.addAll(getEffects());
        return result;
    }


    public static class FeatureAction implements IFeatureAction {
        @Element(required = false)
        String name;
        @Element(required = false)
        private String description;

        @Element(required = false)
        int cost;

        @Override
        public String getAction() {
            return getName();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public int getCost() {
            return cost;
        }

        @Override
        public void applyToCharacter(com.oakonell.dndcharacter.model.character.Character character) {
            // do nothing
        }

        public void setCost(int cost) {
            this.cost = cost;
        }
    }

    public static class FeatureCharacterEffect extends CharacterEffect implements IFeatureAction {
        @Element(required = false)
        private String action;

        @Element(required = false)
        int cost;

        public void setCost(int cost) {
            this.cost = cost;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public int getCost() {
            return cost;
        }

        @Override
        public void applyToCharacter(Character character) {
            character.addEffect(this);
        }
    }
}

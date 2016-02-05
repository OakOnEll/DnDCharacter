package com.oakonell.dndcharacter.model.components;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.*;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.expression.Expression;
import com.oakonell.expression.ExpressionContext;
import com.oakonell.expression.ExpressionType;
import com.oakonell.expression.context.SimpleFunctionContext;
import com.oakonell.expression.context.SimpleVariableContext;

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

    @Element(required = false)
    private String appliesFormula;

    @NonNull
    @ElementList(required = false)
    private List<FeatureCharacterEffect> effects = new ArrayList<>();
    @NonNull
    @ElementList(required = false)
    private List<FeatureAction> actions = new ArrayList<>();


    @NonNull
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

    @NonNull
    public List<FeatureCharacterEffect> getEffects() {
        return effects;
    }

    public void addAction(FeatureAction action) {
        actions.add(action);
    }

    @NonNull
    public List<FeatureAction> getActions() {
        return actions;
    }

    @NonNull
    public List<IFeatureAction> getActionsAndEffects() {
        List<IFeatureAction> result = new ArrayList<>();
        result.addAll(getActions());
        result.addAll(getEffects());
        return result;
    }

    public boolean applies(Character character) {
        if (appliesFormula == null || appliesFormula.trim().length() == 0) return true;

        // TODO can't construct the normal context variables, as it looks on features!
        //    possibly calculate variables without this feature, in some contextual way
        SimpleVariableContext variableContext = new SimpleVariableContext();
        variableContext.setNumber("level", character.getClasses().size());

        try {
            Expression<Boolean> expression = Expression.parse(appliesFormula, ExpressionType.BOOLEAN_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext));
            return expression.evaluate();
        } catch (Exception e) {
            // should be done at formula save time...
            return false;
        }
    }

    public void setAppliesFormula(String appliesFormula) {
        this.appliesFormula = appliesFormula;
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
        public void applyToCharacter(@NonNull Character character) {
            character.addEffect(this);
        }
    }
}

package com.oakonell.dndcharacter.model.character;

import com.oakonell.dndcharacter.views.character.IContextualComponent;
import com.oakonell.dndcharacter.model.components.Feature;
import com.oakonell.dndcharacter.model.components.UseType;
import com.oakonell.dndcharacter.views.character.FeatureContext;
import com.oakonell.expression.context.SimpleVariableContext;

import java.util.Set;

/**
 * Created by Rob on 10/26/2015.
 */
public class FeatureInfo implements IContextualComponent {
    Feature feature;
    BaseCharacterComponent source;

    public String getName() {
        return feature.getName();
    }

    public String getSourceString() {
        return source.getSourceString();
    }

    public String getShortDescription() {
        return feature.getDescription();
    }


    public boolean hasLimitedUses() {
        String formula = feature.getUsesFormula();
        return formula != null && formula.length() > 0;
    }

    public BaseCharacterComponent getSource() {
        return source;
    }

    public Feature getFeature() {
        return feature;
    }

    public int evaluateMaxUses(Character character) {
        SimpleVariableContext variableContext = new SimpleVariableContext();
        source.addExtraFormulaVariables(variableContext);
        return character.evaluateFormula(feature.getUsesFormula(), variableContext);
    }


    public UseType getUseType() {
        return feature.getUseType();
    }

    @Override
    public boolean isInContext(FeatureContext context) {
        return feature.isInContext(context);
    }

    @Override
    public boolean isInContext(Set<FeatureContext> filter) {
        return feature.isInContext(filter);
    }
}

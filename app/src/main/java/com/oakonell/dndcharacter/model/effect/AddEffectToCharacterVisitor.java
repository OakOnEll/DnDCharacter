package com.oakonell.dndcharacter.model.effect;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.character.AbstractContextualComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.components.Feature;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.feature.FeatureViewHelper;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class AddEffectToCharacterVisitor extends AbstractEffectVisitor {
    private final CharacterEffect charEffect;
    //String currentChoiceName;

    private AddEffectToCharacterVisitor(CharacterEffect charEffect) {
        this.charEffect = charEffect;
    }

    @NonNull
    public static CharacterEffect applyToCharacter(final @NonNull CharacterActivity context, final @NonNull Effect race, final @NonNull Character character, @Nullable final Runnable continuation) {
        Element element = XmlUtils.getDocument(race.getXml()).getDocumentElement();
        final CharacterEffect characterEffect = new CharacterEffect();
        readEffect(context, element, characterEffect);

        // look for any variable/prompts
        List<Feature.FeatureEffectVariable > variables = new ArrayList<>();
        List<Element> variableElements = XmlUtils.getChildElements(element, "variable");
        for (Element variableElement : variableElements) {
            String varName = variableElement.getAttribute("name");
            String promptString = variableElement.getAttribute("prompt");
            String valuesString = variableElement.getTextContent();
            String[] values;
            if (valuesString == null || valuesString.trim().length() == 0) {
                values = new String[]{};
            } else {
                values = valuesString.split("\\s*,\\s*");
            }
            // clean up empty strings..

            Feature.FeatureEffectVariable variable = new Feature.FeatureEffectVariable(varName, promptString, values);
            variables.add(variable);
        }

        if (!variables.isEmpty()) {
            final Map<Feature.FeatureEffectVariable, String> variableValues = new HashMap<>();
            final Runnable variableContinuation = new Runnable() {
                @Override
                public void run() {
//                    String theName = characterEffect.getName();
                    String theDescription = characterEffect.getDescription();
                    for (Map.Entry<Feature.FeatureEffectVariable, String> entry : variableValues.entrySet()) {
                        String variablePattern = "\\$" + entry.getKey().getName();
                        String value = entry.getValue();
//                        theName = theName.replaceAll(variablePattern, value);
                        if (theDescription != null) {
                            theDescription = theDescription.replaceAll(variablePattern, value);
                        }
                        // TODO look through the contexts to replace any prompt variables
                    }
  //                  characterEffect.setName(theName);
                    characterEffect.setDescription(theDescription);

                    character.addEffect(characterEffect);
                    if (continuation != null) continuation.run();
                }
            };
            FeatureViewHelper.prompt(variables, variableValues, 0, context, variableContinuation);
            return characterEffect;
        }

        character.addEffect(characterEffect);
        if (continuation != null) continuation.run();

        return characterEffect;
    }

    public static void readEffect(@NonNull Context context, @NonNull Element element, final @NonNull CharacterEffect characterEffect) {
        characterEffect.setName(XmlUtils.getElementText(element, "name"));
        // apply common changes
        ApplyChangesToGenericComponent.applyToCharacter(context, element, new SavedChoices(), characterEffect, null, false);

        AddEffectToCharacterVisitor newMe = new AddEffectToCharacterVisitor(characterEffect);
        newMe.visit(element);



        final String ac = XmlUtils.getElementText(element, "ac");
        characterEffect.setAcFormula(ac);

        // this needs to come after setName, which give the id the same as the name by default
        final String id = XmlUtils.getElementText(element, "id");
        if (id != null && id.trim().length() >0) {
            characterEffect.setId(id);
        }

        final String contextsString = XmlUtils.getElementText(element, "context");
        if (contextsString != null) {
            String[] contexts = contextsString.split(",");
            for (String each : contexts) {
                if (each.trim().length()==0) continue;
                String contextString = each.trim();
                final FeatureContextArgument featureContextArgument = AbstractContextualComponent.parseFeatureContext(contextString);
                characterEffect.addContext(featureContextArgument);
            }
        }



    }

    @Override
    protected void visitShortDescription(@NonNull Element element) {
        charEffect.setDescription(element.getTextContent());
    }
}

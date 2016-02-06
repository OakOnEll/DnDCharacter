package com.oakonell.dndcharacter.model.feat;

import android.content.Context;
import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.character.AbstractContextualComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.effect.AbstractEffectVisitor;
import com.oakonell.dndcharacter.model.effect.Effect;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 11/9/2015.
 */
public class AddFeatToCharacterVisitor extends AbstractEffectVisitor {
    private final CharacterEffect charEffect;
    //String currentChoiceName;

    private AddFeatToCharacterVisitor(CharacterEffect charEffect) {
        this.charEffect = charEffect;
    }

    @NonNull
    public static CharacterEffect applyToCharacter(@NonNull Context context, @NonNull Effect race, @NonNull Character character) {
        Element element = XmlUtils.getDocument(race.getXml()).getDocumentElement();
        CharacterEffect characterEffect = new CharacterEffect();
        readEffect(context, element, characterEffect);

        character.addEffect(characterEffect);
        return characterEffect;
    }

    public static void readEffect(@NonNull Context context, @NonNull Element element, @NonNull CharacterEffect characterEffect) {
        characterEffect.setName(XmlUtils.getElementText(element, "name"));
        // apply common changes
        ApplyChangesToGenericComponent.applyToCharacter(context, element, new SavedChoices(), characterEffect, null, false);

        AddFeatToCharacterVisitor newMe = new AddFeatToCharacterVisitor(characterEffect);
        newMe.visit(element);

        final String contextsString = XmlUtils.getElementText(element, "context");
        if (contextsString != null) {
            String[] contexts = contextsString.split(",");
            for (String each : contexts) {
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

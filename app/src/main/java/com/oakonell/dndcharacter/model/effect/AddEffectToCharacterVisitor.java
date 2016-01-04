package com.oakonell.dndcharacter.model.effect;

import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterEffect;
import com.oakonell.dndcharacter.model.SavedChoices;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.FeatureContext;

import org.w3c.dom.Element;

/**
 * Created by Rob on 11/9/2015.
 */
public class AddEffectToCharacterVisitor extends AbstractEffectVisitor {
    private final CharacterEffect charEffect;
    //String currentChoiceName;

    private AddEffectToCharacterVisitor(CharacterEffect charEffect) {
        this.charEffect = charEffect;
    }

    public static CharacterEffect applyToCharacter(Effect race, Character character) {
        CharacterEffect characterEffect = new CharacterEffect();

        // apply common changes
        Element element = XmlUtils.getDocument(race.getXml()).getDocumentElement();
        characterEffect.setName(XmlUtils.getElementText(element, "name"));
        ApplyChangesToGenericComponent.applyToCharacter(element, new SavedChoices(), characterEffect, character, true);

        AddEffectToCharacterVisitor newMe = new AddEffectToCharacterVisitor(characterEffect);
        newMe.visit(element);

        final String contextsString = XmlUtils.getElementText(element, "context");
        if (contextsString != null) {
            String[] contexts = contextsString.split(",");
            for (String each : contexts) {
                String contextString = each.trim();
                FeatureContext context = FeatureContext.valueOf(contextString.toUpperCase());
                characterEffect.addContext(context);
            }
        }

        character.addEffect(characterEffect);
        return characterEffect;
    }

    @Override
    protected void visitShortDescription(Element element) {
        charEffect.setDescription(element.getTextContent());
    }
}

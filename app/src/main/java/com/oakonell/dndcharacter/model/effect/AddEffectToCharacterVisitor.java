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
        Element element = XmlUtils.getDocument(race.getXml()).getDocumentElement();
        CharacterEffect characterEffect = new CharacterEffect();
        readEffect(element, characterEffect);

        character.addEffect(characterEffect);
        return characterEffect;
    }

    public static void readEffect(Element element, CharacterEffect characterEffect) {
        characterEffect.setName(XmlUtils.getElementText(element, "name"));
        // apply common changes
        ApplyChangesToGenericComponent.applyToCharacter(element, new SavedChoices(), characterEffect, null, false);

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
    }

    @Override
    protected void visitShortDescription(Element element) {
        charEffect.setDescription(element.getTextContent());
    }
}

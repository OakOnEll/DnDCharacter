package com.oakonell.dndcharacter.model.effect;

import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterEffect;
import com.oakonell.dndcharacter.model.CharacterRace;
import com.oakonell.dndcharacter.model.SavedChoices;
import com.oakonell.dndcharacter.model.race.AbstractRaceVisitor;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class AddEffectToCharacterVisitor extends AbstractEffectVisitor {
    //String currentChoiceName;

    private AddEffectToCharacterVisitor(CharacterEffect charEffect) {
//        CharacterRace charRace1 = charRace;
//        SavedChoices savedChoices1 = savedChoices;
//        Map<String, String> customChoices1 = customChoices;
    }

    public static CharacterEffect applyToCharacter(Effect race, Character character) {
        CharacterEffect characterEffect = new CharacterEffect();

        // apply common changes
        Element element = XmlUtils.getDocument(race.getXml()).getDocumentElement();
        characterEffect.setName(XmlUtils.getElementText(element, "name"));
        ApplyChangesToGenericComponent.applyToCharacter(element, new SavedChoices(), characterEffect, character, true);

        AddEffectToCharacterVisitor newMe = new AddEffectToCharacterVisitor(characterEffect);
        newMe.visit(element);

        character.addEffect(characterEffect);
        return characterEffect;
    }


}

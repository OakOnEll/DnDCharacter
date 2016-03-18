package com.oakonell.dndcharacter.model.race;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterRace;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class ApplyRaceToCharacterVisitor extends AbstractRaceVisitor {
    //String currentChoiceName;

    private ApplyRaceToCharacterVisitor(SavedChoices savedChoices, Map<String, String> customChoices, CharacterRace charRace) {
//        CharacterRace charRace1 = charRace;
//        SavedChoices savedChoices1 = savedChoices;
//        Map<String, String> customChoices1 = customChoices;
    }

    public static void applyToCharacter(@NonNull Context context, @NonNull Race race, SavedChoices savedChoices, Map<String, String> customChoices,
                                        @Nullable Race subrace, SavedChoices subraceSavedChoices, Map<String, String> subraceCustomChoices,
                                        @NonNull Character character, boolean preventItemDelete) {
        CharacterRace charRace = new CharacterRace();
        charRace.setSavedChoices(savedChoices);

        // apply common changes
        Element element = XmlUtils.getDocument(race.getXml()).getDocumentElement();
        charRace.setName(XmlUtils.getElementText(element, "name"));
        ApplyChangesToGenericComponent.applyToCharacter(context, element, savedChoices, charRace, character, true && !preventItemDelete);

        ApplyRaceToCharacterVisitor newMe = new ApplyRaceToCharacterVisitor(savedChoices, customChoices, charRace);
        newMe.visit(element);


        if (subrace != null) {
            Element subraceElement = XmlUtils.getDocument(subrace.getXml()).getDocumentElement();
            charRace.setSubraceName(XmlUtils.getElementText(subraceElement, "name"));
            ApplyChangesToGenericComponent.applyToCharacter(context, subraceElement, subraceSavedChoices, charRace, character, false);
            charRace.setSubRaceChoices(subraceSavedChoices);
            newMe.visit(subraceElement);
        }

        character.setRace(charRace);
    }


}

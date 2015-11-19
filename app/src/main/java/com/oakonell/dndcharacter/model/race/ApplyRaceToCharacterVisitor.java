package com.oakonell.dndcharacter.model.race;

import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterBackground;
import com.oakonell.dndcharacter.model.CharacterRace;
import com.oakonell.dndcharacter.model.SavedChoices;
import com.oakonell.dndcharacter.model.background.AbstractBackgroundVisitor;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class ApplyRaceToCharacterVisitor extends AbstractRaceVisitor {
    private final Map<String, String> customChoices;
    private final CharacterRace charRace;
    private final SavedChoices savedChoices;
    String currentChoiceName;

    public static void applyToCharacter(Race race, SavedChoices savedChoices, Map<String, String> customChoices, Character character) {
        CharacterRace charRace = new CharacterRace();
        charRace.setSavedChoices(savedChoices);
        // apply common changes
        Element element = XmlUtils.getDocument(race.getXml()).getDocumentElement();
        ApplyChangesToGenericComponent.applyToCharacter(element, savedChoices, charRace);


        ApplyRaceToCharacterVisitor newMe = new ApplyRaceToCharacterVisitor(savedChoices, customChoices, charRace);
        newMe.visit(element);
        character.setRace(charRace);
    }

    public ApplyRaceToCharacterVisitor(SavedChoices savedChoices, Map<String, String> customChoices, CharacterRace charRace) {
        this.charRace = charRace;
        this.savedChoices = savedChoices;
        this.customChoices = customChoices;
    }


    @Override
    protected void visitRace(Element element) {
        charRace.setName(XmlUtils.getElementText(element, "name"));
        super.visitRace(element);
    }

}

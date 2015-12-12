package com.oakonell.dndcharacter.model.classes;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterClass;
import com.oakonell.dndcharacter.model.SavedChoices;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class ApplyClassToCharacterVisitor extends AbstractClassVisitor {
    private final Map<String, String> customChoices;
    private final CharacterClass charClass;
    private final SavedChoices savedChoices;

    public ApplyClassToCharacterVisitor(SavedChoices savedChoices, Map<String, String> customChoices, CharacterClass charClass) {
        this.charClass = charClass;
        this.savedChoices = savedChoices;
        this.customChoices = customChoices;
    }

//    public static void replaceClassLevel(AClass aClass, SavedChoices savedChoices, Map<String, String> customChoices, Character character, int characterLevel, int classLevel) {
//        // TODO this could get complex... moving other class levels around...
//        CharacterClass charClass = new CharacterClass();
//        charClass.setSavedChoices(savedChoices);
//        // apply common changes
//        Element element = XmlUtils.getDocument(aClass.getXml()).getDocumentElement();
//        ApplyChangesToGenericComponent.applyToCharacter(element, savedChoices, charClass, character);
//
//
//        ApplyClassToCharacterVisitor newMe = new ApplyClassToCharacterVisitor(savedChoices, customChoices, charClass, classLevel);
//        newMe.visit(element);
////        character.getClasses().remove(characterLevel);
////        character.setBackground(charBackground);
//    }


    public static void updateClassLevel(AClass aClass, SavedChoices savedChoices, Map<String, String> customChoices, Character character, int classIndex, int classLevel, int hpRoll) {
        CharacterClass charClass = createCharacterClass(aClass, savedChoices, customChoices, character, classLevel, hpRoll);
        character.getClasses().set(classIndex, charClass);
    }

    @NonNull
    private static CharacterClass createCharacterClass(AClass aClass, SavedChoices savedChoices, Map<String, String> customChoices, Character character, int classLevel, int hpRoll) {
        CharacterClass charClass = new CharacterClass();
        charClass.setSavedChoices(savedChoices);
        // apply common changes
        Element rootClassElement = XmlUtils.getDocument(aClass.getXml()).getDocumentElement();


        if (classLevel == 1) {
            // this will not visit any level elements, but will apply top level stuff as the first class for a character
            ApplyChangesToGenericComponent.applyToCharacter(rootClassElement, savedChoices, charClass, character);

            // grab the first character level skills and such
            ApplyClassToCharacterVisitor newMe = new ApplyClassToCharacterVisitor(savedChoices, customChoices, charClass);
            newMe.visit(rootClassElement);
        }
        // apply root level stuff that is always applicable
        //    make sure to get important top-tier info, like the class name
        String name = XmlUtils.getElementText(rootClassElement, "name");
        charClass.setName(name);
        int hitDie = AClass.getHitDieSides(rootClassElement);
        charClass.setHitDie(hitDie);
        charClass.setHpRoll(hpRoll);
        charClass.setLevel(classLevel);

        Element levelElement = AClass.findLevelElement(rootClassElement, classLevel);
        if (levelElement != null) {
            ApplyChangesToGenericComponent.applyToCharacter(levelElement, savedChoices, charClass, character);

            ApplyClassToCharacterVisitor newMe = new ApplyClassToCharacterVisitor(savedChoices, customChoices, charClass);
            newMe.visit(levelElement);
        }
        return charClass;
    }

    public static void addClassLevel(AClass aClass, SavedChoices savedChoices, Map<String, String> customChoices, Character character, int classLevel, int hpRoll) {
        CharacterClass charClass = createCharacterClass(aClass, savedChoices, customChoices, character, classLevel, hpRoll);
        character.getClasses().add(charClass);
    }


    @Override
    protected void visitLevel(Element element) {
        // the visitor shouldn't actually dive into levels
        return;
    }

}

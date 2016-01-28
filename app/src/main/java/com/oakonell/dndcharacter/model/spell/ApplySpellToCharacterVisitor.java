package com.oakonell.dndcharacter.model.spell;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.spell.CharacterSpell;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by Rob on 11/9/2015.
 */
public class ApplySpellToCharacterVisitor extends AbstractSpellVisitor {

    private final CharacterSpell charSpell;

    private ApplySpellToCharacterVisitor(CharacterSpell charSpell) {
        this.charSpell = charSpell;
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


    @NonNull
    public static CharacterSpell createCharacterSpell(@NonNull Spell spell, @NonNull Character character) {
        CharacterSpell charSpell = new CharacterSpell();

        final Element root = XmlUtils.getDocument(spell.getXml()).getDocumentElement();
        ApplyChangesToGenericComponent.applyToCharacter(root, null, charSpell, character, false);


        ApplySpellToCharacterVisitor newMe = new ApplySpellToCharacterVisitor(charSpell);
        newMe.visit(spell);

        int level = spell.getLevel();
        List<CharacterSpell> spells = character.getSpells(level);
        spells.add(charSpell);

        return charSpell;
    }


    @Override
    protected void visitSpell(Element element) {
        String levelString = XmlUtils.getElementText(element, "level");
        int level = Integer.parseInt(levelString);
        charSpell.setLevel(level);


        String name = XmlUtils.getElementText(element, "name");
        charSpell.setName(name);

        String school = XmlUtils.getElementText(element, "school");
        charSpell.setSchool(school);


        //charSpell.setAttackType();
        String castingTime = XmlUtils.getElementText(element, "castingTime");
        charSpell.setCastingTime(castingTime);

    }

//    @Override
//    protected void visitName(Element element) {
//        String name = element.getTextContent();
//        charSpell.setName(name);
//    }


}

package com.oakonell.dndcharacter.model.spell;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.spell.CastingTimeType;
import com.oakonell.dndcharacter.model.character.spell.CharacterSpell;
import com.oakonell.dndcharacter.model.character.spell.SpellAttackType;
import com.oakonell.dndcharacter.model.character.spell.SpellDurationType;
import com.oakonell.dndcharacter.model.character.spell.SpellRange;
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
    public static CharacterSpell createCharacterSpell(@NonNull Context context, @NonNull Spell spell, @NonNull Character character) {
        // TODO this is only called from spell fragment add spell, and is not getting the "owner"class of the spell, to know whether preparable, etc...
        CharacterSpell charSpell = new CharacterSpell();

        final Element root = XmlUtils.getDocument(spell.getXml()).getDocumentElement();
        ApplyChangesToGenericComponent.applyToCharacter(context, root, null, charSpell, character, false);


        ApplySpellToCharacterVisitor newMe = new ApplySpellToCharacterVisitor(charSpell);
        newMe.visit(spell);


        int level = spell.getLevel();
        List<CharacterSpell> spells = character.getSpells(level);
        spells.add(charSpell);

        return charSpell;
    }


    @Override
    protected void visitSpell(@NonNull Element element) {
        String levelString = XmlUtils.getElementText(element, "level");
        int level = Integer.parseInt(levelString);
        charSpell.setLevel(level);

        String name = XmlUtils.getElementText(element, "name");
        charSpell.setName(name);

        populateCommonSpellProperties(charSpell, element);
    }

//    @Override
//    protected void visitName(Element element) {
//        String name = element.getTextContent();
//        charSpell.setName(name);
//    }


    public static void populateCommonSpellProperties(CharacterSpell charSpell, Element element) {
        String school = XmlUtils.getElementText(element, "school");
        charSpell.setSchool(EnumHelper.stringToEnum(school, SpellSchool.class));


        if ("true".equals(XmlUtils.getElementText(element, "concentration"))) {
            charSpell.setConcentration(true);
        }
        if ("true".equals(XmlUtils.getElementText(element, "ritual"))) {
            charSpell.setRitual(true);
        }

        // casting time
        final String castingTimeTypeString = XmlUtils.getElementText(element, "castingTimeType");
        if (castingTimeTypeString != null) {
            CastingTimeType castingTimeType = EnumHelper.stringToEnum(castingTimeTypeString, CastingTimeType.class);
            charSpell.setCastingType(castingTimeType);
        } else {
            Log.e("SpellToCharacter", "missing casting time type? for spell " + charSpell.getName());
        }
        int castingTime = Integer.parseInt(XmlUtils.getElementText(element, "castingTime"));
        charSpell.setCastingTime(castingTime);


        // duration
        SpellDurationType durationType = EnumHelper.stringToEnum(XmlUtils.getElementText(element, "durationType"), SpellDurationType.class);
        charSpell.setDurationType(durationType);
        final String durationString = XmlUtils.getElementText(element, "duration");
        if (durationString != null) {
            int duration = Integer.parseInt(durationString);
            charSpell.setDurationTime(duration);
        }

        //range
        SpellRange rangeType = EnumHelper.stringToEnum(XmlUtils.getElementText(element, "rangeType"), SpellRange.class);
        charSpell.setRangeType(rangeType);
        final String rangeString = XmlUtils.getElementText(element, "range");
        if (rangeString != null) {
            int range = Integer.parseInt(rangeString);
            charSpell.setRange(range);
        }

        // attack type
        final String attackTypeString = XmlUtils.getElementText(element, "attackType");
        if (attackTypeString != null) {
            SpellAttackType attackType = EnumHelper.stringToEnum(attackTypeString, SpellAttackType.class);
            charSpell.setAttackType(attackType);
        }

        // components
        Element components = XmlUtils.getElement(element, "components");
        charSpell.setUsesVerbal("true".equals(XmlUtils.getElementText(components, "verbal")));
        charSpell.setUsesSomatic("true".equals(XmlUtils.getElementText(components, "somatic")));
        final String material = XmlUtils.getElementText(components, "material");
        charSpell.setComponent(material);
        final String specialMaterial = XmlUtils.getElementText(components, "specialMaterial");
        charSpell.setSpecialComponent(specialMaterial);
        charSpell.setUsesMaterial(material != null || specialMaterial != null);


        String higherLevelDescription = XmlUtils.getElementText(element, "higherLevelDescription");
        if (higherLevelDescription != null) {
            charSpell.setHigherLevelDescription(higherLevelDescription);
        }

        String directRoll = XmlUtils.getElementText(element, "directRoll");
        charSpell.setHasDirectRoll("true".equals(directRoll));
    }

}

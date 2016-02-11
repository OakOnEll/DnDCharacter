package com.oakonell.dndcharacter.model.classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterClass;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.components.RefreshType;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class ApplyClassToCharacterVisitor extends AbstractClassVisitor {

    private final CharacterClass charClass;

    private ApplyClassToCharacterVisitor(SavedChoices savedChoices, Map<String, String> customChoices, CharacterClass charClass) {
        this.charClass = charClass;
        SavedChoices savedChoices1 = savedChoices;
        Map<String, String> customChoices1 = customChoices;
    }

    public static void updateClassLevel(@NonNull Context context, @NonNull AClass aClass, SavedChoices savedChoices, Map<String, String> customChoices, AClass subClass, SavedChoices subclassSavedChoices, @NonNull Character character, int classIndex, int classLevel, int hpRoll) {
        CharacterClass charClass = createCharacterClass(context, aClass, savedChoices, customChoices, subClass, subclassSavedChoices, character, classIndex + 1, classLevel, hpRoll);
        character.getClasses().set(classIndex, charClass);
    }

    @NonNull
    private static CharacterClass createCharacterClass(@NonNull Context context, @NonNull AClass aClass, SavedChoices savedChoices, Map<String, String> customChoices, @Nullable AClass subClass, SavedChoices subclassSavedChoices, Character character, int characterLevel, int classLevel, int hpRoll) {
        CharacterClass charClass = new CharacterClass();
        charClass.setSavedChoices(savedChoices);
        // apply common changes
        Element rootClassElement = XmlUtils.getDocument(aClass.getXml()).getDocumentElement();


        if (characterLevel == 1) {
            // this will not visit any level elements, but will apply top level stuff as the first class for a character
            ApplyChangesToGenericComponent.applyToCharacter(context, rootClassElement, savedChoices, charClass, character, true);

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
            ApplyChangesToGenericComponent.applyToCharacter(context, levelElement, savedChoices, charClass, character, false);

            ApplyClassToCharacterVisitor newMe = new ApplyClassToCharacterVisitor(savedChoices, customChoices, charClass);
            newMe.visitChildren(levelElement);
        }
        if (classLevel == 1 && characterLevel != 1) {
            // apply the root level spell related things
            // TODO maybe better to move these elements to level 1, directly
            ApplyClassToCharacterVisitor newMe = new ApplyClassToCharacterVisitor(savedChoices, customChoices, charClass);
            Element prepared = XmlUtils.getElement(rootClassElement, "preparedSpellsFormula");
            if (prepared != null) {
                newMe.visitPreparedSpells(prepared);
            }
            Element spellCastingStat = XmlUtils.getElement(rootClassElement, "spellCastingStat");
            if (spellCastingStat != null) {
                newMe.visitSpellCastingStat(spellCastingStat);
            }
            Element multiclassCasterFactor = XmlUtils.getElement(rootClassElement, "multiclassCasterFactor");
            if (multiclassCasterFactor != null) {
                newMe.visitMulticlassCasterFactor(multiclassCasterFactor);
            }
        }

        if (subClass != null) {
            Element subClassRootElement = XmlUtils.getDocument(subClass.getXml()).getDocumentElement();
            Element subClassLevelElement = AClass.findLevelElement(subClassRootElement, classLevel);
            if (subClassLevelElement != null) {
                ApplyChangesToGenericComponent.applyToCharacter(context, subClassLevelElement, subclassSavedChoices, charClass, character, false);
                ApplyClassToCharacterVisitor newMe = new ApplyClassToCharacterVisitor(subclassSavedChoices, null, charClass);
                newMe.visitChildren(subClassLevelElement);
                charClass.setSubclassName(subClass.getName());
                charClass.setSubClassChoices(subclassSavedChoices);
            }
        }

        if (character.canChooseAbilityScoreImprovement(aClass, classLevel)) {
            InputStream in = null;
            try {
                in = context.getAssets().open("asi.xml");
            } catch (IOException e) {
                Log.e("ClassLevelEditDialog", "Error parsing asi.xml in assets!", e);
                throw new RuntimeException("Error parsing asi.xml in assets!", e);
            }
            final Element root = XmlUtils.getDocument(in).getDocumentElement();
            ApplyAbilityScoreImprovement.applyASIToCharacter(context, root, savedChoices, charClass, character);
        }

        return charClass;
    }

    public static void addClassLevel(@NonNull Context context, @NonNull AClass aClass, SavedChoices savedChoices, Map<String, String> customChoices, AClass subClass, SavedChoices subclassSavedChoices, @NonNull Character character, int characterlevel, int classLevel, int hpRoll) {
        CharacterClass charClass = createCharacterClass(context, aClass, savedChoices, customChoices, subClass, subclassSavedChoices, character, characterlevel, classLevel, hpRoll);
        character.getClasses().add(charClass);
    }


    @Override
    protected void visitLevel(Element element) {
        // the visitor shouldn't actually dive into levels
    }

    @Override
    protected void visitPreparedSpells(@NonNull Element element) {
        String preparedSpellsFormula = element.getTextContent();
        charClass.setPreparedSpellsFormula(preparedSpellsFormula);
    }

    @Override
    protected void visitMulticlassCasterFactor(@NonNull Element element) {
        String multiclassFactorFormula = element.getTextContent();
        charClass.setMulticlassCasterFactorFormula(multiclassFactorFormula);
    }

    @Override
    protected void visitSpellCastingStat(@NonNull Element element) {
        String statName = element.getTextContent();
        statName = statName.replaceAll(" ", "_");
        statName = statName.toUpperCase();
        StatType stat = StatType.valueOf(StatType.class, statName);
        // TODO handle error
        charClass.setCasterStat(stat);
    }

    @Override
    protected void visitCantrips(Element element) {
        String known = XmlUtils.getElementText(element, "known");
        charClass.setCantripsKnownFormula(known);
    }

    @Override
    protected void visitSpells(Element element) {
        /* <spells>
        * <known>4</known>
        * <slots>
        * <level value="1">4</level>
        * <level value="2">2</level>
        * </slots>
        * </spells>
        */
        String spellsKnown = XmlUtils.getElementText(element, "known");
        charClass.setSpellsKnownFormula(spellsKnown);
        Element slotsElem = XmlUtils.getElement(element, "slots");
        if (slotsElem != null) {
            List<Element> spellLevelElems = XmlUtils.getChildElements(slotsElem, "level");
            Map<Integer, String> levelFormulas = new HashMap<>();
            for (Element each : spellLevelElems) {
                String levelString = each.getAttribute("value");
                int level = Integer.parseInt(levelString);
                levelFormulas.put(level, each.getTextContent());
            }
            charClass.setSpellLevelSlotFormulas(levelFormulas);
            String refreshString = XmlUtils.getElementText(slotsElem, "refreshes");
            RefreshType refreshType = null;
            if (refreshString != null) {
                refreshString = refreshString.toLowerCase();
                refreshString = refreshString.replaceAll(" ", "");
                switch (refreshString) {
                    case "rest": // fall through
                    case "shortrest":
                        refreshType = RefreshType.SHORT_REST;
                        break;
                    case "longrest":
                        refreshType = RefreshType.LONG_REST;
                        break;
                    default:
                        throw new RuntimeException("Unknown refresh string '" + refreshString + "' on class " + charClass.getName() + ", spell slots");
                }
                charClass.setSpellSlotRefresh(refreshType);
            }
        }


    }
}

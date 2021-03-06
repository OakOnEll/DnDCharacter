package com.oakonell.dndcharacter.model.classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterClass;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.model.character.spell.CharacterSpell;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.components.RefreshType;
import com.oakonell.dndcharacter.model.spell.ApplySpellToCharacterVisitor;
import com.oakonell.dndcharacter.model.spell.Spell;
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


    public static void addClassLevel(@NonNull Context context, @NonNull AClass aClass, SavedChoices savedChoices, Map<String, String> customChoices, AClass subClass, SavedChoices subclassSavedChoices, @NonNull Character character, int characterlevel, int classLevel, int hpRoll) {
        CharacterClass charClass = createCharacterClass(context, aClass, savedChoices, customChoices, subClass, subclassSavedChoices, character, characterlevel, classLevel, hpRoll, false);
        character.getClasses().add(charClass);

        character.setHP(character.getHP() + hpRoll + character.getStatBlock(StatType.CONSTITUTION).getModifier());
    }

    public static void updateClassLevel(@NonNull Context context, @NonNull AClass aClass, SavedChoices savedChoices, Map<String, String> customChoices, AClass subClass, SavedChoices subclassSavedChoices, @NonNull Character character, int classIndex, int classLevel, int hpRoll, boolean preventItemDelete) {
        CharacterClass charClass = createCharacterClass(context, aClass, savedChoices, customChoices, subClass, subclassSavedChoices, character, classIndex + 1, classLevel, hpRoll, preventItemDelete);
        character.getClasses().set(classIndex, charClass);
    }

    @NonNull
    private static CharacterClass createCharacterClass(@NonNull Context context, @NonNull AClass aClass, SavedChoices savedChoices, Map<String, String> customChoices, @Nullable AClass subClass, SavedChoices subclassSavedChoices, @NonNull Character character, int characterLevel, int classLevel, int hpRoll, boolean preventItemDelete) {
        CharacterClass charClass = new CharacterClass();
        charClass.setSavedChoices(savedChoices);
        // apply common changes
        Element rootClassElement = XmlUtils.getDocument(aClass.getXml()).getDocumentElement();


        if (characterLevel == 1) {
            // this will not visit any level elements, but will apply top level stuff as the first class for a character
            ApplyChangesToGenericComponent.applyToCharacter(context, rootClassElement, savedChoices, charClass, character, true && !preventItemDelete);

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

        Element subClassRootElement = null;
        if (subClass != null) {
            subClassRootElement = XmlUtils.getDocument(subClass.getXml()).getDocumentElement();
        }
        Element baseSpellInfoElement = null;

        if (classLevel == 1 && characterLevel != 1) {
            Element spellCastingStat = XmlUtils.getElement(rootClassElement, "spellCastingStat");
            if (spellCastingStat != null) {
                baseSpellInfoElement = rootClassElement;
            }
        }
        if (subClass != null && levelElement != null) {
            Element subclassElement = XmlUtils.getElement(levelElement, "subclass");
            if (subclassElement != null) {
                Element spellCastingStat = XmlUtils.getElement(subClassRootElement, "spellCastingStat");
                if (spellCastingStat != null) {
                    baseSpellInfoElement = subClassRootElement;
                }
            }
        }

        // grab the base spell info from either the root class, or the subclass
        if (baseSpellInfoElement != null) {
            // apply the root level spell related things
            // TODO maybe better to move these elements to level 1, directly
            ApplyClassToCharacterVisitor newMe = new ApplyClassToCharacterVisitor(savedChoices, customChoices, charClass);
            Element prepared = XmlUtils.getElement(baseSpellInfoElement, "preparedSpellsFormula");
            if (prepared != null) {
                newMe.visitPreparedSpells(prepared);
            }
            Element spellCastingStat = XmlUtils.getElement(baseSpellInfoElement, "spellCastingStat");
            if (spellCastingStat != null) {
                newMe.visitSpellCastingStat(spellCastingStat);
            }
            Element spellCastingSpellClass = XmlUtils.getElement(baseSpellInfoElement, "spellCastingSpellClass");
            if (spellCastingSpellClass != null) {
                newMe.visitSpellCastingSpellClass(spellCastingSpellClass);
            }
            Element multiclassCasterFactor = XmlUtils.getElement(baseSpellInfoElement, "multiclassCasterFactor");
            if (multiclassCasterFactor != null) {
                newMe.visitMulticlassCasterFactor(multiclassCasterFactor);
            }
        }


        if (levelElement != null) {
            ApplyChangesToGenericComponent.applyToCharacter(context, levelElement, savedChoices, charClass, character, false);

            ApplyClassToCharacterVisitor newMe = new ApplyClassToCharacterVisitor(savedChoices, customChoices, charClass);
            newMe.visitChildren(levelElement);
        }


        List<String> replaceSpellLevelIndex = savedChoices.getChoicesFor("replace_spell");
        List<String> newSpell = savedChoices.getChoicesFor("new_spell");
        if (replaceSpellLevelIndex != null && replaceSpellLevelIndex.size() > 0) {
            replaceSpell(context, charClass, replaceSpellLevelIndex, newSpell, character);
        }


        if (subClass != null) {
            Element subClassLevelElement = AClass.findLevelElement(subClassRootElement, classLevel);
            if (subClassLevelElement != null) {
                ApplyClassToCharacterVisitor newMe = new ApplyClassToCharacterVisitor(subclassSavedChoices, null, charClass);
                newMe.visitChildren(subClassLevelElement);
                charClass.setSubclassName(subClass.getName());
                charClass.setSubClassChoices(subclassSavedChoices);
                ApplyChangesToGenericComponent.applyToCharacter(context, subClassLevelElement, subclassSavedChoices, charClass, character, false);
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

    private static void replaceSpell(Context context, CharacterClass charClass, List<String> replaceSpellLevelIndex, List<String> newSpell, Character character) {
        String levelAndIndexStrings[] = replaceSpellLevelIndex.get(0).split(",");
        if (levelAndIndexStrings.length != 2) {
            throw new RuntimeException("Error on replace spell search MD");
        }
        int knownLevel = Integer.parseInt(levelAndIndexStrings[0]);
        int levelIndex = Integer.parseInt(levelAndIndexStrings[1]);

        String newSpellName = newSpell.get(0);



        List<Spell> spells = new Select()
                .from(Spell.class).where("UPPER(name) = ?", newSpellName.toUpperCase()).execute();
        CharacterSpell characterSpell = null;
        if (!spells.isEmpty()) {
            if (spells.size() > 1) {
                throw new RuntimeException("Too many spells named " + newSpellName);
            }
            final Spell spell = spells.get(0);

             characterSpell = new CharacterSpell();

            final Element root = XmlUtils.getDocument(spell.getXml()).getDocumentElement();
            ApplyChangesToGenericComponent.applyToCharacter(context, root, null, characterSpell, character, false);

            ApplySpellToCharacterVisitor newMe = new ApplySpellToCharacterVisitor(characterSpell);
            newMe.visit(spell);


        } else {
            characterSpell = new CharacterSpell();
            characterSpell.setLevel(1);
            characterSpell.setName(newSpellName);
        }
        charClass.setReplacedSpell(knownLevel, levelIndex, newSpellName, characterSpell);

    }


    @Override
    protected void visitLevel(@NonNull Element element) {
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
        StatType stat = EnumHelper.stringToEnum(statName, StatType.class);
        // TODO handle error
        charClass.setCasterStat(stat);
    }

    @Override
    protected void visitSpellCastingSpellClass(@NonNull Element element) {
        String className = element.getTextContent();
        charClass.setSpellClassFilter(className);

    }

    @Override
    protected void visitCantrips(@NonNull Element element) {
        String known = XmlUtils.getElementText(element, "known");
        if (known != null && known.trim().length() > 0) {
            charClass.setCantripsKnownFormula(known);
        }
    }

    @Override
    protected void visitSpells(@NonNull Element element) {
        /* <spells>
        * <known>4</known>
        * <slots>
        * <level value="1">4</level>
        * <level value="2">2</level>
        * </slots>
        * </spells>
        */
        String spellsKnown = XmlUtils.getElementText(element, "known");
        if (spellsKnown != null && spellsKnown.trim().length() > 0) {
            charClass.setSpellsKnownFormula(spellsKnown);
        }
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
                refreshType = EnumHelper.stringToEnum(refreshString, RefreshType.class);
                charClass.setSpellSlotRefresh(refreshType);
            }
        }


    }
}

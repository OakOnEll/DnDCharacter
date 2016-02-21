package com.oakonell.dndcharacter.views.character.classes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.character.*;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.spell.Spell;
import com.oakonell.dndcharacter.model.spell.SpellSchool;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.character.AbstractComponentViewCreator;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.md.CategoryChoicesMD;
import com.oakonell.dndcharacter.views.character.md.ChooseMD;
import com.oakonell.dndcharacter.views.character.md.ChooseMDTreeNode;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 1/24/2016.
 */
public class SpellCastingClassInfoViewCreator extends AbstractComponentViewCreator {

    SpellCastingClassInfoViewCreator(Character character) {
        super(character, false);
    }

    public ChooseMDTreeNode appendToLayout(@NonNull CharacterActivity characterActivity, @NonNull ViewGroup parent, @NonNull Element rootClassElement, @Nullable Element spells, @Nullable Element cantrips, @NonNull SavedChoices savedChoices) {
        setParent(parent);
        setChoices(savedChoices);
        setActivity(characterActivity);

        Element castingStatElem = XmlUtils.getElement(rootClassElement, "spellCastingStat");
        Element preparedSpellsFormulaElement = XmlUtils.getElement(rootClassElement, "preparedSpellsFormula");
        createGroup("Spell casting");

        ViewGroup mainGroup = getParent();

        {
            String stat = castingStatElem.getTextContent();
            TextView text = new TextView(mainGroup.getContext());
            mainGroup.addView(text);
            text.setText("Spell casting stat: " + stat);
        }

        if (preparedSpellsFormulaElement != null) {
            String prepared = preparedSpellsFormulaElement.getTextContent();

            TextView text = new TextView(mainGroup.getContext());
            mainGroup.addView(text);
            text.setText("Prepared Spells: " + prepared);
        }

        // TODO this is not quite right??
        String ownerClassName = XmlUtils.getElementText(rootClassElement, "name");
        String parentClass = XmlUtils.getElementText(rootClassElement, "parent");
        if (parentClass != null && parentClass.trim().length() > 0) {
            ownerClassName = parentClass;
        }
        String casterClassName = XmlUtils.getElementText(rootClassElement, "spellCastingSpellClass");
        if (casterClassName == null) {
            casterClassName = ownerClassName;
        }
        if (cantrips != null) {
            String known = XmlUtils.getElementText(cantrips, "known");

            TextView text = new TextView(mainGroup.getContext());
            mainGroup.addView(text);
            text.setText("Cantrips known: " + known);

            if (casterClassName != null) {
                int currentCantrips = characterActivity.getCharacter().getCantripsForClass(ownerClassName);
                int numCantripsCanAdd = Integer.parseInt(known) - currentCantrips;

                ChooseMD oldChooseMD = pushChooseMD(new CategoryChoicesMD("addedCantrips", numCantripsCanAdd, 0));
                visitCantripsSearchChoices(casterClassName, numCantripsCanAdd);
                popChooseMD(oldChooseMD);
            }
        }


        if (spells != null) {
//            <known>3</known>
//            <slots>
//            <level value="1">3</level>
//            </slots>

//            createGroup("Spells");
//            ViewGroup spellGroup = getParent();
            ViewGroup spellGroup = mainGroup;

            Element slotsElem = XmlUtils.getElement(spells, "slots");
            List<Element> levelElems = XmlUtils.getChildElements(slotsElem, "level");
            int maxLevel = 0;
            for (Element each : levelElems) {
                String levelString = each.getAttribute("value");
                int level = Integer.parseInt(levelString);
                if (level > maxLevel) {
                    maxLevel = level;
                }
            }

            Element spellsKnownElem = XmlUtils.getElement(spells, "known");
            List<Element> spellList = XmlUtils.getChildElements(spells, "spell");
            if (spellsKnownElem != null) {
                String known = spellsKnownElem.getTextContent();

                TextView text = new TextView(spellGroup.getContext());
                spellGroup.addView(text);
                text.setText("Spells known: " + known);

                if (casterClassName != null && spellList.isEmpty()) {
                    int currentSpells = characterActivity.getCharacter().getSpellsKnownForClass(ownerClassName);
                    int numSpellsCanAdd = Integer.parseInt(known) - currentSpells;

                    ChooseMD oldChooseMD = pushChooseMD(new CategoryChoicesMD("addedSpells", numSpellsCanAdd, 0));
                    visitSpellSearchChoices(casterClassName, maxLevel, numSpellsCanAdd, null);
                    popChooseMD(oldChooseMD);
                }

            }

            for (Element each : levelElems) {
                String level = each.getAttribute("value");
                String slots = each.getTextContent();

                TextView text = new TextView(spellGroup.getContext());
                spellGroup.addView(text);

                text.setText("Level " + level + " slots: " + slots);
            }

            if (spellList.size() > 0) {
                TextView text = new TextView(spellGroup.getContext());
                spellGroup.addView(text);
                text.setText("Spells: ");

                int i = 0;
                for (Element each : spellList) {
                    if (each.getTextContent() == null || each.getTextContent().trim().length() == 0) {
                        ChooseMD oldChooseMD = pushChooseMD(new CategoryChoicesMD("addedSpell" + i, 1, 1));
                        String schoolsString = each.getAttribute("schools");
                        List<SpellSchool> schools = EnumHelper.commaListToEnum(schoolsString, SpellSchool.class);
                        visitSpellSearchChoices(casterClassName, maxLevel, 1, schools);
                        popChooseMD(oldChooseMD);
                    } else {
                        visitSpell(each);
//                        TextView spellText = new TextView(spellGroup.getContext());
//                        spellGroup.addView(spellText);
//                        spellText.setText(" * " + each.getTextContent());
                    }
                    i++;
                }
            }

            setParent(mainGroup);
        }
        return getChoicesMD();
    }


}

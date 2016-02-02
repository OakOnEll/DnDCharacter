package com.oakonell.dndcharacter.views.character.classes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.character.AbstractComponentViewCreator;
import com.oakonell.dndcharacter.views.character.MainActivity;
import com.oakonell.dndcharacter.views.character.md.CategoryChoicesMD;
import com.oakonell.dndcharacter.views.character.md.ChooseMD;
import com.oakonell.dndcharacter.views.character.md.ChooseMDTreeNode;
import com.oakonell.dndcharacter.views.character.md.RootChoiceMDNode;

import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by Rob on 1/24/2016.
 */
public class SpellCastingClassInfoViewCreator extends AbstractComponentViewCreator {

    public ChooseMDTreeNode appendToLayout(@NonNull MainActivity mainActivity, @NonNull ViewGroup parent, @NonNull Element rootClassElement, @Nullable Element spells, @Nullable Element cantrips, @NonNull SavedChoices savedChoices) {
        setParent(parent);
        setChoices(savedChoices);
        setActivity(mainActivity);

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

        String casterClassName = XmlUtils.getElementText(rootClassElement, "name");
        if (cantrips != null) {
            String known = XmlUtils.getElementText(cantrips, "known");

            TextView text = new TextView(mainGroup.getContext());
            mainGroup.addView(text);
            text.setText("Cantrips known: " + known);

            if (casterClassName != null) {
                int currentCantrips = mainActivity.getCharacter().getCantripsForClass(casterClassName);
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
            if (spellsKnownElem != null) {
                String known = spellsKnownElem.getTextContent();

                TextView text = new TextView(spellGroup.getContext());
                spellGroup.addView(text);
                text.setText("Spells known: " + known);

                if (casterClassName != null) {
                    int currentSpells = mainActivity.getCharacter().getSpellsKnownForClass(casterClassName);
                    int numSpellsCanAdd = Integer.parseInt(known) - currentSpells;

                    ChooseMD oldChooseMD = pushChooseMD(new CategoryChoicesMD("addedSpells", numSpellsCanAdd, 0));
                    visitSpellSearchChoices(casterClassName, maxLevel, numSpellsCanAdd);
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

            setParent(mainGroup);
        }
        return getChoicesMD();
    }


}

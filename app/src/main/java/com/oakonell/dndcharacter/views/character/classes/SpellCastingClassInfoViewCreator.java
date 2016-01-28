package com.oakonell.dndcharacter.views.character.classes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.character.AbstractComponentViewCreator;

import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by Rob on 1/24/2016.
 */
public class SpellCastingClassInfoViewCreator extends AbstractComponentViewCreator {
    public void appendToLayout(ViewGroup parent, @NonNull Element rootClassElement, @Nullable Element spells, @Nullable Element cantrips) {
        setParent(parent);

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

        if (cantrips != null) {
            String known = XmlUtils.getElementText(cantrips, "known");

            TextView text = new TextView(mainGroup.getContext());
            mainGroup.addView(text);
            text.setText("Cantrips known: " + known);
        }


        if (spells != null) {
//            <known>3</known>
//            <slots>
//            <level value="1">3</level>
//            </slots>

//            createGroup("Spells");
//            ViewGroup spellGroup = getParent();
            ViewGroup spellGroup = mainGroup;

            Element spellsKnownElem = XmlUtils.getElement(spells, "known");
            if (spellsKnownElem != null) {
                String known = spellsKnownElem.getTextContent();

                TextView text = new TextView(spellGroup.getContext());
                spellGroup.addView(text);
                text.setText("Spells known: " + known);
            }

            Element slotsElem = XmlUtils.getElement(spells, "slots");
            List<Element> levelElems = XmlUtils.getChildElements(slotsElem, "level");
            for (Element each : levelElems) {
                String level = each.getAttribute("value");
                String slots = each.getTextContent();

                TextView text = new TextView(spellGroup.getContext());
                spellGroup.addView(text);

                text.setText("Level " + level + " slots: " + slots);
            }

            setParent(mainGroup);
        }

    }


}

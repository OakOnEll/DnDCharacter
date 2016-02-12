package com.oakonell.dndcharacter.views.character.classes;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.*;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.views.character.AbstractComponentViewCreator;
import com.oakonell.dndcharacter.views.character.md.CheckOptionMD;
import com.oakonell.dndcharacter.views.character.md.MultipleChoicesMD;

import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 1/29/2016.
 */
public class AbilityScoreImprovementViewCreator extends AbstractComponentViewCreator {

    private Map<String, TextView> statTextViews = new HashMap<>();

    public AbilityScoreImprovementViewCreator(Character character) {
        super(character);
    }

    @Override
    protected void onOptionCheckChange(CheckOptionMD optionMD, boolean isChecked) {
        super.onOptionCheckChange(optionMD, isChecked);
        if (!"stat".equals(optionMD.getChoiceName())) return;

        updateStatIncreaseText(optionMD.getChooseMD());
    }

    private void updateStatIncreaseText(MultipleChoicesMD choicesMD) {
        int numChecked = 0;
        for (CheckOptionMD each : choicesMD.getOptions()) {
            if (each.getCheckbox().isChecked()) {
                numChecked++;
            }
        }
        int increase = 2;
        if (numChecked > 1) {
            increase = 1;
        }
        for (String statName : statTextViews.keySet()) {
            updateStatIncreaseText(statName, increase);
        }
    }

    @Override
    protected void visitIncrease(@NonNull Element element) {
        ViewGroup parent = getParent();
        TextView text = new TextView(parent.getContext());
        parent.addView(text);
        String statName = element.getAttribute("name");
        statTextViews.put(statName, text);
        updateStatIncreaseText(statName, Integer.parseInt(element.getTextContent()));


        if (statTextViews.size() == StatType.values().length) {
            updateStatIncreaseText((MultipleChoicesMD) getCurrentChooseMD());
        }
    }

    protected void updateStatIncreaseText(String statName, int num) {
        ViewGroup parent = getParent();
        String string = parent.getResources().getString(R.string.increase_statname_by, statName, num);
        final TextView text = statTextViews.get(statName);
        if (text == null) {
            throw new RuntimeException("No text view for stat named '" + statName + "' was found");
        }
        text.setText(" *  " + string);

    }
}

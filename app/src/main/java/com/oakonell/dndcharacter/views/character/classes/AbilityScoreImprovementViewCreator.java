package com.oakonell.dndcharacter.views.character.classes;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.EnumHelper;
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

    @NonNull
    private Map<String, TextView> statTextViews = new HashMap<>();

    public AbilityScoreImprovementViewCreator(Character character) {
        super(character);
    }

    @Override
    protected void onOptionCheckChange(@NonNull CheckOptionMD optionMD, boolean isChecked) {
        super.onOptionCheckChange(optionMD, isChecked);
        if (!"stat".equals(optionMD.getChoiceName())) return;

        updateStatIncreaseText(optionMD.getChooseMD());
    }

    private void updateStatIncreaseText(@NonNull MultipleChoicesMD choicesMD) {
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
        int value = 0;
        for (Character.ModifierWithSource each : getCharacter().deriveStat(EnumHelper.stringToEnum(statName, StatType.class))) {
            if (each.getSource() != null && each.getSource().equals(getCurrentComponent())) {
                continue;
            }
            value += each.getModifier();
        }
        int newValue = value + num;

        String string = parent.getResources().getString(R.string.increase_statname_by_to, statName, num, newValue);
        final TextView text = statTextViews.get(statName);
        if (text == null) {
            throw new RuntimeException("No text view for stat named '" + statName + "' was found");
        }

        text.setText(" *  " + string);

    }
}

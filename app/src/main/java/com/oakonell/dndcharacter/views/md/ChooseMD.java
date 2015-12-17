package com.oakonell.dndcharacter.views.md;

import android.view.ViewGroup;

import com.oakonell.dndcharacter.model.SavedChoices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/18/2015.
 */
public abstract class ChooseMD<O extends OptionMD> {
    private final String choiceName;
    private final int maxChoices;
    private final List<O> options = new ArrayList<>();

    protected ChooseMD(String choiceName, int maxChoices) {
        this.choiceName = choiceName;
        this.maxChoices = maxChoices;
    }

    public void saveChoice(ViewGroup dynamicView, SavedChoices savedChoices, Map<String, String> customChoices) {
        List<String> list = savedChoices.getChoicesFor(choiceName);
        list.clear();
        for (O each : options) {
            each.saveChoice(dynamicView, list, customChoices);
        }
    }

    public void addOption(O option) {
        options.add(option);
    }

    public List<O> getOptions() {
        return options;
    }

    public int getMaxChoices() {
        return maxChoices;
    }

    public String getChoiceName() {
        return choiceName;
    }

    public abstract boolean validate(ViewGroup dynamicView);
}

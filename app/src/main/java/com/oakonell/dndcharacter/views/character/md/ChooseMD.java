package com.oakonell.dndcharacter.views.character.md;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.model.character.SavedChoices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/18/2015.
 */
public abstract class ChooseMD<O extends OptionMD> {
    private final String choiceName;
    private final int maxChoices;
    private final int minChoices;

    private final List<O> options = new ArrayList<>();

    protected ChooseMD(String choiceName, int maxChoices, int minChoices) {
        this.choiceName = choiceName;
        this.maxChoices = maxChoices;
        this.minChoices = minChoices;
    }

    public void saveChoice(ViewGroup dynamicView, @NonNull SavedChoices savedChoices, Map<String, String> customChoices) {
        List<String> list = savedChoices.getChoicesFor(choiceName);
        list.clear();
        for (O each : options) {
            each.saveChoice(dynamicView, list, customChoices, savedChoices);
        }
    }

    public void addOption(O option) {
        options.add(option);
    }

    @NonNull
    public List<O> getOptions() {
        return options;
    }

    public int getMaxChoices() {
        return maxChoices;
    }

    public int getMinChoices() {
        return minChoices;
    }

    public String getChoiceName() {
        return choiceName;
    }

    public abstract boolean validate(ViewGroup dynamicView);

    public abstract void setEnabled(boolean enabled);
}

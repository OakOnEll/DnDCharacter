package com.oakonell.dndcharacter.model.md;

import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/18/2015.
 */
public class CheckOptionMD extends OptionMD {
    public final String name;

    public CheckOptionMD(ChooseMD chooseMD, int uiId, String optionName) {
        super(chooseMD, uiId);
        this.name = optionName;
    }

    @Override
    public void saveChoice(ViewGroup dynamicView, List<String> list, Map<String, String> customChoices) {
        CheckBox checkbox = (CheckBox) dynamicView.findViewById(uiId);
        if (checkbox.isChecked()) {
            addToSavedList(dynamicView, list, customChoices);
        }
    }

    protected void addToSavedList(ViewGroup dynamicView, List<String> list, Map<String, String> customChoices) {
        list.add(name);
    }
}

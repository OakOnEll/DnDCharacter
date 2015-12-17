package com.oakonell.dndcharacter.views.md;

import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/18/2015.
 */
public class CheckOptionMD extends OptionMD<MultipleChoicesMD> {
    private final String name;
    private final CheckBox checkbox;

    public CheckOptionMD(MultipleChoicesMD chooseMD, CheckBox checkbox, String optionName) {
        super(chooseMD);
        this.checkbox = checkbox;
        this.name = optionName;
    }

    @Override
    public void saveChoice(ViewGroup dynamicView, List<String> list, Map<String, String> customChoices) {
        if (checkbox.isChecked()) {
            addToSavedList(dynamicView, list, customChoices);
        }
    }

    public CheckBox getCheckbox() {
        return checkbox;
    }

    public String getOptionName() {
        return name;
    }

    protected void addToSavedList(ViewGroup dynamicView, List<String> list, Map<String, String> customChoices) {
        list.add(name);
    }
}

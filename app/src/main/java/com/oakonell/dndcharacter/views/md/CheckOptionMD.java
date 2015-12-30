package com.oakonell.dndcharacter.views.md;

import android.view.ViewGroup;
import android.widget.CheckBox;

import com.oakonell.dndcharacter.model.SavedChoices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/18/2015.
 */
public class CheckOptionMD extends OptionMD<MultipleChoicesMD> implements ChooseMDTreeNode {
    private final String name;
    private final CheckBox checkbox;
    private final List<ChooseMD<?>> childChoices = new ArrayList<>();

    public CheckOptionMD(MultipleChoicesMD chooseMD, CheckBox checkbox, String optionName) {
        super(chooseMD);
        this.checkbox = checkbox;
        this.name = optionName;
    }

    @Override
    public void saveChoice(ViewGroup dynamicView, List<String> list, Map<String, String> customChoices, SavedChoices savedChoices) {
        if (checkbox.isChecked()) {
            addToSavedList(dynamicView, list, customChoices);
        }
        for (ChooseMD<?> each : childChoices) {
            each.saveChoice(dynamicView, savedChoices, customChoices);
        }
    }

    @Override
    public boolean validate(ViewGroup dynamicView) {
        boolean isValid = true;
        for (ChooseMD<?> each : childChoices) {
            isValid = each.validate(dynamicView) && isValid;
        }
        return isValid;
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

    public void addChildChoice(ChooseMD<?> choiceMd) {
        childChoices.add(choiceMd);
    }

    @Override
    public List<ChooseMD<?>> getChildChoiceMDs() {
        return childChoices;
    }

    public void setEnabled(boolean enabled) {
        checkbox.setEnabled(enabled);
        for (ChooseMD<?> each : childChoices) {
            each.setEnabled(enabled);
        }
    }
}

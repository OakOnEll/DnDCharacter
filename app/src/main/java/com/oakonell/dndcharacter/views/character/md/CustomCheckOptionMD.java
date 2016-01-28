package com.oakonell.dndcharacter.views.character.md;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/18/2015.
 */
public class CustomCheckOptionMD extends CheckOptionMD {
    private final TextView textView;

    public CustomCheckOptionMD(MultipleChoicesMD chooseMD, CheckBox uiId, String optionName, TextView textView) {
        super(chooseMD, uiId, optionName);
        this.textView = textView;
    }

    protected void addToSavedList(ViewGroup dynamicView, @NonNull List<String> list, @NonNull Map<String, String> customChoices) {
        String custom = textView.getText().toString();
        customChoices.put(getChoiceName(), custom);
        list.add(getOptionName());
    }
}

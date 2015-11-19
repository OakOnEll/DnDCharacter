package com.oakonell.dndcharacter.model.md;

import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/18/2015.
 */
public class CustomCheckOptionMD extends CheckOptionMD {
    public final int textUiId;

    public CustomCheckOptionMD(ChooseMD chooseMD, int uiId, String optionName, int textUiId) {
        super(chooseMD, uiId, optionName);
        this.textUiId = textUiId;
    }

    protected void addToSavedList(ViewGroup dynamicView, List<String> list, Map<String, String> customChoices) {
        EditText text = (EditText) dynamicView.findViewById(textUiId);
        String custom = text.getText().toString();
        customChoices.put(getChoiceName(), custom);
        list.add(name);
    }
}

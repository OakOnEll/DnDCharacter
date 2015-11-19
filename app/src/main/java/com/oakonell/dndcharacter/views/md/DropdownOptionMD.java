package com.oakonell.dndcharacter.views.md;

import android.view.ViewGroup;
import android.widget.Spinner;

import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/18/2015.
 */
public class DropdownOptionMD extends OptionMD {

    public DropdownOptionMD(ChooseMD chooseMD, int uiId) {
        super(chooseMD, uiId);
    }

    @Override
    public void saveChoice(ViewGroup dynamicView, List<String> list, Map<String, String> customChoices) {
        Spinner spinner = (Spinner) dynamicView.findViewById(uiId);
        String selection = (String) spinner.getSelectedItem();
        list.add(selection);
    }
}

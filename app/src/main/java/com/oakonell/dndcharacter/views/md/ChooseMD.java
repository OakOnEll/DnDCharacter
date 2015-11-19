package com.oakonell.dndcharacter.views.md;

import android.view.ViewGroup;

import com.oakonell.dndcharacter.model.SavedChoices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/18/2015.
 */
public class ChooseMD {
    public String choiceName;
    public int maxChoices;
    public List<OptionMD> options = new ArrayList<>();

    public CheckOptionMD findOrOptionNamed(String optionName) {
        for (OptionMD each : options) {
            if (!(each instanceof CheckOptionMD)) continue;
            CheckOptionMD checkMD = (CheckOptionMD) each;
            if (checkMD.name.equals(optionName)) return checkMD;
        }
        return null;
    }

    public void saveChoice(ViewGroup dynamicView, SavedChoices savedChoices, Map<String, String> customChoices)  {
        List<String> list = savedChoices.getChoicesFor(choiceName);
        list.clear();
        for (OptionMD each : options) {
            each.saveChoice(dynamicView, list, customChoices);
        }
    }
}

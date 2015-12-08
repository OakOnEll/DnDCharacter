package com.oakonell.dndcharacter.views.md;

import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/18/2015.
 */
public abstract class OptionMD {
    public final int uiId;
    private final ChooseMD chooseMD;

    public OptionMD(ChooseMD chooseMD, int uiId) {
        this.chooseMD = chooseMD;
        this.uiId = uiId;
    }

    public String getChoiceName() {
        return chooseMD.choiceName;
    }

    public abstract void saveChoice(ViewGroup dynamicView, List<String> list, Map<String, String> customChoices);
}

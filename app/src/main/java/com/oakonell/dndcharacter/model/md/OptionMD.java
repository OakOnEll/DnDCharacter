package com.oakonell.dndcharacter.model.md;

import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/18/2015.
 */
public abstract class OptionMD {
    private final ChooseMD chooseMD;
    public final int uiId;

    public OptionMD(ChooseMD chooseMD, int uiId) {
        this.chooseMD = chooseMD;
        this.uiId = uiId;
    }

    public String getChoiceName() {
        return chooseMD.choiceName;
    }

    public abstract void saveChoice(ViewGroup dynamicView, List<String> list, Map<String, String> customChoices);
}

package com.oakonell.dndcharacter.views.md;

import android.view.ViewGroup;

import com.oakonell.dndcharacter.model.SavedChoices;

import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/18/2015.
 */
public abstract class OptionMD<O extends ChooseMD> {
    private final O chooseMD;

    public OptionMD(O chooseMD) {
        this.chooseMD = chooseMD;
    }

    public String getChoiceName() {
        return chooseMD.getChoiceName();
    }

    public abstract void saveChoice(ViewGroup dynamicView, List<String> list, Map<String, String> customChoices, SavedChoices savedChoices);

    public abstract boolean validate(ViewGroup dynamicView);

    abstract void setEnabled(boolean enabled);
}

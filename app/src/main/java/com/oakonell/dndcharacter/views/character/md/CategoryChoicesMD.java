package com.oakonell.dndcharacter.views.character.md;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

/**
 * Created by Rob on 12/16/2015.
 */
public class CategoryChoicesMD extends ChooseMD<CategoryOptionMD> {

    public CategoryChoicesMD(String choiceName, int maxChoices) {
        super(choiceName, maxChoices);
    }

    @Override
    public boolean validate(@NonNull ViewGroup dynamicView) {
        int numInvalid = 0;
        for (CategoryOptionMD each : getOptions()) {
            if (!each.validate(dynamicView)) {
                numInvalid++;
            }
        }
        return numInvalid == 0;
    }

    @Override
    public void setEnabled(boolean enabled) {
        for (CategoryOptionMD each : getOptions()) {
            each.setEnabled(enabled);
        }
    }
}

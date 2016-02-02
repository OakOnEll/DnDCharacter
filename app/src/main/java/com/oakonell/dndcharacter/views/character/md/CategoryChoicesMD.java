package com.oakonell.dndcharacter.views.character.md;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

/**
 * Created by Rob on 12/16/2015.
 */
public class CategoryChoicesMD extends ChooseMD<CategoryOptionMD> {

    public CategoryChoicesMD(String choiceName, int maxChoices, int minChoices) {
        super(choiceName, maxChoices, minChoices);
    }

    @Override
    public boolean validate(@NonNull ViewGroup dynamicView) {
        int numPopulated = 0;
        for (CategoryOptionMD each : getOptions()) {
            if (each.isPopulated()) {
                numPopulated++;
            }
        }
        if (numPopulated >= getMinChoices()) {
            return true;
        }
        int numToShowError = getMinChoices() - numPopulated;
        for (CategoryOptionMD each : getOptions()) {
            if (numToShowError == 0) break;
            if (each.isPopulated()) continue;

            each.showRequiredError(dynamicView);
            numToShowError--;
        }
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        for (CategoryOptionMD each : getOptions()) {
            each.setEnabled(enabled);
        }
    }
}

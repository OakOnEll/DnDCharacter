package com.oakonell.dndcharacter.views.md;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 12/16/2015.
 */
public class MultipleChoicesMD extends ChooseMD<CheckOptionMD> {
    private final TextView uiLabel;
    private final int minSelections;

    public MultipleChoicesMD(TextView uiLabel, String choiceName, int maxChoices, int minSelections) {
        super(choiceName, maxChoices);
        this.uiLabel = uiLabel;
        this.minSelections = minSelections;
    }

    public CheckOptionMD findOrOptionNamed(String optionName) {
        for (CheckOptionMD each : getOptions()) {
            if (!(each instanceof CheckOptionMD)) continue;
            CheckOptionMD checkMD = (CheckOptionMD) each;
            if (checkMD.getOptionName().equals(optionName)) return checkMD;
        }
        return null;
    }

    public TextView getUiLabel() {
        return uiLabel;
    }

    @Override
    public boolean validate(ViewGroup dynamicView) {
        int checked = 0;
        for (CheckOptionMD each : getOptions()) {
            CheckBox checkBox = each.getCheckbox();
            if (checkBox.isChecked()) checked++;
        }
        if (checked < minSelections) {
            uiLabel.setError("Choose " + minSelections);
            Animation shake = AnimationUtils.loadAnimation(dynamicView.getContext(), R.anim.shake);
            uiLabel.startAnimation(shake);
            return false;
        }
        return true;
    }

}

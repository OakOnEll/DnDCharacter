package com.oakonell.dndcharacter.views.character.md;

import android.support.annotation.NonNull;
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
    private int minChoices;

    public MultipleChoicesMD(TextView uiLabel, String choiceName, int maxChoices, int minChoices) {
        super(choiceName, maxChoices, minChoices);
        this.uiLabel = uiLabel;
    }

    public TextView getUiLabel() {
        return uiLabel;
    }

    @Override
    public boolean validate(@NonNull ViewGroup dynamicView) {
        int checked = 0;
        boolean isValid = true;
        for (CheckOptionMD each : getOptions()) {
            CheckBox checkBox = each.getCheckbox();
            if (checkBox.isChecked()) {
                checked++;
                isValid = each.validate(dynamicView) && isValid;
            }
        }
        if (checked < getMinChoices()) {
            uiLabel.setError(dynamicView.getContext().getString(R.string.choose_n, getMinChoices()));
            Animation shake = AnimationUtils.loadAnimation(dynamicView.getContext(), R.anim.shake);
            uiLabel.startAnimation(shake);
            return false;
        }
        return isValid;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            for (CheckOptionMD each : getOptions()) {
                CheckBox aCheck = each.getCheckbox();
                aCheck.setEnabled(false);
                each.setEnabled(false);
            }
            return;
        }
        int maxChecked = getMaxChoices();
        int numChecked = 0;
        for (CheckOptionMD each : getOptions()) {
            CheckBox aCheck = each.getCheckbox();
            if (aCheck.isChecked()) {
                numChecked++;
            }
        }
        boolean allEnabled = numChecked < maxChecked;
        for (CheckOptionMD each : getOptions()) {
            CheckBox aCheck = each.getCheckbox();
            if (!aCheck.isChecked()) {
                aCheck.setEnabled(allEnabled);
                each.setEnabled(allEnabled);
            } else {
                aCheck.setEnabled(true);
            }
        }

    }

}

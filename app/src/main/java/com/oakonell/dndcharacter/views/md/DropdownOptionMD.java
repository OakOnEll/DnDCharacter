package com.oakonell.dndcharacter.views.md;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Spinner;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.SavedChoices;

import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/18/2015.
 */
public class DropdownOptionMD extends OptionMD<CategoryChoicesMD> {
    private final Spinner spinner;
    private final TextView errorTextView;

    public DropdownOptionMD(CategoryChoicesMD chooseMD, Spinner spinner, TextView errorTextView) {
        super(chooseMD);
        this.spinner = spinner;
        this.errorTextView = errorTextView;
    }

    @Override
    public void saveChoice(ViewGroup dynamicView, List<String> list, Map<String, String> customChoices, SavedChoices savedChoices) {
        String selection = (String) spinner.getSelectedItem();
        list.add(selection);
    }

    public void clearError() {
        errorTextView.setError(null);
    }

    @Override
    public boolean validate(ViewGroup dynamicView) {
        if (spinner.getSelectedItemPosition() < 0) {
            errorTextView.setError("Choose one");
            Animation shake = AnimationUtils.loadAnimation(dynamicView.getContext(), R.anim.shake);
            spinner.startAnimation(shake);
            return false;
        }
        return true;
    }

    public void setEnabled(boolean enabled) {
        spinner.setEnabled(enabled);
    }
}

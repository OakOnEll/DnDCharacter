package com.oakonell.dndcharacter.views.character.md;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Spinner;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.SavedChoices;

import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/18/2015.
 */
public class DropdownOptionMD extends CategoryOptionMD {
    private final Spinner spinner;
    private final TextView errorTextView;

    public DropdownOptionMD(CategoryChoicesMD chooseMD, Spinner spinner, TextView errorTextView) {
        super(chooseMD);
        this.spinner = spinner;
        this.errorTextView = errorTextView;
    }

    @Override
    public void saveChoice(ViewGroup dynamicView, @NonNull List<String> list, Map<String, String> customChoices, SavedChoices savedChoices) {
        if (!isPopulated()) return;
        String selection = (String) spinner.getSelectedItem();
        list.add(selection);
    }

    public void clearError() {
        errorTextView.setError(null);
    }

    @Override
    public boolean isPopulated() {
        return spinner.getSelectedItemPosition() >= 0;
    }

    @Override
    public void showRequiredError(ViewGroup dynamicView) {
        errorTextView.setError(dynamicView.getContext().getString(R.string.choose_one_error));
        Animation shake = AnimationUtils.loadAnimation(dynamicView.getContext(), R.anim.shake);
        spinner.startAnimation(shake);
    }

    public void setEnabled(boolean enabled) {
        spinner.setEnabled(enabled);
    }
}

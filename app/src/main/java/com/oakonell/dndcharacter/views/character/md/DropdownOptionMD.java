package com.oakonell.dndcharacter.views.character.md;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Spinner;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.views.character.AbstractComponentViewCreator;

import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/18/2015.
 */
public class DropdownOptionMD<T> extends CategoryOptionMD {
    private final Spinner spinner;
    private final TextView errorTextView;
    AbstractComponentViewCreator.SpinnerToString<T> toString;

    public DropdownOptionMD(CategoryChoicesMD chooseMD, Spinner spinner, TextView errorTextView, AbstractComponentViewCreator.SpinnerToString<T> toString) {
        super(chooseMD);
        this.spinner = spinner;
        this.errorTextView = errorTextView;
        this.toString = toString;
    }

    @Override
    public void saveChoice(ViewGroup dynamicView, @NonNull List<String> list, Map<String, String> customChoices, SavedChoices savedChoices) {
        if (!isPopulated()) return;
        T selection = (T) spinner.getSelectedItem();
        if (toString == null) {
            list.add((String) selection);
        } else {
            list.add(toString.toSaveString(selection, spinner.getSelectedItemPosition()));
        }
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

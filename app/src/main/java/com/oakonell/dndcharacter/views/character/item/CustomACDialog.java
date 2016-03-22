package com.oakonell.dndcharacter.views.character.item;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.CustomAdjustmentType;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

/**
 * Created by Rob on 3/22/2016.
 */
public class CustomACDialog extends AbstractCharacterDialogFragment {
    private static final String IS_ROOT = "isRoot";
    private EditText comment;
    private EditText formula;

    @NonNull
    public static CustomACDialog createRootACDialog() {
        CustomACDialog dialog = new CustomACDialog();
        Bundle args = new Bundle();
        args.putBoolean(IS_ROOT, true);
        dialog.setArguments(args);
        return dialog;
    }

    public static CustomACDialog createModifyingACDialog() {
        CustomACDialog dialog = new CustomACDialog();
        Bundle args = new Bundle();
        args.putBoolean(IS_ROOT, false);
        dialog.setArguments(args);
        return dialog;
    }

    public boolean isRoot() {
        Bundle args = getArguments();
        return args.getBoolean(IS_ROOT, false);
    }


    @Nullable
    @Override
    protected String getTitle() {
        if (isRoot()) {
            return getString(R.string.add_custom_root_ac_title);
        } else {
            return getString(R.string.add_custom_ac_adjustment_title);
        }
    }

    public View onCreateTheView(@NonNull LayoutInflater inflater, final ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_root_ac_dialog, container);

        comment = (EditText) view.findViewById(R.id.comment);
        formula = (EditText) view.findViewById(R.id.formula);

        return view;
    }

    @Override
    protected boolean onDone() {
        final String commentString = comment.getText().toString().trim();
        boolean isValid = true;
        if (commentString.length() == 0) {
            comment.setError(getString(R.string.enter_a_name));
            Animation shake = AnimationUtils.loadAnimation(comment.getContext(), R.anim.shake);
            comment.startAnimation(shake);
            isValid = false;
        }
        final String formulaString = formula.getText().toString();
        if (formulaString.length() == 0) {
            formula.setError(getString(R.string.enter_an_ac));
            Animation shake = AnimationUtils.loadAnimation(formula.getContext(), R.anim.shake);
            formula.startAnimation(shake);
            isValid = false;
        }
        if (isValid) {
            final int intVal = Integer.parseInt(formulaString);
            if (isRoot()) {
                getCharacter().getCustomAdjustments(CustomAdjustmentType.ROOT_ACS).addAdjustment(commentString, "=" + formulaString, intVal);
            } else {
                getCharacter().getCustomAdjustments(CustomAdjustmentType.MODIFYING_ACS).addAdjustment(commentString, formulaString, intVal);
            }
        }
        return super.onDone();
    }
}
package com.oakonell.dndcharacter.views.character;

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

/**
 * Created by Rob on 3/22/2016.
 */
public class CustomNumericAdjustmentDialog extends AbstractCharacterDialogFragment {
    private static final String TYPE = "type";
    private static final String TITLE = "title";
    private EditText comment;
    private EditText number;


    @Nullable
    @Override
    protected String getTitle() {
        Bundle args = getArguments();
        return args.getString(TITLE);
    }

    @NonNull
    public static CustomNumericAdjustmentDialog createDialog(String title, CustomAdjustmentType type) {
        CustomNumericAdjustmentDialog dialog = new CustomNumericAdjustmentDialog();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(TYPE, type.name());
        dialog.setArguments(args);
        return dialog;
    }

    public View onCreateTheView(@NonNull LayoutInflater inflater, final ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_num_adjustment_dialog, container);

        comment = (EditText) view.findViewById(R.id.comment);
        number = (EditText) view.findViewById(R.id.number);

        return view;
    }

    @Nullable
    private CustomAdjustmentType getType() {
        return CustomAdjustmentType.valueOf(getArguments().getString(TYPE));
    }


    @Override
    protected boolean onDone() {

        final String commentString = comment.getText().toString().trim();
        boolean isValid = true;
        if (commentString.length() == 0) {
            comment.setError(getString(R.string.enter_a_comment));
            Animation shake = AnimationUtils.loadAnimation(comment.getContext(), R.anim.shake);
            comment.startAnimation(shake);
            isValid = false;
        }
        final String formulaString = number.getText().toString();
        if (formulaString.length() == 0) {
            number.setError(getString(R.string.enter_a_value));
            Animation shake = AnimationUtils.loadAnimation(number.getContext(), R.anim.shake);
            number.startAnimation(shake);
            isValid = false;
        }
        int intVal = 0;
        try {
            intVal = Integer.parseInt(formulaString);
        } catch (NumberFormatException e) {
            number.setError(getString(R.string.enter_a_valid_number));
            Animation shake = AnimationUtils.loadAnimation(number.getContext(), R.anim.shake);
            number.startAnimation(shake);
            isValid = false;
        }
        if (isValid) {
            getCharacter().getCustomAdjustments(getType()).addAdjustment(commentString, intVal);
            getMainActivity().updateViews();
            getMainActivity().saveCharacter();
        }
        return super.onDone();
    }
}
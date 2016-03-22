package com.oakonell.dndcharacter.views.character.item;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.*;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

/**
 * Created by Rob on 3/22/2016.
 */
public class CustomRootACDialog extends AbstractCharacterDialogFragment {

    private EditText comment;
    private EditText formula;

    @NonNull
    public static CustomRootACDialog createDialog() {
        return new CustomRootACDialog();
    }

    @Nullable
    @Override
    protected String getTitle() {
        return getString(R.string.add_custom_ac_title);
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
        if (commentString.length() == 0) {

        }
        final String formulaString = formula.getText().toString();
        if (formulaString.length() == 0) {

        }
        final int intVal = Integer.parseInt(formulaString);

        getCharacter().getCustomAdjustments(CustomAdjustmentType.ROOT_ACS).addAdjustment(commentString, "=" + formulaString, intVal);
        return super.onDone();
    }
}
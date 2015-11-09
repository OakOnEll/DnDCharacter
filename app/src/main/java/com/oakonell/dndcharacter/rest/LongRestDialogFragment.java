package com.oakonell.dndcharacter.rest;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.oakonell.dndcharacter.MainActivity;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.*;
import com.oakonell.dndcharacter.model.Character;

import java.util.List;


/**
 * Created by Rob on 11/8/2015.
 */
public class LongRestDialogFragment extends AbstractRestDialogFragment {

    View fullHealingGroup;
    CheckBox fullHealing;

    public static LongRestDialogFragment createDialog(com.oakonell.dndcharacter.model.Character character) {
        LongRestDialogFragment newMe = new LongRestDialogFragment();
        newMe.setCharacter(character);
        return newMe;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.long_rest_dialog, container);
        configureCommon(view);
        getDialog().setTitle("Long Rest");
        Button done = (Button) view.findViewById(R.id.done);
        fullHealingGroup = view.findViewById(R.id.full_heal_group);
        fullHealing = (CheckBox) view.findViewById(R.id.full_healing);

        if (character.getHP() == character.getMaxHP()) {
            fullHealingGroup.setVisibility(View.GONE);
        } else {
            fullHealingGroup.setVisibility(View.VISIBLE);
        }

        extraHealingGroup.setVisibility(View.GONE);

        fullHealing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    extraHealingGroup.setVisibility(View.GONE);
                } else {
                    extraHealingGroup.setVisibility(View.VISIBLE);
                }
                updateView();
            }
        });

        updateView();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO call character short rest, with input of user choices/actions
                character.heal(getHealing());
                character.longRest();
                ((MainActivity) getActivity()).updateViews();
                dismiss();
            }
        });
        return view;
    }

    public void updateView() {
        super.updateView();
    }

    @Override
    protected int getHealing() {
        if (fullHealing.isChecked()) {
            return character.getMaxHP();
        }
        return getExtraHealing();
    }
}

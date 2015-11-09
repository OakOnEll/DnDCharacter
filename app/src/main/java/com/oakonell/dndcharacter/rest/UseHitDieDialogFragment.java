package com.oakonell.dndcharacter.rest;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.oakonell.dndcharacter.R;

import java.util.Random;

/**
 * Created by Rob on 11/8/2015.
 */
public class UseHitDieDialogFragment extends DialogFragment {

    private HitDieUseRow hitDieRow;
    private ShortRestDialogFragment fragment;

    public static UseHitDieDialogFragment createDialog(ShortRestDialogFragment fragment, HitDieUseRow row) {
        UseHitDieDialogFragment newMe = new UseHitDieDialogFragment();
        newMe.setHitDieRow(row);
        newMe.setFragment(fragment);

        return newMe;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.use_hit_die_dialog, container);
        getDialog().setTitle("Use Hit Die");

        final EditText valueText = (EditText) view.findViewById(R.id.value);
        final Button roll = (Button) view.findViewById(R.id.roll_button);
        Button done = (Button) view.findViewById(R.id.done);

        roll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = new Random().nextInt(hitDieRow.dieSides) + 1;
                valueText.setText(value + "");
                //roll.setEnabled(false);
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueString = valueText.getText().toString();
                if (valueString != null && valueString.trim().length() > 0) {
                    int value = Integer.parseInt(valueString);
                    hitDieRow.rolls.add(value);
                    hitDieRow.numDiceRemaining--;
                    hitDieRow.numUses++;
                    fragment.updateView();
                }
                dismiss();
            }
        });

        return view;
    }

    public void setHitDieRow(HitDieUseRow hitDieRow) {
        this.hitDieRow = hitDieRow;
    }

    public void setFragment(ShortRestDialogFragment fragment) {
        this.fragment = fragment;
    }

}

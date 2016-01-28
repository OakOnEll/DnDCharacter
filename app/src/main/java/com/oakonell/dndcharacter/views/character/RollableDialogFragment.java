package com.oakonell.dndcharacter.views.character;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.RandomUtils;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 12/1/2015.
 */
public abstract class RollableDialogFragment extends AbstractCharacterDialogFragment {
    public static final String ADVANTAGE_INDEX = "advantageIndex";
    public static final String START_COLOR = "startColor";
    public static final String ROLL = "roll";
    public static final String ROLL_2 = "roll2";
    public static final String MODIFIER = "modifier";

    private TextView roll1Text;
    private TextView roll2Text;
    private TextView modifierText;
    private TextView totalText;
    private TextView critical_label;
    private AppCompatSpinner advantageSpinner;

    private int modifier;
    private int startColor;

    protected void superCreateView(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            modifier = savedInstanceState.getInt(MODIFIER);
        }

        advantageSpinner = (AppCompatSpinner) view.findViewById(R.id.advantage);

        Button roll = (Button) view.findViewById(R.id.roll_button);
        roll1Text = (TextView) view.findViewById(R.id.roll1);
        roll2Text = (TextView) view.findViewById(R.id.roll2);

        if (savedInstanceState != null) {
            startColor = savedInstanceState.getInt(START_COLOR, roll1Text.getCurrentTextColor());

            roll1Text.setText(savedInstanceState.getString(ROLL, ""));
            roll2Text.setText(savedInstanceState.getString(ROLL_2, ""));
        } else {
            startColor = roll1Text.getCurrentTextColor();
        }

        modifierText = (TextView) view.findViewById(R.id.roll_modifier);
        totalText = (TextView) view.findViewById(R.id.roll_total);

        critical_label = (TextView) view.findViewById(R.id.critical_label);

        List<String> list = new ArrayList<>();
        list.add(getString(R.string.normal_advantage));
        list.add(getString(R.string.advantage));
        list.add(getString(R.string.disadvantage));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        advantageSpinner.setAdapter(dataAdapter);
        int advantageIndex = 0;
        if (savedInstanceState != null) {
            advantageIndex = savedInstanceState.getInt(ADVANTAGE_INDEX, 0);
        }

        advantageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    roll2Text.setVisibility(View.GONE);
                    roll1Text.setTextColor(startColor);
                } else {
                    roll2Text.setVisibility(View.VISIBLE);
                }
                updateRollView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        advantageSpinner.setSelection(advantageIndex);


        roll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roll();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO not sure why the roll Texts aren't being saved per android FW?
        outState.putString(ROLL, roll1Text.getText().toString());
        outState.putString(ROLL_2, roll2Text.getText().toString());
        outState.putInt(MODIFIER, modifier);
        outState.putInt(START_COLOR, startColor);
        outState.putInt(ADVANTAGE_INDEX, advantageSpinner.getSelectedItemPosition());

    }

    private void roll() {
        int roll = RandomUtils.random(1, 20);
        int roll2 = RandomUtils.random(1, 20);

        // TODO animate the roll, with sound fx
        roll1Text.setText(NumberUtils.formatNumber(roll));
        roll2Text.setText(NumberUtils.formatNumber(roll2));

        updateRollView();
    }

    public void updateRollView() {
        int roll = getTextViewInteger(roll1Text);
        int roll2 = getTextViewInteger(roll2Text);
        int total = roll;

        boolean isAdvantage = advantageSpinner.getSelectedItemPosition() == 1;
        boolean isDisadvantage = advantageSpinner.getSelectedItemPosition() == 2;

        if (isAdvantage || isDisadvantage) {
            if (isAdvantage) {
                if (roll >= roll2) {
                    roll1Text.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                    roll2Text.setTextColor(startColor);
                    total = roll;
                } else {
                    roll2Text.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                    roll1Text.setTextColor(startColor);
                    total = roll2;
                }
            }

            if (isDisadvantage) {
                if (roll <= roll2) {
                    roll1Text.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    roll2Text.setTextColor(startColor);
                    total = roll;
                } else {
                    roll2Text.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    roll1Text.setTextColor(startColor);
                    total = roll2;
                }
            }

        }

        // TODO treat natural 20/1 special? (or what about 19 for attack rolls with special feat/feature)
        if (total == 1) {
            critical_label.setText(R.string.critical_failure);
            critical_label.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        } else if (total == 20) {
            critical_label.setText(R.string.critical_success);
            critical_label.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            critical_label.setText("");
        }

        total += modifier;
        totalText.setText(NumberUtils.formatNumber(total));
    }

    private int getTextViewInteger(@NonNull TextView textView) {
        final String string = textView.getText().toString();
        if (string.trim().length() == 0) return 0;
        return Integer.parseInt(string);
    }

    protected void setModifier(int modifier) {
        this.modifier = modifier;
        modifierText.setText(NumberUtils.formatNumber(modifier));
    }

}

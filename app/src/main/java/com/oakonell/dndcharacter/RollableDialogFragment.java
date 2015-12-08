package com.oakonell.dndcharacter;

import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Rob on 12/1/2015.
 */
public class RollableDialogFragment extends DialogFragment {
    RadioButton normalRadio;
    RadioButton advantageRadio;
    RadioButton disadvantageRadio;
    int modifier;
    int startColor;
    private MainActivity mainActivity;
    private TextView roll1Text;
    private TextView roll2Text;
    private TextView modifierText;
    private TextView totalText;
    private TextView critical_label;

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    protected void superCreateView(View view) {
        Button done = (Button) view.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        normalRadio = (RadioButton) view.findViewById(R.id.normal_radio);
        advantageRadio = (RadioButton) view.findViewById(R.id.advantage_radio);
        disadvantageRadio = (RadioButton) view.findViewById(R.id.disadvantage_radio);

        Button roll = (Button) view.findViewById(R.id.roll_button);
        roll1Text = (TextView) view.findViewById(R.id.roll1);
        roll2Text = (TextView) view.findViewById(R.id.roll2);

        startColor = roll1Text.getCurrentTextColor();

        modifierText = (TextView) view.findViewById(R.id.roll_modifier);
        totalText = (TextView) view.findViewById(R.id.roll_total);

        critical_label = (TextView) view.findViewById(R.id.critical_label);
        normalRadio.setChecked(true);

        normalRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                roll1Text.setText("");
                roll2Text.setText("");
                totalText.setText("");
                if (isChecked) {
                    roll2Text.setVisibility(View.GONE);
                } else {
                    roll2Text.setVisibility(View.VISIBLE);
                }
            }
        });
        CompoundButton.OnCheckedChangeListener twoDiceListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                roll1Text.setText("");
                roll2Text.setText("");
                totalText.setText("");
                if (advantageRadio.isChecked() || disadvantageRadio.isChecked()) {
                    roll2Text.setVisibility(View.VISIBLE);
                } else {
                    roll2Text.setVisibility(View.GONE);
                }
            }
        };
        advantageRadio.setOnCheckedChangeListener(twoDiceListener);
        disadvantageRadio.setOnCheckedChangeListener(twoDiceListener);

        roll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roll();
            }
        });
    }

    private void roll() {
        Random rand = new Random();
        int roll = rand.nextInt(19) + 1;
        // TODO animate the roll, with sound fx
        roll1Text.setText(roll + "");

        int total = roll;

        if (advantageRadio.isChecked() || disadvantageRadio.isChecked()) {
            int roll2 = rand.nextInt(20) + 1;
            roll2Text.setText(roll2 + "");
            if (advantageRadio.isChecked()) {
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

            if (disadvantageRadio.isChecked()) {
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

        // TODO treat natural 20/1 special?

        if (total == 1) {
            critical_label.setText("Critical Failure");
            critical_label.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        } else if (total == 20) {
            critical_label.setText("Critical Success");
            critical_label.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            critical_label.setText("");
        }

        total += modifier;
        totalText.setText(total + "");
    }

    protected void setModifier(int modifier) {
        this.modifier = modifier;
        modifierText.setText(modifier + "");
    }

}

package com.oakonell.dndcharacter.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.DamageType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 10/28/2015.
 */
public class HitPointDiaogFragment extends AbstractCharacterDialogFragment {
    EditText hpText;
    RadioButton damage;
    RadioButton heal;
    RadioButton tempHP;
    Spinner type;
    Button add;
    Button subtract;

    Button cancel;
    private TextView start_hp;
    private TextView final_hp;

    public static HitPointDiaogFragment createDialog() {
        return new HitPointDiaogFragment();
    }

    @Override
    public void onCharacterChanged(Character character) {
        // nothing to do?
        updateView();
    }

    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hit_point_dialog, container);
        hpText = (EditText) view.findViewById(R.id.hp);
        getDialog().setTitle("Hit Points");

        damage = (RadioButton) view.findViewById(R.id.damage_radio);
        type = (Spinner) view.findViewById(R.id.damage_type);

        float minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (type.getPrompt().length() + 2) * NoDefaultSpinner.SPINNER_TEXT_SP, type.getResources().getDisplayMetrics());
        type.setMinimumWidth((int) minWidth);


        heal = (RadioButton) view.findViewById(R.id.heal_radio);
        tempHP = (RadioButton) view.findViewById(R.id.temp_hp_radio);

        add = (Button) view.findViewById(R.id.add);
        subtract = (Button) view.findViewById(R.id.subtract);

        start_hp = (TextView) view.findViewById(R.id.start_hp);
        final_hp = (TextView) view.findViewById(R.id.final_hp);

        cancel = (Button) view.findViewById(R.id.cancel);

        //type.setAdapter();
        List<String> list = new ArrayList<String>();
        for (DamageType each : DamageType.values()) {
            list.add(each.toString());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(dataAdapter);


        View.OnClickListener radioListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == damage && damage.isChecked()) {
                    heal.setChecked(false);
                    tempHP.setChecked(false);
                    type.setEnabled(true);
                }
                if (v == heal && heal.isChecked()) {
                    damage.setChecked(false);
                    tempHP.setChecked((false));
                    type.setEnabled(false);
                }
                if (v == tempHP && tempHP.isChecked()) {
                    damage.setChecked(false);
                    heal.setChecked((false));
                    type.setEnabled(false);
                }
                updateView();
            }

        };
        damage.setOnClickListener(radioListener);
        heal.setOnClickListener(radioListener);
        tempHP.setOnClickListener(radioListener);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHp(1);

            }
        });
        subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHp(-1);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        hpText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateView();
            }
        });

        return view;
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);
        updateView();
    }


    private void updateView() {
        Character character = getCharacter();
        int currentHp = character.getHP();
        int tempHp = character.getTempHp();
        String startHptext = currentHp + "/" + character.getMaxHP();
        if (character.getTempHp() > 0) {
            startHptext += " + " + tempHp;
        }
        start_hp.setText(startHptext);
        int hpToApply = getHp();

        int finalHp = character.getTotalHP();

        if (damage.isChecked()) {
            // TODO extract this to avoid redundancy
            tempHp -= hpToApply;
            finalHp = Math.max(character.getHP() + tempHp, 0);
        } else if (heal.isChecked()) {
            finalHp = Math.min(hpToApply + finalHp, character.getMaxHP() + character.getTempHp());
        } else if (tempHP.isChecked()) {
            // TODO extract this to avoid redundancy
            tempHp = tempHp + hpToApply;
        }
        String finalHpText = finalHp + "/" + character.getMaxHP();
        if (tempHp > 0) {
            finalHpText += " + " + tempHp;
        }
        final_hp.setText(finalHpText);
        conditionallyEnableDone();
    }


    @Override
    protected boolean onDone() {
        Character character = getCharacter();
        if (damage.isChecked()) {
            character.damage(getHp());
        } else if (heal.isChecked()) {
            character.heal(getHp());
        } else if (tempHP.isChecked()) {
            character.addTempHp(getHp());
        }
        return super.onDone();
    }

    private void addHp(int amount) {
        int hp = getHp();
        String hpString;
        hp = Math.max(0, hp + amount);
        hpString = hp + "";
        hpText.setText(hpString);
        hpText.setSelection(hpString.length());
        conditionallyEnableDone();
    }

    private int getHp() {
        int hp = 0;
        String hpString = hpText.getText().toString();
        if (!(hpString == null || hpString.length() == 0)) {
            hp = Integer.parseInt(hpString);
        }
        return hp;
    }

    protected void conditionallyEnableDone() {
        boolean canApply = (damage.isChecked() || heal.isChecked() || tempHP.isChecked()) && getHp() > 0;
        enableDone(canApply);
    }


}

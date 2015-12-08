package com.oakonell.dndcharacter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.CharacterItem;

import java.util.Random;

/**
 * Created by Rob on 12/8/2015.
 */
public class WeaponAttackDialogFragment extends RollableDialogFragment {
    CharacterItem item;

    Button attack_roll_button;
    TextView attack_roll1;
    TextView attack_roll_modifier;
    TextView attack_roll_total;

    TextView description;
    TextView name;
    TextView attack_bonus;
    TextView damage_descr;
    private int attackRoll;
    private int damageModifier = 2;

    public static WeaponAttackDialogFragment create(MainActivity mainActivity, CharacterItem item) {
        WeaponAttackDialogFragment newMe = new WeaponAttackDialogFragment();
        newMe.setMainActivity(mainActivity);
        newMe.setItem(item);
        return newMe;
    }

    public void setItem(CharacterItem item) {
        this.item = item;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weapon_attack_dialog, container);
        superCreateView(view);
        setModifier(5);

        description = (TextView) view.findViewById(R.id.description);
        name = (TextView) view.findViewById(R.id.weapon_label);
        attack_bonus = (TextView) view.findViewById(R.id.attack_bonus);
        damage_descr = (TextView) view.findViewById(R.id.damage_descr);

        attack_roll_button = (Button) view.findViewById(R.id.attack_roll_button);
        attack_roll1 = (TextView) view.findViewById(R.id.attack_roll1);
        attack_roll_modifier = (TextView) view.findViewById(R.id.attack_roll_modifier);
        attack_roll_total = (TextView) view.findViewById(R.id.attack_roll_total);

        attack_roll_modifier.setText(damageModifier + "");

        attack_roll_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollAttack();
            }
        });


        return view;
    }

    private void rollAttack() {
        Random rand = new Random();
        attackRoll = rand.nextInt(6) + 1;

        // TODO animate the roll, with sound fx
        attack_roll1.setText(attackRoll + "");
        attack_roll1.setVisibility(View.VISIBLE);
        
        updateAttackRollView();
    }

    private void updateAttackRollView() {
        int total = attackRoll;
        total += damageModifier;
        attack_roll_total.setText(total + "");
    }
}

package com.oakonell.dndcharacter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterItem;
import com.oakonell.dndcharacter.model.CharacterWeapon;
import com.oakonell.dndcharacter.model.DamageType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 12/8/2015.
 */
public class WeaponAttackDialogFragment extends RollableDialogFragment {
    Button attack_roll_button;
    TextView attack_roll1;
    TextView attack_roll_modifier;
    TextView attack_roll_total;

    TextView description;
    TextView name;
    TextView attack_bonus;
    TextView damage_descr;

    CharacterWeapon weapon;
    private int damageModifier = 2;

    public static WeaponAttackDialogFragment create(CharacterItem item) {
        WeaponAttackDialogFragment newMe = new WeaponAttackDialogFragment();
        String name = item.getName();

        Bundle args = new Bundle();
        args.putString("weapon", name);
        newMe.setArguments(args);

        return newMe;
    }


    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weapon_attack_dialog, container);
        superCreateView(view, savedInstanceState);

        description = (TextView) view.findViewById(R.id.description);
        name = (TextView) view.findViewById(R.id.weapon_label);
        attack_bonus = (TextView) view.findViewById(R.id.attack_bonus);
        damage_descr = (TextView) view.findViewById(R.id.damage_descr);

        attack_roll_button = (Button) view.findViewById(R.id.attack_roll_button);
        attack_roll1 = (TextView) view.findViewById(R.id.attack_roll1);
        attack_roll_modifier = (TextView) view.findViewById(R.id.attack_roll_modifier);
        attack_roll_total = (TextView) view.findViewById(R.id.attack_roll_total);


        attack_roll_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollAttack();
            }
        });


        return view;
    }

    @Override
    public void onCharacterLoaded(com.oakonell.dndcharacter.model.Character character) {
        super.onCharacterLoaded(character);

        loadWeapon(character);

        final CharacterWeapon.AttackModifiers attackModifiers = weapon.getAttackModifiers(character);
        damageModifier = attackModifiers.getDamageModifier();
        setModifier(attackModifiers.getAttackBonus());
        attack_bonus.setText(attackModifiers.getAttackBonus() + "");

        // updateViews
        name.setText(weapon.getName());
        //description.setText(weapon.get);

        damage_descr.setText(weapon.getDamageString());
        attack_roll_modifier.setText(damageModifier + "");
    }

    private void loadWeapon(Character character) {
        String name = getArguments().getString("weapon");

        List<CharacterWeapon> weapons = new ArrayList<>();
        for (CharacterWeapon each : character.getWeapons()) {
            if (each.getName().equals(name)) {
                weapons.add(each);
            }
        }
        if (weapons.isEmpty()) {
            throw new RuntimeException("No weapon named '" + name + "' in inventory");
        }
        if (weapons.size() > 1) {
            // compare damages?

        }
        weapon = weapons.get(0);
    }

    @Override
    public void onCharacterChanged(Character character) {
        // TODO
        onCharacterLoaded(character);
    }

    private void rollAttack() {
        Map<DamageType, Integer> damages = new HashMap<>();

        for (CharacterWeapon.DamageFormula each : weapon.getDamages()) {
            int value = getCharacter().evaluateFormula(each.getDamageFormula(), null);
            Integer damage = damages.get(each.getType());
            if (damage == null) damage = 0;
            damage += value;
            damages.put(each.getType(), damage);
        }

        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        int total = 0;
        for (Map.Entry<DamageType, Integer> each : damages.entrySet()) {
            final DamageType type = each.getKey();
            final Integer value = each.getValue();
            if (!isFirst) {
                builder.append(", ");
            }
            builder.append(value);
            builder.append(" ");
            builder.append(type.toString());
            isFirst = false;
            total += value;
        }


        // TODO animate the roll, with sound fx
        attack_roll1.setText(builder.toString());
        attack_roll1.setVisibility(View.VISIBLE);

        updateAttackRollView(total);
    }

    private void updateAttackRollView(int attackRoll) {
        int total = attackRoll;
        total += damageModifier;
        attack_roll_total.setText(total + "");
    }
}

package com.oakonell.dndcharacter;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterItem;
import com.oakonell.dndcharacter.model.CharacterWeapon;
import com.oakonell.dndcharacter.model.DamageType;
import com.oakonell.dndcharacter.views.DividerItemDecoration;

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
    private CheckBox two_handed;
    private CheckBox use_dexterity;

    private Button add_another;
    private RecyclerView damagesRecyclerView;
    private ViewGroup total_group;
    private TextView attack_roll_final_total;


    // these are persistence safe do to being initialized onCharacterLoad()
    CharacterWeapon weapon;
    private int damageModifier = 2;

    // these need to be persistence enabled
    private DamagesListAdapter damageListAdapter;
    AttackDamageInfo attackDamageInfo;


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
        two_handed = (CheckBox) view.findViewById(R.id.two_handed);
        use_dexterity = (CheckBox) view.findViewById(R.id.use_dexterity);

        attack_roll_button = (Button) view.findViewById(R.id.attack_roll_button);
        attack_roll1 = (TextView) view.findViewById(R.id.attack_roll1);
        attack_roll_modifier = (TextView) view.findViewById(R.id.attack_roll_modifier);
        attack_roll_total = (TextView) view.findViewById(R.id.attack_roll_total);

        add_another = (Button) view.findViewById(R.id.add_another);
        damagesRecyclerView = (RecyclerView) view.findViewById(R.id.damages);
        total_group = (ViewGroup) view.findViewById(R.id.total_group);
        attack_roll_final_total = (TextView) view.findViewById(R.id.attack_roll_final_total);


        add_another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total_group.setVisibility(View.VISIBLE);
                if (attackDamageInfo != null) {
                    damageListAdapter.damages.add(attackDamageInfo);
                    damageListAdapter.notifyDataSetChanged();
                    attack_roll1.setText("");
                    attack_roll_total.setText("");


                    attackDamageInfo = null;

                    updateRollTotal();
                }
            }
        });

        ArrayList<AttackDamageInfo> damagesList = new ArrayList<>();
        damageListAdapter = new DamagesListAdapter(this, damagesList);

        if (savedInstanceState != null) {
            attackDamageInfo = savedInstanceState.getParcelable("attackDamageInfo");
            ArrayList<AttackDamageInfo> damageList = savedInstanceState.getParcelableArrayList("damageList");
            if (damageList != null) {
                damageListAdapter.damages.addAll(damageList);
            }
            updateRollTotal();

            // TODO this erroneously includes the damage bonus
            attack_roll1.setText(attackDamageInfo.getDescription());

            //damages.put(first, damages.get(first) + damageModifier);

            attack_roll_total.setText(attackDamageInfo.getDescription());

        }

        damagesRecyclerView.setAdapter(damageListAdapter);

        damagesRecyclerView.setHasFixedSize(false);
        damagesRecyclerView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        damagesRecyclerView.addItemDecoration(itemDecoration);


        attack_roll_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollAttack();
            }
        });


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
        private DamagesListAdapter damageListAdapter;
    AttackDamageInfo attackDamageInfo;
         */
        if (attackDamageInfo != null) {
            outState.putParcelable("attackDamageInfo", attackDamageInfo);
        }
        if (!damageListAdapter.damages.isEmpty()) {
            outState.putParcelableArrayList("damageList", damageListAdapter.damages);
        }

    }

    private void updateRollTotal() {
        Map<DamageType, Integer> sum = new HashMap<>();
        for (AttackDamageInfo each : damageListAdapter.damages) {
            addAttackDamage(sum, each);
        }
        if (attackDamageInfo != null) {
            addAttackDamage(sum, attackDamageInfo);
        }
        AttackDamageInfo info = new AttackDamageInfo();
        info.damages.putAll(sum);
        attack_roll_final_total.setText(info.getDescription());
    }

    private void addAttackDamage(Map<DamageType, Integer> sum, AttackDamageInfo each) {
        for (Map.Entry<DamageType, Integer> damage : each.damages.entrySet()) {
            final DamageType type = damage.getKey();
            final Integer value = damage.getValue();
            Integer currentTypeSum = sum.get(type);
            if (currentTypeSum == null) currentTypeSum = 0;
            currentTypeSum += value;
            sum.put(type, currentTypeSum);
        }
    }

    @Override
    public void onCharacterLoaded(com.oakonell.dndcharacter.model.Character character) {
        super.onCharacterLoaded(character);

        loadWeapon(character);

        updateViews();
    }

    private void updateViews() {
        Character character = getCharacter();
        final CharacterWeapon.AttackModifiers attackModifiers = weapon.getAttackModifiers(character, use_dexterity.isChecked());
        damageModifier = attackModifiers.getDamageModifier();
        setModifier(attackModifiers.getAttackBonus());


        // updateViews

        if (weapon.isVersatile()) {
            two_handed.setVisibility(View.VISIBLE);
            two_handed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO more to do here?
                    updateViews();
                }
            });
        } else {
            two_handed.setOnCheckedChangeListener(null);
            two_handed.setVisibility(View.GONE);
        }

        if (weapon.isFinesse()) {
            use_dexterity.setVisibility(View.VISIBLE);
            use_dexterity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateViews();
                }
            });
        } else {
            use_dexterity.setOnCheckedChangeListener(null);
            use_dexterity.setVisibility(View.GONE);
        }


        description.setText(weapon.getDescriptionString());
        attack_bonus.setText(attackModifiers.getAttackBonus() + "");

        name.setText(weapon.getName());
        //description.setText(weapon.get);

        // TODO handle versatile/two handed adjustment here
        if (two_handed.isChecked()) {
            damage_descr.setText(weapon.getVersatileDamageString());
        } else {
            damage_descr.setText(weapon.getDamageString());
        }
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

        List<CharacterWeapon.DamageFormula> weaponDamages = weapon.getDamages();
        if (two_handed.isChecked()) {
            weaponDamages = weapon.getVersatileDamages();
        }
        attackDamageInfo = new AttackDamageInfo();
        final Map<DamageType, Integer> damages = attackDamageInfo.damages;
        DamageType first = null;
        for (CharacterWeapon.DamageFormula each : weaponDamages) {
            int value = getCharacter().evaluateFormula(each.getDamageFormula(), null);
            Integer damage = damages.get(each.getType());
            if (first == null) {
                first = each.getType();
            }
            if (damage == null) damage = 0;
            damage += value;
            damages.put(each.getType(), damage);
        }


        // TODO animate the roll, with sound fx
        attack_roll1.setText(attackDamageInfo.getDescription());
        attack_roll1.setVisibility(View.VISIBLE);

        damages.put(first, damages.get(first) + damageModifier);

        attack_roll_total.setText(attackDamageInfo.getDescription());

        updateRollTotal();
    }


    static class DamageViewHolder extends RecyclerView.ViewHolder {
        final TextView damage;

        public DamageViewHolder(View itemView) {
            super(itemView);
            damage = (TextView) itemView.findViewById(R.id.damage);
        }
    }

    static class AttackDamageInfo implements Parcelable {
        Map<DamageType, Integer> damages = new HashMap<>();

        AttackDamageInfo() {

        }

        protected AttackDamageInfo(Parcel in) {
            int size = in.readInt();

            for (int i = 0; i < size; i++) {
                String typeString = in.readString();
                int value = in.readInt();
                final DamageType damageType = DamageType.valueOf(typeString);
                damages.put(damageType, value);
            }
        }

        public static final Creator<AttackDamageInfo> CREATOR = new Creator<AttackDamageInfo>() {
            @Override
            public AttackDamageInfo createFromParcel(Parcel in) {
                return new AttackDamageInfo(in);
            }

            @Override
            public AttackDamageInfo[] newArray(int size) {
                return new AttackDamageInfo[size];
            }
        };

        public String getDescription() {
            StringBuilder builder = new StringBuilder();
            boolean isFirst = true;
            for (Map.Entry<DamageType, Integer> each : damages.entrySet()) {
                if (!isFirst) {
                    builder.append(", ");
                }
                final DamageType type = each.getKey();
                final Integer value = each.getValue();
                builder.append(value);
                builder.append(" ");
                builder.append(type);
                isFirst = false;
            }
            return builder.toString();
        }

        public int getTotal() {
            int total = 0;
            for (Map.Entry<DamageType, Integer> each : damages.entrySet()) {
                total += each.getValue();
            }
            return total;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(damages.size());
            for (Map.Entry<DamageType, Integer> entry : damages.entrySet()) {
                dest.writeString(entry.getKey().toString());
                dest.writeInt(entry.getValue());
            }
        }
    }

    static class DamagesListAdapter extends RecyclerView.Adapter<DamageViewHolder> {
        private final WeaponAttackDialogFragment context;
        private final ArrayList<AttackDamageInfo> damages;

        DamagesListAdapter(WeaponAttackDialogFragment context, ArrayList<AttackDamageInfo> damages) {
            this.damages = damages;
            this.context = context;
        }

        @Override
        public DamageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.damage_row, parent, false);
            return new DamageViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(DamageViewHolder holder, int position) {
            AttackDamageInfo row = damages.get(position);

            holder.damage.setText(row.getDescription());
        }

        @Override
        public int getItemCount() {
            return damages.size();
        }
    }
}

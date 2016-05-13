package com.oakonell.dndcharacter.views.character.spell;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.model.character.DamageType;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.character.spell.CharacterSpell;
import com.oakonell.dndcharacter.model.character.spell.SpellAttackType;
import com.oakonell.dndcharacter.model.character.spell.SpellDurationType;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.NoDefaultSpinner;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.RollableDialogFragment;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Rob on 12/8/2015.
 */
public class SpellDialogFragment extends RollableDialogFragment {
    public static final String SPELL = "spell";

    public static final String ATTACK_DAMAGE_INFO = "attackDamageInfo";
    public static final String DAMAGE_LIST = "damageList";

    private TextView attack_roll1;
    private TextView attack_roll_modifier;
    private TextView attack_roll_total;
    private ViewGroup damage_input;
    private EditText attack_roll_input;
    private NoDefaultSpinner attack_roll_input_type;

    private TextView description;
    private TextView higher_levels;
    private TextView name;
    private TextView attack_bonus;
    private TextView save_dc;

    private TextView damage_descr;

    private Button add_another;
    private RecyclerView damagesRecyclerView;
    private ViewGroup total_group;
    private TextView attack_roll_final_total;

    private Spinner spell_slot_level;
    private TextView spell_level;
    private TextView spell_school;

    // these are persistence safe do to being initialized onCharacterLoad()
    private CharacterSpell spell;
    private int damageModifier = 2;

    // these need to be persistence enabled
    private DamagesListAdapter damageListAdapter;
    @Nullable
    private AttackDamageInfo attackDamageInfo;
    private ArrayList<String> spellLevels;
    private Button cast_button;
    private TextView components;
    private TextView casting_time;
    private TextView range;
    private TextView duration;
    private View attack_roll_layout;
    private View main_roll;


    @NonNull
    public static SpellDialogFragment create(@NonNull CharacterSpell spell) {
        SpellDialogFragment newMe = new SpellDialogFragment();
        String name = spell.getName();

        Bundle args = new Bundle();
        args.putString(SPELL, name);
        newMe.setArguments(args);

        return newMe;
    }


    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.spell_dialog, container);
        superCreateView(view, savedInstanceState);

        description = (TextView) view.findViewById(R.id.description);
        higher_levels = (TextView) view.findViewById(R.id.higher_levels);
        name = (TextView) view.findViewById(R.id.weapon_label);
        attack_bonus = (TextView) view.findViewById(R.id.attack_bonus);
        save_dc = (TextView) view.findViewById(R.id.save_dc);
        damage_descr = (TextView) view.findViewById(R.id.damage_descr);

        Button attack_roll_button = (Button) view.findViewById(R.id.attack_roll_button);
        attack_roll1 = (TextView) view.findViewById(R.id.attack_roll1);
        attack_roll_modifier = (TextView) view.findViewById(R.id.attack_roll_modifier);
        attack_roll_total = (TextView) view.findViewById(R.id.attack_roll_total);
        attack_roll_input = (EditText) view.findViewById(R.id.attack_roll_input);
        damage_input = (ViewGroup) view.findViewById(R.id.damage_input);

        attack_roll_layout = view.findViewById(R.id.attack_roll_layout);
        main_roll = view.findViewById(R.id.main_roll);

        attack_roll_input_type = (NoDefaultSpinner) view.findViewById(R.id.attack_roll_input_type);
        float minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (attack_roll_input_type.getPrompt().length() + 2) * NoDefaultSpinner.SPINNER_TEXT_SP, attack_roll_input_type.getResources().getDisplayMetrics());
        attack_roll_input_type.setMinimumWidth((int) minWidth);

        List<String> list = new ArrayList<>();
        for (DamageType each : DamageType.values()) {
            list.add(getString(each.getStringResId()));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        attack_roll_input_type.setAdapter(dataAdapter);

        add_another = (Button) view.findViewById(R.id.add_another);
        damagesRecyclerView = (RecyclerView) view.findViewById(R.id.damages);
        total_group = (ViewGroup) view.findViewById(R.id.total_group);
        attack_roll_final_total = (TextView) view.findViewById(R.id.attack_roll_final_total);

        spell_slot_level = (Spinner) view.findViewById(R.id.spell_slot_level);
        cast_button = (Button) view.findViewById(R.id.cast_button);
        cast_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                castSpell();
            }
        });

        spell_level = (TextView) view.findViewById(R.id.spell_level);
        spell_school = (TextView) view.findViewById(R.id.spell_school);

        casting_time = (TextView) view.findViewById(R.id.casting_time);
        range = (TextView) view.findViewById(R.id.range);
        duration = (TextView) view.findViewById(R.id.duration);
        components = (TextView) view.findViewById(R.id.components);

        // TODO this is overriding the state
        attack_roll_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(@NonNull Editable s) {
                damageInputChanged(attack_roll_input_type.getSelectedItemPosition(), s.toString().trim());
            }
        });

        attack_roll_input_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                damageInputChanged(position, attack_roll_input.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        add_another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total_group.setVisibility(View.VISIBLE);
                if (attackDamageInfo != null) {
                    damageListAdapter.damages.add(attackDamageInfo);
                    damageListAdapter.notifyDataSetChanged();
                    attack_roll_input.setText("");
                    attack_roll1.setText("");
                    attack_roll_total.setText("");
                    attackDamageInfo = null;

                    attack_roll_total.setVisibility(View.GONE);
                    damage_input.setVisibility(View.VISIBLE);

                    updateRollTotal();
                }
            }
        });
        add_another.setEnabled(true);

        ArrayList<AttackDamageInfo> damagesList = new ArrayList<>();
        damageListAdapter = new DamagesListAdapter(this, damagesList);

        if (savedInstanceState != null) {
            attackDamageInfo = savedInstanceState.getParcelable(ATTACK_DAMAGE_INFO);
            ArrayList<AttackDamageInfo> damageList = savedInstanceState.getParcelableArrayList(DAMAGE_LIST);
            if (damageList != null) {
                damageListAdapter.damages.addAll(damageList);
            }
            updateRollTotal();

            if (attackDamageInfo != null) {
                attack_roll1.setText(attackDamageInfo.getDescription(getResources()));

                //damages.put(first, damages.get(first) + damageModifier);

                attack_roll_total.setText(attackDamageInfo.getDescription(getResources()));
            }
            if (attack_roll_total.getText().toString().trim().length() > 0) {
                // restore state?
                // possibly not restoring properly due to text watcher?
                attack_roll_total.setVisibility(View.VISIBLE);
                damage_input.setVisibility(View.GONE);
                add_another.setEnabled(true);


            }
        }

        damagesRecyclerView.setAdapter(damageListAdapter);

        damagesRecyclerView.setHasFixedSize(false);
        damagesRecyclerView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

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

    protected void castSpell() {
        final CharacterActivity context = SpellDialogFragment.this.getMainActivity();
        int spell_level_temp = 0;
        if (spell.getLevel() != 0) {
            spell_level_temp = spell_slot_level.getSelectedItemPosition() + spell.getLevel();
            context.getCharacter().useSpellSlot(spell_level_temp);
        }
        final int spell_level = spell_level_temp;
        if (spell.getDurationType() == SpellDurationType.INSTANTANEOUS) {
            context.updateViews();
            context.saveCharacter();
            return;
        }
        if (spell.isConcentration()) {
            addSpellAsEffect(context, spell_level, true);
            return;
        }
        // ask if want to create an effect
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        addSpellAsEffect(context, spell_level, false);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getString(R.string.create_effect_for_spell, spell.getName())).setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

    private void addSpellAsEffect(CharacterActivity context, int spell_level, boolean concentration) {
        CharacterEffect effect = new CharacterEffect();
        String name;
        if (concentration) {
            name = getString(R.string.spell_concentration, spell.getName());
        } else {
            name = getString(R.string.spell_effect, spell.getName());
        }
        effect.setName(name);
        String description = getString(R.string.range_label) + spell.getRangeString(getResources());
        description += "\n";
        description += getString(R.string.duration_label) + spell.getDurationString(getResources());
        description += "\n";
        description += spell.getDescription();
        if (spell.getHigherLevelDescription() != null) {
            description += "\n\n--\n" + spell.getHigherLevelDescription();
        }
        if (spell_level > 0) {
            description += "\n\n" + getString(R.string.spell_cast_level, spell_level);
        }
        if (concentration) {
            description += context.getString(R.string.concentration_postfix);
        }

        effect.setDescription(description);
        getCharacter().addEffect(effect);
        context.updateViews();
        context.saveCharacter();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.cast_spell);
    }

    protected boolean isCancelable(boolean hasCancelButton) {
        return false;
    }

    private void damageInputChanged(int typeIndex, @NonNull String string) {
        if (damage_input.getVisibility() != View.VISIBLE) return;
        attackDamageInfo = null;
        try {
            if (typeIndex < 0) {
                add_another.setEnabled(false);
                return;
            }
            if (string.length() == 0) {
                add_another.setEnabled(false);
                return;
            }
            int value = Integer.parseInt(string);
            final DamageType damageType = DamageType.values()[typeIndex];
            attackDamageInfo = new AttackDamageInfo();
            attackDamageInfo.damages.put(damageType, value);
            add_another.setEnabled(true);
        } finally {
            updateRollTotal();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
        private DamagesListAdapter damageListAdapter;
    AttackDamageInfo attackDamageInfo;
         */
        if (attackDamageInfo != null) {
            outState.putParcelable(ATTACK_DAMAGE_INFO, attackDamageInfo);
        }
        if (!damageListAdapter.damages.isEmpty()) {
            outState.putParcelableArrayList(DAMAGE_LIST, damageListAdapter.damages);
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
        attack_roll_final_total.setText(info.getDescription(getResources()));
    }

    private void addAttackDamage(@NonNull Map<DamageType, Integer> sum, @NonNull AttackDamageInfo each) {
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
    public void onCharacterLoaded(@NonNull Character character) {
        super.onCharacterLoaded(character);

        loadSpell(character);


        updateViews();
    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        super.onCharacterChanged(character);

        loadSpell(character);
        updateViews();
    }

    @NonNull
    @Override
    protected Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        // TODO include spell name as arg?
        filter.add(new FeatureContextArgument(FeatureContext.DICE_ROLL));
        filter.add(new FeatureContextArgument(FeatureContext.SPELL_CAST));
        return filter;
    }

    @Override
    protected FeatureContextArgument getNoteContext() {
        // TODO include spell name as arg?
        return new FeatureContextArgument(FeatureContext.SPELL_CAST);
    }

    private void updateViews() {
        Character character = getCharacter();
        StatType stat = spell.getCastingStat();
        int modifier = 0;
        if (stat != null) {
            modifier = character.getStatBlock(stat).getModifier();
            setModifier(modifier + character.getProficiency());
        }

        save_dc.setText(NumberUtils.formatNumber(8 + modifier + character.getProficiency()));

        // updateViews


        description.setText(spell.getDescription());
        if (spell.getHigherLevelDescription() != null) {
            higher_levels.setText(spell.getHigherLevelDescription());
        } else {
            higher_levels.setVisibility(View.GONE);
        }
        attack_bonus.setText(NumberUtils.formatNumber(modifier + character.getProficiency()));

        name.setText(spell.getName());

        attack_roll_modifier.setText(NumberUtils.formatNumber(damageModifier));

        if (spell.getLevel() == 0) {
            spell_slot_level.setVisibility(View.GONE);
        } else {
            final Character.SpellLevelInfo levelInfo = getCharacter().getSpellInfos().get(spell_slot_level.getSelectedItemPosition() + spell.getLevel());
            cast_button.setEnabled(levelInfo.getSlotsAvailable() > 0);
        }

        spell_level.setText(getString(R.string.level_n_spell, spell.getLevel()));
        if (spell.getSchool() != null) {
            spell_school.setText(spell.getSchool().getStringResId());
        } else {
            spell_school.setText(R.string.unknown_school);
        }

        casting_time.setText(spell.getCastingTimeString(getResources()));
        range.setText(spell.getRangeString(getResources()));
        duration.setText(spell.getDurationString(getResources()));
        components.setText(spell.getComponentString());

        if (spell.getAttackType() == null || !(spell.getAttackType() == SpellAttackType.MELEE_ATTACK || spell.getAttackType() == SpellAttackType.RANGED_ATTACK)) {
            attack_roll_layout.setVisibility(View.GONE);
        }
        if (spell.hasDirectRoll()) {
            main_roll.setVisibility(View.VISIBLE);
        } else {
            main_roll.setVisibility(View.GONE);
        }

    }

    private void loadSpell(@NonNull Character character) {
        String name = getArguments().getString(SPELL);

        List<CharacterSpell> spells = new ArrayList<>();
        for (Character.SpellLevelInfo eachSpellLevel : character.getSpellInfos()) {
            for (Character.CharacterSpellWithSource each : eachSpellLevel.getSpellInfos()) {
                if (each.getSpell().getName().equals(name)) {
                    spells.add(each.getSpell());
                }

            }
        }
        if (spells.isEmpty()) {
            // TODO deal with weapon having been deleted
            throw new RuntimeException("No spell named '" + name + "' in spell list");
        }
        if (spells.size() > 1) {
            // TODO compare damages?

        }
        spell = spells.get(0);

        // update the spell slot drop down descriptions
        ArrayAdapter spell_slot_levelAdapter = (ArrayAdapter) spell_slot_level.getAdapter();
        if (spell_slot_levelAdapter == null) {
            spellLevels = new ArrayList<>();
            spell_slot_levelAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spellLevels);
            spell_slot_level.setAdapter(spell_slot_levelAdapter);
        }
        spellLevels.clear();
        for (Character.SpellLevelInfo each : getCharacter().getSpellInfos()) {
            if (each.getLevel() == 0) continue;
            if (each.getLevel() < spell.getLevel()) continue;
            spellLevels.add(getString(R.string.spell_slot_level_and_uses, each.getLevel(), each.getSlotsAvailable()));
        }
        spell_slot_levelAdapter.notifyDataSetChanged();

        spell_slot_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Character.SpellLevelInfo levelInfo = getCharacter().getSpellInfos().get(spell_slot_level.getSelectedItemPosition() + spell.getLevel());
                cast_button.setEnabled(levelInfo.getSlotsAvailable() > 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void rollAttack() {
        attack_roll_total.setVisibility(View.VISIBLE);
        damage_input.setVisibility(View.GONE);
        // this need to come before the creation/assignment below
        attack_roll_input.setText("");

// TODO handle spell damages
//        List<CharacterWeapon.DamageFormula> weaponDamages = spell.getDamages();
        attackDamageInfo = new AttackDamageInfo();
        final Map<DamageType, Integer> damages = attackDamageInfo.damages;
        DamageType first = null;
//        for (CharacterWeapon.DamageFormula each : weaponDamages) {
//            int value = getCharacter().evaluateFormula(each.getDamageFormula(), null);
//            Integer damage = damages.get(each.getType());
//            if (first == null) {
//                first = each.getType();
//            }
//            if (damage == null) damage = 0;
//            damage += value;
//            damages.put(each.getType(), damage);
//        }

        // TODO animate the roll, with sound fx
        attack_roll1.setText(attackDamageInfo.getDescription(getResources()));

        attack_roll_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attack_roll_total.setVisibility(View.GONE);
                damage_input.setVisibility(View.VISIBLE);
                attackDamageInfo = null;
                updateRollTotal();
            }
        });

        attack_roll1.setVisibility(View.VISIBLE);


        Integer damage = damages.get(first);
        if (damage == null) {
            damage = 0;
        }
        damages.put(first, damage + damageModifier);

        attack_roll_total.setText(attackDamageInfo.getDescription(getResources()));
        add_another.setEnabled(true);

        updateRollTotal();
    }


    static class DamageViewHolder extends BindableComponentViewHolder<AttackDamageInfo, SpellDialogFragment, DamagesListAdapter> {
        @NonNull
        final TextView damage;

        public DamageViewHolder(@NonNull View itemView) {
            super(itemView);
            damage = (TextView) itemView.findViewById(R.id.damage);
        }

        @Override
        public void bind(@NonNull SpellDialogFragment context, DamagesListAdapter adapter, @NonNull AttackDamageInfo row) {
            damage.setText(row.getDescription(context.getResources()));
        }
    }

    static class AttackDamageInfo implements Parcelable {
        final Map<DamageType, Integer> damages = new HashMap<>();

        AttackDamageInfo() {

        }

        protected AttackDamageInfo(@NonNull Parcel in) {
            int size = in.readInt();

            for (int i = 0; i < size; i++) {
                String typeString = in.readString();
                int value = in.readInt();
                final DamageType damageType = EnumHelper.stringToEnum(typeString, DamageType.class);
                damages.put(damageType, value);
            }
        }

        public static final Creator<AttackDamageInfo> CREATOR = new Creator<AttackDamageInfo>() {
            @NonNull
            @Override
            public AttackDamageInfo createFromParcel(@NonNull Parcel in) {
                return new AttackDamageInfo(in);
            }

            @NonNull
            @Override
            public AttackDamageInfo[] newArray(int size) {
                return new AttackDamageInfo[size];
            }
        };

        @NonNull
        public String getDescription(@NonNull Resources resources) {
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
                builder.append(resources.getString(type.getStringResId()));
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
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeInt(damages.size());
            for (Map.Entry<DamageType, Integer> entry : damages.entrySet()) {
                dest.writeString(entry.getKey().name());
                dest.writeInt(entry.getValue());
            }
        }
    }

    static class DamagesListAdapter extends RecyclerView.Adapter<DamageViewHolder> {
        private final SpellDialogFragment context;
        private final ArrayList<AttackDamageInfo> damages;

        DamagesListAdapter(SpellDialogFragment context, ArrayList<AttackDamageInfo> damages) {
            this.damages = damages;
            this.context = context;
        }

        @NonNull
        @Override
        public DamageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.damage_row, parent, false);
            return new DamageViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(@NonNull DamageViewHolder holder, int position) {
            AttackDamageInfo row = damages.get(position);
            holder.bind(context, this, row);
        }

        @Override
        public int getItemCount() {
            return damages.size();
        }
    }
}

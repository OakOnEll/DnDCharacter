package com.oakonell.dndcharacter.views.character.item;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.DamageType;
import com.oakonell.dndcharacter.model.character.item.CharacterWeapon;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.model.item.CreateCharacterWeaponVisitor;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.model.item.ItemType;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.NoDefaultSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Rob on 3/18/2016.
 */
public class CharacterWeaponEditDialogFragment extends AbstractCharacterItemEditDialogFragment<CharacterWeapon> {
    private RecyclerView damages;
    private CheckBox versatile;
    private RecyclerView versatile_damages;
    private CheckBox ranged;
    private EditText rangeText;
    private CheckBox thrown;
    private CheckBox loading;
    private CheckBox ammunition;
    private EditText ammunition_name;
    private CheckBox finesse;
    private CheckBox two_handed;
    private CheckBox reach;
    private CheckBox heavy;
    private CheckBox light;
    private CheckBox special;
    private EditText special_comment;
    private DamagesAdapter damagesAdapter;
    private DamagesAdapter versatileDamagesAdapter;

    @NonNull
    public static CharacterWeaponEditDialogFragment createAddDialog() {
        CharacterWeaponEditDialogFragment newMe = new CharacterWeaponEditDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ADD, true);
        newMe.setArguments(args);

        return newMe;
    }

    @NonNull
    public static CharacterWeaponEditDialogFragment createDialog(Character character, CharacterWeapon item) {
        CharacterWeaponEditDialogFragment newMe = new CharacterWeaponEditDialogFragment();

        // TODO encode which item- nameText and index? just index...
        long id = item.getId();
        Bundle args = new Bundle();
        args.putLong(ID, id);
        newMe.setArguments(args);

        return newMe;
    }


    public View onCreateTheView(@NonNull LayoutInflater inflater, final ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.character_weapon_edit_dialog, container);

        damages = (RecyclerView) view.findViewById(R.id.damages);

        versatile = (CheckBox) view.findViewById(R.id.versatile);
        versatile_damages = (RecyclerView) view.findViewById(R.id.versatile_damages);

        //-----
        damagesAdapter = new DamagesAdapter(this, new ArrayList<CharacterWeapon.DamageFormula>());
        damages.setAdapter(damagesAdapter);

        damages.setHasFixedSize(false);
        damages.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        damages.addItemDecoration(itemDecoration);


        // ---------
        versatileDamagesAdapter = new DamagesAdapter(this, new ArrayList<CharacterWeapon.DamageFormula>());
        versatile_damages.setAdapter(versatileDamagesAdapter);

        versatile_damages.setHasFixedSize(false);
        versatile_damages.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        versatile_damages.addItemDecoration(itemDecoration);

        versatile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateVersatileViews(isChecked);
            }
        });
        //----

        ranged = (CheckBox) view.findViewById(R.id.ranged);
        rangeText = (EditText) view.findViewById(R.id.range);
        thrown = (CheckBox) view.findViewById(R.id.thrown);
        loading = (CheckBox) view.findViewById(R.id.loading);
        ammunition = (CheckBox) view.findViewById(R.id.ammunition);
        ammunition_name = (EditText) view.findViewById(R.id.ammunition_name);

        ranged.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateRangedViews(isChecked);
            }
        });

        ammunition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ammunition_name.setEnabled(isChecked && ranged.isChecked());
            }
        });

        finesse = (CheckBox) view.findViewById(R.id.finesse);
        two_handed = (CheckBox) view.findViewById(R.id.two_handed);
        reach = (CheckBox) view.findViewById(R.id.reach);

        heavy = (CheckBox) view.findViewById(R.id.heavy);
        light = (CheckBox) view.findViewById(R.id.light);
        heavy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    light.setChecked(false);
                }
            }
        });
        light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    heavy.setChecked(false);
                }
            }
        });

        special = (CheckBox) view.findViewById(R.id.special);
        special_comment = (EditText) view.findViewById(R.id.special_comment);

        special.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                special_comment.setEnabled(isChecked);
            }
        });

        afterCreateView(view);

        return view;
    }

    protected void updateVersatileViews(boolean isChecked) {
        if (isChecked) {
            versatile_damages.setVisibility(View.VISIBLE);
        } else {
            versatile_damages.setVisibility(View.GONE);
        }
    }

    protected void updateRangedViews(boolean isChecked) {
        rangeText.setEnabled(isChecked);
        thrown.setEnabled(isChecked);
        loading.setEnabled(isChecked);
        ammunition.setEnabled(isChecked);
        ammunition_name.setEnabled(isChecked && ammunition.isChecked());
    }


    @Override
    protected String getTitle() {
        Bundle args = getArguments();
        if (args.getBoolean(ADD, false)) {
            return getString(R.string.add_weapon_title);
        }
        return getString(R.string.edit_weapon_title);
    }


    @Override
    protected void addItem(CharacterWeapon item) {
        getCharacter().addWeapon(item);
    }

    @Override
    protected ItemType getItemType() {
        return ItemType.WEAPON;
    }

    @NonNull
    @Override
    protected CharacterWeapon newCharacterItem() {
        return new CharacterWeapon();
    }

    protected void updateItem(ItemRow itemRow) {
        item = CreateCharacterWeaponVisitor.createWeapon(getMainActivity(), itemRow, null);
        item.setName(itemRow.getName());
    }

    @NonNull
    protected SelectItemDialogFragment createSelectItemDialogFragment() {
        return SelectItemDialogFragment.createDialog(new SelectItemDialogFragment.ItemSelectedListener() {
            @Override
            public boolean itemSelected(long id) {
                ItemRow itemRow = ItemRow.load(ItemRow.class, id);
                updateItem(itemRow);
                updateViewsFromItem();
                return true;
            }
        }, ItemType.WEAPON, getCharacter().deriveToolProficiencies(ProficiencyType.WEAPON));
    }

    @Override
    protected void updateViewsFromItem() {
        super.updateViewsFromItem();

        damagesAdapter.damages.clear();
        damagesAdapter.setDamages(item.getDamages());

        versatile.setChecked(item.isVersatile());
        versatileDamagesAdapter.setDamages(item.getVersatileDamages());

        updateVersatileViews(item.isVersatile());

        ranged.setChecked(item.isRanged());
        rangeText.setText(item.getRange());
        thrown.setChecked(item.isThrown());
        loading.setChecked(item.isLoading());
        ammunition.setChecked(item.usesAmmunition());
        ammunition_name.setText(item.usesAmmunition() ? item.getAmmunition() : "");
        updateRangedViews(item.isRanged());

        finesse.setChecked(item.isFinesse());
        two_handed.setChecked(item.isTwoHanded());
        reach.setChecked(item.usesReach());

        heavy.setChecked(item.isHeavy());
        light.setChecked(item.isLight());

        special.setChecked(item.isSpecial());
        special_comment.setText(item.isSpecial() ? item.specialComment() : "");
        special_comment.setEnabled(item.isSpecial());
    }

    @Override
    protected void setItemProperties(CharacterWeapon item) {
        super.setItemProperties(item);
        item.getDamages().clear();
        item.getDamages().addAll(damagesAdapter.damages);

        item.setIsVersatile(versatile.isChecked());
        item.getVersatileDamages().clear();
        item.getVersatileDamages().addAll(versatileDamagesAdapter.damages);

        item.setIsRanged(ranged.isChecked());
        if (ranged.isChecked()) {
            item.setRange(rangeText.getText().toString());
            item.setIsThrown(thrown.isChecked());
            item.setIsLoading(loading.isChecked());
            item.setUsesAmmunition(ammunition.isChecked());
            if (ammunition.isChecked()) {
                item.setAmmunition(ammunition_name.getText().toString());
            } else {
                item.setAmmunition(null);
            }
        } else {
            item.setRange("");
            item.setIsThrown(false);
            item.setIsLoading(false);
            item.setUsesAmmunition(false);
            item.setAmmunition(null);
        }

        item.setIsFinesse(finesse.isChecked());
        item.setIsTwoHanded(two_handed.isChecked());
        item.setIsReach(reach.isChecked());

        item.setIsHeavy(heavy.isChecked());
        item.setIsLight(light.isChecked());

        item.setIsSpecial(special.isChecked());
        item.setSpecialComment(special.isChecked() ? special_comment.getText().toString() : null);
    }

    @Override
    protected boolean validate() {
        boolean valid = true;
        // TODO validate
        // validate formulas for damages/versatile damages
        int[] numDamagesResult = new int[]{0};
        boolean damagesValid = validateDamages(damagesAdapter.damages, damages, numDamagesResult);
        valid = damagesValid && numDamagesResult[0] > 0;

        if (versatile.isChecked()) {
            numDamagesResult = new int[]{0};
            damagesValid = validateDamages(versatileDamagesAdapter.damages, versatile_damages, numDamagesResult);
            valid = damagesValid && numDamagesResult[0] > 0;
        }


        return super.validate() && valid;
    }

    private boolean validateDamages(List<CharacterWeapon.DamageFormula> damagesList, RecyclerView recyclerView, int[] numDamagesResult) {
        boolean valid = true;
        int index;
        index = -1;
        for (CharacterWeapon.DamageFormula each : damagesList) {
            index++;
            final String formula = each.getDamageFormula();
            DamageType type = each.getType();
            if (formula == null || formula.trim().length() == 0) {
                continue;
            }
            numDamagesResult[0]++;
            final DamagesViewHolder childDamage = (DamagesViewHolder) recyclerView.findViewHolderForAdapterPosition(index);
            if (type == null) {
                recyclerView.scrollToPosition(index);
                if (childDamage != null) {
                    childDamage.damage.setError(getString(R.string.damage_type_required));
                    Animation shake = AnimationUtils.loadAnimation(getMainActivity(), R.anim.shake);
                    childDamage.damage.startAnimation(shake);
                    valid = false;
                }
            }
            try {
                getCharacter().evaluateFormula(formula, null);
            } catch (Exception e) {
                Log.e("CharacterWeaponEdit", "Error parsing weapon damage '" + formula + "'", e);
                versatile_damages.scrollToPosition(index);
                if (childDamage != null) {
                    childDamage.damage.setError(getString(R.string.enter_valid_formula));
                    Animation shake = AnimationUtils.loadAnimation(getMainActivity(), R.anim.shake);
                    childDamage.damage.startAnimation(shake);
                    valid = false;
                }
            }
        }

        if (numDamagesResult[0] == 0) {
            // TODO recyclerView.setError(getString(R.string.enter_a_damage));
            Animation shake = AnimationUtils.loadAnimation(recyclerView.getContext(), R.anim.shake);
            recyclerView.startAnimation(shake);
        }


        return valid;
    }


    @Override
    public void onCharacterLoaded(@NonNull Character character) {


        // this needs to go after the adapters are created, so the updateViewsFromItem can access them..
        super.onCharacterLoaded(character);

    }


    private static class DamagesAdapter extends RecyclerView.Adapter<DamagesViewHolder> {
        private List<CharacterWeapon.DamageFormula> damages;
        private final CharacterWeaponEditDialogFragment context;

        public DamagesAdapter(CharacterWeaponEditDialogFragment context, @NonNull List<CharacterWeapon.DamageFormula> damages) {
            this.context = context;
            this.damages = damages;
            if (damages.isEmpty()) {
                // need at least one..
                damages.add(new CharacterWeapon.DamageFormula());
            }
        }

//
//        public void reloadList(@NonNull List<CharacterWeapon.DamageFormula> damages) {
//            // TODO don't throw away user entered data?
//            this.damages = damages;
//            notifyDataSetChanged();
//        }

        public CharacterWeapon.DamageFormula getItem(int position) {
            return damages.get(position);
        }


        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return damages.size();
        }

        @NonNull
        @Override
        public DamagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context.getMainActivity(), R.layout.weapon_damage_list_item, null);
            DamagesViewHolder viewHolder = new DamagesViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final DamagesViewHolder viewHolder, int position) {
            final CharacterWeapon.DamageFormula row = getItem(position);
            viewHolder.bind(context, this, row);
        }


        public void setDamages(List<CharacterWeapon.DamageFormula> damages) {
            this.damages.clear();
            this.damages.addAll(damages);
            if (this.damages.isEmpty()) {
                // need at least one..
                this.damages.add(new CharacterWeapon.DamageFormula());
            }
            notifyDataSetChanged();
        }
    }

    static class DamagesViewHolder extends BindableComponentViewHolder<CharacterWeapon.DamageFormula, CharacterWeaponEditDialogFragment, DamagesAdapter> {
        private final EditText damage;
        private final Spinner damageType;
        private final View add_another;

        public DamagesViewHolder(@NonNull View itemView) {
            super(itemView);
            damage = (EditText) itemView.findViewById(R.id.damage);
            damageType = (Spinner) itemView.findViewById(R.id.damage_type);
            add_another = itemView.findViewById(R.id.add_another);


        }

        @Override
        public void bind(final CharacterWeaponEditDialogFragment context, final DamagesAdapter adapter, final CharacterWeapon.DamageFormula info) {
            // TODO link the edited text to the model- be careful of text listener from possible previous row
            damage.setOnFocusChangeListener(null);
            damage.setText(info.getDamageFormula());

            damage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) return;

                    String text = damage.getText().toString().trim();
                    if (text.length() > 0) {
                        try {
                            context.getCharacter().evaluateFormula(text, null);
                        } catch (Exception e) {
                            Log.e("CharacterWeaponEdit", "Error parsing weapon damage '" + text + "'", e);
                            damage.setError(e.getMessage());
                            Animation shake = AnimationUtils.loadAnimation(damage.getContext(), R.anim.shake);
                            damage.startAnimation(shake);
                            return;
                        }
                        info.setDamageFormula(text);
                    }
                }
            });
            add_another.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.damages.add(new CharacterWeapon.DamageFormula());
                    adapter.notifyDataSetChanged();
                }
            });

            if (damageType.getAdapter() == null) {
                List<String> list = new ArrayList<>();
                for (DamageType each : DamageType.values()) {
                    list.add(context.getString(each.getStringResId()));
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context.getMainActivity(), android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                damageType.setAdapter(dataAdapter);

                float minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (damageType.getPrompt().length() + 2) * NoDefaultSpinner.SPINNER_TEXT_SP, damageType.getResources().getDisplayMetrics());
                damageType.setMinimumWidth((int) minWidth);
            }
            damageType.setOnItemSelectedListener(null);
            if (info.getType() != null) {
                damageType.setSelection(Arrays.asList(DamageType.values()).indexOf(info.getType()));
            } else {
                damageType.setSelection(-1);
            }
            damageType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    info.setType(DamageType.values()[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO handle rotation/save state
    }

    @Override
    protected CharacterWeapon getItemById(long id) {
        return getCharacter().getWeaponById(id);
    }
}

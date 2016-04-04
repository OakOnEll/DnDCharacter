package com.oakonell.dndcharacter.views.character;


import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.DamageType;
import com.oakonell.dndcharacter.model.character.VulnerabilityType;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.ItemTouchHelperAdapter;
import com.oakonell.dndcharacter.views.NoDefaultSpinner;
import com.oakonell.dndcharacter.views.SimpleItemTouchHelperCallback;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 10/28/2015.
 */
public class HitPointDiaogFragment extends AbstractCharacterDialogFragment {
    private static final int UNDO_DELAY = 5000;
    public static final String HP_ROWS = "hpRows";
    private EditText hpText;
    private RadioButton damage;
    private RadioButton heal;
    private RadioButton tempHP;
    private Spinner type;
    private Spinner vulnerability_type;

    private TextView start_hp;
    private TextView final_hp;
    private Button add_another;
    private RecyclerView hpListView;

    @Nullable
    private HitPointsAdapter hpListAdapter;
    @Nullable
    private ArrayList<HpRow> hpList = new ArrayList<>();

    @NonNull
    public static HitPointDiaogFragment createDialog() {
        return new HitPointDiaogFragment();
    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        super.onCharacterChanged(character);
        updateView();
    }

    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hit_point_dialog, container);
        hpText = (EditText) view.findViewById(R.id.hp);

        damage = (RadioButton) view.findViewById(R.id.damage_radio);

        type = (Spinner) view.findViewById(R.id.damage_type);
        float minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (type.getPrompt().length() + 2) * NoDefaultSpinner.SPINNER_TEXT_SP, type.getResources().getDisplayMetrics());
        type.setMinimumWidth((int) minWidth);

        vulnerability_type = (Spinner) view.findViewById(R.id.vulnerability_type);
        minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (vulnerability_type.getPrompt().length() + 2) * NoDefaultSpinner.SPINNER_TEXT_SP, vulnerability_type.getResources().getDisplayMetrics());
        vulnerability_type.setMinimumWidth((int) minWidth);


        add_another = (Button) view.findViewById(R.id.add_another);

        heal = (RadioButton) view.findViewById(R.id.heal_radio);
        tempHP = (RadioButton) view.findViewById(R.id.temp_hp_radio);

        Button add = (Button) view.findViewById(R.id.add);
        Button subtract = (Button) view.findViewById(R.id.subtract);

        start_hp = (TextView) view.findViewById(R.id.start_hp);
        final_hp = (TextView) view.findViewById(R.id.final_hp);

        hpListView = (RecyclerView) view.findViewById(R.id.hp_list);


        List<String> list = new ArrayList<>();
        for (DamageType each : DamageType.values()) {
            list.add(getString(each.getStringResId()));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(dataAdapter);


        List<String> vulnerabilityList = new ArrayList<>();
        for (VulnerabilityType each : VulnerabilityType.values()) {
            vulnerabilityList.add(getString(each.getStringResId()));
        }
        ArrayAdapter<String> susceptibleListDataAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, vulnerabilityList);
        susceptibleListDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vulnerability_type.setAdapter(susceptibleListDataAdapter);


        View.OnClickListener radioListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == damage && damage.isChecked()) {
                    heal.setChecked(false);
                    tempHP.setChecked(false);
                    type.setEnabled(true);
                    vulnerability_type.setEnabled(true);
                }
                if (v == heal && heal.isChecked()) {
                    damage.setChecked(false);
                    tempHP.setChecked((false));
                    type.setEnabled(false);
                    vulnerability_type.setEnabled(false);
                }
                if (v == tempHP && tempHP.isChecked()) {
                    damage.setChecked(false);
                    heal.setChecked((false));
                    type.setEnabled(false);
                    vulnerability_type.setEnabled(false);
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


        if (savedInstanceState != null) {
            hpList = savedInstanceState.getParcelableArrayList(HP_ROWS);
        }

        hpListAdapter = new HitPointsAdapter(this, hpList);
        hpListView.setAdapter(hpListAdapter);

        hpListView.setHasFixedSize(false);
        hpListView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        hpListView.addItemDecoration(itemDecoration);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(hpListAdapter, false, true);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(hpListView);

        hpListAdapter.registerAdapterDataObserver(new AdapterDataObserver() {
            @Override
            public void onChanged() {
                updateView();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                updateView();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                updateView();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                updateView();
            }
        });

        add_another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HpType hpType = getHpType();

                DamageType damageType = null;
                VulnerabilityType vulnerabilityType = null;
                if (hpType == HpType.DAMAGE) {
                    int typeIndex = type.getSelectedItemPosition();
                    if (typeIndex >= 0) {
                        damageType = DamageType.values()[typeIndex];
                    }

                    int vulnerabilityTypeIndex = vulnerability_type.getSelectedItemPosition();
                    if (vulnerabilityTypeIndex >= 0) {
                        vulnerabilityType = VulnerabilityType.values()[vulnerabilityTypeIndex];
                    }

                }


                HpRow row = new HpRow(hpType, damageType, vulnerabilityType, getHp());
                hpList.add(row);
                hpListAdapter.notifyDataSetChanged();

                hpText.setText("");
            }
        });

        return view;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.hit_points_title);
    }

    @Nullable
    private HpType getHpType() {
        HpType hpType = null;
        if (damage.isChecked()) {
            hpType = HpType.DAMAGE;
        } else if (heal.isChecked()) {
            hpType = HpType.HEAL;
        } else if (tempHP.isChecked()) {
            hpType = HpType.TEMP_HP;
        }
        return hpType;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(HP_ROWS, hpList);
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);

        updateView();
    }

    @NonNull
    @Override
    protected Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.DICE_ROLL));
        filter.add(new FeatureContextArgument(FeatureContext.HIT_POINTS));
        return filter;
    }

    private void updateView() {
        Character character = getCharacter();
        int currentHp = character.getHP();
        int tempHp = character.getTempHp();
        String startHptext = getString(R.string.fraction_d_slash_d, currentHp, character.getMaxHP());
        if (character.getTempHp() > 0) {
            startHptext += " + " + tempHp;
        }
        start_hp.setText(startHptext);
        int hpToApply = getHp();

        int finalHp = character.getHP();

        HpType hpType = getHpType();
        if (hpType == HpType.DAMAGE) {
            // TODO extract this to avoid redundancy
            tempHp -= hpToApply;
            finalHp = Math.max(finalHp + tempHp, 0);
            tempHp = Math.max(0, tempHp);
        } else if (hpType == HpType.HEAL) {
            finalHp = Math.min(hpToApply + finalHp, character.getMaxHP() + character.getTempHp());
        } else if (hpType == HpType.TEMP_HP) {
            // TODO extract this to avoid redundancy
            tempHp = tempHp + hpToApply;
        }

        for (HpRow each : hpList) {
            if (each.beingDeleted()) continue;

            hpType = each.hpType;
            hpToApply = each.hp;
            if (hpType == HpType.DAMAGE) {
                // TODO extract this to avoid redundancy
                tempHp -= hpToApply;
                finalHp = Math.max(finalHp + tempHp, 0);
                tempHp = Math.max(0, tempHp);
            } else if (hpType == HpType.HEAL) {
                finalHp = Math.min(hpToApply + finalHp, character.getMaxHP() + character.getTempHp());
            } else if (hpType == HpType.TEMP_HP) {
                // TODO extract this to avoid redundancy
                tempHp = tempHp + hpToApply;
            }
        }

        String finalHpText = getString(R.string.fraction_d_slash_d, finalHp, character.getMaxHP());
        if (tempHp > 0) {
            finalHpText += " + " + tempHp;
        }
        final_hp.setText(finalHpText);
        conditionallyEnableDone();
    }

    @Override
    protected boolean onDone() {
        Character character = getCharacter();
        HpType hpType = getHpType();

        if (hpType == HpType.DAMAGE) {
            character.damage(getHp());
        } else if (hpType == HpType.HEAL) {
            character.heal(getHp());
        } else if (hpType == HpType.TEMP_HP) {
            character.addTempHp(getHp());
        }

        for (HpRow each : hpList) {
            if (each.beingDeleted()) continue;
            hpType = each.hpType;
            int hpToApply = each.hp;
            if (hpType == HpType.DAMAGE) {
                character.damage(hpToApply);
            } else if (hpType == HpType.HEAL) {
                character.heal(hpToApply);
            } else if (hpType == HpType.TEMP_HP) {
                character.addTempHp(hpToApply);
            }
        }

        return super.onDone();
    }

    private void addHp(int amount) {
        int hp = getHp();
        hp = Math.max(0, hp + amount);
        String hpString = hp + "";
        hpText.setText(hpString);
        hpText.setSelection(hpString.length());
        conditionallyEnableDone();
    }

    private int getHp() {
        int hp = 0;
        String hpString = hpText.getText().toString();
        if (!(hpString.length() == 0)) {
            hp = Integer.parseInt(hpString);
        }
        return hp;
    }

    protected void conditionallyEnableDone() {
        boolean canApply = (damage.isChecked() || heal.isChecked() || tempHP.isChecked()) && getHp() > 0;
        enableDone(canApply || !hpList.isEmpty());
        add_another.setEnabled(canApply);
    }

    public enum HpType {
        DAMAGE, HEAL, TEMP_HP;

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public static class HpRow implements Parcelable {
        // Method to recreate a HpRow from a Parcel
        @NonNull
        public static Creator<HpRow> CREATOR = new Creator<HpRow>() {

            @NonNull
            @Override
            public HpRow createFromParcel(@NonNull Parcel source) {
                return new HpRow(source);
            }

            @NonNull
            @Override
            public HpRow[] newArray(int size) {
                return new HpRow[size];
            }

        };
        final HpType hpType;
        @Nullable
        final DamageType damageType;
        @Nullable
        final VulnerabilityType vulnerabilityType;
        final int hp;
        @Nullable
        Long deleteRequestedTime;

        public HpRow(HpType hpType, @Nullable DamageType damageType, @Nullable VulnerabilityType vulnerabilityType, int hp) {
            this.hpType = hpType;
            this.damageType = damageType;
            this.hp = hp;
            this.vulnerabilityType = vulnerabilityType;
        }

        HpRow(@NonNull Parcel parcel) {
            byte b = parcel.readByte();
            beingDeleted(b != 0);

            hpType = HpType.values()[parcel.readInt()];
            int damageTypeIndex = parcel.readInt();
            if (damageTypeIndex >= 0) {
                damageType = DamageType.values()[damageTypeIndex];
            } else {
                damageType = null;
            }

            int vulnerabilityTypeIndex = parcel.readInt();
            if (vulnerabilityTypeIndex >= 0) {
                vulnerabilityType = VulnerabilityType.values()[vulnerabilityTypeIndex];
            } else {
                vulnerabilityType = null;
            }

            hp = parcel.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeByte((byte) (deleteRequestedTime != null ? 1 : 0));
            dest.writeInt(hpType.ordinal());
            if (damageType != null) {
                dest.writeInt(damageType.ordinal());
            } else {
                dest.writeInt(-1);
            }
            if (vulnerabilityType != null) {
                dest.writeInt(vulnerabilityType.ordinal());
            } else {
                dest.writeInt(-1);
            }
            dest.writeInt(hp);
        }

        public boolean beingDeleted() {
            return deleteRequestedTime != null;
        }

        public void beingDeleted(boolean delete) {
            if (delete) {
                deleteRequestedTime = System.currentTimeMillis();
            } else {
                deleteRequestedTime = null;
            }
        }

        @Nullable
        public Long deletedTime() {
            return deleteRequestedTime;
        }
    }

    public abstract static class AbstractHPViewHolder extends BindableComponentViewHolder<HpRow, HitPointDiaogFragment, HitPointsAdapter> {

        public AbstractHPViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }

    public static class DeleteHitPointRowViewHolder extends HitPointRowViewHolder {
        @NonNull
        private final Button undo;

        public DeleteHitPointRowViewHolder(@NonNull View itemView) {
            super(itemView);
            undo = (Button) itemView.findViewById(R.id.undo);
        }

        @Override
        public void bind(@NonNull final HitPointDiaogFragment context, @NonNull final HitPointsAdapter adapter, @NonNull final HpRow hpRow) {
            super.bind(context, adapter, hpRow);

            undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hpRow.beingDeleted(false);
                    adapter.notifyItemChanged(getAdapterPosition());
                }
            });
        }

    }

    public static class HitPointRowViewHolder extends AbstractHPViewHolder {
        @NonNull
        private final TextView type;
        @NonNull
        private final TextView vulnerableType;
        @NonNull
        private final TextView amount;

        public HitPointRowViewHolder(@NonNull View itemView) {
            super(itemView);
            type = (TextView) itemView.findViewById(R.id.type);
            amount = (TextView) itemView.findViewById(R.id.amount);
            vulnerableType = (TextView) itemView.findViewById(R.id.vulnerability);
        }

        @Override
        public void bind(@NonNull HitPointDiaogFragment context, HitPointsAdapter adapter, @NonNull HpRow hpRow) {
            if (hpRow.hpType == HpType.DAMAGE) {
                if (hpRow.damageType == null) {
                    type.setText(R.string.damage_label);
                } else {
                    type.setText(context.getString(hpRow.damageType.getStringResId()));
                }
                if (hpRow.vulnerabilityType == null) {
                    vulnerableType.setText("");
                } else {
                    vulnerableType.setText(context.getString(hpRow.vulnerabilityType.getStringResId()));
                }
            } else if (hpRow.hpType == HpType.HEAL) {
                type.setText(R.string.heal);
            } else if (hpRow.hpType == HpType.TEMP_HP) {
                type.setText(R.string.temp_hp);
            }
            amount.setText(NumberUtils.formatNumber(hpRow.hp));
        }

    }

    public static class HitPointsAdapter extends RecyclerView.Adapter<AbstractHPViewHolder> implements ItemTouchHelperAdapter {
        private final List<HpRow> hpRows;
        private final HitPointDiaogFragment fragment;

        HitPointsAdapter(HitPointDiaogFragment fragment, List<HpRow> hpRows) {
            this.hpRows = hpRows;
            this.fragment = fragment;
        }

        public int getItemViewType(int position) {
            final HpRow hpRow = hpRows.get(position);
            if (hpRow.beingDeleted()) {
                return 1;
            }
            return 0;
        }

        @NonNull
        @Override
        public AbstractHPViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hp_row_delete, parent, false);
                return new DeleteHitPointRowViewHolder(newView);
            }
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hp_row, parent, false);
            return new HitPointRowViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(@NonNull AbstractHPViewHolder holder, int position) {
            holder.bind(fragment, this, hpRows.get(position));
        }

        @Override
        public int getItemCount() {
            return hpRows.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            // not implemented
            return false;
        }

        @Override
        public void onItemDismiss(final int position) {
            final HpRow hpRow = hpRows.get(position);
            if (hpRow.beingDeleted()) {
                // actually delete the record, now
                hpRows.remove(hpRow);
                notifyItemRemoved(position);
            }

            hpRow.beingDeleted(true);
            notifyItemChanged(position);

            fragment.hpListView.postDelayed(new Runnable() {
                public void run() {
                    // may have been deleted, undone, and then redeleted
                    Long deletedTime = hpRow.deletedTime();
                    if (deletedTime == null) return;
                    if (System.currentTimeMillis() - deletedTime >= UNDO_DELAY) {
                        // actually delete the record, now
                        hpRows.remove(hpRow);
                        int newIndex = hpRows.indexOf(hpRow);
                        if (fragment.getMainActivity() != null) {
                            notifyItemRemoved(newIndex);
                        }
                    }
                }
            }, UNDO_DELAY);

        }
    }

}

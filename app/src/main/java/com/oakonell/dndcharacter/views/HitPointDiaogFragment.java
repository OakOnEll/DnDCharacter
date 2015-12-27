package com.oakonell.dndcharacter.views;


import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
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
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.DamageType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 10/28/2015.
 */
public class HitPointDiaogFragment extends AbstractCharacterDialogFragment {
    private static final int UNDO_DELAY = 5000;
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
    private Button add_another;
    private RecyclerView hpListView;
    private HitPointsAdapter hpListAdapter;

    public enum HpType {
        DAMAGE, HEAL, TEMP_HP
    }

    public static class HpRow implements Parcelable {
        Long deleteRequestedTime;
        final HpType hpType;
        final DamageType damageType;
        final int hp;

        public HpRow(HpType hpType, DamageType damageType, int hp) {
            this.hpType = hpType;
            this.damageType = damageType;
            this.hp = hp;
        }

        HpRow(Parcel parcel) {
            byte b = parcel.readByte();
            beingDeleted(b != 0);

            hpType = HpType.values()[parcel.readInt()];
            int damageTypeIndex = parcel.readInt();
            if (damageTypeIndex >= 0) {
                damageType = DamageType.values()[damageTypeIndex];
            } else {
                damageType = null;
            }
            hp = parcel.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte((byte) (deleteRequestedTime != null ? 1 : 0));
            dest.writeInt(hpType.ordinal());
            if (damageType != null) {
                dest.writeInt(damageType.ordinal());
            } else {
                dest.writeInt(-1);
            }
            dest.writeInt(hp);
        }

        // Method to recreate a HpRow from a Parcel
        public static Creator<HpRow> CREATOR = new Creator<HpRow>() {

            @Override
            public HpRow createFromParcel(Parcel source) {
                return new HpRow(source);
            }

            @Override
            public HpRow[] newArray(int size) {
                return new HpRow[size];
            }

        };

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

        public Long deletedTime() {
            return deleteRequestedTime;
        }
    }

    private ArrayList<HpRow> hpList = new ArrayList<>();

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

        add_another = (Button) view.findViewById(R.id.add_another);

        heal = (RadioButton) view.findViewById(R.id.heal_radio);
        tempHP = (RadioButton) view.findViewById(R.id.temp_hp_radio);

        add = (Button) view.findViewById(R.id.add);
        subtract = (Button) view.findViewById(R.id.subtract);

        start_hp = (TextView) view.findViewById(R.id.start_hp);
        final_hp = (TextView) view.findViewById(R.id.final_hp);

        cancel = (Button) view.findViewById(R.id.cancel);

        hpListView = (RecyclerView) view.findViewById(R.id.hp_list);

        List<String> list = new ArrayList<>();
        for (DamageType each : DamageType.values()) {
            list.add(each.toString());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
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


        if (savedInstanceState != null) {
            hpList = savedInstanceState.getParcelableArrayList("hpRows");
        }

        hpListAdapter = new HitPointsAdapter(this, hpList);
        hpListView.setAdapter(hpListAdapter);

        hpListView.setHasFixedSize(false);
        hpListView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

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
                if (hpType == HpType.DAMAGE) {
                    int typeIndex = type.getSelectedItemPosition();
                    if (typeIndex >= 0) {
                        damageType = DamageType.values()[typeIndex];
                    }
                }


                HpRow row = new HpRow(hpType, damageType, getHp());
                hpList.add(row);
                hpListAdapter.notifyDataSetChanged();

                hpText.setText("");
            }
        });

        return view;
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("hpRows", hpList);
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

    public static class BindableViewHolder extends RecyclerView.ViewHolder {

        public BindableViewHolder(View itemView) {
            super(itemView);
        }

        public void bindTo(HitPointDiaogFragment context, RecyclerView.Adapter adapter, HpRow hpRow) {

        }
    }

    public static class DeleteHitPointRowViewHolder extends HitPointRowViewHolder {
        private final Button undo;

        public DeleteHitPointRowViewHolder(View itemView) {
            super(itemView);
            undo = (Button) itemView.findViewById(R.id.undo);
        }

        public void bindTo(final HitPointDiaogFragment context, final RecyclerView.Adapter adapter, final HpRow hpRow) {
            super.bindTo(context, adapter, hpRow);

            undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hpRow.beingDeleted(false);
                    adapter.notifyItemChanged(getAdapterPosition());
                }
            });
        }

    }

    public static class HitPointRowViewHolder extends BindableViewHolder {
        private final TextView type;
        private final TextView amount;

        public HitPointRowViewHolder(View itemView) {
            super(itemView);
            type = (TextView) itemView.findViewById(R.id.type);
            amount = (TextView) itemView.findViewById(R.id.amount);
        }

        public void bindTo(HitPointDiaogFragment context, RecyclerView.Adapter adapter, HpRow hpRow) {
            if (hpRow.hpType == HpType.DAMAGE) {
                if (hpRow.damageType == null) {
                    type.setText("Damage");
                } else {
                    type.setText(hpRow.damageType.toString());
                }
            } else if (hpRow.hpType == HpType.HEAL) {
                type.setText("Heal");
            } else if (hpRow.hpType == HpType.TEMP_HP) {
                type.setText("Temp HP");
            }
            amount.setText(hpRow.hp + "");
        }

    }

    public static class HitPointsAdapter extends RecyclerView.Adapter<BindableViewHolder> implements ItemTouchHelperAdapter {
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

        @Override
        public BindableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hp_row_delete, parent, false);
                return new DeleteHitPointRowViewHolder(newView);
            }
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hp_row, parent, false);
            return new HitPointRowViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(BindableViewHolder holder, int position) {
            holder.bindTo(fragment, this, hpRows.get(position));
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

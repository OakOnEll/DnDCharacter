package com.oakonell.dndcharacter.views;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.oakonell.dndcharacter.MainActivity;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterArmor;
import com.oakonell.expression.context.SimpleVariableContext;

import java.util.List;

/**
 * Created by Rob on 12/21/2015.
 */
public class ArmorClassDialogFragment extends AbstractCharacterDialogFragment {

    private TextView acText;
    private RootAcAdapter rootAcAdapter;
    private ModifyingAcAdapter modifyingAcAdapter;
    private RecyclerView rootList;
    private RecyclerView modList;

    public static ArmorClassDialogFragment createDialog() {
        ArmorClassDialogFragment newMe = new ArmorClassDialogFragment();
        return newMe;
    }

    public View onCreateTheView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.armor_class_dialog, container);

        acText = (TextView) view.findViewById(R.id.ac);

        rootList = (RecyclerView) view.findViewById(R.id.root_ac_list);
        modList = (RecyclerView) view.findViewById(R.id.mod_ac_list);


        return view;
    }

    @Override
    protected void onDone() {
        super.onDone();
        for (Character.ArmorClassWithSource each : rootAcAdapter.list) {
            if (!each.isArmor()) continue;
            ((CharacterArmor) each.getSource()).setEquipped(each.isEquipped());
        }
        for (Character.ArmorClassWithSource each : modifyingAcAdapter.list) {
            if (!each.isArmor()) continue;
            ((CharacterArmor) each.getSource()).setEquipped(each.isEquipped());
        }
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);

        rootAcAdapter = new RootAcAdapter(this, character);
        rootList.setAdapter(rootAcAdapter);
        rootList.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rootList.setHasFixedSize(false);


        modifyingAcAdapter = new ModifyingAcAdapter(this, character);
        modList.setAdapter(modifyingAcAdapter);
        modList.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        modList.setHasFixedSize(false);

        rootAcAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                updateAC();
            }
        });
        modifyingAcAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                updateAC();
            }
        });

        updateAC();
    }

    private void updateAC() {
        int ac = 0;
        for (Character.ArmorClassWithSource each : rootAcAdapter.list) {
            if (each.isEquipped()) {
                ac = each.getValue();
            }
        }

        for (Character.ArmorClassWithSource each : modifyingAcAdapter.list) {
            if (each.isEquipped()) {
                ac += each.getValue();
            }
        }

        acText.setText(ac + "");

    }

    public static class RootAcViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBox;
        private final TextView name;
        private final TextView formula;
        private final TextView value;

        public RootAcViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            formula = (TextView) itemView.findViewById(R.id.formula);
            value = (TextView) itemView.findViewById(R.id.value);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }
    }

    public static class RootAcAdapter extends RecyclerView.Adapter<RootAcViewHolder> {
        private final Character character;
        List<Character.ArmorClassWithSource> list;
        ArmorClassDialogFragment fragment;

        RootAcAdapter(ArmorClassDialogFragment armorClassDialogFragment, Character character) {
            this.character = character;
            list = character.deriveRootAcs();
            this.fragment = armorClassDialogFragment;
        }

        @Override
        public RootAcViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.armor_class_root_item, parent, false);
            RootAcViewHolder holder = new RootAcViewHolder(newView);
            return holder;
        }

        @Override
        public void onBindViewHolder(RootAcViewHolder holder, final int position) {
            final Character.ArmorClassWithSource row = list.get(position);
            holder.name.setText(row.getSourceString());
            holder.formula.setText(row.getFormula());

            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(row.isEquipped());
            holder.checkBox.setEnabled(!row.isDisabled);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    row.setIsEquipped(isChecked);
                    Character.ArmorClassWithSource selected = null;
                    if (isChecked) {
                        if (!row.isArmor()) {
                            buttonView.setEnabled(false);
                        } else {
                            buttonView.setEnabled(true);
                        }
                        selected = row;
                        int index = -1;
                        for (Character.ArmorClassWithSource other : list) {
                            index++;
                            if (other == row) continue;
                            if (!other.isEquipped()) continue;
                            other.setIsEquipped(false);
                            other.isDisabled = false;
                            notifyItemChanged(index);
                        }
                    } else {
                        int index = -1;
                        for (Character.ArmorClassWithSource other : list) {
                            index++;
                            if (other == row) continue;
                            if (other.isDisabled) continue;
                            selected = other;
                            other.setIsEquipped(true);
                            other.isDisabled = true;
                            notifyItemChanged(index);
                            break;
                        }

                    }
                    fragment.updateModifyingRows(selected.isArmor());
                    fragment.updateAC();
                    // update other list items
                }
            });

            String formula = row.getFormula();
            int val = row.getValue();
            String stringVal = val + "";
            if (stringVal.equals(formula)) {
                holder.value.setText("");
            } else {
                holder.value.setText("=" + stringVal);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private void updateModifyingRows(boolean armor) {
        int position = -1;
        for (Character.ArmorClassWithSource each : modifyingAcAdapter.list) {
            position++;
            if (each.isArmor()) continue;

            String activeFormula = each.getSource().getActiveFormula();
            if (activeFormula == null) continue;
            SimpleVariableContext variableContext = new SimpleVariableContext();
            variableContext.setBoolean("armor", armor);
            boolean shouldEquip = modifyingAcAdapter.character.evaluateBooleanFormula(activeFormula, variableContext);
            if (each.isEquipped() != shouldEquip) {
                each.setIsEquipped(shouldEquip);
                modifyingAcAdapter.notifyItemChanged(position);
            }
        }
    }


    public static class ModifyingAcAdapter extends RecyclerView.Adapter<RootAcViewHolder> {
        private final Character character;
        List<Character.ArmorClassWithSource> list;
        ArmorClassDialogFragment fragment;

        ModifyingAcAdapter(ArmorClassDialogFragment armorClassDialogFragment, Character character) {
            this.character = character;
            list = character.deriveModifyingAcs();
            this.fragment = armorClassDialogFragment;
        }

        @Override
        public RootAcViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.armor_class_root_item, parent, false);
            RootAcViewHolder holder = new RootAcViewHolder(newView);
            return holder;
        }

        @Override
        public void onBindViewHolder(RootAcViewHolder holder, final int position) {
            final Character.ArmorClassWithSource row = list.get(position);
            holder.name.setText(row.getSourceString());
            holder.formula.setText(row.getFormula());

            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(row.isEquipped());
            holder.checkBox.setEnabled(!row.isDisabled);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    row.setIsEquipped(isChecked);
                    fragment.updateAC();
                    // update other list items, and calculate and update ac on this dialog
                }
            });

            String formula = row.getFormula();
            int val = row.getValue();
            String stringVal = val + "";
            if (stringVal.equals(formula)) {
                holder.value.setText("");
            } else {
                holder.value.setText("=" + stringVal);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

}




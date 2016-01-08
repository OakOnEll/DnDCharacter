package com.oakonell.dndcharacter.views;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.oakonell.dndcharacter.BindableComponentViewHolder;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterArmor;
import com.oakonell.expression.context.SimpleVariableContext;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 12/21/2015.
 */
public class ArmorClassDialogFragment extends AbstractCharacterDialogFragment {
    private TextView acText;
    private RecyclerView rootList;
    private RecyclerView modList;
    private ViewGroup modifiers_group;

    private RootAcAdapter rootAcAdapter;
    private ModifyingAcAdapter modifyingAcAdapter;
    private String baseArmorSaved;
    private ArrayList<String> modifyingArmorSaved;

    public static ArmorClassDialogFragment createDialog() {
        return new ArmorClassDialogFragment();
    }

    public View onCreateTheView(LayoutInflater inflater, final ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.armor_class_dialog, container);

        acText = (TextView) view.findViewById(R.id.ac);

        rootList = (RecyclerView) view.findViewById(R.id.root_ac_list);
        modList = (RecyclerView) view.findViewById(R.id.mod_ac_list);

        modifiers_group = (ViewGroup) view.findViewById(R.id.modifiers_group);


        if (savedInstanceState != null) {
            baseArmorSaved = savedInstanceState.getString("baseArmor");
            modifyingArmorSaved = savedInstanceState.getStringArrayList("modifyingArmor");
        }

        return view;
    }

    @Override
    protected String getTitle() {
        return "Armor Class";
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        for (Character.ArmorClassWithSource each : rootAcAdapter.list) {
            if (each.isEquipped()) {
                outState.putString("baseArmor", each.getSourceString());
                break;
            }
        }
        ArrayList<String> equippedBonuses = new ArrayList<>();
        for (Character.ArmorClassWithSource each : modifyingAcAdapter.list) {
            if (each.isEquipped()) {
                equippedBonuses.add(each.getSourceString());
            }
        }
        outState.putStringArrayList("modifyingArmor", equippedBonuses);
    }

    @Override
    protected boolean onDone() {
        for (Character.ArmorClassWithSource each : rootAcAdapter.list) {
            if (!each.isArmor()) continue;
            ((CharacterArmor) each.getSource()).setEquipped(each.isEquipped());
        }
        for (Character.ArmorClassWithSource each : modifyingAcAdapter.list) {
            if (!each.isArmor()) continue;
            ((CharacterArmor) each.getSource()).setEquipped(each.isEquipped());
        }
        return super.onDone();
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);

        rootAcAdapter = new RootAcAdapter(this, character);
        if (baseArmorSaved != null) {
            for (Character.ArmorClassWithSource each : rootAcAdapter.list) {
                if (each.getSourceString().equals(baseArmorSaved)) {
                    each.setIsEquipped(true);
                } else {
                    each.setIsEquipped(false);
                }
            }
            baseArmorSaved = null;
        }

        rootList.setAdapter(rootAcAdapter);
        rootList.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rootList.setHasFixedSize(false);


        modifyingAcAdapter = new ModifyingAcAdapter(this, character);
        if (modifyingArmorSaved != null) {
            for (Character.ArmorClassWithSource each : modifyingAcAdapter.list) {
                if (modifyingArmorSaved.contains(each.getSourceString())) {
                    each.setIsEquipped(true);
                } else {
                    each.setIsEquipped(false);
                }
            }
            modifyingArmorSaved = null;
        }
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

    @Override
    protected Set<FeatureContext> getContextFilter() {
        Set<FeatureContext> filter = new HashSet<>();
        filter.add(FeatureContext.ARMOR_CLASS);
        filter.add(FeatureContext.TO_HIT);
        return filter;
    }

    @Override
    public void onCharacterChanged(Character character) {
        super.onCharacterChanged(character);
        rootAcAdapter.reloadList(character);
        modifyingAcAdapter.reloadList(character);
        updateAC();
    }

    private void updateAC() {
        if (modifyingAcAdapter.list.isEmpty()) {
            modifiers_group.setVisibility(View.GONE);
        } else {
            modifiers_group.setVisibility(View.VISIBLE);
        }
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

    private void updateModifyingRows(boolean armor) {
        int position = -1;
        for (Character.ArmorClassWithSource each : modifyingAcAdapter.list) {
            position++;
            if (each.isArmor()) continue;

            String activeFormula = each.getSource().getActiveFormula();
            if (activeFormula == null) continue;
            SimpleVariableContext variableContext = new SimpleVariableContext();
            variableContext.setBoolean("armor", armor);
            boolean shouldEquip = getCharacter().evaluateBooleanFormula(activeFormula, variableContext);
            if (each.isEquipped() != shouldEquip) {
                each.setIsEquipped(shouldEquip);
                modifyingAcAdapter.notifyItemChanged(position);
            }
        }
    }

    public static class AcViewHolder extends BindableComponentViewHolder<Character.ArmorClassWithSource, ArmorClassDialogFragment, RootAcAdapter> {
        private final CheckBox checkBox;
        private final TextView name;
        private final TextView formula;
        private final TextView value;

        public AcViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            formula = (TextView) itemView.findViewById(R.id.formula);
            value = (TextView) itemView.findViewById(R.id.value);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }

        @Override
        public void bind(final ArmorClassDialogFragment context, final RootAcAdapter adapter, final Character.ArmorClassWithSource row) {
            name.setText(row.getSourceString());
            formula.setText(row.getFormula());
            final List<Character.ArmorClassWithSource> list = adapter.list;
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(row.isEquipped());
            checkBox.setEnabled(!row.isDisabled && list.size() > 1);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    row.setIsEquipped(isChecked);
                    Character.ArmorClassWithSource selected = null;
                    int selectedIndex = -1;
                    if (isChecked) {
                        selected = row;
                        int index = -1;
                        for (Character.ArmorClassWithSource other : list) {
                            index++;
                            if (other == row) {
                                selectedIndex = index;
                                continue;
                            }
                            if (!other.isEquipped()) continue;
                            other.setIsEquipped(false);
                            other.isDisabled = false;
                            adapter.notifyItemChanged(index);
                        }
                    } else {
                        int index = -1;
                        for (Character.ArmorClassWithSource other : list) {
                            index++;
                            if (other == row) continue;
                            if (other.isDisabled) continue;
                            selected = other;
                            selectedIndex = index;
                            other.setIsEquipped(true);
                            //other.isDisabled = true;
                            adapter.notifyItemChanged(index);
                            break;
                        }

                    }
                    if (selected != null) {
                        selected.isDisabled = !selected.isArmor();
                        adapter.notifyItemChanged(selectedIndex);
                        context.updateModifyingRows(selected.isArmor());
                    }
                    context.updateAC();
                    // update other list items
                }
            });

            String formula = row.getFormula();
            int val = row.getValue();
            String stringVal = val + "";
            if (stringVal.equals(formula)) {
                value.setText("");
            } else {
                value.setText("=" + stringVal);
            }
        }
    }

    public static class RootAcAdapter extends RecyclerView.Adapter<AcViewHolder> {
        List<Character.ArmorClassWithSource> list;
        ArmorClassDialogFragment fragment;

        RootAcAdapter(ArmorClassDialogFragment armorClassDialogFragment, Character character) {
            list = character.deriveRootAcs();
            this.fragment = armorClassDialogFragment;
        }

        public void reloadList(Character character) {
            list = character.deriveRootAcs();
        }

        @Override
        public AcViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.armor_class_root_item, parent, false);
            return new AcViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(AcViewHolder holder, final int position) {
            final Character.ArmorClassWithSource row = list.get(position);
            holder.bind(fragment, this, row);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public static class ModifyingAcAdapter extends RecyclerView.Adapter<AcViewHolder> {
        List<Character.ArmorClassWithSource> list;
        ArmorClassDialogFragment fragment;

        ModifyingAcAdapter(ArmorClassDialogFragment armorClassDialogFragment, Character character) {
            list = character.deriveModifyingAcs();
            this.fragment = armorClassDialogFragment;
        }

        public void reloadList(Character character) {
            list = character.deriveRootAcs();
        }

        @Override
        public AcViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.armor_class_root_item, parent, false);
            return new AcViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(AcViewHolder holder, final int position) {
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




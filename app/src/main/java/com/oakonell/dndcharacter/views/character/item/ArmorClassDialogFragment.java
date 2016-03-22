package com.oakonell.dndcharacter.views.character.item;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AdjustmentComponentSource;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ComponentType;
import com.oakonell.dndcharacter.model.character.CustomAdjustmentType;
import com.oakonell.dndcharacter.model.character.CustomAdjustments;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 12/21/2015.
 */
public class ArmorClassDialogFragment extends AbstractCharacterDialogFragment {
    public static final String BASE_ARMOR = "baseArmor";
    public static final String MODIFYING_ARMOR = "modifyingArmor";

    private TextView acText;
    private RecyclerView rootList;
    private RecyclerView modList;
    private ViewGroup modifiers_group;

    private RootAcAdapter rootAcAdapter;
    private ModifyingAcAdapter modifyingAcAdapter;
    @Nullable
    private String baseArmorSaved;
    @Nullable
    private ArrayList<String> modifyingArmorSaved;
    private View add_base_adjustment;
    private View add_modifying_adjustment;

    @NonNull
    public static ArmorClassDialogFragment createDialog() {
        return new ArmorClassDialogFragment();
    }

    public View onCreateTheView(@NonNull LayoutInflater inflater, final ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.armor_class_dialog, container);

        acText = (TextView) view.findViewById(R.id.ac);

        rootList = (RecyclerView) view.findViewById(R.id.root_ac_list);
        add_base_adjustment = view.findViewById(R.id.add_base_adjustment);
        modList = (RecyclerView) view.findViewById(R.id.mod_ac_list);

        modifiers_group = (ViewGroup) view.findViewById(R.id.modifiers_group);
        add_modifying_adjustment = view.findViewById(R.id.add_modifying_adjustment);


        if (savedInstanceState != null) {
            baseArmorSaved = savedInstanceState.getString(BASE_ARMOR);
            modifyingArmorSaved = savedInstanceState.getStringArrayList(MODIFYING_ARMOR);
        }

        add_base_adjustment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomACDialog dialog = CustomACDialog.createRootACDialog();
                dialog.show(getFragmentManager(), "custom_ac");
            }
        });
        add_modifying_adjustment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomACDialog dialog = CustomACDialog.createModifyingACDialog();
                dialog.show(getFragmentManager(), "custom_ac");
            }
        });

        return view;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.armor_class_title);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        for (Character.ArmorClassWithSource each : rootAcAdapter.list) {
            if (each.isEquipped()) {
                outState.putString(BASE_ARMOR, each.getSourceString(getResources()));
                break;
            }
        }
        ArrayList<String> equippedBonuses = new ArrayList<>();
        for (Character.ArmorClassWithSource each : modifyingAcAdapter.list) {
            if (each.isEquipped()) {
                equippedBonuses.add(each.getSourceString(getResources()));
            }
        }
        outState.putStringArrayList(MODIFYING_ARMOR, equippedBonuses);
    }

    @Override
    protected boolean onDone() {
        for (Character.ArmorClassWithSource each : rootAcAdapter.list) {
            each.setEquipped(getResources(), getCharacter(), each.isEquipped());
        }
        for (Character.ArmorClassWithSource each : modifyingAcAdapter.list) {
            each.setEquipped(getResources(), getCharacter(), each.isEquipped());
        }
        return super.onDone();
    }

    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        super.onCharacterLoaded(character);

        rootAcAdapter = new RootAcAdapter(this, character);
        if (baseArmorSaved != null) {
            for (Character.ArmorClassWithSource each : rootAcAdapter.list) {
                if (each.getSourceString(getResources()).equals(baseArmorSaved)) {
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
                if (modifyingArmorSaved.contains(each.getSourceString(getResources()))) {
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

    @NonNull
    @Override
    protected Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.ARMOR_CLASS));
        filter.add(new FeatureContextArgument(FeatureContext.TO_HIT));
        return filter;
    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
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

        acText.setText(NumberUtils.formatNumber(ac));

    }

//    private void updateModifyingRows(boolean armor) {
//        int position = -1;
//        for (Character.ArmorClassWithSource each : modifyingAcAdapter.list) {
//            position++;
//            if (each.isArmor()) continue;
//
//            String activeFormula = each.getSource().getActiveFormula();
//            if (activeFormula == null) continue;
//            SimpleVariableContext variableContext = new SimpleVariableContext();
//            variableContext.setBoolean("armor", armor);
//            boolean shouldEquip = getCharacter().evaluateBooleanFormula(activeFormula, variableContext);
//            if (each.isEquipped() != shouldEquip) {
//                each.setIsEquipped(shouldEquip);
//                modifyingAcAdapter.notifyItemChanged(position);
//            }
//        }
//    }

    public static class AcViewHolder extends BindableComponentViewHolder<Character.ArmorClassWithSource, ArmorClassDialogFragment, RootAcAdapter> {
        @NonNull
        private final CheckBox checkBox;
        @NonNull
        private final TextView name;
        @NonNull
        private final TextView formula;
        @NonNull
        private final TextView value;
        @NonNull
        private final View delete;

        public AcViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            formula = (TextView) itemView.findViewById(R.id.formula);
            value = (TextView) itemView.findViewById(R.id.value);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            delete = itemView.findViewById(R.id.delete);
        }

        @Override
        public void bind(@NonNull final ArmorClassDialogFragment context, @NonNull final RootAcAdapter adapter, @NonNull final Character.ArmorClassWithSource row) {
            name.setText(row.getSourceString(context.getResources()));
            formula.setText(row.getFormula());
            final List<Character.ArmorClassWithSource> list = adapter.list;
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(row.isEquipped());
            checkBox.setEnabled(!row.isDisabled && list.size() > 1);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // clear out other checked rows for root ACs, only one allowed
                        for (Character.ArmorClassWithSource each : adapter.list) {
                            if (each == row) continue;
                            each.setIsEquipped(false);
                        }
                    }
                    row.setIsEquipped(isChecked);

                    adapter.updateDueToAChange();
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
                value.setText(context.getString(R.string.armor_class_base_formula, stringVal));
            }

            if (row.getSource() != null && row.getSource().getType() == ComponentType.CUSTOM_ADJUSTMENT) {
                delete.setVisibility(View.VISIBLE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomAdjustments adjustments = context.getCharacter().getCustomAdjustments(CustomAdjustmentType.ROOT_ACS);
                        adjustments.delete(((AdjustmentComponentSource) row.getSource()).getAdjustment());
                        context.getMainActivity().updateViews();
                    }
                });
            } else {
                delete.setVisibility(View.GONE);
            }
        }
    }

    public static class RootAcAdapter extends RecyclerView.Adapter<AcViewHolder> {
        List<Character.ArmorClassWithSource> list;
        final ArmorClassDialogFragment fragment;

        RootAcAdapter(ArmorClassDialogFragment armorClassDialogFragment, @NonNull Character character) {
            list = character.deriveRootAcs();
            this.fragment = armorClassDialogFragment;
        }

        public void reloadList(@NonNull Character character) {
            list = character.deriveRootAcs();
            notifyDataSetChanged();
        }

        public void updateDueToAChange() {
            Character.modifyRootAcs(fragment.getCharacter(), fragment.modifyingAcAdapter.list, list);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public AcViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.armor_class_root_item, parent, false);
            return new AcViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(@NonNull AcViewHolder holder, final int position) {
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
        final ArmorClassDialogFragment fragment;

        ModifyingAcAdapter(ArmorClassDialogFragment armorClassDialogFragment, @NonNull Character character) {
            list = character.deriveModifyingAcs();
            this.fragment = armorClassDialogFragment;
        }

        public void reloadList(@NonNull Character character) {
            list = character.deriveModifyingAcs();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public AcViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.armor_class_root_item, parent, false);
            return new AcViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(@NonNull AcViewHolder holder, final int position) {
            final Character.ArmorClassWithSource row = list.get(position);
            holder.name.setText(row.getSourceString(fragment.getResources()));
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
                    fragment.rootAcAdapter.updateDueToAChange();
                }
            });

            String formula = row.getFormula();
            int val = row.getValue();
            String stringVal = val + "";
            if (stringVal.equals(formula)) {
                holder.value.setText("");
            } else {
                holder.value.setText(fragment.getString(R.string.armor_class_base_formula, stringVal));
            }

            if (row.getSource() != null && row.getSource().getType() == ComponentType.CUSTOM_ADJUSTMENT) {
                holder.delete.setVisibility(View.VISIBLE);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomAdjustments adjustments = fragment.getCharacter().getCustomAdjustments(CustomAdjustmentType.MODIFYING_ACS);
                        adjustments.delete(((AdjustmentComponentSource) row.getSource()).getAdjustment());
                        fragment.getMainActivity().updateViews();
                    }
                });
            } else {
                holder.delete.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }


}




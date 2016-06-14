package com.oakonell.dndcharacter.views.character.item;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.activeandroid.Model;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.item.CharacterArmor;
import com.oakonell.dndcharacter.model.character.item.CharacterItem;
import com.oakonell.dndcharacter.model.character.item.CharacterWeapon;
import com.oakonell.dndcharacter.model.item.CreateCharacterArmorVisitor;
import com.oakonell.dndcharacter.model.item.CreateCharacterWeaponVisitor;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.model.item.ItemType;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;
import com.oakonell.dndcharacter.views.CursorIndexesByName;
import com.oakonell.dndcharacter.views.NoDefaultSpinner;
import com.oakonell.dndcharacter.views.character.AbstractSelectComponentDialogFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 2/1/2016.
 */
public class SelectItemDialogFragment extends AbstractSelectComponentDialogFragment<AbstractSelectComponentDialogFragment.RowViewHolder> {

    private static final String ITEM_TYPE = "itemType";
    private static final String CATEGORY_PROFICIENCIES = "category_proficiencies";
    private static final String NAME_PROFICIENCIES = "name_proficiencies";
    private ItemSelectedListener listener;
    private ItemType itemType;
    private NoDefaultSpinner itemTypeSpinner;
    AppCompatCheckBox limit_to_proficient;

    @Nullable
    private ArrayList<String> catList = new ArrayList<>();
    @Nullable
    private ArrayList<String> nameList = new ArrayList<>();

    public void setListener(ItemSelectedListener listener) {
        this.listener = listener;
    }

    public interface ItemSelectedListener {
        boolean itemSelected(long id);
    }

    @NonNull
    public static SelectItemDialogFragment createDialog(@NonNull ItemSelectedListener listener, @Nullable ItemType itemType, @Nullable List<Character.ToolProficiencyWithSource> proficiencies) {
        SelectItemDialogFragment dialog = new SelectItemDialogFragment();

        Bundle args = new Bundle();
        if (itemType != null) {
            args.putString(ITEM_TYPE, itemType.name());
        }
        if (proficiencies != null) {
            ArrayList<String> catList = new ArrayList<>();
            ArrayList<String> nameList = new ArrayList<>();
            for (Character.ToolProficiencyWithSource each : proficiencies) {
                if (each.getProficiency().getCategory() != null) {
                    catList.add(each.getProficiency().getCategory().toUpperCase());
                } else {
                    nameList.add(each.getProficiency().getName().toUpperCase());
                }
            }
            args.putStringArrayList(CATEGORY_PROFICIENCIES, catList);
            args.putStringArrayList(NAME_PROFICIENCIES, nameList);
        }
        dialog.setArguments(args);

        dialog.setListener(listener);
        return dialog;
    }

    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateTheView(inflater, container, savedInstanceState);

        if (getArguments() != null) {
            String itemTypeString = getArguments().getString(ITEM_TYPE);
            if (itemTypeString != null && itemTypeString.trim().length() > 0) {
                itemType = EnumHelper.stringToEnum(itemTypeString, ItemType.class);
            }
            catList = getArguments().getStringArrayList(CATEGORY_PROFICIENCIES);
            nameList = getArguments().getStringArrayList(NAME_PROFICIENCIES);
        }


        itemTypeSpinner = (NoDefaultSpinner) view.findViewById(R.id.item_type);
        final String prompt = getString(R.string.item_type_prompt);
        itemTypeSpinner.setPrompt(prompt);
        float minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (prompt.length() + 2) * NoDefaultSpinner.SPINNER_TEXT_SP, itemTypeSpinner.getResources().getDisplayMetrics());
        itemTypeSpinner.setMinimumWidth((int) minWidth);

        List<String> itemTypes = new ArrayList<>();
        for (ItemType each : ItemType.values()) {
            itemTypes.add(getString(each.getStringResId()));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                R.layout.large_spinner_text, itemTypes);
        dataAdapter.setDropDownViewResource(R.layout.large_spinner_text);
        itemTypeSpinner.setAdapter(dataAdapter);


        limit_to_proficient = (AppCompatCheckBox) view.findViewById(R.id.limit_to_proficient);
        limit_to_proficient.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                search();
            }
        });


        if (itemType != null) {
            int index = itemTypes.indexOf(getString(itemType.getStringResId()));
            itemTypeSpinner.setSelection(index);
            itemTypeSpinner.setEnabled(false);
            if (itemType == ItemType.EQUIPMENT) {
                limit_to_proficient.setVisibility(View.GONE);
            }
        } else {
            itemTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    search();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

        search();
        return view;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.select_item);
    }

    @NonNull
    @Override
    public Class<? extends ItemRow> getComponentClass() {
        return ItemRow.class;
    }


    @Override
    protected boolean applyAction(long id) {
        if (listener != null) {
            return listener.itemSelected(id);
        }
        throw new RuntimeException("No Listener!");
        //return true;
    }

    @Nullable
    @Override
    protected String getSelection() {
        if (itemTypeSpinner == null) return null;

        String itemTypeFilter = "";
        int index = itemTypeSpinner.getSelectedItemPosition();
        if (index >= 0) {
            itemTypeFilter = " itemType = ?";
        }

        String proficientFilter = "";
        if (limit_to_proficient.isChecked()) {
            StringBuilder builder = new StringBuilder();
            if (nameList != null) {
                builder.append(" and (upper(name) in (");
                boolean first = true;
                for (String name : nameList) {
                    if (!first) builder.append(",");
                    builder.append("'");
                    builder.append(name.replaceAll("'", "''"));
                    builder.append("'");
                    first = false;
                }
                builder.append(")");
            }
            if (catList != null) {
                if (nameList != null) {
                    builder.append(" or ");
                } else {
                    builder.append(" and ");
                }
                builder.append(" upper(category) in (");
                boolean first = true;
                for (String category : catList) {
                    if (!first) builder.append(",");
                    builder.append("'");
                    builder.append(category.replaceAll("'", "''"));
                    builder.append("'");
                    first = false;
                }
                builder.append(")");
            }
            if (nameList != null) {
                builder.append(")");
            }
            proficientFilter = builder.toString();
        }

        return itemTypeFilter + proficientFilter;
    }

    @Nullable
    @Override
    protected String[] getSelectionArgs() {
        if (itemTypeSpinner == null) return null;
        int index = itemTypeSpinner.getSelectedItemPosition();
        if (index < 0) return null;
        ItemType type = ItemType.values()[index];
        return new String[]{type.name()};
    }

    protected int getDialogResource() {
        return R.layout.item_search_dialog;
    }

    protected int getListItemResource() {
        return R.layout.item_search_list_item;
    }

    @NonNull
    @Override
    public RowViewHolder newRowViewHolder(@NonNull View newView) {
        return new ItemRowViewHolder(newView);
    }

    private class ItemRowViewHolder extends RowViewHolder {

        @NonNull
        private final TextView itemTypeView;
        @NonNull
        private final TextView categoryView;
        @NonNull
        private final TextView costView;
        @NonNull
        private final TextView not_proficient;

        public ItemRowViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTypeView = (TextView) itemView.findViewById(R.id.item_type);
            categoryView = (TextView) itemView.findViewById(R.id.category);
            costView = (TextView) itemView.findViewById(R.id.cost);
            not_proficient = (TextView) itemView.findViewById(R.id.not_proficient);
        }

        @Override
        public void bindTo(Cursor cursor, AbstractSelectComponentDialogFragment context, CursorComponentListAdapter adapter, CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);
            final String rawItemTypeString = cursor.getString(cursorIndexesByName.getIndex(cursor, "itemType"));
            final String category = cursor.getString(cursorIndexesByName.getIndex(cursor, "category"));
            final String cost = cursor.getString(cursorIndexesByName.getIndex(cursor, "cost"));
            ItemType type = EnumHelper.stringToEnum(rawItemTypeString, ItemType.class);
            String typeString = context.getString(type.getStringResId());

            if (SelectItemDialogFragment.this.itemType != null) {
                itemTypeView.setVisibility(View.GONE);
            }
            if (catList != null || nameList != null) {
                boolean prof = false;
                final String nameString = cursor.getString(cursorIndexesByName.getIndex(cursor, "name"));
                if (catList != null && category != null && catList.contains(category.toUpperCase())) {
                    prof = true;
                }
                if (nameList != null && nameList.contains(nameString.toUpperCase())) {
                    prof = true;
                }
                if (prof) {
                    not_proficient.setVisibility(View.GONE);
                } else {
                    not_proficient.setVisibility(View.VISIBLE);
                }
            } else {
                not_proficient.setVisibility(View.GONE);
            }
            itemTypeView.setText(typeString);
            categoryView.setText(category);
            costView.setText(cost);

        }
    }

    public static class DefaultAddItemListener implements SelectItemDialogFragment.ItemSelectedListener {
        private final CharacterActivity activity;

        DefaultAddItemListener(CharacterActivity activity) {
            this.activity = activity;
        }

        @Override
        public boolean itemSelected(long id) {
            ItemRow itemRow = ItemRow.load(ItemRow.class, id);
            final ItemType itemType = itemRow.getItemType();
            switch (itemType) {
                case ARMOR:
                    CharacterArmor armor = CreateCharacterArmorVisitor.createArmor(activity, itemRow, activity.getCharacter());
                    armor.setName(itemRow.getName());
                    break;
                case WEAPON:
                    CharacterWeapon weapon = CreateCharacterWeaponVisitor.createWeapon(activity, itemRow, activity.getCharacter());
                    weapon.setName(itemRow.getName());
                    break;
                case EQUIPMENT:
                    CharacterItem item = new CharacterItem();
                    item.setName(itemRow.getName());

                    ApplyChangesToGenericComponent.applyToCharacter(activity, XmlUtils.getDocument(itemRow.getXml()).getDocumentElement(), null, item, activity.getCharacter(), false);
                    activity.getCharacter().addItem(item);
                    break;
            }

            activity.updateViews();
            activity.saveCharacter();
            return true;
        }
    }

}

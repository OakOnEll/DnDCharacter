package com.oakonell.dndcharacter.views.character.item;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.activeandroid.Model;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.character.*;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.item.CharacterArmor;
import com.oakonell.dndcharacter.model.character.item.CharacterItem;
import com.oakonell.dndcharacter.model.character.item.CharacterWeapon;
import com.oakonell.dndcharacter.model.feat.Feat;
import com.oakonell.dndcharacter.model.item.CreateCharacterArmorVisitor;
import com.oakonell.dndcharacter.model.item.CreateCharacterWeaponVisitor;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.model.item.ItemType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.XmlUtils;
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
    private ItemSelectedListener listener;
    private ItemType itemType;
    private NoDefaultSpinner itemTypeSpinner;

    public void setListener(ItemSelectedListener listener) {
        this.listener = listener;
    }

    public interface ItemSelectedListener {
        boolean itemSelected(long id);
    }

    @NonNull
    public static SelectItemDialogFragment createDialog(@NonNull ItemSelectedListener listener, ItemType itemType) {
        SelectItemDialogFragment dialog = new SelectItemDialogFragment();

        if (itemType != null) {
            Bundle args = new Bundle();
            args.putString(ITEM_TYPE, itemType.name());
            dialog.setArguments(args);
        }
        dialog.setListener(listener);
        return dialog;
    }

    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateTheView(inflater, container, savedInstanceState);

        if (getArguments() != null) {
            String itemTypeString = getArguments().getString(ITEM_TYPE);
            if (itemTypeString != null && itemTypeString.trim().length() > 0) {
                itemType = ItemType.valueOf(itemTypeString);
            }
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

        if (itemType != null) {
            int index = itemTypes.indexOf(getString(itemType.getStringResId()));
            itemTypeSpinner.setSelection(index);
            itemTypeSpinner.setEnabled(false);
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
    public Class<? extends Model> getComponentClass() {
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
        int index = itemTypeSpinner.getSelectedItemPosition();
        if (index < 0) return null;
        return " itemType = ?";
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

        private final TextView itemTypeView;
        private final TextView categoryView;
        private final TextView costView;

        public ItemRowViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTypeView = (TextView) itemView.findViewById(R.id.item_type);
            categoryView = (TextView) itemView.findViewById(R.id.category);
            costView = (TextView) itemView.findViewById(R.id.cost);
        }

        @Override
        public void bindTo(@NonNull Cursor cursor, @NonNull AbstractSelectComponentDialogFragment context, RecyclerView.Adapter adapter, @NonNull CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);
            final String rawItemTypeString = cursor.getString(cursorIndexesByName.getIndex(cursor, "itemType"));
            final String category = cursor.getString(cursorIndexesByName.getIndex(cursor, "category"));
            final String cost = cursor.getString(cursorIndexesByName.getIndex(cursor, "cost"));
            ItemType type = ItemType.valueOf(rawItemTypeString);
            String typeString = context.getString(type.getStringResId());

            if (SelectItemDialogFragment.this.itemType != null) {
                itemTypeView.setVisibility(View.GONE);
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

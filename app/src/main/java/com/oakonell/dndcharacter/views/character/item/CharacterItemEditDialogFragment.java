package com.oakonell.dndcharacter.views.character.item;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.item.CharacterItem;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.model.item.ItemType;
import com.oakonell.dndcharacter.utils.XmlUtils;

/**
 * Created by Rob on 3/18/2016.
 */
public class CharacterItemEditDialogFragment extends AbstractCharacterItemEditDialogFragment<CharacterItem> {

    @NonNull
    public static CharacterItemEditDialogFragment createAddDialog() {
        CharacterItemEditDialogFragment newMe = new CharacterItemEditDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ADD, true);
        newMe.setArguments(args);

        return newMe;
    }

    @NonNull
    public static CharacterItemEditDialogFragment createDialog(Character character, CharacterItem item) {
        CharacterItemEditDialogFragment newMe = new CharacterItemEditDialogFragment();

        // TODO encode which item- nameText and index? just index...
        long id = item.getId();
        Bundle args = new Bundle();
        args.putLong(ID, id);
        newMe.setArguments(args);

        return newMe;
    }


    public View onCreateTheView(@NonNull LayoutInflater inflater, final ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.character_item_edit_dialog, container);
        afterCreateView(view);
        return view;
    }


    @Override
    protected String getTitle() {
        Bundle args = getArguments();
        if (args.getBoolean(ADD, false)) {
            return getString(R.string.add_item_title);
        }
        return getString(R.string.edit_item_title);
    }


    protected void addItem(CharacterItem item) {
        getCharacter().addItem(item);
    }

    @NonNull
    @Override
    protected CharacterItem newCharacterItem() {
        return new CharacterItem();
    }

    protected void updateItem(ItemRow itemRow) {
        item.setName(itemRow.getName());
        ApplyChangesToGenericComponent.applyToCharacter(getMainActivity(), XmlUtils.getDocument(itemRow.getXml()).getDocumentElement(), null, item, getCharacter(), false);
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
        }, ItemType.EQUIPMENT, null);
    }

    @Override
    protected CharacterItem getItemById(long id) {
        return getCharacter().getItemById(id);
    }
}

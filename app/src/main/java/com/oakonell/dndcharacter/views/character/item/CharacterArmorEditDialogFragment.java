package com.oakonell.dndcharacter.views.character.item;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.item.CharacterArmor;
import com.oakonell.dndcharacter.model.character.item.CharacterItem;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.model.item.CreateCharacterArmorVisitor;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.model.item.ItemType;

/**
 * Created by Rob on 3/18/2016.
 */
public class CharacterArmorEditDialogFragment extends AbstractCharacterItemEditDialogFragment<CharacterArmor> {

    private CheckBox equipped;
    private EditText armor_class;

    @NonNull
    public static CharacterArmorEditDialogFragment createAddDialog(boolean isForCompanion) {
        CharacterArmorEditDialogFragment newMe = new CharacterArmorEditDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ADD, true);
        args.putBoolean(COMPANION_ARG, isForCompanion);
        newMe.setArguments(args);

        return newMe;
    }

    @NonNull
    public static CharacterArmorEditDialogFragment createDialog(Character character, CharacterItem item) {
        CharacterArmorEditDialogFragment newMe = new CharacterArmorEditDialogFragment();

        // TODO encode which item- nameText and index? just index...
        long id = item.getId();
        Bundle args = new Bundle();
        args.putLong(ID, id);
        newMe.setArguments(args);

        return newMe;
    }


    public View onCreateTheView(@NonNull LayoutInflater inflater, final ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.character_armor_edit_dialog, container);
        afterCreateView(view);

        equipped = (CheckBox) view.findViewById(R.id.equipped);
        armor_class = (EditText) view.findViewById(R.id.armor_class);

        return view;
    }


    @Override
    protected String getTitle() {
        Bundle args = getArguments();
        if (args.getBoolean(ADD, false)) {
            return getString(R.string.add_armor_title);
        }
        return getString(R.string.edit_armor_title);
    }

    @Override
    protected CharacterArmor getItemById(long id) {
        return getDisplayedCharacter().getArmorById(id);
    }

    @Override
    protected void addItem(CharacterArmor item) {
        getDisplayedCharacter().addArmor(item);
    }

    @Override
    protected ItemType getItemType() {
        return ItemType.ARMOR;
    }

    @NonNull
    @Override
    protected CharacterArmor newCharacterItem() {
        return new CharacterArmor();
    }

    protected void updateItem(ItemRow itemRow) {
        item = CreateCharacterArmorVisitor.createArmor(getMainActivity(), itemRow, null);
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
        }, ItemType.ARMOR, getDisplayedCharacter().deriveToolProficiencies(ProficiencyType.ARMOR));
    }

    @Override
    protected void updateViewsFromItem() {
        super.updateViewsFromItem();
        equipped.setChecked(item.isEquipped());
        armor_class.setText(item.getAcFormula());
    }

    @Override
    protected void setItemProperties(CharacterArmor item) {
        super.setItemProperties(item);
        item.setEquipped(getResources(), getDisplayedCharacter(), equipped.isChecked());
        item.setAcFormula(armor_class.getText().toString());
    }

    @Override
    protected boolean validate() {
        // TODO validate armor class formula
        boolean valid = true;
        if (armor_class.getText().toString().trim().length() == 0) {
            armor_class.setError(getString(R.string.enter_a_name));
            Animation shake = AnimationUtils.loadAnimation(armor_class.getContext(), R.anim.shake);
            armor_class.startAnimation(shake);

            valid = false;
        }
        return valid && super.validate();
    }


}

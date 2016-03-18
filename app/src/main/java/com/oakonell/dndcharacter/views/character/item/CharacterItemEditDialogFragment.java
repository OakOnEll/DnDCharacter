package com.oakonell.dndcharacter.views.character.item;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.item.CharacterItem;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.model.item.ItemType;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 3/18/2016.
 */
public class CharacterItemEditDialogFragment extends AbstractCharacterDialogFragment {

    private static final String NAME = "name";
    private static final String ADD = "add";

    private EditText nameText;
    private EditText weightText;
    private EditText costText;
    private EditText notesText;
    private ImageButton search;

    private CharacterItem item;

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
        String name = item.getName();
        List<CharacterItem> matchingItems = new ArrayList<>();
        for (CharacterItem each : character.getItems()) {
            if (each.getName().equals(name)) {
                matchingItems.add(each);
            }
        }
        if (matchingItems.size() == 0) {
            throw new RuntimeException("Should be an item named '" + name + "' in equipment inventory");
        }
        if (matchingItems.size() > 1) {
            throw new RuntimeException("Found multiple items named '" + name + "' in equipment inventory");
        }
        Bundle args = new Bundle();
        args.putString(NAME, name);
        newMe.setArguments(args);

        return newMe;
    }

    public View onCreateTheView(@NonNull LayoutInflater inflater, final ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.character_item_edit_dialog, container);
        nameText = (EditText) view.findViewById(R.id.name);
        weightText = (EditText) view.findViewById(R.id.weight);
        costText = (EditText) view.findViewById(R.id.cost);
        notesText = (EditText) view.findViewById(R.id.notes);
        search = (ImageButton) view.findViewById(R.id.search);
        return view;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.edit_item_title);
    }


    @Override
    protected boolean onDone() {
        if (nameText.getText().toString().trim().length() == 0) {
            nameText.setError(getString(R.string.enter_a_name));
            Animation shake = AnimationUtils.loadAnimation(nameText.getContext(), R.anim.shake);
            nameText.startAnimation(shake);

            return false;
        }
        item.setName(nameText.getText().toString());
        item.setWeight(weightText.getText().toString());
        item.setCost(costText.getText().toString());
        item.setNotes(notesText.getText().toString());
        if (getArguments().getBoolean(ADD, false)) {
            getCharacter().addItem(item);
        }
        return super.onDone();
    }

    public void onCharacterLoaded(@NonNull com.oakonell.dndcharacter.model.character.Character character) {
        super.onCharacterLoaded(character);

        Bundle args = getArguments();
        if (!args.getBoolean(ADD, false)) {
            search.setVisibility(View.GONE);
            String name = args.getString(NAME);
            List<CharacterItem> matchingItems = new ArrayList<>();
            for (CharacterItem each : character.getItems()) {
                if (each.getName().equals(name)) {
                    matchingItems.add(each);
                }
            }
            if (matchingItems.size() == 0) {
                throw new RuntimeException("Should be an item named '" + name + "' in equipment inventory");
            }
            if (matchingItems.size() > 1) {
                throw new RuntimeException("Found multiple items named '" + name + "' in equipment inventory");
            }
            item = matchingItems.get(0);
        } else {
            search.setVisibility(View.VISIBLE);
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectItemDialogFragment dialog = SelectItemDialogFragment.createDialog(new SelectItemDialogFragment.ItemSelectedListener() {
                        @Override
                        public boolean itemSelected(long id) {
                            ItemRow itemRow = ItemRow.load(ItemRow.class, id);
                            item.setName(itemRow.getName());

                            ApplyChangesToGenericComponent.applyToCharacter(getMainActivity(), XmlUtils.getDocument(itemRow.getXml()).getDocumentElement(), null, item, getCharacter(), false);
                            updateViewsFromItem();
                            return true;
                        }
                    }, ItemType.EQUIPMENT, null);
                    dialog.show(getFragmentManager(), "select_item");
                }
            });
            item = new CharacterItem();
        }

        updateViewsFromItem();

    }

    protected void updateViewsFromItem() {
        nameText.setText(item.getName());
        weightText.setText(item.getWeight());
        costText.setText(item.getCost());
        notesText.setText(item.getNotes());
    }
}

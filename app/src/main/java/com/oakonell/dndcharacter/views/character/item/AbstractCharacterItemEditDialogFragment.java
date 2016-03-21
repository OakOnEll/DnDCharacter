package com.oakonell.dndcharacter.views.character.item;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.item.CharacterItem;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 3/18/2016.
 */
public abstract class AbstractCharacterItemEditDialogFragment<I extends CharacterItem> extends AbstractCharacterDialogFragment {

    protected static final String ID = "id";
    protected static final String ADD = "add";

    private EditText nameText;
    private EditText weightText;
    private EditText costText;
    private EditText notesText;
    private ImageButton search;

    protected I item;


    protected void afterCreateView(View view) {
        nameText = (EditText) view.findViewById(R.id.name);
        weightText = (EditText) view.findViewById(R.id.weight);
        costText = (EditText) view.findViewById(R.id.cost);
        notesText = (EditText) view.findViewById(R.id.notes);
        search = (ImageButton) view.findViewById(R.id.search);

    }

    @NonNull
    protected abstract I newCharacterItem();

    protected abstract void addItem(I item);

    @Override
    protected boolean onDone() {
        if (!validate()) return false;
        item.setName(nameText.getText().toString());
        item.setWeight(weightText.getText().toString());
        item.setCost(costText.getText().toString());
        item.setNotes(notesText.getText().toString());
        setItemProperties(item);
        if (getArguments().getBoolean(ADD, false)) {
            addItem(item);
        }
        return super.onDone();
    }

    protected void setItemProperties(I item) {

    }

    protected boolean validate() {
        if (nameText.getText().toString().trim().length() == 0) {
            nameText.setError(getString(R.string.enter_a_name));
            Animation shake = AnimationUtils.loadAnimation(nameText.getContext(), R.anim.shake);
            nameText.startAnimation(shake);

            return false;
        }
        return true;
    }


    public void onCharacterLoaded(@NonNull com.oakonell.dndcharacter.model.character.Character character) {
        super.onCharacterLoaded(character);

        Bundle args = getArguments();
        if (!args.getBoolean(ADD, false)) {
            search.setVisibility(View.GONE);
            long id = args.getLong(ID);
            item = getItemById(id);
        } else {
            search.setVisibility(View.VISIBLE);
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectItemDialogFragment dialog = createSelectItemDialogFragment();
                    dialog.show(getFragmentManager(), "select_item");
                }
            });
            item = newCharacterItem();
        }

        updateViewsFromItem();

    }

    protected abstract I getItemById(long id);

    @NonNull
    protected abstract SelectItemDialogFragment createSelectItemDialogFragment();


    protected void updateViewsFromItem() {
        nameText.setText(item.getName());
        weightText.setText(item.getWeight());
        costText.setText(item.getCost());
        notesText.setText(item.getNotes());
    }

}

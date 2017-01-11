package com.oakonell.dndcharacter.views.character.item;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.item.CharacterItem;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.model.item.ItemType;
import com.oakonell.dndcharacter.views.NoDefaultSpinner;
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
    private NoDefaultSpinner categorySpinner;
    private EditText weightText;
    private EditText costText;
    private EditText notesText;
    private ImageButton search;

    protected I item;
    private ArrayAdapter<String> categoriesAdapter;


    protected void afterCreateView(View view) {
        nameText = (EditText) view.findViewById(R.id.name);
        categorySpinner = (NoDefaultSpinner) view.findViewById(R.id.category);
        weightText = (EditText) view.findViewById(R.id.weight);
        costText = (EditText) view.findViewById(R.id.cost);
        notesText = (EditText) view.findViewById(R.id.notes);
        search = (ImageButton) view.findViewById(R.id.search);

        // look for existing categories for this item type, and populate spinner
        //   also add <new> option, to prompt for a new category

        final Select select = new Select(new String[]{"_id,category"}).distinct();
        From nameSelect = select.from(ItemRow.class).orderBy("category").groupBy("category");
        nameSelect = nameSelect.where("itemType = ?", getItemType());
        final List<ItemRow> categoryResults = nameSelect.execute();
        final List<String> categories = new ArrayList<>();
        for (ItemRow each : categoryResults) {
            if (each.getCategory() == null) continue;
            categories.add(each.getCategory());
        }
        categories.add(getString(R.string.new_category_spinner_entry));

        categorySpinner.setPromptId(R.string.category_prompt);

        categoriesAdapter = new ArrayAdapter<>(getContext(), R.layout.large_spinner_text, categories);
        categoriesAdapter.setDropDownViewResource(R.layout.large_spinner_text);
        categorySpinner.setAdapter(categoriesAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == categoriesAdapter.getCount() - 1) {
                    // TODO prompt for new category, add to list
                    //   probably need to keep around for save/restore state as well

                    // Set an EditText view to get user input
                    final EditText input = new EditText(AbstractCharacterItemEditDialogFragment.this.getActivity());

                    new AlertDialog.Builder(AbstractCharacterItemEditDialogFragment.this.getActivity())
                            .setMessage(R.string.enter_new_category)
                            .setView(input)
                            .setPositiveButton(R.string.ok_button_label, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Editable editable = input.getText();

                                    categories.add(categories.size() - 1, editable.toString());
                                    categoriesAdapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(R.string.cancel_button_label, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Do nothing.
                                }
                            }).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    protected abstract ItemType getItemType();

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

        String category = (String) categorySpinner.getSelectedItem();
        item.setCategory(category);

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
        int numCategories = categoriesAdapter.getCount();
        if (item.getCategory() != null) {
            for (int i = 0; i < numCategories; i++) {
                if (item.getCategory().equalsIgnoreCase(categoriesAdapter.getItem(i))) {
                    categorySpinner.setSelection(i);
                }
            }
        } else {
            categorySpinner.setSelection(-1);
        }

        nameText.setText(item.getName());
        weightText.setText(item.getWeight());
        costText.setText(item.getCost());
        notesText.setText(item.getNotes());
    }

}

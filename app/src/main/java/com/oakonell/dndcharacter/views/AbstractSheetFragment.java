package com.oakonell.dndcharacter.views;


import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.AbstractBaseActivity;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.*;
import com.oakonell.dndcharacter.model.Character;

/**
 * Created by Rob on 10/27/2015.
 */
public class AbstractSheetFragment extends Fragment {
    protected Character character;
    EditText character_name;
    TextView classes;
    TextView race;
    TextView background;
    TextView character_name_read_only;
    Button changeName;
    Button cancelName;

    public void updateViews() {
        updateViews((ViewGroup) getView());
    }

    protected void updateViews(View rootView) {
        if (character == null) {
            character_name.setText("");
            character_name_read_only.setText("");

            race.setText("");
            background.setText("");
            classes.setText("");
            return;
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(character.getName());
        character_name_read_only.setText(character.getName());
        character_name.setText(character.getName());

        race.setText(character.getRaceName());
        background.setText(character.getBackgroundName());
        classes.setText(character.getClassesString());

    }

    public void setCharacter(com.oakonell.dndcharacter.model.Character character) {
        this.character = character;
        if (getView() != null) {
            updateViews(getView());
        }
    }

    protected void superCreateViews(View rootView) {
        character_name_read_only = (TextView) rootView.findViewById(R.id.character_name_read);
        character_name = (EditText) rootView.findViewById(R.id.character_name);
        classes = (TextView) rootView.findViewById(R.id.classes);
        race = (TextView) rootView.findViewById(R.id.race);
        background = (TextView) rootView.findViewById(R.id.background);
        changeName = (Button) rootView.findViewById(R.id.change_name);
        cancelName = (Button) rootView.findViewById(R.id.cancel);

        character_name_read_only.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowNameEdit(true);
                character_name.requestFocus();
            }
        });

        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                character.setName(character_name.getText().toString());
                allowNameEdit(false);
                updateViews();
                // a name change should update recent characters
                ((AbstractBaseActivity)getActivity()).populateRecentCharacters();
            }
        });
        cancelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                character_name.setText(character.getName());
                allowNameEdit(false);
                updateViews();
            }
        });
    }

    private void allowNameEdit(boolean b) {
        int editVis = b ? View.VISIBLE : View.GONE;
        int readOnlyVis = b ? View.GONE : View.VISIBLE;

        character_name.setVisibility(editVis);
        changeName.setVisibility(editVis);
        cancelName.setVisibility(editVis);

        character_name.selectAll();

        character_name_read_only.setVisibility(readOnlyVis);
    }

}

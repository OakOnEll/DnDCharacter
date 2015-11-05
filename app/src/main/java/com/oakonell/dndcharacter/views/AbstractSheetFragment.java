package com.oakonell.dndcharacter.views;


import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.*;
import com.oakonell.dndcharacter.model.Character;

/**
 * Created by Rob on 10/27/2015.
 */
public class AbstractSheetFragment extends Fragment {
    protected Character character;
    TextView character_name;
    TextView classes;
    TextView race;
    TextView background;

    public void updateViews() {
        updateViews((ViewGroup) getView());
    }

    protected void updateViews(View rootView) {
        if (character == null) {
            character_name.setText("");

            race.setText("");
            background.setText("");
            classes.setText("");
            return;
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(character.getName());
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
        character_name = (TextView) rootView.findViewById(R.id.character_name);
        classes = (TextView) rootView.findViewById(R.id.classes);
        race = (TextView) rootView.findViewById(R.id.race);
        background = (TextView) rootView.findViewById(R.id.background);
    }

}

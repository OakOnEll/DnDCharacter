package com.oakonell.dndcharacter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.*;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.views.FeatureBlockView;

/**
 * Created by Rob on 10/26/2015.
 */
public class NotesFragment extends Fragment {
    private com.oakonell.dndcharacter.model.Character character;
    TextView character_name;
    TextView classes;
    TextView race;
    TextView background;
EditText notes;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notes_sheet, container, false);

        character_name = (TextView) rootView.findViewById(R.id.character_name);
        classes = (TextView) rootView.findViewById(R.id.classes);
        race = (TextView) rootView.findViewById(R.id.race);
        background = (TextView) rootView.findViewById(R.id.background);
        notes = (EditText) rootView.findViewById(R.id.notes);

        return rootView;
    }

    private void updateViews(ViewGroup rootView, Character character) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(character.getName());
        character_name.setText(character.getName());

        race.setText(character.getRaceName());
        background.setText(character.getBackgroundName());
        classes.setText(character.getClassesString());

        notes.setText(character.getNotes());
    }

    public void setCharacter(Character character) {
        this.character = character;
        if (getView() != null) {
            updateViews((ViewGroup) getView(), character);
        }
    }
}

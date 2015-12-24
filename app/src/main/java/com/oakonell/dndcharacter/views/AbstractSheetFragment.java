package com.oakonell.dndcharacter.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.oakonell.dndcharacter.AbstractBaseActivity;
import com.oakonell.dndcharacter.MainActivity;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.views.background.ApplyBackgroundDialogFragment;
import com.oakonell.dndcharacter.views.classes.CharacterLevelsDialogFragment;
import com.oakonell.dndcharacter.views.race.ApplyRaceDialogFragment;

/**
 * Created by Rob on 10/27/2015.
 */
public abstract class AbstractSheetFragment extends Fragment implements OnCharacterLoaded {
    EditText character_name;
    TextView classes;
    TextView race;
    TextView background;
    TextView character_name_read_only;
    Button changeName;
    Button cancelName;

    public void updateViews() {
        updateViews(getView());
    }

    protected MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    protected Character getCharacter() {
        return getMainActivity().character;
    }

    protected void updateViews(View rootView) {
        Character character = getCharacter();
        if (character == null) {
            Toast.makeText(getActivity(), "Update views with a null character!?", Toast.LENGTH_SHORT).show();

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

        race.setText(character.getDisplayRaceName());
        background.setText(character.getBackgroundName());
        classes.setText(character.getClassesString());

    }

    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        View view = onCreateTheView(inflater, container, savedInstanceState);
        ((MainActivity) getActivity()).addCharacterLoadLister(this);
        return view;
    }

    protected abstract View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected void superCreateViews(View rootView) {
        character_name_read_only = (TextView) rootView.findViewById(R.id.character_name_read);
        character_name = (EditText) rootView.findViewById(R.id.character_name);
        classes = (TextView) rootView.findViewById(R.id.classes);
        race = (TextView) rootView.findViewById(R.id.race);
        background = (TextView) rootView.findViewById(R.id.background);
        changeName = (Button) rootView.findViewById(R.id.change_name);
        cancelName = (Button) rootView.findViewById(R.id.cancel);

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

    @Override
    public void onCharacterLoaded(Character character) {

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
                getCharacter().setName(character_name.getText().toString());
                allowNameEdit(false);
                updateViews();
                // a name change should update recent characters
                ((AbstractBaseActivity) getActivity()).populateRecentCharacters();
            }
        });
        cancelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                character_name.setText(getCharacter().getName());
                allowNameEdit(false);
                updateViews();
            }
        });

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ApplyBackgroundDialogFragment dialog = ApplyBackgroundDialogFragment.createDialog(getCharacter(), null);
                    dialog.show(getFragmentManager(), "background");
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Unable to build ui: \n" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException("Unable to build ui", e);
                }
            }
        });
        race.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ApplyRaceDialogFragment dialog = ApplyRaceDialogFragment.createDialog(getCharacter(), null);
                    dialog.show(getFragmentManager(), "race");
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Unable to build ui: \n" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException("Unable to build ui", e);
                }

            }
        });
        classes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CharacterLevelsDialogFragment dialog = CharacterLevelsDialogFragment.createDialog(getCharacter());
                    dialog.show(getFragmentManager(), "classes");
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Unable to build ui: \n" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException("Unable to build ui", e);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateViews();
    }

}

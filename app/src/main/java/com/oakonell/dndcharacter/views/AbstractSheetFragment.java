package com.oakonell.dndcharacter.views;


import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.AbstractBaseActivity;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.views.background.ApplyBackgroundDialogFragment;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.views.race.ApplyRaceDialogFragment;

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
                ((AbstractBaseActivity) getActivity()).populateRecentCharacters();
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

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Background background = new Select().from(Background.class).where("name = ?", character.getBackgroundName()).executeSingle();
                    ApplyBackgroundDialogFragment dialog = ApplyBackgroundDialogFragment.createDialog(character, background);
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
                    Race race= new Select().from(Race.class).where("name = ?", character.getRaceName()).executeSingle();
                    ApplyRaceDialogFragment dialog = ApplyRaceDialogFragment.createDialog(character, race);
                    dialog.show(getFragmentManager(), "race");
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Unable to build ui: \n" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException("Unable to build ui", e);
                }

            }
        });
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
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

package com.oakonell.dndcharacter.background;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.MainActivity;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.background.ApplyBackgroundToCharacterVisitor;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.model.background.SavedChoices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class ApplyBackgroundDialogFragment extends DialogFragment {
    private Background background;
    private Map<String, BackgroundViewCreatorVisitor.ChooseMD> chooseMDs;
    private Character character;

    public static ApplyBackgroundDialogFragment createDialog(Character character, Background background) {
        ApplyBackgroundDialogFragment newMe = new ApplyBackgroundDialogFragment();
        newMe.setBackground(background);
        newMe.setCharacter(character);
        return newMe;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.background_dialog, container);

        final ViewGroup dynamic = (ViewGroup) view.findViewById(R.id.dynamic_content);
        Button done = (Button) view.findViewById(R.id.done);
        final Spinner name = (Spinner) view.findViewById(R.id.name);

        List<String> list = new ArrayList<String>();
        final List<Background> backgrounds = new Select()
                .from(Background.class).orderBy("name").execute();
        int index = 0;
        int current = -1;
        for (Background each : backgrounds) {
            if (each.name.equals(character.getBackgroundName())) {
                current = index;
            }
            list.add(each.name);
            index++;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.large_spinner_text, list);
        dataAdapter.setDropDownViewResource(R.layout.large_spinner_text);
        name.setAdapter(dataAdapter);

        name.setSelection(current);
        // TODO split into multiple pages, especially for personality traits

        BackgroundViewCreatorVisitor visitor = new BackgroundViewCreatorVisitor();
        chooseMDs = visitor.appendToLayout(background, dynamic, character, character.getBackgroundChoices());

        name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Background newBackground = backgrounds.get(position);
                if (newBackground == background) return;

                dynamic.removeAllViews();
                BackgroundViewCreatorVisitor visitor = new BackgroundViewCreatorVisitor();
                chooseMDs = visitor.appendToLayout(newBackground, dynamic, character, new SavedChoices());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectBackgroundIndex = name.getSelectedItemPosition();
                Background background = backgrounds.get(selectBackgroundIndex);
                ApplyBackgroundToCharacterVisitor visitor = new ApplyBackgroundToCharacterVisitor(dynamic, chooseMDs, character);
                visitor.visit(background);
                dismiss();
                ((MainActivity) getActivity()).updateViews();
            }
        });

        return view;
    }

    public void setBackground(Background background) {
        this.background = background;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }
}
package com.oakonell.dndcharacter;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.views.AbstractSheetFragment;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Created by Rob on 10/26/2015.
 */
public class PersonaFragment extends AbstractSheetFragment {
    TextView age;
    TextView height;
    TextView weight;
    TextView eyes;
    TextView skin;
    TextView hair;
    EditText traits;
    EditText ideals;
    EditText bonds;
    EditText flaws;
    EditText backstory;
    TextView languages;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.persona_sheet, container, false);

        superCreateViews(rootView);
        age = (TextView) rootView.findViewById(R.id.age);
        height = (TextView) rootView.findViewById(R.id.height);
        weight = (TextView) rootView.findViewById(R.id.weight);
        eyes = (TextView) rootView.findViewById(R.id.eyes);
        skin = (TextView) rootView.findViewById(R.id.skin);
        hair = (TextView) rootView.findViewById(R.id.hair);
        languages = (TextView) rootView.findViewById(R.id.languages);

        traits = (EditText) rootView.findViewById(R.id.personality_traits);
        ideals = (EditText) rootView.findViewById(R.id.ideals);
        bonds = (EditText) rootView.findViewById(R.id.bonds);
        flaws = (EditText) rootView.findViewById(R.id.flaws);
        backstory = (EditText) rootView.findViewById(R.id.backstory);


        traits.addTextChangedListener(new AfterChangedWatcher() {
            @Override
            void textChanged(String string) {
                character.setPersonalityTrait(string);
            }
        });
        ideals.addTextChangedListener(new AfterChangedWatcher() {
            @Override
            void textChanged(String string) {
                character.setIdeals(string);
            }
        });
        bonds.addTextChangedListener(new AfterChangedWatcher() {
            @Override
            void textChanged(String string) {
                character.setBonds(string);
            }
        });
        flaws.addTextChangedListener(new AfterChangedWatcher() {
            @Override
            void textChanged(String string) {
                character.setFlaws(string);
            }
        });
        backstory.addTextChangedListener(new AfterChangedWatcher() {
            @Override
            void textChanged(String string) {
                character.setBackstory(string);
            }
        });

        updateViews(rootView);

        // need to hook a notes text watcher, to update the model
        return rootView;
    }

    @Override
    protected void updateViews(View rootView) {
        super.updateViews(rootView);
        backstory.setText(character.getBackstory());
        traits.setText(character.getPersonalityTrait());
        ideals.setText(character.getIdeals());
        bonds.setText(character.getBonds());
        flaws.setText(character.getFlaws());
        languages.setText(character.getLanguagesString());
    }

    private static abstract class AfterChangedWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            textChanged(s.toString());
        }

        abstract void textChanged(String string);
    }
}

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
    private static final Object NON_USER_CHANGE = new Object();
    TextView age;
    TextView height;
    TextView weight;
    TextView eyes;
    TextView skin;
    TextView hair;

    TextView specialty;
    TextView specialtyTitle;
    ViewGroup specialtyGroup;
    EditText traits;
    EditText ideals;
    EditText bonds;
    EditText flaws;
    EditText backstory;
    TextView languages;
    ViewGroup languageGroup;

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
        languageGroup = (ViewGroup) rootView.findViewById(R.id.language_group);

        specialtyGroup = (ViewGroup) rootView.findViewById(R.id.specialty_group);
        specialtyTitle = (TextView) rootView.findViewById(R.id.specialty_title);
        specialty = (TextView) rootView.findViewById(R.id.specialty);

        traits = (EditText) rootView.findViewById(R.id.personality_traits);
        ideals = (EditText) rootView.findViewById(R.id.ideals);
        bonds = (EditText) rootView.findViewById(R.id.bonds);
        flaws = (EditText) rootView.findViewById(R.id.flaws);
        backstory = (EditText) rootView.findViewById(R.id.backstory);


        traits.addTextChangedListener(new AfterChangedWatcher() {
            @Override
            void textChanged(String string) {
                if (traits.getTag() == NON_USER_CHANGE) return;
                character.setPersonalityTrait(string);
                character.setTraitSavedChoiceToCustom("traits");
            }
        });
        ideals.addTextChangedListener(new AfterChangedWatcher() {
            @Override
            void textChanged(String string) {
                if (ideals.getTag() == NON_USER_CHANGE) return;
                character.setIdeals(string);
                character.setTraitSavedChoiceToCustom("ideals");
            }
        });
        bonds.addTextChangedListener(new AfterChangedWatcher() {
            @Override
            void textChanged(String string) {
                if (bonds.getTag() == NON_USER_CHANGE) return;
                character.setBonds(string);
                character.setTraitSavedChoiceToCustom("bonds");
            }
        });
        flaws.addTextChangedListener(new AfterChangedWatcher() {
            @Override
            void textChanged(String string) {
                if (flaws.getTag() == NON_USER_CHANGE) return;
                character.setFlaws(string);
                character.setTraitSavedChoiceToCustom("flaws");
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

        final String specialtyString = character.getSpecialty();
        if (specialtyString != null && specialtyString.trim().length() > 0) {
            specialtyGroup.setVisibility(View.VISIBLE);
            specialtyTitle.setText(character.getSpecialtyTitle());
            specialty.setText(specialtyString);
        } else {
            specialtyGroup.setVisibility(View.GONE);
        }
        nonUserUpdate(traits, character.getPersonalityTrait());
        nonUserUpdate(ideals, character.getIdeals());
        nonUserUpdate(bonds, character.getBonds());
        nonUserUpdate(flaws, character.getFlaws());

        languages.setText(character.getLanguagesString());

        languageGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguagesDialogFragment fragment = LanguagesDialogFragment.create((MainActivity) getActivity());
                fragment.show(getFragmentManager(), "language_dialog");
            }
        });
    }

    private void nonUserUpdate(EditText editText, String value) {
        editText.setTag(NON_USER_CHANGE);
        editText.setText(value);
        editText.setTag(null);
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

package com.oakonell.dndcharacter.views.character.persona;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Alignment;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.character.AbstractSheetFragment;

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

    TextView xp;
    ViewGroup xpGroup;
    private ViewGroup alignment_group;
    private TextView alignment;

    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
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

        alignment_group = (ViewGroup) rootView.findViewById(R.id.alignment_group);
        alignment = (TextView) rootView.findViewById(R.id.alignment);

        xp = (TextView) rootView.findViewById(R.id.xp);
        xpGroup = (ViewGroup) rootView.findViewById(R.id.xp_group);

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
                if (getCharacter() == null) return;
                if (notDifferent(getCharacter().getPersonalityTrait(), string)) return;
                getCharacter().setPersonalityTrait(string);
                getCharacter().setTraitSavedChoiceToCustom("traits");
            }
        });
        ideals.addTextChangedListener(new AfterChangedWatcher() {
            @Override
            void textChanged(String string) {
                if (ideals.getTag() == NON_USER_CHANGE) return;
                if (getCharacter() == null) return;
                if (notDifferent(getCharacter().getIdeals(), string)) return;
                getCharacter().setIdeals(string);
                getCharacter().setTraitSavedChoiceToCustom("ideals");
            }
        });
        bonds.addTextChangedListener(new AfterChangedWatcher() {
            @Override
            void textChanged(String string) {
                if (bonds.getTag() == NON_USER_CHANGE) return;
                if (getCharacter() == null) return;
                if (notDifferent(getCharacter().getBonds(), string)) return;
                getCharacter().setBonds(string);
                getCharacter().setTraitSavedChoiceToCustom("bonds");
            }
        });
        flaws.addTextChangedListener(new AfterChangedWatcher() {
            @Override
            void textChanged(String string) {
                if (flaws.getTag() == NON_USER_CHANGE) return;
                if (getCharacter() == null) return;
                if (notDifferent(getCharacter().getFlaws(), string)) return;
                getCharacter().setFlaws(string);
                getCharacter().setTraitSavedChoiceToCustom("flaws");
            }
        });
        backstory.addTextChangedListener(new AfterChangedWatcher() {
            @Override
            void textChanged(String string) {
                if (getCharacter() == null) return;
                getCharacter().setBackstory(string);
            }
        });

        // need to hook a notes text watcher, to update the model
        return rootView;
    }

    private boolean notDifferent(String string, String newString) {
        String value = string;
        if (value == null) value = "";
        String newValue = newString;
        if (newValue == null) newValue = "";
        return value.trim().equals(newValue.trim());
    }

    @Override
    protected void updateViews(@Nullable View rootView) {
        super.updateViews(rootView);
        Character character = getCharacter();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RaceLooksDialogFragment dialog = RaceLooksDialogFragment.create();
                dialog.show(getFragmentManager(), "race_looks_dialog");
            }
        };
        // weird state
        if (rootView == null) return;
        if (character.getRaceName() != null) {
            rootView.findViewById(R.id.looks_group).setOnClickListener(onClickListener);
        } else {
            // TODO perhaps should launch the choose a race dialog, or maybe just an alert that race should be chosen
            rootView.findViewById(R.id.looks_group).setOnClickListener(null);
        }


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
                LanguagesDialogFragment fragment = LanguagesDialogFragment.create();
                fragment.show(getFragmentManager(), "language_dialog");
            }
        });

        final Alignment alignment = character.getAlignment();
        if (alignment != null) {
            this.alignment.setText(getString(alignment.getStringResId()));
        } else {
            this.alignment.setText("");
        }
        alignment_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlignmentDialogFragment fragment = AlignmentDialogFragment.create();
                fragment.show(getFragmentManager(), "alignment_dialog");
            }
        });

        xp.setText(NumberUtils.formatNumber(character.getXp()));
        xpGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExperienceDialogFragment fragment = ExperienceDialogFragment.create();
                fragment.show(getFragmentManager(), "xp_dialog");
            }
        });


        age.setText(character.getAge());
        weight.setText(character.getWeight());
        height.setText(character.getHeight());
        hair.setText(character.getHair());
        skin.setText(character.getSkin());
        eyes.setText(character.getEyes());

    }

    private void nonUserUpdate(@NonNull EditText editText, String value) {
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
        public void afterTextChanged(@Nullable Editable s) {
            if (s == null) {
                textChanged("");
                return;
            }
            textChanged(s.toString());
        }

        abstract void textChanged(String string);
    }
}

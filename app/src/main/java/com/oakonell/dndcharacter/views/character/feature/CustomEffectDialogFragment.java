package com.oakonell.dndcharacter.views.character.feature;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

/**
 * Created by Rob on 1/3/2016.
 */
public class CustomEffectDialogFragment extends AbstractCharacterDialogFragment {

    private EditText name;
    private EditText description;

    @NonNull
    public static CustomEffectDialogFragment createDialog() {
        final CustomEffectDialogFragment dialogFragment = new CustomEffectDialogFragment();
        return dialogFragment;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.new_custom_effect);
    }


    public View onCreateTheView(@NonNull LayoutInflater inflater, final ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_effect_dialog, container);

        name = (EditText) view.findViewById(R.id.name);
        description = (EditText) view.findViewById(R.id.description);

        description.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    pressDone();
                }
                return true;
            }
        });

        return view;
    }


    @Override
    protected boolean onDone() {
        boolean isValid = true;
        String nameString = name.getText().toString();
        if (nameString.length() == 0) {
            name.setError(getString(R.string.enter_a_name));
            Animation shake = AnimationUtils.loadAnimation(name.getContext(), R.anim.shake);
            name.startAnimation(shake);
            isValid = false;
        }
        final String descriptionString = description.getText().toString();
        if (descriptionString.length() == 0) {
            description.setError(getString(R.string.enter_an_ac));
            Animation shake = AnimationUtils.loadAnimation(description.getContext(), R.anim.shake);
            description.startAnimation(shake);
            isValid = false;
        }
        if (isValid) {
            CharacterEffect effect = new CharacterEffect();
            effect.setName(nameString);
            effect.setDescription(descriptionString);
            getCharacter().addEffect(effect);

            getMainActivity().updateViews();
            getMainActivity().saveCharacter();
        }
        return super.onDone();

    }
}

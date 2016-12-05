package com.oakonell.dndcharacter.views.character;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.DeathSaveResult;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.views.NoDefaultSpinner;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 11/30/2015.
 */
public class DeathSaveDialogFragment extends RollableDialogFragment {

    private NoDefaultSpinner death_save_result;
    private List<DeathSaveResult> deathSaveResultValues;

    @NonNull
    public static DeathSaveDialogFragment create(boolean isForCompanion) {
        final DeathSaveDialogFragment fragment = new DeathSaveDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(COMPANION_ARG, isForCompanion);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.death_save_dialog, container);
        superCreateView(view, savedInstanceState);

        death_save_result = (NoDefaultSpinner) view.findViewById(R.id.death_save_result);

        final String prompt = getString(R.string.death_save_prompt);
        death_save_result.setPrompt(prompt);
        float minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (prompt.length() + 2) * NoDefaultSpinner.SPINNER_TEXT_SP, death_save_result.getResources().getDisplayMetrics());
        death_save_result.setMinimumWidth((int) minWidth);

        List<String> deathSaveResults = new ArrayList<>();
        deathSaveResultValues = new ArrayList<>();
        deathSaveResultValues.add(DeathSaveResult.CRIT_FAIL);
        deathSaveResultValues.add(DeathSaveResult.FAIL);
        deathSaveResultValues.add(DeathSaveResult.PASS);
        deathSaveResultValues.add(DeathSaveResult.CRIT_PASS);
        for (DeathSaveResult each : deathSaveResultValues) {
            deathSaveResults.add(getString(each.getStringResId()));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                R.layout.large_spinner_text, deathSaveResults);
        dataAdapter.setDropDownViewResource(R.layout.large_spinner_text);
        death_save_result.setAdapter(dataAdapter);


        return view;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.death_save_roll);
    }

    @NonNull
    @Override
    protected Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.DEATH_SAVE));
        return filter;
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);
        setModifier(0);
    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        super.onCharacterChanged(character);
    }

    @Override
    protected void rollResult(int total) {
        if (total == 0) {
            return;
        }
        if (total <= 1) {
            death_save_result.setSelection(deathSaveResultValues.indexOf(DeathSaveResult.CRIT_FAIL));
        } else if (total <= 9) {
            death_save_result.setSelection(deathSaveResultValues.indexOf(DeathSaveResult.FAIL));
        } else if (total <= 19) {
            death_save_result.setSelection(deathSaveResultValues.indexOf(DeathSaveResult.PASS));
        } else if (total >= 20) {
            death_save_result.setSelection(deathSaveResultValues.indexOf(DeathSaveResult.CRIT_PASS));
        }
    }


    @Override
    protected boolean onDone() {
        if (death_save_result.getSelectedItemPosition() < 0) {
            Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
            death_save_result.startAnimation(shake);
            return false;
        }
        AbstractCharacter character = getCharacter();
        if (isForCompanion()) {
            character = getCharacter().getDisplayedCompanion();
        }
        int position = death_save_result.getSelectedItemPosition();
        final DeathSaveResult result = deathSaveResultValues.get(position);
        switch (result) {
            case CRIT_FAIL:
                character.failDeathSave();
                character.failDeathSave();
                break;
            case FAIL:
                character.failDeathSave();
                break;
            case PASS:
                character.passDeathSave();
                break;
            case CRIT_PASS:
                character.stabilize();
                character.setHP(1);
                break;
        }
        return super.onDone();
    }
}

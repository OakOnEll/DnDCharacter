package com.oakonell.dndcharacter.views.character.rest;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.rest.AbstractRestRequest;
import com.oakonell.dndcharacter.views.character.CharacterActivity;

/**
 * Created by Rob on 12/19/2016.
 */

public abstract class RestHealingViewHelper<T extends AbstractRestRequest> {
    private final AbstractRestDialogFragment context;

    private View extraHealingGroup;
    private TextView extraHealingtextView;
    private TextView finalHp;
    private TextView startHp;
    private View finalHpGroup;
    private View noHealingGroup;
    private AbstractCharacter character;

    protected RestHealingViewHelper(AbstractRestDialogFragment context) {
        this.context = context;
    }

    protected AbstractRestDialogFragment getFragment() {
        return context;
    }

    protected boolean allowExtraHealing() {
        return getCharacter().getHP() != getCharacter().getMaxHP();
    }

    public void conditionallyShowExtraHealing() {
        extraHealingGroup.setVisibility(allowExtraHealing() ? View.VISIBLE : View.GONE);
    }

    public void onCharacterLoaded(AbstractCharacter character) {
        this.character = character;
        conditionallyShowExtraHealing();

        if (character.getHP() == character.getMaxHP()) {
            noHealingGroup.setVisibility(View.VISIBLE);

            finalHpGroup.setVisibility(View.GONE);
        } else {
            noHealingGroup.setVisibility(View.GONE);

            finalHpGroup.setVisibility(View.VISIBLE);
        }

        startHp.setText(context.getString(R.string.fraction_d_slash_d, character.getHP(), character.getMaxHP()));

    }

    public void configureCommon(View view, Bundle savedInstanceState) {
        startHp = (TextView) view.findViewById(R.id.start_hp);
        finalHp = (TextView) view.findViewById(R.id.final_hp);
        finalHpGroup = view.findViewById(R.id.final_hp_group);
        extraHealingGroup = view.findViewById(R.id.extra_heal_group);
        extraHealingtextView = (TextView) view.findViewById(R.id.extra_healing);
        noHealingGroup = view.findViewById(R.id.no_healing_group);


        extraHealingtextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateView(getCharacter());
            }
        });
    }

    public AbstractCharacter getCharacter() {
        return character;
    }

    public int getExtraHealing() {
        String extraHealString = extraHealingtextView.getText().toString();
        if (extraHealString.trim().length() > 0) {
            return Integer.parseInt(extraHealString);
        }
        return 0;
    }

    public void updateView(AbstractCharacter character) {
        int hp = character.getHP();
        int healing = getHealing();
        hp = Math.min(hp + healing, character.getMaxHP());

        finalHp.setText(context.getString(R.string.fraction_d_slash_d, hp, character.getMaxHP()));
    }

    protected abstract int getHealing();

    public void onCharacterChanged(AbstractCharacter character) {

    }

    public abstract boolean updateRequest(T request);
}

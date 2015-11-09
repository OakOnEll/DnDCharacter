package com.oakonell.dndcharacter.rest;

import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.*;
import com.oakonell.dndcharacter.model.Character;

/**
 * Created by Rob on 11/8/2015.
 */
public abstract class AbstractRestDialogFragment extends DialogFragment {
    protected com.oakonell.dndcharacter.model.Character character;

    public void setCharacter(Character character) {
        this.character = character;
    }

    private TextView extraHealingtextView;
    private TextView finalHp;
    private TextView startHp;
    private View finalHpGroup;
    protected View extraHealingGroup;
    private View featureResetsGroup;
    private View noHealingGroup;


    protected void configureCommon(View view) {
        featureResetsGroup = view.findViewById(R.id.feature_resets);
        startHp = (TextView) view.findViewById(R.id.start_hp);
        finalHp = (TextView) view.findViewById(R.id.final_hp);
        finalHpGroup = (View) view.findViewById(R.id.final_hp_group);
        extraHealingGroup = (View) view.findViewById(R.id.extra_heal_group);
        extraHealingtextView = (TextView) view.findViewById(R.id.extra_healing);
        noHealingGroup = (View) view.findViewById(R.id.no_healing_group);

        if (character.getHP() == character.getMaxHP()) {
            noHealingGroup.setVisibility(View.VISIBLE);

            finalHpGroup.setVisibility(View.GONE);
            extraHealingGroup.setVisibility(View.GONE);
        } else {
            noHealingGroup.setVisibility(View.GONE);

            finalHpGroup.setVisibility(View.VISIBLE);
            extraHealingGroup.setVisibility(View.VISIBLE);
        }

        extraHealingtextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateView();
            }
        });
        startHp.setText(character.getHP() + " / " + character.getMaxHP());


        featureResetsGroup.setVisibility(View.GONE);
    }

    protected int getExtraHealing() {
        String extraHealString = extraHealingtextView.getText().toString();
        if (extraHealString != null && extraHealString.trim().length() > 0) {
            return Integer.parseInt(extraHealString);
        }
        return 0;
    }

    public void updateView() {
        int hp = character.getHP();
        int healing = getHealing();
        hp = Math.min(hp + healing, character.getMaxHP());
        finalHp.setText(hp + " / " + character.getMaxHP());
    }

    protected abstract int getHealing();

}

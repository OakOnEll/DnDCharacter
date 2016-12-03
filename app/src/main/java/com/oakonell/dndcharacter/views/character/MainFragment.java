package com.oakonell.dndcharacter.views.character;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.SpeedType;
import com.oakonell.dndcharacter.model.character.stats.SkillType;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.character.feat.InitiativeDialogFragment;
import com.oakonell.dndcharacter.views.character.feat.PassivePerceptionDialogFragment;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;
import com.oakonell.dndcharacter.views.character.item.ArmorClassDialogFragment;
import com.oakonell.dndcharacter.views.character.race.SpeedDialogFragment;
import com.oakonell.dndcharacter.views.character.stats.SaveThrowBlockDialogFragment;
import com.oakonell.dndcharacter.views.character.stats.SavingThrowBlockView;
import com.oakonell.dndcharacter.views.character.stats.SkillBlockDialogFragment;
import com.oakonell.dndcharacter.views.character.stats.SkillBlockView;
import com.oakonell.dndcharacter.views.character.stats.StatBlockDialogFragment;
import com.oakonell.dndcharacter.views.character.stats.StatBlockView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 10/22/2015.
 */
public class MainFragment extends AbstractSheetFragment {

    AbstractCharacterViewHelper characterViewHelper = new AbstractCharacterViewHelper(this);

    TextView hitDice;

    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_sheet, container, false);
        Log.d("Main", "onCreateTheView");

        superCreateViews(rootView);
        characterViewHelper.onCreateView(rootView);
        hitDice = (TextView) rootView.findViewById(R.id.hit_dice);

        return rootView;
    }


    protected void updateViews(@NonNull View rootView) {
        super.updateViews(rootView);
        Log.d("Main", "updateViews");
        final Character character = getCharacter();
        characterViewHelper.updateViews(rootView, character);

        hitDice.setText(character.getHitDiceString());


    }

}

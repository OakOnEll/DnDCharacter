package com.oakonell.dndcharacter.views.character;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;

/**
 * Created by Rob on 10/22/2015.
 */
public class MainFragment extends AbstractSheetFragment {

    AbstractCharacterViewHelper characterViewHelper = new AbstractCharacterViewHelper(this, false);

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

        if (hitDice == null) return;

        hitDice.setText(character.getHitDiceString());


    }

}

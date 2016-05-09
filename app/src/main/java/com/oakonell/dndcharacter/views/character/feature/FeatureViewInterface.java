package com.oakonell.dndcharacter.views.character.feature;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rob on 5/6/2016.
 */
public interface FeatureViewInterface {
    TextView getSourceTextView();

    ViewGroup getLimitedUsesGroupViewGroup();

    TextView getUsesLabelTextView();

    TextView getShortDescriptionTextView();

    EditText getUsesRemainingEditText();
    TextView getUsesRemainingReadOnlyTextView();

    TextView getRefreshesLabelTextView();

    TextView getUsesTotalTextView();

    boolean isReadOnly();

    Spinner getSpellSlotLevelSpinner();

    Button getUseSpellSlotButton();

    ViewGroup getSpellSlotUseGroup();

    ArrayList<String> getSpellLevels();

    Button getAddUseButton();

    Button getSubtractUseButton();
}

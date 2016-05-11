package com.oakonell.dndcharacter.views.character.feature;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.character.*;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.components.Feature;
import com.oakonell.dndcharacter.model.components.IFeatureAction;
import com.oakonell.dndcharacter.views.character.CharacterActivity;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

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

    RecyclerView getActionListView();

    Set<FeatureContextArgument> getActionFilter();

    int getUsesRemaining(CharacterActivity context, FeatureInfo info);

    int getSpellSlotsAvailable(Character.SpellLevelInfo each);

    void useAction(CharacterActivity context, FeatureInfo info, IFeatureAction action, Map<String, String> values);

    void useFeature(CharacterActivity context, FeatureInfo info, int value);

    void useSpellSlot(final CharacterActivity context, Character.SpellLevelInfo levelInfo);
}

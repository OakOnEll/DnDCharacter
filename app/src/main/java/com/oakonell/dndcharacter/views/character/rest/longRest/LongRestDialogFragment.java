package com.oakonell.dndcharacter.views.character.rest.longRest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.character.rest.LongRestRequest;
import com.oakonell.dndcharacter.model.components.RefreshType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;
import com.oakonell.dndcharacter.views.character.rest.AbstractRestDialogFragment;
import com.oakonell.dndcharacter.views.character.rest.RestHealingViewHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by Rob on 11/8/2015.
 */
public class LongRestDialogFragment extends AbstractRestDialogFragment<LongRestRequest, LongRestHeadlerViewHelper> {
    public static final String DICE_RESTORES = "diceRestores";
    @Nullable
    private Bundle savedDiceRestoreBundle;



    @NonNull
    protected LongRestHeadlerViewHelper createHealingViewHelper() {
        return new LongRestHeadlerViewHelper(this);
    }

    @NonNull
    public static LongRestDialogFragment createDialog() {
        return new LongRestDialogFragment();
    }


    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.long_rest_dialog, container);
        configureCommon(view, savedInstanceState);

        // TODO
        if (savedInstanceState != null) {
            savedDiceRestoreBundle = savedInstanceState.getBundle(DICE_RESTORES);
        }


        return view;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.long_rest_title);
    }

    @NonNull
    @Override
    protected Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.LONG_REST));
        return filter;
    }

    @Override
    protected FeatureContextArgument getNoteContext() {
        return new FeatureContextArgument(FeatureContext.LONG_REST);
    }

    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        super.onCharacterLoaded(character);
        if (savedDiceRestoreBundle != null) {
            for (HitDieRestoreRow each : getHealingViewHelper().diceAdapter.diceCounts) {
                int savedNum = savedDiceRestoreBundle.getInt(each.dieSides + "", -1);
                if (savedNum >= 0) {
                    each.numDiceToRestore = savedNum;
                }
            }
            savedDiceRestoreBundle = null;
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle diceRestoreBundle = new Bundle();
        for (HitDieRestoreRow each : getHealingViewHelper().diceAdapter.diceCounts) {
            int sides = each.dieSides;
            diceRestoreBundle.putInt(sides + "", each.numDiceToRestore);
        }
        outState.putBundle(DICE_RESTORES, diceRestoreBundle);
    }

    @Override
    protected boolean onDone() {
        boolean isValid = true;
        LongRestRequest request = new LongRestRequest();

        //noinspection ConstantConditions
        isValid = isValid && updateCommonRequest(request);
        isValid = isValid && super.onDone();
        if (isValid) {
            getCharacter().longRest(request);
        }
        return isValid;
    }

    @Override
    protected boolean shouldReset(RefreshType refreshesOn) {
        return true;
    }

    protected int getSlotsToRestore(@NonNull Character.SpellLevelInfo each) {
        return each.getMaxSlots() - each.getSlotsAvailable();
    }

}

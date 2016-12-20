package com.oakonell.dndcharacter.views.character.rest.shortRest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.character.rest.ShortRestRequest;
import com.oakonell.dndcharacter.model.components.RefreshType;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;
import com.oakonell.dndcharacter.views.character.rest.AbstractRestDialogFragment;
import com.oakonell.expression.context.SimpleVariableContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Rob on 11/7/2015.
 */
public class ShortRestDialogFragment extends AbstractRestDialogFragment<ShortRestRequest, ShortRestHeadlerViewHelper> {
    public static final String DICE_COUNTS = "diceCounts";
    @Nullable
    private Map<Integer, HitDieUseRow> savedDiceCounts;


    @NonNull
    protected ShortRestHeadlerViewHelper createHealingViewHelper() {
        return new ShortRestHeadlerViewHelper(this);
    }

    @NonNull
    public static ShortRestDialogFragment createDialog() {
        return new ShortRestDialogFragment();
    }

    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.short_rest_dialog, container);
        configureCommon(view, savedInstanceState);

        if (savedInstanceState != null) {
            ArrayList<HitDieUseRow> savedList = savedInstanceState.getParcelableArrayList(DICE_COUNTS);
            if (savedList != null) {
                savedDiceCounts = new HashMap<>();
                for (HitDieUseRow each : savedList) {
                    savedDiceCounts.put(each.dieSides, each);
                }
            }
        }

        return view;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.short_rest_title);
    }

    @NonNull
    @Override
    protected Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.SHORT_REST));
        filter.add(new FeatureContextArgument(FeatureContext.DICE_ROLL));
        return filter;
    }

    @Override
    protected FeatureContextArgument getNoteContext() {
        return new FeatureContextArgument(FeatureContext.SHORT_REST);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(DICE_COUNTS, getHealingViewHelper().diceAdapter.diceCounts);
    }

    @Override
    protected boolean onDone() {
        boolean isValid = true;
        ShortRestRequest request = new ShortRestRequest();

        //noinspection ConstantConditions
        isValid = isValid && updateCommonRequest(request);
        isValid = isValid && super.onDone();
        if (isValid) {
            getCharacter().shortRest(request);
        }
        return isValid;
    }

    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        super.onCharacterLoaded(character);

        if (savedDiceCounts != null) {
            getHealingViewHelper().diceAdapter.populateDiceCounts(character, savedDiceCounts);
            savedDiceCounts = null;
        }
        updateView();
    }

    @Override
    protected boolean shouldReset(RefreshType refreshesOn) {
        return refreshesOn == RefreshType.SHORT_REST;
    }


    protected int getSlotsToRestore(@NonNull Character.SpellLevelInfo info) {
        int value = 0;
        for (Character.CastingClassInfo each : getCharacter().getCasterClassInfo()) {
            final RefreshType refreshType = each.getSpellSlotRefresh();
            if (refreshType != RefreshType.SHORT_REST) continue;
            final String slotFormula = each.getSlotMap().get(info.getLevel());
            if (slotFormula == null || slotFormula.length() == 0) continue;

            SimpleVariableContext variableContext = new SimpleVariableContext();
            variableContext.setNumber("classLevel", each.getClassLevel());
            value += getCharacter().evaluateFormula(slotFormula, variableContext);
        }
        return Math.min(info.getMaxSlots() - info.getSlotsAvailable(), value);
    }

}
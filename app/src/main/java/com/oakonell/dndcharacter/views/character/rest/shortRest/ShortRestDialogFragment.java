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
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;
import com.oakonell.dndcharacter.views.character.rest.AbstractRestDialogFragment;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rob on 11/7/2015.
 */
public class ShortRestDialogFragment extends AbstractRestDialogFragment<ShortRestRequest, ShortRestHeadlerViewHelper> {

    @NonNull
    protected ShortRestHeadlerViewHelper createHealingViewHelper() {
        return new ShortRestHeadlerViewHelper(this);
    }

    @NonNull
    public static ShortRestDialogFragment createDialog() {
        return new ShortRestDialogFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.short_rest_dialog;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.short_rest_title);
    }

    @NonNull
    @Override
    public Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.SHORT_REST));
        filter.add(new FeatureContextArgument(FeatureContext.DICE_ROLL));
        return filter;
    }

    @Override
    public FeatureContextArgument getNoteContext() {
        return new FeatureContextArgument(FeatureContext.SHORT_REST);
    }

    @Override
    protected ShortRestRequest createRestRequest(Character character) {
        return character.createShortRestRequest(getMainActivity());
    }
}
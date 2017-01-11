package com.oakonell.dndcharacter.views.character.rest.longRest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.character.rest.LongRestRequest;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;
import com.oakonell.dndcharacter.views.character.rest.AbstractRestDialogFragment;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rob on 11/8/2015.
 */
public class LongRestDialogFragment extends AbstractRestDialogFragment<LongRestRequest, LongRestHeadlerViewHelper> {

    @NonNull
    protected LongRestHeadlerViewHelper createHealingViewHelper() {
        return new LongRestHeadlerViewHelper(this);
    }

    @NonNull
    public static LongRestDialogFragment createDialog() {
        return new LongRestDialogFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.long_rest_dialog;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.long_rest_title);
    }

    @NonNull
    @Override
    public Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.LONG_REST));
        return filter;
    }

    @Override
    public FeatureContextArgument getNoteContext() {
        return new FeatureContextArgument(FeatureContext.LONG_REST);
    }

    @Override
    protected LongRestRequest createRestRequest(Character character) {
        return character.createLongRestRequest(getMainActivity());
    }
}

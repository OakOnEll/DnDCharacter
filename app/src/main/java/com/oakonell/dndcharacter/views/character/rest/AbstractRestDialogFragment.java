package com.oakonell.dndcharacter.views.character.rest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.rest.AbstractRestRequest;
import com.oakonell.dndcharacter.model.character.rest.CompanionRest;
import com.oakonell.dndcharacter.model.character.rest.LongRestRequest;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

import java.util.List;

/**
 * Created by Rob on 11/8/2015.
 */
public abstract class AbstractRestDialogFragment<RT extends AbstractRestRequest<?>, T extends RestHealingViewHelper<RT>> extends AbstractCharacterDialogFragment {
    public static final String REQUEST_SAVE_KEY = "request";

    T healingViewHelper = createHealingViewHelper();

    @NonNull
    protected abstract T createHealingViewHelper();

    protected T getHealingViewHelper() {
        return healingViewHelper;
    }

    private FeatureResetsAdapter featureResetAdapter;
    private RecyclerView featureListView;

    private SpellSlotsResetsAdapter spellSlotResetAdapter;
    private RecyclerView spell_slot_list;

    private CompanionResetsAdapter companionResetsAdapter;
    private RecyclerView companionListView;
    private View companion_resets;

    RT request;

    public RT getRequest() {
        return request;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            request = savedInstanceState.getParcelable(REQUEST_SAVE_KEY);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResourceId(), container);
        configureCommon(view, savedInstanceState);

        return view;
    }

    abstract protected int getLayoutResourceId();


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (featureResetAdapter == null) return;

        outState.putParcelable(REQUEST_SAVE_KEY, request);
    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        super.onCharacterChanged(character);
        healingViewHelper.onCharacterLoaded(request);
        updateView();
    }

    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        super.onCharacterLoaded(character);
        if (request == null) {
            request = createRestRequest(character);
        }
        healingViewHelper.onCharacterLoaded(request);

        buildFeatureResets();
        buildSpellSlotResets();
        buildCompanionRests();
        updateView();
    }

    protected abstract RT createRestRequest(Character character);

    private void buildCompanionRests() {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);

        final List<CompanionRest> companionRestRequests = (List<CompanionRest>) request.getCompanionRestRequests();
        companionResetsAdapter = new CompanionResetsAdapter(this, companionRestRequests);
        companionListView.setAdapter(companionResetsAdapter);

        companionListView.setHasFixedSize(false);
        companionListView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        companionListView.addItemDecoration(itemDecoration);

        if (companionRestRequests.isEmpty()) {
            companion_resets.setVisibility(View.GONE);
        } else {
            companion_resets.setVisibility(View.VISIBLE);
        }
    }

    protected void buildSpellSlotResets() {
        spellSlotResetAdapter = new SpellSlotsResetsAdapter(getActivity(), request.getSpellSlotResets());
        spell_slot_list.setAdapter(spellSlotResetAdapter);

        spell_slot_list.setHasFixedSize(false);
        spell_slot_list.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST);
        spell_slot_list.addItemDecoration(horizontalDecoration);
    }

    protected void buildFeatureResets() {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);


        featureResetAdapter = new FeatureResetsAdapter(getActivity(), request.getFeatureResets());
        featureListView.setAdapter(featureResetAdapter);

        featureListView.setHasFixedSize(false);
        featureListView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        featureListView.addItemDecoration(itemDecoration);
    }

    protected void configureCommon(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //featureResetsGroup = view.findViewById(R.id.feature_resets);
        healingViewHelper.configureCommon(view);


        featureListView = (RecyclerView) view.findViewById(R.id.feature_list);
        spell_slot_list = (RecyclerView) view.findViewById(R.id.spell_slot_list);
        companionListView = (RecyclerView) view.findViewById(R.id.companion_list);
        companion_resets = view.findViewById(R.id.companion_resets);
    }


    public void updateView() {
        Character character = getCharacter();
        if (character == null) return;
        healingViewHelper.updateView(request);
    }

    @Override
    protected boolean onDone() {
        boolean isValid = true;

        //noinspection ConstantConditions
        isValid = isValid && validateCommonRequest(request);
        isValid = isValid && super.onDone();
        if (isValid) {
            request.apply(getCharacter());
        }
        return isValid;
    }

    protected boolean validateCommonRequest(@NonNull RT request) {
        // TODO rename validate, and validate various values? Or is this done on per row basis...
//        boolean isValid = healingViewHelper.updateRequest(request);
//        for (FeatureResetInfo each : featureResetAdapter.resets) {
//            if (each.reset) {
//                isValid = isValid && each.numToRestore <= each.maxToRestore;
//                request.addFeatureReset(each.name, each.numToRestore);
//            }
//        }
//        for (SpellSlotResetInfo each : spellSlotResetAdapter.resets) {
//            if (each.reset) {
//                isValid = isValid && each.restoreSlots <= each.maxSlots - each.availableSlots;
//                request.addSpellSlotReset(each.level, each.restoreSlots);
//            }
//        }
//        for (CompanionResetInfo each : companionResetsAdapter.resets) {
//            if (each.reset) {
//                request.addCompanionReset(each.name, each.companionIndex);
//            }
//        }
        boolean isValid = true;
        return isValid;
    }

    @Override
    public void hideKeyboardFrom(@NonNull TextView v) {
        super.hideKeyboardFrom(v);
    }
}

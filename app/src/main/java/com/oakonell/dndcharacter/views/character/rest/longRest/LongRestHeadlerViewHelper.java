package com.oakonell.dndcharacter.views.character.rest.longRest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.rest.AbstractRestRequest;
import com.oakonell.dndcharacter.model.character.rest.LongRestRequest;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.rest.AbstractRestDialogFragment;
import com.oakonell.dndcharacter.views.character.rest.RestHealingViewHelper;

/**
 * Created by Rob on 12/19/2016.
 */

public class LongRestHeadlerViewHelper extends RestHealingViewHelper<LongRestRequest> {
    private View fullHealingGroup;
    private CheckBox fullHealing;
    protected HitDiceRestoreAdapter diceAdapter;
    private RecyclerView hitDiceListView;


    LongRestHeadlerViewHelper(LongRestDialogFragment context) {
        super(context);
    }

    @Override
    public int getCompanionRestLayoutId() {
        return R.layout.companion_long_rest_layout;
    }

    @Override
    public void configureCommon(View view) {
        super.configureCommon(view);
        fullHealingGroup = view.findViewById(R.id.full_heal_group);
        fullHealing = (CheckBox) view.findViewById(R.id.full_healing);

        hitDiceListView = (RecyclerView) view.findViewById(R.id.hit_dice_restore_list);

        fullHealing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                conditionallyShowExtraHealing();
                updateView(getRequest());
            }
        });

        // touch up hit die restore list header
        view.findViewById(R.id.restore_lbl).setVisibility(View.VISIBLE);
        view.findViewById(R.id.dice_to_restore).setVisibility(View.GONE);
    }

    @Override
    public void onCharacterLoaded(LongRestRequest request) {
        super.onCharacterLoaded(request);

        fullHealing.setChecked(getRequest().isFullHealing());

        AbstractRestDialogFragment fragment = getFragment();

        diceAdapter = new HitDiceRestoreAdapter(fragment, request);

        hitDiceListView.setAdapter(diceAdapter);

        hitDiceListView.setHasFixedSize(false);
        hitDiceListView.setLayoutManager(UIUtils.createLinearLayoutManager(fragment.getActivity(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(fragment.getActivity(), DividerItemDecoration.VERTICAL_LIST);
        hitDiceListView.addItemDecoration(itemDecoration);
    }

    @Override
    protected boolean allowExtraHealing() {
        return !getRequest().isFullHealing();
    }

    @Override
    public void updateView(LongRestRequest request) {
        super.updateView(request);
        if (request == null) return;
        if (allowExtraHealing()) {
            fullHealingGroup.setVisibility(View.GONE);
        } else {
            fullHealingGroup.setVisibility(View.VISIBLE);
        }
    }


}
package com.oakonell.dndcharacter.views.character.rest.shortRest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.rest.ShortRestRequest;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.rest.AbstractRestDialogFragment;
import com.oakonell.dndcharacter.views.character.rest.RestHealingViewHelper;

/**
 * Created by Rob on 12/19/2016.
 */

public class ShortRestHeadlerViewHelper extends RestHealingViewHelper<ShortRestRequest> {
    public HitDiceAdapter diceAdapter;
    private RecyclerView hitDiceListView;


    public ShortRestHeadlerViewHelper(ShortRestDialogFragment context) {
        super(context);
    }

    @Override
    public boolean updateRequest(ShortRestRequest request) {
        for (HitDieUseRow each : diceAdapter.diceCounts) {
            request.addHitDiceUsed(each.dieSides, each.rolls.size());
        }
        request.setHealing(getHealing());
        return true;
    }

    @Override
    public void configureCommon(View view) {
        super.configureCommon(view);
        hitDiceListView = (RecyclerView) view.findViewById(R.id.hit_dice_list);


    }

    protected int getHealing() {
        int healing = 0;
        for (HitDieUseRow each : diceAdapter.diceCounts) {
            for (Integer eachRoll : each.rolls) {
                healing += eachRoll;
            }
        }

        healing += getExtraHealing();
        return healing;
    }

    public void onCharacterChanged(AbstractCharacter character) {
        diceAdapter.reloadList(character);
    }

    @Override
    public void onCharacterLoaded(AbstractCharacter character) {
        super.onCharacterLoaded(character);

        AbstractRestDialogFragment fragment = getFragment();

        diceAdapter = new HitDiceAdapter(fragment, character);

        hitDiceListView.setAdapter(diceAdapter);
        hitDiceListView.setHasFixedSize(false);
        hitDiceListView.setLayoutManager(UIUtils.createLinearLayoutManager(fragment.getActivity(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(fragment.getActivity(), DividerItemDecoration.VERTICAL_LIST);
        hitDiceListView.addItemDecoration(itemDecoration);
    }

    @Override
    public void updateView(AbstractCharacter character) {
        super.updateView(character);
        if (diceAdapter != null) {
            diceAdapter.notifyDataSetChanged();
        }
        if (getCharacter().getHP() == getCharacter().getMaxHP()) {
            hitDiceListView.setVisibility(View.GONE);
        } else {
            hitDiceListView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getCompanionRestLayoutId() {
        return R.layout.companion_short_rest_layout;
    }

}
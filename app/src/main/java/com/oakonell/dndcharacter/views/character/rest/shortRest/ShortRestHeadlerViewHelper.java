package com.oakonell.dndcharacter.views.character.rest.shortRest;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.rest.ShortRestRequest;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
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
    public void configureCommon(View view) {
        super.configureCommon(view);
        hitDiceListView = (RecyclerView) view.findViewById(R.id.hit_dice_list);
    }

    @Override
    public void onCharacterLoaded(ShortRestRequest request) {
        super.onCharacterLoaded(request);

        AbstractRestDialogFragment fragment = getFragment();

        diceAdapter = new HitDiceAdapter(fragment, request);

        hitDiceListView.setAdapter(diceAdapter);
        hitDiceListView.setHasFixedSize(false);
        hitDiceListView.setLayoutManager(UIUtils.createLinearLayoutManager(fragment.getActivity(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(fragment.getActivity(), DividerItemDecoration.VERTICAL_LIST);
        hitDiceListView.addItemDecoration(itemDecoration);
    }

    @Override
    public void updateView(ShortRestRequest request) {
        super.updateView(request);
        if (diceAdapter != null) {
            diceAdapter.notifyDataSetChanged();
        }
        if (!allowExtraHealing()) {
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
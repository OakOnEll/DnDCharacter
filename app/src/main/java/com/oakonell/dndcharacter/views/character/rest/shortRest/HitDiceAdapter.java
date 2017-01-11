package com.oakonell.dndcharacter.views.character.rest.shortRest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.rest.ShortRestRequest;
import com.oakonell.dndcharacter.views.character.rest.AbstractRestDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 12/19/2016.
 */

public class HitDiceAdapter extends RecyclerView.Adapter<HitDiceViewHolder> {
    List<HitDieUseRow> diceCounts;
    private final AbstractRestDialogFragment context;

    public HitDiceAdapter(AbstractRestDialogFragment context, @NonNull ShortRestRequest request) {
        this.context = context;
        populateDiceCounts(request);
    }

    protected void populateDiceCounts(@NonNull ShortRestRequest request) {
        diceCounts = request.getHitDieUses();
    }

    public void reloadList(@NonNull ShortRestRequest request) {
        populateDiceCounts(request);
        notifyDataSetChanged();
    }

    public HitDieUseRow getItem(int position) {
        return diceCounts.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return diceCounts.size();
    }

    @NonNull
    @Override
    public HitDiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context.getActivity(), R.layout.hit_dice_item, null);
        HitDiceViewHolder viewHolder = new HitDiceViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final HitDiceViewHolder viewHolder, int position) {
        final HitDieUseRow row = getItem(position);
        viewHolder.bind(context, this, row);
    }


}
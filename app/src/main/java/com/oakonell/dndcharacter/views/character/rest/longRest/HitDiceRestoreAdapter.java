package com.oakonell.dndcharacter.views.character.rest.longRest;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.rest.LongRestRequest;
import com.oakonell.dndcharacter.views.character.rest.AbstractRestDialogFragment;

import java.util.List;

/**
 * Created by Rob on 12/19/2016.
 */

public class HitDiceRestoreAdapter extends RecyclerView.Adapter<HitDiceRestoreViewHolder> {
    List<HitDieRestoreRow> diceCounts;
    private final AbstractRestDialogFragment context;

    public HitDiceRestoreAdapter(AbstractRestDialogFragment context, @NonNull LongRestRequest request) {
        this.context = context;
        populateDiceCounts(request);
    }

    private void populateDiceCounts(@NonNull LongRestRequest request) {
        diceCounts = request.getHitDiceToRestore();
    }


    public void reloadList(@NonNull LongRestRequest request) {
        populateDiceCounts(request);
        notifyDataSetChanged();
    }

    public HitDieRestoreRow getItem(int position) {
        return diceCounts.get(position);
    }

    @NonNull
    @Override
    public HitDiceRestoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context.getActivity(), R.layout.hit_dice_restore_item, null);
        HitDiceRestoreViewHolder viewHolder = new HitDiceRestoreViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final HitDiceRestoreViewHolder viewHolder, final int position) {
        final HitDieRestoreRow row = getItem(position);
        viewHolder.bind(context.getActivity(), this, row);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return diceCounts.size();
    }

}
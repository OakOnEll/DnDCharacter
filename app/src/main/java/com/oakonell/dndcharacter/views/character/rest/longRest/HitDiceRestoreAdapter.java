package com.oakonell.dndcharacter.views.character.rest.longRest;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.views.character.rest.AbstractRestDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 12/19/2016.
 */

public class HitDiceRestoreAdapter extends RecyclerView.Adapter<HitDiceRestoreViewHolder> {
    List<HitDieRestoreRow> diceCounts;
    private final AbstractRestDialogFragment context;

    public HitDiceRestoreAdapter(AbstractRestDialogFragment context, @NonNull AbstractCharacter character) {
        this.context = context;
        populateDiceCounts(character);
    }

    private void populateDiceCounts(@NonNull AbstractCharacter character) {
        diceCounts = new ArrayList<>();
        for (Character.HitDieRow each : character.getHitDiceCounts()) {
            HitDieRestoreRow newRow = new HitDieRestoreRow();
            newRow.dieSides = each.dieSides;
            newRow.currentDiceRemaining = each.numDiceRemaining;
            newRow.totalDice = each.totalDice;
            newRow.numDiceToRestore = Math.min(Math.max(each.totalDice / 2, 1), each.totalDice - each.numDiceRemaining);

            diceCounts.add(newRow);
        }
    }


    public void reloadList(@NonNull AbstractCharacter character) {
        populateDiceCounts(character);
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
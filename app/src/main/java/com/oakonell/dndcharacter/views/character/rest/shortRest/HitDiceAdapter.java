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
import com.oakonell.dndcharacter.views.character.rest.AbstractRestDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 12/19/2016.
 */

public class HitDiceAdapter extends RecyclerView.Adapter<HitDiceViewHolder> {
    ArrayList<HitDieUseRow> diceCounts;
    private final AbstractRestDialogFragment context;

    public HitDiceAdapter(AbstractRestDialogFragment context, @NonNull AbstractCharacter character) {
        this.context = context;
        populateDiceCounts(character, null);
    }

    protected void populateDiceCounts(@NonNull AbstractCharacter character, @Nullable Map<Integer, HitDieUseRow> existingRows) {
        diceCounts = new ArrayList<>();
        for (Character.HitDieRow each : character.getHitDiceCounts()) {
            HitDieUseRow newRow = new HitDieUseRow();
            newRow.dieSides = each.dieSides;
            newRow.numDiceRemaining = each.numDiceRemaining;
            newRow.totalDice = each.totalDice;

            if (existingRows != null) {
                HitDieUseRow existing = existingRows.get(newRow.dieSides);
                if (existing != null) {
                    newRow.rolls.addAll(existing.rolls);
                    if (newRow.rolls.size() > newRow.numDiceRemaining && newRow.numDiceRemaining > 1) {
                        newRow.rolls.subList(0, newRow.numDiceRemaining - 1);
                    }
                    newRow.numDiceRemaining -= newRow.rolls.size();
                }
            }

            diceCounts.add(newRow);
        }
    }

    public void reloadList(@NonNull AbstractCharacter character) {
        // TODO don't throw away user entered data?
        Map<Integer, HitDieUseRow> existing = new HashMap<>();
        for (HitDieUseRow each : diceCounts) {
            existing.put(each.dieSides, each);
        }
        populateDiceCounts(character, existing);
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
package com.oakonell.dndcharacter.views.character.rest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.SpellSlotResetInfo;

import java.util.List;

/**
 * Created by Rob on 1/10/2017.
 */
class SpellSlotsResetsAdapter extends RecyclerView.Adapter<SpellSlotResetViewHolder> {
    private final List<SpellSlotResetInfo> resets;
    private final Context context;

    SpellSlotsResetsAdapter(Context context, List<SpellSlotResetInfo> resets) {
        this.context = context;
        this.resets = resets;
    }

    @NonNull
    @Override
    public SpellSlotResetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.rest_spell_slot_reset_item, null);
        SpellSlotResetViewHolder holder = new SpellSlotResetViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SpellSlotResetViewHolder holder, int position) {
        holder.bind(context, this, resets.get(position));
    }

    @Override
    public int getItemCount() {
        return resets.size();
    }
}

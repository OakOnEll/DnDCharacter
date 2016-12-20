package com.oakonell.dndcharacter.views.character.rest.shortRest;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;

import java.util.List;

/**
 * Created by Rob on 12/19/2016.
 */

class HitDieUseAdapter extends RecyclerView.Adapter<HitDieUseViewHolder> {
    List<Integer> rolls;
    int hitDieRow;
    final HitDiceAdapter adapter;

    HitDieUseAdapter(HitDiceAdapter adapter) {
        this.adapter = adapter;
    }

    @NonNull
    @Override
    public HitDieUseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.hit_die_use_row, null);
        HitDieUseViewHolder viewHolder = new HitDieUseViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HitDieUseViewHolder holder, final int position) {
        final Integer value = rolls.get(position);
        holder.bind(adapter, this, value);
    }

    @Override
    public int getItemCount() {
        return rolls == null ? 0 : rolls.size();
    }

    public void setData(int position, List<Integer> rolls) {
        this.hitDieRow = position;
        this.rolls = rolls;
        notifyDataSetChanged();
    }


    protected HitDiceAdapter getHitDiceAdapter() {
        return adapter;
    }

}
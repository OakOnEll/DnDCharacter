package com.oakonell.dndcharacter.views.character.rest.shortRest;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;

/**
 * Created by Rob on 12/19/2016.
 */

class HitDieUseViewHolder extends BindableComponentViewHolder<Integer, HitDiceAdapter, HitDieUseAdapter> {
    @NonNull
    final TextView value;
    @NonNull
    final View deleteButton;

    public HitDieUseViewHolder(@NonNull View view) {
        super(view);
        value = (TextView) view.findViewById(R.id.value);
        deleteButton = view.findViewById(R.id.delete_button);
    }

    @Override
    public void bind(@NonNull final HitDiceAdapter context, @NonNull final HitDieUseAdapter adapter, final Integer rollValue) {
        value.setText(NumberUtils.formatNumber(rollValue));
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.rolls.remove(getAdapterPosition());
                context.diceCounts.get(adapter.hitDieRow).numDiceRemaining++;
                adapter.notifyDataSetChanged();
                adapter.getHitDiceAdapter().notifyDataSetChanged();
            }
        });
    }
}
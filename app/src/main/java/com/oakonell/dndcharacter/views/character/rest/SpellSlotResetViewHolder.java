package com.oakonell.dndcharacter.views.character.rest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.SpellSlotResetInfo;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;

/**
 * Created by Rob on 1/10/2017.
 */
class SpellSlotResetViewHolder extends BindableComponentViewHolder<SpellSlotResetInfo, Context, SpellSlotsResetsAdapter> {
    @NonNull
    private final CheckBox level;
    @NonNull
    private final TextView slots;
    @NonNull
    private final TextView final_slots;

    public SpellSlotResetViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.findViewById(R.id.level_label).setVisibility(View.GONE);
        level = (CheckBox) itemView.findViewById(R.id.level);
        level.setVisibility(View.VISIBLE);
        slots = (TextView) itemView.findViewById(R.id.slots);
        final_slots = (TextView) itemView.findViewById(R.id.final_slots);
    }

    @Override
    public void bind(@NonNull Context context, @NonNull final SpellSlotsResetsAdapter adapter, @NonNull final SpellSlotResetInfo info) {
        level.setText(NumberUtils.formatNumber(info.level));
        level.setOnCheckedChangeListener(null);
        level.setChecked(info.reset);
        slots.setText(context.getString(R.string.fraction_d_slash_d, info.availableSlots, info.maxSlots));
        level.setEnabled(info.availableSlots < info.maxSlots);


        level.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    info.restoreSlots = info.maxSlots - info.availableSlots;
                } else {
                    info.restoreSlots = 0;
                }
                info.reset = isChecked;
                adapter.notifyItemChanged(getAdapterPosition());
            }
        });

        final_slots.setText(context.getString(R.string.fraction_d_slash_d, info.availableSlots + info.restoreSlots, info.maxSlots));
    }
}

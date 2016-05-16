package com.oakonell.dndcharacter.views.character.spell;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.utils.NumberUtils;

/**
 * Created by Rob on 1/24/2016.
 */
public class SpellsViewHolder extends SpellLevelsAdapter.AbstractSpellLevelViewHolder {
    private static final java.lang.String SLOT_EDIT_FRAGMENT = "slot_edit_frag";
    @NonNull
    private final TextView available_slots;
    @NonNull
    private final TextView total_slots;
    @NonNull
    private final TextView level;
    @NonNull
    private final Button use_button;
    @NonNull
    private final ViewGroup slots_group;

    public SpellsViewHolder(@NonNull View itemView) {
        super(itemView);
        level = (TextView) itemView.findViewById(R.id.level);
        available_slots = (TextView) itemView.findViewById(R.id.available_slots);
        total_slots = (TextView) itemView.findViewById(R.id.total_slots);
        use_button = (Button) itemView.findViewById(R.id.use_button);
        slots_group = (ViewGroup) itemView.findViewById(R.id.slots_group);
    }

    @Override
    public void bind(@NonNull final SpellsFragment context, SpellLevelsAdapter adapter, final @NonNull com.oakonell.dndcharacter.model.character.Character.SpellLevelInfo info) {
        super.bind(context, adapter, info);
        available_slots.setText(NumberUtils.formatNumber(info.getSlotsAvailable()));
        total_slots.setText(NumberUtils.formatNumber(info.getMaxSlots()));
        level.setText(NumberUtils.formatNumber(info.getLevel()));
        use_button.setEnabled(info.getSlotsAvailable() > 0);
        use_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getCharacter().useSpellSlot(info.getLevel());
                context.updateViews();
                context.getMainActivity().saveCharacter();
            }
        });

        slots_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SpellSlotEditDialog slotEditDialog = SpellSlotEditDialog.createDialog(info.getLevel());
                slotEditDialog.show(context.getFragmentManager(), SLOT_EDIT_FRAGMENT);
            }
        });
    }

    @NonNull
    @Override
    protected AbstractSpellAdapter newAdapter(SpellsFragment context, @NonNull Character.SpellLevelInfo info) {
        return new SpellAdapter(context, info);
    }

    public static class SpellAdapter extends AbstractSpellAdapter<SpellViewHolder> {
        SpellAdapter(SpellsFragment context, @NonNull Character.SpellLevelInfo spellLevelInfo) {
            super(context, spellLevelInfo);
        }

        @NonNull
        @Override
        public SpellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spell_char_row, parent, false);
            return new SpellViewHolder(newView);
        }
    }

    public static class SpellViewHolder extends AbstractSpellAdapter.AbstractSpellViewHolder {
        @NonNull
        final CheckBox prepared;

        public SpellViewHolder(@NonNull View itemView) {
            super(itemView);
            prepared = (CheckBox) itemView.findViewById(R.id.prepared);
        }

        @Override
        public void bind(@NonNull final SpellsFragment context, @NonNull AbstractSpellAdapter adapter, @NonNull final Character.CharacterSpellWithSource info) {
            super.bind(context, adapter, info);
            if (info.getSpell().isPreparable() || info.getSpell().isAlwaysPrepared()) {
                prepared.setVisibility(View.VISIBLE);
            } else {
                prepared.setVisibility(View.INVISIBLE);
            }
            prepared.setOnCheckedChangeListener(null);
            if (info.getSpell().isAlwaysPrepared()) {
                prepared.setChecked(true);
                prepared.setEnabled(false);
            } else {
                prepared.setEnabled(true);
                prepared.setChecked(info.getSpell().isPrepared());
                prepared.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        info.getSpell().setPrepared(isChecked);
                        context.updateViews();
                        context.getMainActivity().saveCharacter();
                    }
                });
            }
        }
    }
}

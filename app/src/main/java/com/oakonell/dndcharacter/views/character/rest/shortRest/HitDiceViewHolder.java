package com.oakonell.dndcharacter.views.character.rest.shortRest;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.RandomUtils;
import com.oakonell.dndcharacter.utils.SoundFXUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.character.rest.AbstractRestDialogFragment;

/**
 * Created by Rob on 12/19/2016.
 */

class HitDiceViewHolder extends BindableComponentViewHolder<HitDieUseRow, AbstractRestDialogFragment, HitDiceAdapter> {
    @NonNull
    final TextView numDice;
    @NonNull
    final TextView die;
    @NonNull
    final ViewGroup hit_die_group;
    @NonNull
    final Button useButton;
    @NonNull
    final RecyclerView uses;

    @NonNull
    final ViewGroup use_hit_die_group;
    @NonNull
    final EditText hit_die_val;
    @NonNull
    private final TextView con_mod_text;
    @NonNull
    final Button roll;
    @NonNull
    final Button apply;
    @NonNull
    final Button cancel;

    HitDieUseAdapter usesAdapter;

    public HitDiceViewHolder(@NonNull View view) {
        super(view);
        hit_die_group = (ViewGroup) view.findViewById(R.id.hit_die_group);
        numDice = (TextView) view.findViewById(R.id.num_dice);
        die = (TextView) view.findViewById(R.id.die);
        uses = (RecyclerView) view.findViewById(R.id.hit_die_vals_list);
        useButton = (Button) view.findViewById(R.id.use_die_button);

        use_hit_die_group = (ViewGroup) view.findViewById(R.id.use_hit_die_group);
        hit_die_val = (EditText) view.findViewById(R.id.hit_die_val);
        con_mod_text = (TextView) view.findViewById(R.id.con_mod_text);
        roll = (Button) view.findViewById(R.id.roll);
        apply = (Button) view.findViewById(R.id.apply);
        cancel = (Button) view.findViewById(R.id.cancel);
    }

    @Override
    public void bind(@NonNull final AbstractRestDialogFragment context, @NonNull final HitDiceAdapter adapter, @NonNull final HitDieUseRow row) {
        if (usesAdapter == null) {
            usesAdapter = new HitDieUseAdapter(adapter);
            uses.setAdapter(usesAdapter);

            uses.setHasFixedSize(false);
            uses.setLayoutManager(new LinearLayoutManager(context.getActivity(), LinearLayoutManager.HORIZONTAL, false));
        }

        hit_die_group.setVisibility(View.VISIBLE);
        use_hit_die_group.setVisibility(View.GONE);

        numDice.setText(NumberUtils.formatNumber(row.numDiceRemaining));
        die.setText(NumberUtils.formatNumber(row.dieSides));
        if (row.numDiceRemaining > 0) {
            useButton.setEnabled(true);
            useButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    use_hit_die_group.setVisibility(View.VISIBLE);
                    hit_die_group.setVisibility(View.GONE);
                    useButton.setEnabled(false);
                }
            });
        } else {
            useButton.setEnabled(false);
            useButton.setOnClickListener(null);
        }
        con_mod_text.setText(context.getString(R.string.con_mod_hit_die, NumberUtils.formatSignedNumber(context.getCharacter().getStatBlock(StatType.CONSTITUTION).getModifier())));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hit_die_val.setText("");
                use_hit_die_group.setVisibility(View.GONE);
                hit_die_group.setVisibility(View.VISIBLE);
                context.hideKeyboardFrom(hit_die_val);
                useButton.setEnabled(true);
            }
        });

        roll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundFXUtils.playDiceRoll(context.getActivity());

                int value = RandomUtils.random(1, row.dieSides);
                hit_die_val.setText(NumberUtils.formatNumber(value));
                apply.setEnabled(true);
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueString = hit_die_val.getText().toString();
                if (valueString.trim().length() > 0) {
                    int value = Integer.parseInt(valueString);
                    int conMod = context.getCharacter().getStatBlock(StatType.CONSTITUTION).getModifier();
                    value = value + conMod;
                    if (value <= 0) {
                        // you gain at least 1 HP from using a hit die during a short rest!
                        // https://twitter.com/mikemearls/status/713776373018402816
                        value = 1;
                    }

                    // TODO validate value < max
                    row.rolls.add(value);
                    row.numDiceRemaining--;
                    context.hideKeyboardFrom(hit_die_val);
                    adapter.notifyDataSetChanged();
                    context.updateView();
                }
                cancel.performClick();
            }
        });

        usesAdapter.setData(getAdapterPosition(), row.rolls);
    }
}
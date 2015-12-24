package com.oakonell.dndcharacter.views.rest;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.RandomUtils;
import com.oakonell.dndcharacter.model.ShortRestRequest;
import com.oakonell.dndcharacter.model.components.RefreshType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Rob on 11/7/2015.
 */
public class ShortRestDialogFragment extends AbstractRestDialogFragment {
    private final List<HitDieUseRow> diceUses = new ArrayList<>();
    private HitDiceAdapter diceAdapter;
    private ListView hitDiceListView;

    public static ShortRestDialogFragment createDialog() {
        ShortRestDialogFragment newMe = new ShortRestDialogFragment();
        return newMe;
    }

    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.short_rest_dialog, container);
        configureCommon(view);

        getDialog().setTitle("Short Rest");

        hitDiceListView = (ListView) view.findViewById(R.id.hit_dice_list);

        return view;
    }


    @Override
    protected void onDone() {
        super.onDone();
        Character character = getCharacter();
        ShortRestRequest request = new ShortRestRequest();
        for (HitDieUseRow each : diceUses) {
            request.addHitDiceUsed(each.dieSides, each.numUses);
        }
        request.setHealing(getHealing());
        character.heal(getHealing());

        updateCommonRequest(request);
        character.shortRest(request);
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);
        if (character.getHP() == character.getMaxHP()) {
            hitDiceListView.setVisibility(View.GONE);
        } else {
            hitDiceListView.setVisibility(View.VISIBLE);
        }
        List<Character.HitDieRow> diceCounts = character.getHitDiceCounts();
        for (Character.HitDieRow each : diceCounts) {
            HitDieUseRow newRow = new HitDieUseRow();
            newRow.dieSides = each.dieSides;
            newRow.numDiceRemaining = each.numDiceRemaining;
            newRow.totalDice = each.totalDice;
            diceUses.add(newRow);
        }
        diceAdapter = new HitDiceAdapter(getActivity(), diceUses);
        hitDiceListView.setAdapter(diceAdapter);


    }

    @Override
    protected boolean shouldReset(RefreshType refreshesOn) {
        return refreshesOn == RefreshType.SHORT_REST;
    }

    public void updateView() {
        super.updateView();
        if (diceAdapter != null) {
            diceAdapter.notifyDataSetChanged();
        }
    }

    protected int getHealing() {
        int healing = 0;
        for (HitDieUseRow each : diceUses) {
            for (Integer eachRoll : each.rolls) {
                // TODO include the con mod
                healing += eachRoll;
            }
        }

        healing += getExtraHealing();
        return healing;
    }


    private class HitDiceAdapter extends BaseAdapter {
        List<HitDieUseRow> diceCounts;
        private Context context;

        public HitDiceAdapter(Context context, List<HitDieUseRow> diceCounts) {
            this.context = context;
            this.diceCounts = diceCounts;
        }

        @Override
        public int getCount() {
            return diceCounts.size();
        }

        @Override
        public HitDieUseRow getItem(int position) {
            return diceCounts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(context, R.layout.hit_dice_item, null);
                viewHolder = new ViewHolder();

                viewHolder.numDice = (TextView) view.findViewById(R.id.num_dice);
                viewHolder.die = (TextView) view.findViewById(R.id.die);
                viewHolder.useText = (TextView) view.findViewById(R.id.hit_die_vals_text);
                viewHolder.useButton = (Button) view.findViewById(R.id.use_die_button);

                viewHolder.use_hit_die_group = (ViewGroup) view.findViewById(R.id.use_hit_die_group);
                viewHolder.hit_die_val = (EditText) view.findViewById(R.id.hit_die_val);
                viewHolder.roll = (Button) view.findViewById(R.id.roll);
                viewHolder.apply = (Button) view.findViewById(R.id.apply);
                viewHolder.cancel = (Button) view.findViewById(R.id.cancel);


                view.setTag(viewHolder);

            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            final HitDieUseRow row = getItem(position);
            viewHolder.numDice.setText(row.numDiceRemaining + "");
            viewHolder.die.setText(row.dieSides + "");
            StringBuilder builder = new StringBuilder();
            for (Iterator<Integer> iter = row.rolls.iterator(); iter.hasNext(); ) {
                Integer value = iter.next();
                builder.append(value);
                if (iter.hasNext()) {
                    builder.append(", ");
                }
            }
            viewHolder.useText.setText(builder.toString());
            final ViewHolder finalHolder = viewHolder;
            if (row.numDiceRemaining > 0) {
                viewHolder.useButton.setEnabled(true);
                viewHolder.useButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finalHolder.use_hit_die_group.setVisibility(View.VISIBLE);
                        finalHolder.useButton.setEnabled(false);
                    }
                });
            } else {
                viewHolder.useButton.setEnabled(false);
                viewHolder.useButton.setOnClickListener(null);
            }

            viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalHolder.hit_die_val.setText("");
                    finalHolder.use_hit_die_group.setVisibility(View.GONE);
                    finalHolder.useButton.setEnabled(true);
                }
            });

            viewHolder.roll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int value = RandomUtils.random(1, row.dieSides);
                    finalHolder.hit_die_val.setText(value + "");
                    finalHolder.apply.setEnabled(true);
                }
            });

            viewHolder.hit_die_val = (EditText) view.findViewById(R.id.hit_die_val);
            viewHolder.apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String valueString = finalHolder.hit_die_val.getText().toString();
                    if (valueString.trim().length() > 0) {
                        int value = Integer.parseInt(valueString);
                        row.rolls.add(value);
                        row.numDiceRemaining--;
                        row.numUses++;
                        notifyDataSetChanged();
                        updateView();
                    }
                    finalHolder.cancel.performClick();
                }
            });


            return view;

        }

        class ViewHolder {
            TextView numDice;
            TextView die;
            TextView useText;
            Button useButton;

            ViewGroup use_hit_die_group;
            EditText hit_die_val;
            Button roll;
            Button apply;
            Button cancel;
        }

    }

}
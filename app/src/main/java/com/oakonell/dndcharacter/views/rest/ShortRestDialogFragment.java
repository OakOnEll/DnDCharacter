package com.oakonell.dndcharacter.views.rest;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.oakonell.dndcharacter.MainActivity;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.Character;
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

    public static ShortRestDialogFragment createDialog(Character character) {
        ShortRestDialogFragment newMe = new ShortRestDialogFragment();
        newMe.setCharacter(character);
        return newMe;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.short_rest_dialog, container);
        configureCommon(view);

        getDialog().setTitle("Short Rest");
        Button done = (Button) view.findViewById(R.id.done);

        ListView hitDiceListView = (ListView) view.findViewById(R.id.hit_dice_list);


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


        updateView();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShortRestRequest request = new ShortRestRequest();
                for (HitDieUseRow each : diceUses) {
                    request.addHitDiceUsed(each.dieSides, each.numUses);
                }
                request.setHealing(getHealing());
                character.heal(getHealing());

                updateCommonRequest(request);
                character.shortRest(request);
                ((MainActivity) getActivity()).updateViews();
                dismiss();
            }
        });
        return view;
    }


    @Override
    protected boolean shouldReset(RefreshType refreshesOn) {
        return refreshesOn == RefreshType.SHORT_REST;
    }

    public void updateView() {
        super.updateView();
        diceAdapter.notifyDataSetChanged();
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

    void promptForHitDieHeal(final HitDieUseRow row) {
        UseHitDieDialogFragment frag = UseHitDieDialogFragment.createDialog(this, row);
        frag.show(getFragmentManager(), "use_hit_die");
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
            if (row.numDiceRemaining > 0) {
                viewHolder.useButton.setEnabled(true);
                viewHolder.useButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        promptForHitDieHeal(row);
                    }
                });
            } else {
                viewHolder.useButton.setEnabled(false);
                viewHolder.useButton.setOnClickListener(null);
            }

            return view;

        }

        class ViewHolder {
            TextView numDice;
            TextView die;
            TextView useText;
            Button useButton;
        }

    }

}
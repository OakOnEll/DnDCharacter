package com.oakonell.dndcharacter.views.rest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.RandomUtils;
import com.oakonell.dndcharacter.model.ShortRestRequest;
import com.oakonell.dndcharacter.model.components.RefreshType;
import com.oakonell.dndcharacter.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Rob on 11/7/2015.
 */
public class ShortRestDialogFragment extends AbstractRestDialogFragment {
    private HitDiceAdapter diceAdapter;
    private RecyclerView hitDiceListView;
    private Map<Integer, HitDieUseRow> savedDiceCounts;

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

        hitDiceListView = (RecyclerView) view.findViewById(R.id.hit_dice_list);

        if (savedInstanceState != null) {
            ArrayList<HitDieUseRow> savedList = savedInstanceState.getParcelableArrayList("diceCounts");
            savedDiceCounts = new HashMap<>();
            for (HitDieUseRow each : savedList) {
                savedDiceCounts.put(each.dieSides, each);
            }
        }

        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("diceCounts", diceAdapter.diceCounts);
    }

    @Override
    protected boolean onDone() {
        boolean isValid = true;
        ShortRestRequest request = new ShortRestRequest();
        for (HitDieUseRow each : diceAdapter.diceCounts) {
            request.addHitDiceUsed(each.dieSides, each.rolls.size());
        }
        request.setHealing(getHealing());

        isValid = isValid && updateCommonRequest(request);
        isValid = isValid && super.onDone();
        if (isValid) {
            getCharacter().shortRest(request);
        }
        return isValid;
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);

        diceAdapter = new HitDiceAdapter(getActivity(), character);

        if (savedDiceCounts != null) {
            diceAdapter.populateDiceCounts(character, savedDiceCounts);
            savedDiceCounts = null;
        }

        hitDiceListView.setAdapter(diceAdapter);

        hitDiceListView.setHasFixedSize(false);
        hitDiceListView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        hitDiceListView.addItemDecoration(itemDecoration);


        updateView();
    }

    @Override
    public void onCharacterChanged(Character character) {
        diceAdapter.reloadList(character);
        updateView();
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
        if (getCharacter().getHP() == getCharacter().getMaxHP()) {
            hitDiceListView.setVisibility(View.GONE);
        } else {
            hitDiceListView.setVisibility(View.VISIBLE);
        }
    }

    protected int getHealing() {
        int healing = 0;
        for (HitDieUseRow each : diceAdapter.diceCounts) {
            for (Integer eachRoll : each.rolls) {
                // TODO include the con mod
                healing += eachRoll;
            }
        }

        healing += getExtraHealing();
        return healing;
    }


    private class HitDiceAdapter extends RecyclerView.Adapter<HitDieUseViewHolder> {
        ArrayList<HitDieUseRow> diceCounts;
        private Context context;

        public HitDiceAdapter(Context context, Character character) {
            this.context = context;
            populateDiceCounts(character, null);
        }

        private void populateDiceCounts(Character character, Map<Integer, HitDieUseRow> existingRows) {
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
                        if (newRow.rolls.size() > newRow.numDiceRemaining) {
                            newRow.rolls.subList(0, newRow.numDiceRemaining - 1);
                        }
                        newRow.numDiceRemaining -= newRow.rolls.size();
                    }
                }

                diceCounts.add(newRow);
            }
        }

        public void reloadList(Character character) {
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

        @Override
        public HitDieUseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            HitDieUseViewHolder viewHolder;
            View view = View.inflate(context, R.layout.hit_dice_item, null);
            viewHolder = new HitDieUseViewHolder(view);

            viewHolder.numDice = (TextView) view.findViewById(R.id.num_dice);
            viewHolder.die = (TextView) view.findViewById(R.id.die);
            viewHolder.useText = (TextView) view.findViewById(R.id.hit_die_vals_text);
            viewHolder.useButton = (Button) view.findViewById(R.id.use_die_button);

            viewHolder.use_hit_die_group = (ViewGroup) view.findViewById(R.id.use_hit_die_group);
            viewHolder.hit_die_val = (EditText) view.findViewById(R.id.hit_die_val);
            viewHolder.roll = (Button) view.findViewById(R.id.roll);
            viewHolder.apply = (Button) view.findViewById(R.id.apply);
            viewHolder.cancel = (Button) view.findViewById(R.id.cancel);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final HitDieUseViewHolder viewHolder, int position) {
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
                        viewHolder.use_hit_die_group.setVisibility(View.VISIBLE);
                        viewHolder.useButton.setEnabled(false);
                    }
                });
            } else {
                viewHolder.useButton.setEnabled(false);
                viewHolder.useButton.setOnClickListener(null);
            }

            viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.hit_die_val.setText("");
                    viewHolder.use_hit_die_group.setVisibility(View.GONE);
                    viewHolder.useButton.setEnabled(true);
                }
            });

            viewHolder.roll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int value = RandomUtils.random(1, row.dieSides);
                    viewHolder.hit_die_val.setText(value + "");
                    viewHolder.apply.setEnabled(true);
                }
            });

            viewHolder.apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String valueString = viewHolder.hit_die_val.getText().toString();
                    if (valueString.trim().length() > 0) {
                        int value = Integer.parseInt(valueString);
                        row.rolls.add(value);
                        row.numDiceRemaining--;
                        notifyDataSetChanged();
                        updateView();
                    }
                    viewHolder.cancel.performClick();
                }
            });

        }


    }

    static class HitDieUseViewHolder extends RecyclerView.ViewHolder {
        TextView numDice;
        TextView die;
        TextView useText;
        Button useButton;

        ViewGroup use_hit_die_group;
        EditText hit_die_val;
        Button roll;
        Button apply;
        Button cancel;

        public HitDieUseViewHolder(View itemView) {
            super(itemView);
        }
    }
}
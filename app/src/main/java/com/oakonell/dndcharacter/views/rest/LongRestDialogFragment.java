package com.oakonell.dndcharacter.views.rest;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.LongRestRequest;
import com.oakonell.dndcharacter.model.components.RefreshType;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Rob on 11/8/2015.
 */
public class LongRestDialogFragment extends AbstractRestDialogFragment {

    private final List<HitDieRestoreRow> diceUses = new ArrayList<>();
    View fullHealingGroup;
    CheckBox fullHealing;
    private HitDiceRestoreAdapter diceAdapter;
    private ListView hitDiceListView;

    public static LongRestDialogFragment createDialog() {
        return new LongRestDialogFragment();
    }

    @Override
    protected boolean allowExtraHealing() {
        return !fullHealing.isChecked();
    }

    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.long_rest_dialog, container);
        configureCommon(view);
        getDialog().setTitle("Long Rest");

        fullHealingGroup = view.findViewById(R.id.full_heal_group);
        fullHealing = (CheckBox) view.findViewById(R.id.full_healing);

        hitDiceListView = (ListView) view.findViewById(R.id.hit_dice_restore_list);

        fullHealing.setChecked(true);

        fullHealing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                conditionallyShowExtraHealing();
                updateView();
            }
        });

        return view;
    }


    @Override
    protected void onDone() {
        super.onDone();
        LongRestRequest request = new LongRestRequest();
        request.setHealing(getHealing());
        for (HitDieRestoreRow each : diceUses) {
            if (each.numDiceToRestore > 0) {
                request.restoreHitDice(each.dieSides, each.numDiceToRestore);
            }
        }
        updateCommonRequest(request);
        getCharacter().longRest(request);
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);
        List<com.oakonell.dndcharacter.model.Character.HitDieRow> diceCounts = character.getHitDiceCounts();

        for (Character.HitDieRow each : diceCounts) {
            HitDieRestoreRow newRow = new HitDieRestoreRow();
            newRow.dieSides = each.dieSides;
            newRow.currentDiceRemaining = each.numDiceRemaining;
            newRow.totalDice = each.totalDice;
            newRow.numDiceToRestore = Math.min(Math.max(each.totalDice / 2, 1), each.totalDice - each.numDiceRemaining);

            diceUses.add(newRow);
        }
        diceAdapter = new HitDiceRestoreAdapter(getActivity(), diceUses);
        hitDiceListView.setAdapter(diceAdapter);


        if (character.getHP() == character.getMaxHP()) {
            fullHealingGroup.setVisibility(View.GONE);
        } else {
            fullHealingGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected boolean shouldReset(RefreshType refreshesOn) {
        return true;
    }

    public void updateView() {
        super.updateView();
        diceAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getHealing() {
        if (fullHealing.isChecked()) {
            return getCharacter().getMaxHP() - getCharacter().getHP();
        }
        return getExtraHealing();
    }

    private class HitDieRestoreRow {
        public int dieSides;
        public int currentDiceRemaining;
        public int numDiceToRestore;
        public int totalDice;
    }

    private class HitDiceRestoreAdapter extends BaseAdapter {
        List<HitDieRestoreRow> diceCounts;
        private Context context;

        public HitDiceRestoreAdapter(Context context, List<HitDieRestoreRow> diceCounts) {
            this.context = context;
            this.diceCounts = diceCounts;
        }

        @Override
        public int getCount() {
            return diceCounts.size();
        }

        @Override
        public HitDieRestoreRow getItem(int position) {
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
                view = View.inflate(context, R.layout.hit_dice_restore_item, null);
                viewHolder = new ViewHolder();
                viewHolder.dieSides = (TextView) view.findViewById(R.id.die);
                viewHolder.currentDiceRemaining = (TextView) view.findViewById(R.id.current_dice_remaining);
                viewHolder.numDiceToRestore = (TextView) view.findViewById(R.id.dice_to_restore);
                viewHolder.totalDice = (TextView) view.findViewById(R.id.total);
                viewHolder.resultDice = (TextView) view.findViewById(R.id.resultant_dice);
                view.setTag(viewHolder);

            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            final HitDieRestoreRow row = getItem(position);
            viewHolder.dieSides.setText(row.dieSides + "");
            viewHolder.currentDiceRemaining.setText(row.currentDiceRemaining + "");
            viewHolder.numDiceToRestore.setText(row.numDiceToRestore + "");
            viewHolder.totalDice.setText(row.totalDice + "");
            viewHolder.resultDice.setText((row.currentDiceRemaining + row.numDiceToRestore) + "");
            return view;
        }

        class ViewHolder {
            TextView dieSides;
            TextView currentDiceRemaining;
            TextView numDiceToRestore;
            TextView totalDice;
            TextView resultDice;
        }

    }

}

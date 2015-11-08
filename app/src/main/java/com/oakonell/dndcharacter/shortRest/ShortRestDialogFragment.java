package com.oakonell.dndcharacter.shortRest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Rob on 11/7/2015.
 */
public class ShortRestDialogFragment extends DialogFragment {
    private Character character;
    private HitDiceAdapter diceAdapter;
    private TextView extraHealingtextView;
    private TextView finalHp;
    private final List<HitDieUseRow> diceUses = new ArrayList<>();


    public static ShortRestDialogFragment createDialog(Character character) {
        ShortRestDialogFragment newMe = new ShortRestDialogFragment();
        newMe.setCharacter(character);
        return newMe;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.short_rest_dialog, container);
        getDialog().setTitle("Short Rest");
        Button done = (Button) view.findViewById(R.id.done);

        View featureResetsGroup = view.findViewById(R.id.feature_resets);
        TextView startHp = (TextView) view.findViewById(R.id.start_hp);
        finalHp = (TextView) view.findViewById(R.id.final_hp);
        View finalHpGroup = (View) view.findViewById(R.id.final_hp_group);
        View extraHealingGroup = (View) view.findViewById(R.id.extra_heal_group);
        View noHealingGroup = (View) view.findViewById(R.id.no_healing_group);
        ListView hitDiceListView = (ListView) view.findViewById(R.id.hit_dice_list);
        extraHealingtextView = (TextView) view.findViewById(R.id.extra_healing);

        featureResetsGroup.setVisibility(View.GONE);


        if (character.getHP() == character.getMaxHP()) {
            noHealingGroup.setVisibility(View.VISIBLE);

            hitDiceListView.setVisibility(View.GONE);
            finalHpGroup.setVisibility(View.GONE);
            extraHealingGroup.setVisibility(View.GONE);
        } else {
            noHealingGroup.setVisibility(View.GONE);

            hitDiceListView.setVisibility(View.VISIBLE);
            finalHpGroup.setVisibility(View.VISIBLE);
            extraHealingGroup.setVisibility(View.VISIBLE);
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

        extraHealingtextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateView();
            }
        });

        startHp.setText(character.getHP() + " / " + character.getMaxHP());
        updateView();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO call character short rest, with input of user choices/actions
                character.heal(getHealing());
                ((MainActivity) getActivity()).updateViews();
                dismiss();
            }
        });
        return view;
    }

    public void updateView() {
        diceAdapter.notifyDataSetChanged();
        int hp = character.getHP();
        int healing = getHealing();
        hp = Math.min(hp + healing, character.getMaxHP());
        finalHp.setText(hp + " / " + character.getMaxHP());
    }

    private int getHealing() {
        int healing = 0;
        for (HitDieUseRow each : diceUses) {
            for (Integer eachRoll : each.rolls) {
                // TODO include the con mod
                healing += eachRoll;
            }
        }

        String extraHealString = extraHealingtextView.getText().toString();
        if (extraHealString != null && extraHealString.trim().length() > 0) {
            healing += Integer.parseInt(extraHealString);
        }
        return healing;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }


    private class HitDiceAdapter extends BaseAdapter {
        class ViewHolder {
            TextView numDice;
            TextView die;
            TextView useText;
            Button useButton;
        }

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

    }

    void promptForHitDieHeal(final HitDieUseRow row) {
        UseHitDieDialogFragment frag = UseHitDieDialogFragment.createDialog(this, row);
        frag.show(getFragmentManager(), "use_hit_die");
    }

}
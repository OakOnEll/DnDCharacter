package com.oakonell.dndcharacter.views.character.rest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.rest.LongRestRequest;
import com.oakonell.dndcharacter.model.components.RefreshType;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by Rob on 11/8/2015.
 */
public class LongRestDialogFragment extends AbstractRestDialogFragment {
    private View fullHealingGroup;
    private CheckBox fullHealing;
    private HitDiceRestoreAdapter diceAdapter;
    private RecyclerView hitDiceListView;
    private Bundle savedDiceRestoreBundle;

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

        fullHealingGroup = view.findViewById(R.id.full_heal_group);
        fullHealing = (CheckBox) view.findViewById(R.id.full_healing);

        hitDiceListView = (RecyclerView) view.findViewById(R.id.hit_dice_restore_list);

        fullHealing.setChecked(true);

        fullHealing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                conditionallyShowExtraHealing();
                updateView();
            }
        });

        // touch up hit die restore list header
        view.findViewById(R.id.restore_lbl).setVisibility(View.VISIBLE);
        view.findViewById(R.id.dice_to_restore).setVisibility(View.GONE);

        if (savedInstanceState != null) {
            savedDiceRestoreBundle = savedInstanceState.getBundle("diceRestores");
        }

        return view;
    }

    @Override
    protected String getTitle() {
        return "Long Rest";
    }

    @Override
    protected Set<FeatureContext> getContextFilter() {
        Set<FeatureContext> filter = new HashSet<>();
        filter.add(FeatureContext.LONG_REST);
        return filter;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle diceRestoreBundle = new Bundle();
        for (HitDieRestoreRow each : diceAdapter.diceCounts) {
            int sides = each.dieSides;
            diceRestoreBundle.putInt(sides + "", each.numDiceToRestore);
        }
        outState.putBundle("diceRestores", diceRestoreBundle);
    }

    @Override
    protected boolean onDone() {
        boolean isValid = true;
        LongRestRequest request = new LongRestRequest();
        for (HitDieRestoreRow each : diceAdapter.diceCounts) {
            if (each.numDiceToRestore > 0) {
                request.restoreHitDice(each.dieSides, each.numDiceToRestore);
            }
        }
        request.setHealing(getHealing());

        //noinspection ConstantConditions
        isValid = isValid && updateCommonRequest(request);
        isValid = isValid && super.onDone();
        if (isValid) {
            getCharacter().longRest(request);
        }
        return isValid;
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);

        diceAdapter = new HitDiceRestoreAdapter(getActivity(), character);

        if (savedDiceRestoreBundle != null) {
            for (HitDieRestoreRow each : diceAdapter.diceCounts) {
                int savedNum = savedDiceRestoreBundle.getInt(each.dieSides + "", -1);
                if (savedNum >= 0) {
                    each.numDiceToRestore = savedNum;
                }
            }
            savedDiceRestoreBundle = null;
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
        updateView();
        diceAdapter.reloadList(character);
    }

    @Override
    public void updateView() {
        super.updateView();
        if (getCharacter().getHP() == getCharacter().getMaxHP()) {
            fullHealingGroup.setVisibility(View.GONE);
        } else {
            fullHealingGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected boolean shouldReset(RefreshType refreshesOn) {
        return true;
    }


    @Override
    protected int getHealing() {
        if (fullHealing.isChecked()) {
            return getCharacter().getMaxHP() - getCharacter().getHP();
        }
        return getExtraHealing();
    }

    static class HitDiceRestoreViewHolder extends BindableComponentViewHolder<HitDieRestoreRow, Context, HitDiceRestoreAdapter> {
        TextView dieSides;
        TextView currentDiceRemaining;
        EditText numDiceToRestore;
        TextView totalDice;
        TextView resultDice;
        TextWatcher numDiceToRestoreWatcher;

        public HitDiceRestoreViewHolder(View view) {
            super(view);
            dieSides = (TextView) view.findViewById(R.id.die);
            currentDiceRemaining = (TextView) view.findViewById(R.id.current_dice_remaining);
            numDiceToRestore = (EditText) view.findViewById(R.id.dice_to_restore);
            totalDice = (TextView) view.findViewById(R.id.total);
            resultDice = (TextView) view.findViewById(R.id.resultant_dice);
        }

        @Override
        public void bind(final Context context, final HitDiceRestoreAdapter adapter, final HitDieRestoreRow row) {
            if (numDiceToRestoreWatcher != null) {
                numDiceToRestore.removeTextChangedListener(numDiceToRestoreWatcher);
            }

            dieSides.setText("d" + row.dieSides);
            currentDiceRemaining.setText(NumberUtils.formatNumber(row.currentDiceRemaining));
            numDiceToRestore.setText(NumberUtils.formatNumber(row.numDiceToRestore));
            totalDice.setText(NumberUtils.formatNumber(row.totalDice));
            resultDice.setText(NumberUtils.formatNumber((row.currentDiceRemaining + row.numDiceToRestore)));

            numDiceToRestore.setEnabled(row.currentDiceRemaining != row.totalDice);

            numDiceToRestoreWatcher = new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    numDiceToRestore.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String string = s.toString();
                    if (string.length() == 0) {
                        row.numDiceToRestore = 0;
                        resultDice.setText(NumberUtils.formatNumber((row.currentDiceRemaining + row.numDiceToRestore)));
                        return;
                    }
                    try {
                        int value = Integer.parseInt(string);
                        if (value > row.totalDice - row.currentDiceRemaining) {
                            row.numDiceToRestore = 0;
                            numDiceToRestore.setError(context.getString(R.string.enter_number_less_than_equal_n, (row.totalDice - row.currentDiceRemaining)));
                            return;
                        }
                        row.numDiceToRestore = value;
                        resultDice.setText(NumberUtils.formatNumber(row.currentDiceRemaining + row.numDiceToRestore));
                    } catch (NumberFormatException e) {
                        row.numDiceToRestore = 0;
                        numDiceToRestore.setError(context.getString(R.string.enter_number_less_than_equal_n, (row.totalDice - row.currentDiceRemaining)));
                    }
                }
            };
            numDiceToRestore.addTextChangedListener(numDiceToRestoreWatcher);
        }
    }

    private class HitDieRestoreRow {
        public int dieSides;
        public int currentDiceRemaining;
        public int numDiceToRestore;
        public int totalDice;
    }

    private class HitDiceRestoreAdapter extends RecyclerView.Adapter<HitDiceRestoreViewHolder> {
        List<HitDieRestoreRow> diceCounts;
        private Context context;

        public HitDiceRestoreAdapter(Context context, Character character) {
            this.context = context;
            populateDiceCounts(character);
        }

        private void populateDiceCounts(Character character) {
            diceCounts = new ArrayList<>();
            for (Character.HitDieRow each : character.getHitDiceCounts()) {
                HitDieRestoreRow newRow = new HitDieRestoreRow();
                newRow.dieSides = each.dieSides;
                newRow.currentDiceRemaining = each.numDiceRemaining;
                newRow.totalDice = each.totalDice;
                newRow.numDiceToRestore = Math.min(Math.max(each.totalDice / 2, 1), each.totalDice - each.numDiceRemaining);

                diceCounts.add(newRow);
            }
        }


        public void reloadList(Character character) {
            populateDiceCounts(character);
            notifyDataSetChanged();
        }

        public HitDieRestoreRow getItem(int position) {
            return diceCounts.get(position);
        }

        @Override
        public HitDiceRestoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context, R.layout.hit_dice_restore_item, null);
            HitDiceRestoreViewHolder viewHolder = new HitDiceRestoreViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final HitDiceRestoreViewHolder viewHolder, final int position) {
            final HitDieRestoreRow row = getItem(position);
            viewHolder.bind(context, this, row);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return diceCounts.size();
        }


    }
}

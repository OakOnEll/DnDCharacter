package com.oakonell.dndcharacter.views.character.rest;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.character.rest.ShortRestRequest;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.components.RefreshType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.RandomUtils;
import com.oakonell.dndcharacter.utils.SoundFXUtils;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;
import com.oakonell.expression.context.SimpleVariableContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Rob on 11/7/2015.
 */
public class ShortRestDialogFragment extends AbstractRestDialogFragment {
    public static final String DICE_COUNTS = "diceCounts";
    private HitDiceAdapter diceAdapter;
    private RecyclerView hitDiceListView;
    @Nullable
    private Map<Integer, HitDieUseRow> savedDiceCounts;

    @NonNull
    public static ShortRestDialogFragment createDialog() {
        return new ShortRestDialogFragment();
    }

    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.short_rest_dialog, container);
        configureCommon(view);

        hitDiceListView = (RecyclerView) view.findViewById(R.id.hit_dice_list);

        if (savedInstanceState != null) {
            ArrayList<HitDieUseRow> savedList = savedInstanceState.getParcelableArrayList(DICE_COUNTS);
            if (savedList != null) {
                savedDiceCounts = new HashMap<>();
                for (HitDieUseRow each : savedList) {
                    savedDiceCounts.put(each.dieSides, each);
                }
            }
        }

        return view;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.short_rest_title);
    }

    @NonNull
    @Override
    protected Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.SHORT_REST));
        filter.add(new FeatureContextArgument(FeatureContext.DICE_ROLL));
        return filter;
    }

    @Override
    protected FeatureContextArgument getNoteContext() {
        return new FeatureContextArgument(FeatureContext.SHORT_REST);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(DICE_COUNTS, diceAdapter.diceCounts);
    }

    @Override
    protected boolean onDone() {
        boolean isValid = true;
        ShortRestRequest request = new ShortRestRequest();
        for (HitDieUseRow each : diceAdapter.diceCounts) {
            request.addHitDiceUsed(each.dieSides, each.rolls.size());
        }
        request.setHealing(getHealing());

        //noinspection ConstantConditions
        isValid = isValid && updateCommonRequest(request);
        isValid = isValid && super.onDone();
        if (isValid) {
            getCharacter().shortRest(request);
        }
        return isValid;
    }

    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        super.onCharacterLoaded(character);

        diceAdapter = new HitDiceAdapter(this, character);

        if (savedDiceCounts != null) {
            diceAdapter.populateDiceCounts(character, savedDiceCounts);
            savedDiceCounts = null;
        }

        hitDiceListView.setAdapter(diceAdapter);

        hitDiceListView.setHasFixedSize(false);
        hitDiceListView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        hitDiceListView.addItemDecoration(itemDecoration);


        updateView();
    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
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


    private static class HitDiceAdapter extends RecyclerView.Adapter<HitDiceViewHolder> {
        ArrayList<HitDieUseRow> diceCounts;
        private final ShortRestDialogFragment context;

        public HitDiceAdapter(ShortRestDialogFragment context, @NonNull Character character) {
            this.context = context;
            populateDiceCounts(character, null);
        }

        private void populateDiceCounts(@NonNull Character character, @Nullable Map<Integer, HitDieUseRow> existingRows) {
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
                        if (newRow.rolls.size() > newRow.numDiceRemaining && newRow.numDiceRemaining > 1) {
                            newRow.rolls.subList(0, newRow.numDiceRemaining - 1);
                        }
                        newRow.numDiceRemaining -= newRow.rolls.size();
                    }
                }

                diceCounts.add(newRow);
            }
        }

        public void reloadList(@NonNull Character character) {
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

        @NonNull
        @Override
        public HitDiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context.getMainActivity(), R.layout.hit_dice_item, null);
            HitDiceViewHolder viewHolder = new HitDiceViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final HitDiceViewHolder viewHolder, int position) {
            final HitDieUseRow row = getItem(position);
            viewHolder.bind(context, this, row);
        }


    }

    static class HitDiceViewHolder extends BindableComponentViewHolder<HitDieUseRow, ShortRestDialogFragment, HitDiceAdapter> {
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
        public void bind(@NonNull final ShortRestDialogFragment context, @NonNull final HitDiceAdapter adapter, @NonNull final HitDieUseRow row) {
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

    static class HitDieUseViewHolder extends BindableComponentViewHolder<Integer, HitDiceAdapter, HitDieUseAdapter> {
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
                    adapter.notifyItemChanged(adapter.hitDieRow);
                }
            });
        }
    }

    static class HitDieUseAdapter extends RecyclerView.Adapter<HitDieUseViewHolder> {
        List<Integer> rolls;
        int hitDieRow;
        final HitDiceAdapter adapter;

        HitDieUseAdapter(HitDiceAdapter adapter) {
            this.adapter = adapter;
        }

        @NonNull
        @Override
        public HitDieUseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.hit_die_use_row, null);
            HitDieUseViewHolder viewHolder = new HitDieUseViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull HitDieUseViewHolder holder, final int position) {
            final Integer value = rolls.get(position);
            holder.bind(adapter, this, value);
        }

        @Override
        public int getItemCount() {
            return rolls == null ? 0 : rolls.size();
        }

        public void setData(int position, List<Integer> rolls) {
            this.hitDieRow = position;
            this.rolls = rolls;
            notifyDataSetChanged();
        }
    }


    protected int getSlotsToRestore(@NonNull Character.SpellLevelInfo info) {
        int value = 0;
        for (Character.CastingClassInfo each : getCharacter().getCasterClassInfo()) {
            final RefreshType refreshType = each.getSpellSlotRefresh();
            if (refreshType != RefreshType.SHORT_REST) continue;
            final String slotFormula = each.getSlotMap().get(info.getLevel());
            if (slotFormula == null || slotFormula.length() == 0) continue;

            SimpleVariableContext variableContext = new SimpleVariableContext();
            variableContext.setNumber("classLevel", each.getClassLevel());
            value += getCharacter().evaluateFormula(slotFormula, variableContext);
        }
        return Math.min(info.getMaxSlots() - info.getSlotsAvailable(), value);
    }


}
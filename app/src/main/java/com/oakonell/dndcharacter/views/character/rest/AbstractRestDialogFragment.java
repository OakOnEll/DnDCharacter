package com.oakonell.dndcharacter.views.character.rest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CompanionResetInfo;
import com.oakonell.dndcharacter.model.character.FeatureInfo;
import com.oakonell.dndcharacter.model.character.FeatureResetInfo;
import com.oakonell.dndcharacter.model.character.SpellSlotResetInfo;
import com.oakonell.dndcharacter.model.character.companion.CharacterCompanion;
import com.oakonell.dndcharacter.model.character.rest.AbstractRestRequest;
import com.oakonell.dndcharacter.model.character.rest.ShortRestRequest;
import com.oakonell.dndcharacter.model.companion.Companion;
import com.oakonell.dndcharacter.model.components.RefreshType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Rob on 11/8/2015.
 */
public abstract class AbstractRestDialogFragment<RT extends AbstractRestRequest, T extends RestHealingViewHelper<RT>> extends AbstractCharacterDialogFragment {
    public static final String FEATURE_RESETS_SAVE_BUNDLE_KEY = "featureResets";
    public static final String SPELL_SLOT_RESETS_SAVE_BUNDLE_KEY = "spellSlotResets";
    private static final String COMPANION_RESETS_SAVE_BUNDLE_KEY = "companionResets";

    public static final String RESET = "reset";
    public static final String NUM_TO_RESTORE = "numToRestore";


    T healingViewHelper = createHealingViewHelper();

    @NonNull
    protected abstract T createHealingViewHelper();

    protected T getHealingViewHelper() {
        return healingViewHelper;
    }

    private FeatureResetsAdapter featureResetAdapter;
    private RecyclerView featureListView;
    private SpellSlotsResetsAdapter spellSlotResetAdapter;
    private RecyclerView spell_slot_list;


    private CompanionResetsAdapter companionResetsAdapter;
    private RecyclerView companionListView;
    private View companion_resets;

    @Nullable
    private Bundle savedFeatureResets;
    @Nullable
    private Bundle savedSpellSlotResets;
    @Nullable
    private Bundle savedCompanionResets;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedFeatureResets = savedInstanceState.getBundle(FEATURE_RESETS_SAVE_BUNDLE_KEY);
            savedSpellSlotResets = savedInstanceState.getBundle(SPELL_SLOT_RESETS_SAVE_BUNDLE_KEY);
            savedCompanionResets = savedInstanceState.getBundle(COMPANION_RESETS_SAVE_BUNDLE_KEY);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (featureResetAdapter == null || featureResetAdapter.resets == null) return;

        Bundle resets = new Bundle();
        for (FeatureResetInfo each : featureResetAdapter.resets) {
            Bundle reset = new Bundle();
            reset.putByte(RESET, (byte) (each.reset ? 1 : 0));
            reset.putInt(NUM_TO_RESTORE, each.numToRestore);
            resets.putBundle(each.name, reset);
        }
        outState.putBundle(FEATURE_RESETS_SAVE_BUNDLE_KEY, resets);

        Bundle slotResets = new Bundle();
        for (SpellSlotResetInfo each : spellSlotResetAdapter.resets) {
            Bundle reset = new Bundle();
            reset.putByte(RESET, (byte) (each.reset ? 1 : 0));
            reset.putInt(NUM_TO_RESTORE, each.restoreSlots);
            slotResets.putBundle(each.level + "", reset);
        }
        outState.putBundle(SPELL_SLOT_RESETS_SAVE_BUNDLE_KEY, slotResets);

        Bundle companionResets = new Bundle();
        for (CompanionResetInfo each : companionResetsAdapter.resets) {
            Bundle reset = new Bundle();
            reset.putByte(RESET, (byte) (each.reset ? 1 : 0));
            companionResets.putBundle(each.companionIndex + "", reset);
        }
        outState.putBundle(FEATURE_RESETS_SAVE_BUNDLE_KEY, companionResets);


    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        super.onCharacterChanged(character);
        healingViewHelper.onCharacterChanged(character);
        updateView();
    }

    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        super.onCharacterLoaded(character);
        healingViewHelper.onCharacterLoaded(character);

        buildFeatureResets(character);
        buildSpellSlotResets(character);
        buildCompanionRests(character);
    }

    private void buildCompanionRests(Character character) {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);

        Collection<CharacterCompanion> companions = character.getCompanions();
        List<CompanionResetInfo> companionResets = new ArrayList<>();
        int index = -1;
        for (CharacterCompanion each : companions) {
            index++;
            if (!each.isActive()) continue;

            CompanionResetInfo resetInfo = new CompanionResetInfo();
            resetInfo.name = each.getName();
            resetInfo.companionIndex = index;
            resetInfo.reset = true;
            resetInfo.description = each.getRace().getName() + " - " + each.getType().getName(getResources());

            Bundle resetBundle = null;
            if (savedCompanionResets != null) {
                resetBundle = savedCompanionResets.getBundle(index + "");
            }
            if (resetBundle != null) {
                resetInfo.reset = resetBundle.getByte(RESET) != 0;
            } else {
                resetInfo.reset = true;
            }

            companionResets.add(resetInfo);
        }
        savedCompanionResets = null;

        companionResetsAdapter = new CompanionResetsAdapter(getActivity(), companionResets);
        companionListView.setAdapter(companionResetsAdapter);

        companionListView.setHasFixedSize(false);
        companionListView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        companionListView.addItemDecoration(itemDecoration);

        if (companionResets.isEmpty()) {
            companion_resets.setVisibility(View.GONE);
        } else {
            companion_resets.setVisibility(View.VISIBLE);
        }

    }

    protected void buildSpellSlotResets(@NonNull Character character) {
        List<SpellSlotResetInfo> spellSlotResets = new ArrayList<>();

        final List<Character.SpellLevelInfo> spellInfos = character.getSpellInfos();
        for (Character.SpellLevelInfo each : spellInfos) {
            if (each.getLevel() == 0) continue;
            SpellSlotResetInfo resetInfo = new SpellSlotResetInfo();
            resetInfo.level = each.getLevel();
            resetInfo.maxSlots = each.getMaxSlots();
            resetInfo.availableSlots = each.getSlotsAvailable();

            Bundle resetBundle = null;
            if (savedSpellSlotResets != null) {
                resetBundle = savedSpellSlotResets.getBundle(each.getLevel() + "");
            }
            if (resetBundle != null) {
                resetInfo.reset = resetBundle.getByte(RESET) != 0;
                resetInfo.restoreSlots = resetBundle.getInt(NUM_TO_RESTORE);
            } else {

                int toRestore = getSlotsToRestore(each);
                resetInfo.reset = toRestore > 0;
                if (resetInfo.reset) {
                    resetInfo.restoreSlots = toRestore;
                } else {
                    resetInfo.restoreSlots = 0;
                }
            }
            spellSlotResets.add(resetInfo);
        }
        savedSpellSlotResets = null;


        spellSlotResetAdapter = new SpellSlotsResetsAdapter(getActivity(), spellSlotResets);
        spell_slot_list.setAdapter(spellSlotResetAdapter);

        spell_slot_list.setHasFixedSize(false);
        spell_slot_list.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST);
        spell_slot_list.addItemDecoration(horizontalDecoration);
    }

    protected void buildFeatureResets(@NonNull Character character) {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);

        Collection<FeatureInfo> features = character.getFeatureInfos();
        List<FeatureResetInfo> featureResets = new ArrayList<>();
        for (FeatureInfo each : features) {
            RefreshType refreshOn = each.getRefreshesOn();
            if (refreshOn == null) continue;

            FeatureResetInfo resetInfo = new FeatureResetInfo();
            resetInfo.name = each.getName();
            resetInfo.description = each.getShortDescription();
            int maxUses = each.evaluateMaxUses(character);
            int usesRemaining = character.getUsesRemaining(each);
            Bundle resetBundle = null;
            if (savedFeatureResets != null) {
                resetBundle = savedFeatureResets.getBundle(each.getName());
            }
            if (resetBundle != null) {
                resetInfo.reset = resetBundle.getByte(RESET) != 0;
                resetInfo.numToRestore = resetBundle.getInt(NUM_TO_RESTORE);
            } else {
                resetInfo.reset = shouldReset(refreshOn);
                if (resetInfo.reset) {
                    resetInfo.numToRestore = maxUses - usesRemaining;
                } else {
                    resetInfo.numToRestore = 0;
                }
            }
            resetInfo.refreshOn = refreshOn;
            resetInfo.maxToRestore = maxUses - usesRemaining;
            resetInfo.uses = getString(R.string.fraction_d_slash_d, usesRemaining, maxUses);
            resetInfo.needsResfesh = usesRemaining != maxUses;
            featureResets.add(resetInfo);
        }
        savedFeatureResets = null;
        featureResetAdapter = new FeatureResetsAdapter(getActivity(), featureResets);
        featureListView.setAdapter(featureResetAdapter);

        featureListView.setHasFixedSize(false);
        featureListView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        featureListView.addItemDecoration(itemDecoration);
    }


    protected abstract int getSlotsToRestore(Character.SpellLevelInfo each);

    protected void configureCommon(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //featureResetsGroup = view.findViewById(R.id.feature_resets);
        healingViewHelper.configureCommon(view, savedInstanceState);


        featureListView = (RecyclerView) view.findViewById(R.id.feature_list);
        spell_slot_list = (RecyclerView) view.findViewById(R.id.spell_slot_list);
        companionListView = (RecyclerView) view.findViewById(R.id.companion_list);
        companion_resets = view.findViewById(R.id.companion_resets);
    }

    protected abstract boolean shouldReset(RefreshType refreshesOn);

    public void updateView() {
        Character character = getCharacter();
        if (character == null) return;
        healingViewHelper.updateView(character);
    }

    protected boolean updateCommonRequest(@NonNull RT request) {
        boolean isValid = healingViewHelper.updateRequest(request);
        for (FeatureResetInfo each : featureResetAdapter.resets) {
            if (each.reset) {
                isValid = isValid && each.numToRestore <= each.maxToRestore;
                request.addFeatureReset(each.name, each.numToRestore);
            }
        }
        for (SpellSlotResetInfo each : spellSlotResetAdapter.resets) {
            if (each.reset) {
                isValid = isValid && each.restoreSlots <= each.maxSlots - each.availableSlots;
                request.addSpellSlotReset(each.level, each.restoreSlots);
            }
        }
        for (CompanionResetInfo each : companionResetsAdapter.resets) {
            if (each.reset) {
                request.addCompanionReset(each.name, each.companionIndex);
            }
        }
        return isValid;
    }

    static class SpellSlotResetViewHolder extends BindableComponentViewHolder<SpellSlotResetInfo, Context, SpellSlotsResetsAdapter> {
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

    static class SpellSlotsResetsAdapter extends RecyclerView.Adapter<SpellSlotResetViewHolder> {
        private final List<SpellSlotResetInfo> resets;
        private final Context context;

        SpellSlotsResetsAdapter(Context context, List<SpellSlotResetInfo> resets) {
            this.context = context;
            this.resets = resets;
        }

        @NonNull
        @Override
        public SpellSlotResetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context, R.layout.rest_spell_slot_reset_item, null);
            SpellSlotResetViewHolder holder = new SpellSlotResetViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull SpellSlotResetViewHolder holder, int position) {
            holder.bind(context, this, resets.get(position));
        }

        @Override
        public int getItemCount() {
            return resets.size();
        }
    }

    static class CompanionResetViewHolder extends BindableComponentViewHolder<CompanionResetInfo, Context, CompanionResetsAdapter> {
        @NonNull
        final CheckBox name;
        @NonNull
        final TextView description;

        public CompanionResetViewHolder(@NonNull View view) {
            super(view);
            name = (CheckBox) view.findViewById(R.id.name);
            description = (TextView) view.findViewById(R.id.description);
        }

        @Override
        public void bind(@NonNull final Context context, final CompanionResetsAdapter adapter, @NonNull final CompanionResetInfo row) {
            name.setOnCheckedChangeListener(null);
            name.setText(row.name);
            name.setChecked((row.reset));
            description.setText(row.description);
            name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    row.reset = isChecked;
                }
            });
        }
    }

    static class CompanionResetsAdapter extends RecyclerView.Adapter<CompanionResetViewHolder> {
        private final List<CompanionResetInfo> resets;
        private final Context context;

        public CompanionResetsAdapter(Context context, List<CompanionResetInfo> resets) {
            this.context = context;
            this.resets = resets;
        }


        public CompanionResetInfo getItem(int position) {
            return resets.get(position);
        }

        @NonNull
        @Override
        public CompanionResetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context, R.layout.companion_rest_layout, null);
            CompanionResetViewHolder viewHolder = new CompanionResetViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final CompanionResetViewHolder viewHolder, int position) {
            final CompanionResetInfo row = getItem(position);
            viewHolder.bind(context, this, row);

        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return resets.size();
        }


    }

    static class FeatureResetsAdapter extends RecyclerView.Adapter<FeatureResetViewHolder> {
        private final List<FeatureResetInfo> resets;
        private final Context context;

        public FeatureResetsAdapter(Context context, List<FeatureResetInfo> resets) {
            this.context = context;
            this.resets = resets;
        }


        public FeatureResetInfo getItem(int position) {
            return resets.get(position);
        }

        @NonNull
        @Override
        public FeatureResetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context, R.layout.feature_reset_item, null);
            FeatureResetViewHolder viewHolder = new FeatureResetViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final FeatureResetViewHolder viewHolder, int position) {
            final FeatureResetInfo row = getItem(position);
            viewHolder.bind(context, this, row);

        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return resets.size();
        }


    }

    static class FeatureResetViewHolder extends BindableComponentViewHolder<FeatureResetInfo, Context, FeatureResetsAdapter> {
        @Nullable
        public TextWatcher watcher;
        @NonNull
        final CheckBox name;
        @NonNull
        final TextView description;
        @NonNull
        final TextView uses;
        @NonNull
        final EditText numToRestore;

        public FeatureResetViewHolder(@NonNull View view) {
            super(view);
            name = (CheckBox) view.findViewById(R.id.feature_name);
            description = (TextView) view.findViewById(R.id.description);
            uses = (TextView) view.findViewById(R.id.uses);
            numToRestore = (EditText) view.findViewById(R.id.num_to_restore);
        }

        @Override
        public void bind(@NonNull final Context context, final FeatureResetsAdapter adapter, @NonNull final FeatureResetInfo row) {
            if (watcher != null) {
                numToRestore.removeTextChangedListener(watcher);
            }
            name.setOnCheckedChangeListener(null);

            name.setText(row.name);
            name.setChecked(row.reset);

            description.setText(row.description);

            uses.setText(row.uses);

            numToRestore.setText(NumberUtils.formatNumber(row.numToRestore));
            numToRestore.setEnabled(row.reset);

            if (!row.needsResfesh) {
                name.setChecked(false);
                name.setEnabled(false);
                numToRestore.setEnabled(false);
            } else {
                name.setEnabled(true);
            }

            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    numToRestore.setError(null);
                }

                @Override
                public void afterTextChanged(@Nullable Editable s) {
                    // TODO error handling, if val is too large
                    if (s == null) return;
                    if (!row.reset) return;

                    String stringVal = s.toString().trim();
                    if (stringVal.length() == 0) return;
                    int value;
                    try {
                        value = Integer.parseInt(stringVal);
                    } catch (Exception e) {
                        numToRestore.setError(context.getString(R.string.enter_number_less_than_equal_n, row.maxToRestore));
                        return;
                    }
                    if (value > row.maxToRestore) {
                        numToRestore.setError(context.getString(R.string.enter_number_less_than_equal_n, row.maxToRestore));
                    }

                    row.numToRestore = value;
                }
            };
            numToRestore.addTextChangedListener(watcher);
            this.watcher = watcher;

            name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    row.reset = isChecked;
                    numToRestore.setEnabled(row.reset);
                    if (row.reset) {
                        // force a validation
                        numToRestore.setText(numToRestore.getText());
                    } else {
                        numToRestore.setError(null);
                    }
                }
            });
        }
    }

    @Override
    public void hideKeyboardFrom(@NonNull TextView v) {
        super.hideKeyboardFrom(v);
    }
}

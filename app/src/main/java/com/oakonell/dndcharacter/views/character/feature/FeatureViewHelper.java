package com.oakonell.dndcharacter.views.character.feature;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.*;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.components.UseType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.ComponentLaunchHelper;

import java.util.ArrayList;

/**
 * Created by Rob on 5/6/2016.
 */
public class FeatureViewHelper {
    private final CharacterActivity context;
    final FeatureViewInterface view;

    @NonNull
    public final TextView source;
    @NonNull
    public final TextView shortDescription;

    @NonNull
    public final ViewGroup limited_uses_group;

    @NonNull
    public final TextView uses_label;
    private final Button add_use;
    private final Button subtract_use;
    @NonNull
    public final EditText uses_remaining;
    public final TextView uses_remaining_readonly;
    public final TextView uses_total;

    @NonNull
    public final TextView refreshes_label;

    private final Button use_spell_slot;
    private final Spinner spell_slot_level;
    private final ViewGroup spell_slot_use_group;


    FeatureViewHelper(CharacterActivity activity, @NonNull final FeatureViewInterface view) {
        this.context = activity;
        this.view = view;

        source = view.getSourceTextView();
        limited_uses_group = view.getLimitedUsesGroupViewGroup();
        uses_label = view.getUsesLabelTextView();
        shortDescription = view.getShortDescriptionTextView();

        uses_remaining = view.getUsesRemainingEditText();
        uses_remaining_readonly = view.getUsesRemainingReadOnlyTextView();
        uses_total = view.getUsesTotalTextView();
        refreshes_label = view.getRefreshesLabelTextView();

        spell_slot_level = view.getSpellSlotLevelSpinner();
        use_spell_slot = view.getUseSpellSlotButton();
        spell_slot_use_group = view.getSpellSlotUseGroup();

        add_use = view.getAddUseButton();
        subtract_use = view.getSubtractUseButton();
    }

    public void bind(@NonNull final FeatureInfo info) {
        if (view.isReadOnly()) {
            add_use.setVisibility(View.GONE);
            subtract_use.setVisibility(View.GONE);
            uses_remaining.setVisibility(View.GONE);
            uses_remaining_readonly.setVisibility(View.VISIBLE);
        } else {
            add_use.setVisibility(View.VISIBLE);
            subtract_use.setVisibility(View.VISIBLE);
            uses_remaining.setVisibility(View.VISIBLE);
            uses_remaining_readonly.setVisibility(View.GONE);
        }

        source.setText(info.getSourceString(context.getResources()));
        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentLaunchHelper.editComponent(context, info.getSource(), false);
            }
        });

        boolean hasLimitedUses = info.hasLimitedUses();
        if (!hasLimitedUses) {
            limited_uses_group.setVisibility(View.GONE);
        } else {
            limited_uses_group.setVisibility(View.VISIBLE);
            bindLimitedUseViews(info);

            if (info.getUseType() == UseType.PER_USE) {
                uses_label.setText(R.string.uses);
            } else {
                uses_label.setText(R.string.pool);
            }
        }

        shortDescription.setText(info.getShortDescription());


        if (info.usesSpellSlot()) {
            spell_slot_use_group.setVisibility(View.VISIBLE);

            ArrayList<String> spellLevels = view.getSpellLevels();

            ArrayAdapter spell_slot_levelAdapter = (ArrayAdapter) spell_slot_level.getAdapter();
            if (spell_slot_levelAdapter == null) {
                spell_slot_levelAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spellLevels);
                spell_slot_level.setAdapter(spell_slot_levelAdapter);
            }
            spellLevels.clear();
            for (com.oakonell.dndcharacter.model.character.Character.SpellLevelInfo each : context.getCharacter().getSpellInfos()) {
                if (each.getLevel() == 0) continue;
                spellLevels.add(context.getString(R.string.spell_slot_level_and_uses, each.getLevel(), each.getSlotsAvailable()));
            }
            spell_slot_levelAdapter.notifyDataSetChanged();

            spell_slot_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    final Character.SpellLevelInfo levelInfo = context.getCharacter().getSpellInfos().get(spell_slot_level.getSelectedItemPosition() + 1);
                    use_spell_slot.setEnabled(levelInfo.getSlotsAvailable() > 0);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            final Character.SpellLevelInfo levelInfo = context.getCharacter().getSpellInfos().get(spell_slot_level.getSelectedItemPosition() + 1);
            use_spell_slot.setEnabled(levelInfo.getSlotsAvailable() > 0);
            use_spell_slot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO actually use, or commit only on done depending on 'view' context
                    context.getCharacter().useSpellSlot(spell_slot_level.getSelectedItemPosition() + 1);
                    context.saveCharacter();
                    context.updateViews();
                }
            });
        } else {
            spell_slot_use_group.setVisibility(View.GONE);
        }

    }

    protected void bindLimitedUseViews(@NonNull final FeatureInfo info) {
        final int maxUses = info.evaluateMaxUses(context.getCharacter());
        final int usesRemaining = context.getCharacter().getUsesRemaining(info);


        updateView(maxUses, usesRemaining);
        add_use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(uses_remaining.getText().toString());
                value = value + 1;
                uses_remaining.setText(NumberUtils.formatNumber(value));
                uses_remaining_readonly.setText(NumberUtils.formatNumber(value));

                updateView(maxUses, value);
            }
        });
        subtract_use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(uses_remaining.getText().toString());
                value = value - 1;
                uses_remaining.setText(NumberUtils.formatNumber(value));
                uses_remaining_readonly.setText(NumberUtils.formatNumber(value));

                updateView(maxUses, value);
            }
        });

        uses_remaining.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                uses_remaining.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if (string.length() == 0) {
                    updateView(maxUses, 0);
                    return;
                }
                try {
                    int value = Integer.parseInt(string);
                    if (value > maxUses) {
                        uses_remaining.setError(context.getString(R.string.enter_number_less_than_equal_n, maxUses));
                        return;
                    }
                    updateView(maxUses, value);
                } catch (NumberFormatException e) {
                    uses_remaining.setError(context.getString(R.string.enter_number_less_than_equal_n, maxUses));
                }
            }
        });

        uses_remaining.setText(NumberUtils.formatNumber(usesRemaining));
        uses_remaining_readonly.setText(NumberUtils.formatNumber(usesRemaining));
        uses_total.setText(context.getString(R.string.fraction_slash_d, maxUses));
        refreshes_label.setText(context.getString(R.string.refreshes_on_s, context.getString(info.getRefreshesOn().getStringResId())));
    }

    private void updateView(int maxUses, int usesRemaining) {
        add_use.setEnabled(usesRemaining < maxUses);
        subtract_use.setEnabled(usesRemaining > 0);
    }
}

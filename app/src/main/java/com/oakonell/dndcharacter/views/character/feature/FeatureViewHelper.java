package com.oakonell.dndcharacter.views.character.feature;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.*;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.components.Feature;
import com.oakonell.dndcharacter.model.components.IFeatureAction;
import com.oakonell.dndcharacter.model.components.UseType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.ComponentLaunchHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.deanwild.flowtextview.FlowTextView;

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

    public final RecyclerView action_list;
    private ActionAdapter actionsAdapter;
    private Set<FeatureContextArgument> filter;

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

        action_list = view.getActionListView();
        filter = view.getActionFilter();
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

        shortDescription.setText(info.getShortDescription());

        source.setText(info.getSourceString(context.getResources()));
        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentLaunchHelper.editComponent(context, info.getSource(), false);
            }
        });

        bindLimitedUseViews(info);

        bindSpellSlotViews(info);

        bindActions(info);

    }

    private void bindActions(FeatureInfo info) {
        if (actionsAdapter == null) {
            actionsAdapter = new ActionAdapter(this, context);
            action_list.setAdapter(actionsAdapter);

            action_list.setLayoutManager(UIUtils.createLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            action_list.setHasFixedSize(false);

            DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST);
            action_list.addItemDecoration(itemDecoration);
        }
        actionsAdapter.setFeature(info, filter);
    }

    protected void bindSpellSlotViews(@NonNull FeatureInfo info) {
        if (!info.usesSpellSlot()) {
            spell_slot_use_group.setVisibility(View.GONE);
            return;
        }
        spell_slot_use_group.setVisibility(View.VISIBLE);

        ArrayList<String> spellLevels = view.getSpellLevels();

        ArrayAdapter spell_slot_levelAdapter = (ArrayAdapter) spell_slot_level.getAdapter();
        if (spell_slot_levelAdapter == null) {
            spell_slot_levelAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spellLevels);
            spell_slot_level.setAdapter(spell_slot_levelAdapter);
        }
        spellLevels.clear();
        for (Character.SpellLevelInfo each : context.getCharacter().getSpellInfos()) {
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

    }

    protected void bindLimitedUseViews(@NonNull final FeatureInfo info) {
        boolean hasLimitedUses = info.hasLimitedUses();
        if (!hasLimitedUses) {
            limited_uses_group.setVisibility(View.GONE);
            return;
        }
        limited_uses_group.setVisibility(View.VISIBLE);

        final int maxUses = info.evaluateMaxUses(context.getCharacter());
        final int usesRemaining = context.getCharacter().getUsesRemaining(info);


        updateView(maxUses, usesRemaining);
        bindEditableLimitedUsesViews(maxUses);

        uses_remaining.setText(NumberUtils.formatNumber(usesRemaining));
        uses_remaining_readonly.setText(NumberUtils.formatNumber(usesRemaining));
        uses_total.setText(context.getString(R.string.fraction_slash_d, maxUses));
        refreshes_label.setText(context.getString(R.string.refreshes_on_s, context.getString(info.getRefreshesOn().getStringResId())));

        if (info.getUseType() == UseType.PER_USE) {
            uses_label.setText(R.string.uses);
        } else {
            uses_label.setText(R.string.pool);
        }

    }

    private void bindEditableLimitedUsesViews(final int maxUses) {
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
    }

    public void updateView(int maxUses, int usesRemaining) {
        add_use.setEnabled(usesRemaining < maxUses);
        subtract_use.setEnabled(usesRemaining > 0);
        if (actionsAdapter != null) {
            actionsAdapter.notifyDataSetChanged();
        }
    }

    public static class ActionAdapter extends RecyclerView.Adapter<ActionViewHolder> {
        FeatureViewHelper viewHelper;
        private final CharacterActivity context;
        private FeatureInfo info;
        private List<IFeatureAction> list;

        ActionAdapter(FeatureViewHelper viewHelper, CharacterActivity context) {
            this.viewHelper = viewHelper;
            this.context = context;
        }

        @NonNull
        @Override
        public ActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feature_action_row, parent, false);
            return new ActionViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(@NonNull ActionViewHolder holder, int position) {
            IFeatureAction action = list.get(position);
            holder.bind(context, this, action);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void setFeature(@NonNull FeatureInfo info, @Nullable Set<FeatureContextArgument> filter) {
            list = new ArrayList<>();
            for (IFeatureAction each : info.getActionsAndEffects()) {
                if (filter == null || (!each.hasActionContext() || each.isActionInContext(filter))) {
                    list.add(each);
                }
            }

            this.info = info;

            notifyDataSetChanged();
        }

    }


    public static class ActionViewHolder extends BindableComponentViewHolder<IFeatureAction, CharacterActivity, ActionAdapter> {
        @NonNull
        public final ViewGroup use_group;
        @NonNull
        public final Button useButton;
        @NonNull
        public final FlowTextView use_description;
        @NonNull
        public final TextView remaining_uses;

        @NonNull
        public final ViewGroup pool_apply_group;
        @NonNull
        public final TextView pool_value;
        @NonNull
        public final ImageButton pool_apply_button;
        @NonNull
        public final ImageButton pool_cancel_button;

        public ActionViewHolder(@NonNull View view) {
            super(view);
            remaining_uses = (TextView) view.findViewById(R.id.remaining_uses);


            useButton = (Button) view.findViewById(R.id.use_button);
            use_description = (FlowTextView) view.findViewById(R.id.use_description);
            // the default FlowTextView text size is not correct..
            use_description.setTextSize(remaining_uses.getTextSize());

            pool_apply_group = (ViewGroup) view.findViewById(R.id.pool_apply_group);
            use_group = (ViewGroup) view.findViewById(R.id.use_description);
            pool_value = (TextView) view.findViewById(R.id.pool_value);
            pool_apply_button = (ImageButton) view.findViewById(R.id.pool_apply_button);
            pool_cancel_button = (ImageButton) view.findViewById(R.id.pool_cancel_button);
        }

        public void bind(@NonNull final CharacterActivity context, @NonNull final ActionAdapter adapter, @NonNull final IFeatureAction action) {
            pool_apply_group.setVisibility(View.GONE);
            final FeatureInfo info = adapter.info;

            int maxUses = info.evaluateMaxUses(context.getCharacter());
            final int usesRemaining = maxUses == 0 ? 0 : adapter.viewHelper.view.getUsesRemaining(context, info);

            final String description = action.getActionDescription();
            Spanned html = Html.fromHtml(description == null ? "" : description);
            use_description.setText(html);

            if (action.getCost() > 0 || maxUses == 0) {
                // TODO get a short description? Possibly place the cost on the button?
                if (action.getCost() != 1) {
                    useButton.setText(context.getString(R.string.action_with_cost, action.getAction(), action.getCost()));
                } else {
                    useButton.setText(action.getAction());
                }
                useButton.setEnabled(usesRemaining >= action.getCost() || maxUses == 0);
                // simple use action
                useButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO ask for possible prompts for action
                        final Map<Feature.FeatureEffectVariable, String> values = new HashMap<>();
                        Runnable continuation = new Runnable() {
                            @Override
                            public void run() {
                                adapter.viewHelper.useAction(info, action, values);
                            }
                        };
                        if (!action.hasVariables()) {
                            continuation.run();
                            return;
                        }

                        // prompt
                        final List<Feature.FeatureEffectVariable> variables = action.getVariables();

                        prompt(variables, values, 0, context, continuation);
                    }


                });
            } else {
                useButton.setText(action.getAction());
                useButton.setEnabled(usesRemaining > 0);
                // pooled use action
                remaining_uses.setText(" / " + usesRemaining);
                useButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pool_apply_group.setVisibility(View.VISIBLE);
                        pool_value.requestFocus();

                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(pool_value, InputMethodManager.SHOW_IMPLICIT);

                        use_group.setVisibility(View.GONE);
                    }
                });

                pool_cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pool_apply_group.setVisibility(View.GONE);
                        pool_value.setText("");
                        use_group.setVisibility(View.VISIBLE);

                    }
                });
                pool_apply_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String string = pool_value.getText().toString().trim();
                        if (string.length() == 0) {
                            pool_value.setError(context.getString(R.string.enter_number_less_than_equal_n, usesRemaining));
                            return;
                        }
                        int value;
                        try {
                            value = Integer.parseInt(string);
                        } catch (NumberFormatException e) {
                            pool_value.setError(context.getString(R.string.enter_number_less_than_equal_n, usesRemaining));
                            return;
                        }
                        if (value > usesRemaining) {
                            pool_value.setError(context.getString(R.string.enter_number_less_than_equal_n, usesRemaining));
                            return;
                        }
                        pool_apply_group.setVisibility(View.GONE);
                        pool_value.setText("");
                        use_group.setVisibility(View.VISIBLE);
                        //adapter.notifyItemChanged(getAdapterPosition());
                        adapter.viewHelper.useFeature(info, value);
                    }
                });

                pool_value.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            pool_apply_button.performClick();
                            return true;
                        }
                        return false;
                    }
                });

                pool_value.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        pool_value.setError(null);
                    }

                    @Override
                    public void afterTextChanged(@Nullable Editable s) {
                        if (s == null) return;
                        String string = s.toString();
                        if (string.length() == 0) return;
                        int value;
                        try {
                            value = Integer.parseInt(string);
                        } catch (NumberFormatException e) {
                            pool_value.setError(context.getString(R.string.enter_number_less_than_equal_n, usesRemaining));
                            return;
                        }
                        if (value > usesRemaining) {
                            pool_value.setError(context.getString(R.string.enter_number_less_than_equal_n, usesRemaining));
                        }
                    }
                });
            }
        }


    }

    private void useFeature(FeatureInfo info, int value) {
        view.useFeature(context, info, value);
    }

    private void useAction(FeatureInfo info, IFeatureAction action, Map<Feature.FeatureEffectVariable, String> values) {
        view.useAction(context, info, action, values);
    }


    public static void prompt(@NonNull final List<Feature.FeatureEffectVariable> variables, @NonNull final Map<Feature.FeatureEffectVariable, String> values, final int index, @NonNull final CharacterActivity context, @NonNull final Runnable continuation) {
// TODO  how to deal with rotation/save state/restore with the continuation?
        final Feature.FeatureEffectVariable variable = variables.get(index);
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle(variable.getPrompt());

        b.setNegativeButton(R.string.cancel_button_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@NonNull DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final String[] choices = variable.getValues();
        if (choices.length == 0) {
            final EditText input = new EditText(context);
            b.setView(input);
            b.setPositiveButton(R.string.ok_button_label, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(@NonNull DialogInterface dialog, int which) {
                    dialog.dismiss();
                    values.put(variable, input.getText().toString());
                    int next = index + 1;
                    if (next < variables.size()) {
                        prompt(variables, values, next, context, continuation);
                    } else {
                        continuation.run();
                    }
                }
            });
        } else {
            b.setItems(choices, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(@NonNull DialogInterface dialog, int which) {
                    dialog.dismiss();
                    values.put(variable, choices[which]);
                    int next = index + 1;
                    if (next < variables.size()) {
                        prompt(variables, values, next, context, continuation);
                    } else {
                        continuation.run();
                    }
                }

            });
        }
        b.show();
    }

}

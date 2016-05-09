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
import android.widget.Toast;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.FeatureInfo;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.components.Feature;
import com.oakonell.dndcharacter.model.components.IFeatureAction;
import com.oakonell.dndcharacter.model.components.UseType;
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
 * Created by Rob on 1/4/2016.
 */
public class FeatureViewHolder extends BindableComponentViewHolder<FeatureInfo, CharacterActivity, RecyclerView.Adapter<?>> implements FeatureViewInterface {
    @NonNull
    public final TextView name;
    @NonNull
    public final TextView source;
    @NonNull
    public final TextView shortDescription;

    @NonNull
    public final ViewGroup limited_uses_group;

    @NonNull
    public final TextView uses_label;
    @NonNull
    public final TextView uses_remaining;

    @NonNull
    public final TextView refreshes_label;

    @NonNull
    public final RecyclerView action_list;
    private final Button use_spell_slot;
    private final Spinner spell_slot_level;
    private final ViewGroup spell_slot_use_group;

    private Set<FeatureContextArgument> filter;

    private ActionAdapter actionsAdapter;
    private ArrayList<String> spellLevels;


    public FeatureViewHolder(@NonNull View view) {
        super(view);
        name = (TextView) view.findViewById(R.id.name);
        source = (TextView) view.findViewById(R.id.source);

        limited_uses_group = (ViewGroup) view.findViewById(R.id.limited_uses_group);
        action_list = (RecyclerView) view.findViewById(R.id.actions_list);

        uses_label = (TextView) view.findViewById(R.id.uses_label);
        uses_remaining = (TextView) view.findViewById(R.id.remaining);
        shortDescription = (TextView) view.findViewById(R.id.short_description);
        refreshes_label = (TextView) view.findViewById(R.id.refreshes_label);

        spell_slot_use_group = (ViewGroup) view.findViewById(R.id.spell_slot_use_group);
        spell_slot_level = (Spinner) view.findViewById(R.id.spell_slot_level);
        use_spell_slot = (Button) view.findViewById(R.id.use_spell_slot);
    }

    public FeatureViewHolder(@NonNull View view, Set<FeatureContextArgument> filter) {
        this(view);
        this.filter = filter;
    }

    @Override
    public void bind(@NonNull final CharacterActivity context, final RecyclerView.Adapter<?> adapter, @NonNull final FeatureInfo info) {
        final int position = getAdapterPosition();

//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FeatureViewDialogFragment dialog = FeatureViewDialogFragment.createDialog(info);
//                dialog.show(context.getSupportFragmentManager(), "feature");
//            }
//        });


        name.setText(info.getName());
        FeatureViewHelper viewHelper = new FeatureViewHelper(context, this);
        viewHelper.bind(info);



        if (info.usesSpellSlot()) {
            spell_slot_use_group.setVisibility(View.VISIBLE);

            ArrayAdapter spell_slot_levelAdapter = (ArrayAdapter) spell_slot_level.getAdapter();
            if (spell_slot_levelAdapter == null) {
                spellLevels = new ArrayList<>();
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
                    context.getCharacter().useSpellSlot(spell_slot_level.getSelectedItemPosition() + 1);
                    context.saveCharacter();
                    context.updateViews();
                }
            });
        } else {
            spell_slot_use_group.setVisibility(View.GONE);
        }

        if (actionsAdapter == null) {
            actionsAdapter = new ActionAdapter(context);
            action_list.setAdapter(actionsAdapter);

            action_list.setLayoutManager(UIUtils.createLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            action_list.setHasFixedSize(false);

            DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST);
            action_list.addItemDecoration(itemDecoration);
        }
        actionsAdapter.setFeature(info, filter);
        itemView.requestLayout();

    }

    @Override
    public TextView getSourceTextView() {
        return source;
    }

    @Override
    public ViewGroup getLimitedUsesGroupViewGroup() {
        return limited_uses_group;
    }

    @Override
    public TextView getUsesLabelTextView() {
        return uses_label;
    }

    @Override
    public TextView getShortDescriptionTextView() {
        return shortDescription;
    }

    @Override
    public TextView getUsesRemainingTextView() {
        return uses_remaining;
    }

    @Override
    public TextView getRefreshesLabelTextView() {
        return refreshes_label;
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
            final int usesRemaining = context.getCharacter().getUsesRemaining(info);

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
                                adapter.context.getCharacter().useFeatureAction(info, action, values);
                                useButton.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        context.updateViews();
                                        context.saveCharacter();
                                    }
                                }, 10);
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
                        context.getCharacter().useFeature(info, value);
                        context.saveCharacter();
                        pool_apply_group.setVisibility(View.GONE);
                        pool_value.setText("");
                        use_group.setVisibility(View.VISIBLE);
                        //adapter.notifyItemChanged(getAdapterPosition());
                        use_group.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                context.updateViews();
                                context.saveCharacter();
                            }
                        }, 10);
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


    public static class ActionAdapter extends RecyclerView.Adapter<ActionViewHolder> {
        private final CharacterActivity context;
        private FeatureInfo info;
        private List<IFeatureAction> list;

        ActionAdapter(CharacterActivity context) {
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


    public static void prompt(@NonNull final List<Feature.FeatureEffectVariable> variables, @NonNull final Map<Feature.FeatureEffectVariable, String> values, final int index, @NonNull final CharacterActivity context, @NonNull final Runnable continuation) {
// TODO  how to deal with rotation/save state/restore with the continuation?
        final Feature.FeatureEffectVariable variable = variables.get(index);
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle(variable.getPrompt());

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




package com.oakonell.dndcharacter.views.character.feature;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.FeatureInfo;
import com.oakonell.dndcharacter.model.components.IFeatureAction;
import com.oakonell.dndcharacter.model.components.UseType;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.ComponentLaunchHelper;
import com.oakonell.dndcharacter.views.character.CharacterActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 1/4/2016.
 */
public class FeatureViewHolder extends BindableComponentViewHolder<FeatureInfo, CharacterActivity, RecyclerView.Adapter<?>> {
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
    private ActionAdapter actionsAdapter;


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

    }

    @Override
    public void bind(@NonNull final CharacterActivity context, final RecyclerView.Adapter<?> adapter, @NonNull final FeatureInfo info) {
        final int position = getAdapterPosition();

        name.setText(info.getName());
        source.setText(info.getSourceString(context.getResources()));
        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentLaunchHelper.editComponent(context, info.getSource(), false);
            }
        });

        if (actionsAdapter == null) {
            actionsAdapter = new ActionAdapter(context);
            action_list.setAdapter(actionsAdapter);

            action_list.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            action_list.setHasFixedSize(false);

            DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST);
            action_list.addItemDecoration(itemDecoration);
        }
        actionsAdapter.setFeature(info);


        boolean hasLimitedUses = info.hasLimitedUses();
        if (!hasLimitedUses) {
            limited_uses_group.setVisibility(View.GONE);
        } else {
            limited_uses_group.setVisibility(View.VISIBLE);
            bindLimitedUseViews(context, adapter, info, position);
            if (info.getUseType() == UseType.PER_USE) {
                uses_label.setText(R.string.uses);
            } else {
                uses_label.setText(R.string.pool);
            }
        }

        shortDescription.setText(info.getShortDescription());
    }

    protected void bindLimitedUseViews(@NonNull final CharacterActivity context, final RecyclerView.Adapter<?> adapter, @NonNull final FeatureInfo info, final int position) {
        int maxUses = info.evaluateMaxUses(context.getCharacter());
        final int usesRemaining = context.getCharacter().getUsesRemaining(info);

        uses_remaining.setText(context.getString(R.string.fraction_d_slash_d, usesRemaining, maxUses));
        refreshes_label.setText(context.getString(R.string.refreshes_on_s, context.getString(info.getRefreshesOn().getStringResId())));
    }


    private static class ActionViewHolder extends BindableComponentViewHolder<IFeatureAction, CharacterActivity, ActionAdapter> {
        @NonNull
        public final ViewGroup use_group;
        @NonNull
        public final Button useButton;
        @NonNull
        public final TextView use_description;
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
            use_description = (TextView) view.findViewById(R.id.use_description);

            pool_apply_group = (ViewGroup) view.findViewById(R.id.pool_apply_group);
            use_group = (ViewGroup) view.findViewById(R.id.use_group);
            pool_value = (TextView) view.findViewById(R.id.pool_value);
            pool_apply_button = (ImageButton) view.findViewById(R.id.pool_apply_button);
            pool_cancel_button = (ImageButton) view.findViewById(R.id.pool_cancel_button);
        }

        public void bind(@NonNull final CharacterActivity context, @NonNull final ActionAdapter adapter, @NonNull final IFeatureAction action) {
            pool_apply_group.setVisibility(View.GONE);
            final FeatureInfo info = adapter.info;

            int maxUses = info.evaluateMaxUses(context.getCharacter());
            final int usesRemaining = context.getCharacter().getUsesRemaining(info);

            use_description.setText(action.getActionDescription());

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
                        adapter.context.getCharacter().useFeatureAction(info, action);
                        useButton.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                context.updateViews();
                                context.saveCharacter();
                            }
                        }, 10);
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


    private static class ActionAdapter extends RecyclerView.Adapter<ActionViewHolder> {
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

        public void setFeature(@NonNull FeatureInfo info) {
            this.list = new ArrayList<>(info.getActionsAndEffects());
            this.info = info;
            notifyDataSetChanged();
        }
    }
}




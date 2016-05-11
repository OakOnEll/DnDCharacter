package com.oakonell.dndcharacter.views.character.feature;

import android.content.res.Resources;
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
import android.widget.ImageView;
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
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Rob on 5/4/2016.
 */
public class FeatureViewDialogFragment extends AbstractCharacterDialogFragment implements FeatureViewInterface {
    public static final String NAME = "name";
    private TextView source;
    private TextView shortDescription;

    private ViewGroup limited_uses_group;
    private TextView uses_label;
    private Button add_use;
    private Button subtract_use;
    private EditText uses_remaining;
    private TextView uses_remaining_readonly;
    private TextView uses_total;
    private TextView refreshes_label;

    private ArrayList<String> spellLevels = new ArrayList<>();
    private ViewGroup spell_slot_use_group;
    private Spinner spell_slot_level;
    private Button use_spell_slot;

    private RecyclerView action_list;
    private FeatureViewHelper viewHelper;

    // TODO handle rotate/savestate for these pending actions
    List<IPendingUse> pendingActions = new ArrayList<>();

    private ViewGroup pending_actions_group;
    private RecyclerView pending_actions_list;
    private PendingActionAdapter pendingActionsAdapter;


    @NonNull
    public static FeatureViewDialogFragment createDialog(FeatureInfo featureInfo) {
        final FeatureViewDialogFragment dialogFragment = new FeatureViewDialogFragment();
        String name = featureInfo.getName();

        Bundle args = new Bundle();
        args.putString(NAME, name);

        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Nullable
    private String getNameArgument() {
        return getArguments().getString(NAME);
    }

    @Nullable
    @Override
    protected String getTitle() {
        return getNameArgument();
    }


    @Nullable
    @Override
    protected View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feature_dialog, container);

        source = (TextView) view.findViewById(R.id.source);

        limited_uses_group = (ViewGroup) view.findViewById(R.id.limited_uses_group);
        action_list = (RecyclerView) view.findViewById(R.id.actions_list);

        uses_label = (TextView) view.findViewById(R.id.uses_label);
        uses_remaining = (EditText) view.findViewById(R.id.remaining);
        uses_remaining_readonly = (TextView) view.findViewById(R.id.remaining_readonly);
        add_use = (Button) view.findViewById(R.id.add_use);
        subtract_use = (Button) view.findViewById(R.id.subtract_use);

        uses_total = (TextView) view.findViewById(R.id.total);
        shortDescription = (TextView) view.findViewById(R.id.short_description);
        refreshes_label = (TextView) view.findViewById(R.id.refreshes_label);

        spell_slot_use_group = (ViewGroup) view.findViewById(R.id.spell_slot_use_group);
        spell_slot_level = (Spinner) view.findViewById(R.id.spell_slot_level);
        use_spell_slot = (Button) view.findViewById(R.id.use_spell_slot);

        action_list = (RecyclerView) view.findViewById(R.id.actions_list);

        pending_actions_group = (ViewGroup) view.findViewById(R.id.pending_actions_group);
        pending_actions_list = (RecyclerView) view.findViewById(R.id.pending_actions_list);


        return view;
    }

    @Override
    public void onCharacterLoaded(com.oakonell.dndcharacter.model.character.Character character) {
        super.onCharacterLoaded(character);
        viewHelper = new FeatureViewHelper(getMainActivity(), this);
        FeatureInfo info = character.getFeatureNamed(getNameArgument());
        viewHelper.bind(info);

        pendingActionsAdapter = new PendingActionAdapter(this);
        pending_actions_list.setAdapter(pendingActionsAdapter);

        pending_actions_list.setLayoutManager(UIUtils.createLinearLayoutManager(getMainActivity(), LinearLayoutManager.VERTICAL, false));
        pending_actions_list.setHasFixedSize(false);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getMainActivity(), DividerItemDecoration.VERTICAL_LIST);
        pending_actions_list.addItemDecoration(itemDecoration);
        updateViews();
    }

    @Override
    protected boolean onDone() {
        boolean isValid = super.onDone();
        FeatureInfo info = getCharacter().getFeatureNamed(getNameArgument());
        final int maxUses = info.evaluateMaxUses(getCharacter());

        String string = uses_remaining.getText().toString();
        int value = 0;
        if (string.length() > 0) {
            try {
                value = Integer.parseInt(string);
                if (value > maxUses) {
                    uses_remaining.setError(getString(R.string.enter_number_less_than_equal_n, maxUses));
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                uses_remaining.setError(getString(R.string.enter_number_less_than_equal_n, maxUses));
                isValid = false;
            }
        }
        if (isValid) {
            commitChanges(info, value);
        }
        return isValid;
    }

    private void commitChanges(FeatureInfo info, int value) {
        for (IPendingUse each : pendingActions) {
            each.apply(getCharacter());
        }
        getCharacter().setUsesRemaining(info, value);

        shortDescription.postDelayed(new Runnable() {
            @Override
            public void run() {
                getMainActivity().updateViews();
                getMainActivity().saveCharacter();
            }
        }, 10);
    }


    private void updateViews() {
        if (pendingActions.isEmpty()) {
            pending_actions_group.setVisibility(View.GONE);
        } else {
            pending_actions_group.setVisibility(View.VISIBLE);
        }
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
    public EditText getUsesRemainingEditText() {
        return uses_remaining;
    }

    @Override
    public TextView getUsesRemainingReadOnlyTextView() {
        return uses_remaining_readonly;
    }

    @Override
    public TextView getUsesTotalTextView() {
        return uses_total;
    }

    @Override
    public TextView getRefreshesLabelTextView() {
        return refreshes_label;
    }

    public boolean isReadOnly() {
        return false;
    }

    @Override
    public Spinner getSpellSlotLevelSpinner() {
        return spell_slot_level;
    }

    @Override
    public Button getUseSpellSlotButton() {
        return use_spell_slot;
    }

    @Override
    public ViewGroup getSpellSlotUseGroup() {
        return spell_slot_use_group;
    }

    @Override
    public ArrayList<String> getSpellLevels() {
        return spellLevels;
    }

    @Override
    public Button getAddUseButton() {
        return add_use;
    }

    @Override
    public Button getSubtractUseButton() {
        return subtract_use;
    }

    @Override
    public RecyclerView getActionListView() {
        return action_list;
    }

    @Override
    public Set<FeatureContextArgument> getActionFilter() {
        return Collections.emptySet();
    }


    @Override
    public void useAction(final CharacterActivity context, FeatureInfo info, IFeatureAction action, Map<Feature.FeatureEffectVariable, String> values) {
        final int maxUses = info.evaluateMaxUses(context.getCharacter());
        if (maxUses > 0) {
            int value = Integer.parseInt(uses_remaining.getText().toString());
            value = value - action.getCost();
            uses_remaining.setText(NumberUtils.formatNumber(value));
            uses_remaining_readonly.setText(NumberUtils.formatNumber(value));
            viewHelper.updateView(maxUses, value);
        }

        PendingAction pendingAction = new PendingAction();
        pendingAction.action = action;
        pendingAction.info = info;
        pendingAction.values = values;
        pendingActions.add(pendingAction);
        pendingActionsAdapter.notifyDataSetChanged();
        updateViews();
    }

    @Override
    public void useFeature(final CharacterActivity context, FeatureInfo info, int value) {
        final int maxUses = info.evaluateMaxUses(context.getCharacter());
        if (maxUses > 0) {
            int remaining = Integer.parseInt(uses_remaining.getText().toString());
            remaining = remaining - value;
            uses_remaining.setText(NumberUtils.formatNumber(remaining));
            uses_remaining_readonly.setText(NumberUtils.formatNumber(remaining));

            viewHelper.updateView(maxUses, remaining);
        }
        PendingFeatureUse pendingUse = new PendingFeatureUse();
        pendingUse.info = info;
        pendingUse.value = value;
        pendingActions.add(pendingUse);
        pendingActionsAdapter.notifyDataSetChanged();
        updateViews();
    }

    @Override
    public int getUsesRemaining(CharacterActivity context, FeatureInfo info) {
        int remaining = Integer.parseInt(uses_remaining.getText().toString());
        return remaining;
    }

    public static class PendingAction implements IPendingUse {
        FeatureInfo info;
        IFeatureAction action;
        Map<Feature.FeatureEffectVariable, String> values;

        @Override
        public void apply(Character character) {
            character.useFeatureAction(info, action, values);
        }

        @Override
        public String getText(Resources resources) {
            if (values != null && values.size() > 0) {
                StringBuilder builder = new StringBuilder("(");
                for (Iterator<Map.Entry<Feature.FeatureEffectVariable, String>> iter = values.entrySet().iterator(); iter.hasNext(); ) {
                    Map.Entry<Feature.FeatureEffectVariable, String> each = iter.next();
                    Feature.FeatureEffectVariable key = each.getKey();
                    String value = each.getValue();
                    String name = key.getName();
                    builder.append(name);
                    builder.append("='");
                    builder.append(value);
                    builder.append("'");
                    if (iter.hasNext()) {
                        builder.append("'");
                    }
                }
                builder.append(")");
                return "Use " + action.getName() + " with " + builder.toString();
            }
            return "Use " + action.getName();
        }

        @Override
        public void undo(FeatureViewDialogFragment dialog) {
            final int maxUses = info.evaluateMaxUses(dialog.getCharacter());
            int value = 0;
            if (maxUses > 0) {
                value = Integer.parseInt(dialog.uses_remaining.getText().toString());
                value = Math.min(value + action.getCost(), maxUses);
                dialog.uses_remaining.setText(NumberUtils.formatNumber(value));
                dialog.uses_remaining_readonly.setText(NumberUtils.formatNumber(value));
            }
            dialog.viewHelper.updateView(maxUses, value);
        }
    }

    public static class PendingFeatureUse implements IPendingUse {
        FeatureInfo info;
        int value;

        @Override
        public void apply(Character character) {
            character.useFeature(info, value);
        }

        @Override
        public String getText(Resources resources) {
            if (info.getUseType() == UseType.POOL) {
                return "Use " + info.getName() + " for " + value;
            }
            return "Use " + info.getName();
        }

        @Override
        public void undo(FeatureViewDialogFragment dialog) {
            int remaining = Integer.parseInt(dialog.uses_remaining.getText().toString());
            final int maxUses = info.evaluateMaxUses(dialog.getCharacter());

            remaining = Math.min(remaining + value, maxUses);
            dialog.uses_remaining.setText(NumberUtils.formatNumber(remaining));
            dialog.uses_remaining_readonly.setText(NumberUtils.formatNumber(remaining));

            dialog.viewHelper.updateView(maxUses, remaining);
        }
    }

    interface IPendingUse {
        void apply(Character character);

        String getText(Resources resources);

        void undo(FeatureViewDialogFragment dialog);
    }


    public static class PendingActionViewHolder extends BindableComponentViewHolder<IPendingUse, CharacterActivity, PendingActionAdapter> {
        private final TextView pending_action;
        ImageView delete;

        public PendingActionViewHolder(@NonNull View view) {
            super(view);
            pending_action = (TextView) view.findViewById(R.id.pending_action);
            delete = (ImageView) view.findViewById(R.id.delete);
        }

        public void bind(@NonNull final CharacterActivity context, @NonNull final PendingActionAdapter adapter, @NonNull final IPendingUse action) {
            pending_action.setText(action.getText(context.getResources()));
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.dialog.pendingActions.remove(action);
                    action.undo(adapter.dialog);
                    adapter.notifyDataSetChanged();
                    adapter.dialog.updateViews();
                }
            });
        }
    }


    public static class PendingActionAdapter extends RecyclerView.Adapter<PendingActionViewHolder> {
        FeatureViewDialogFragment dialog;

        PendingActionAdapter(FeatureViewDialogFragment dialog) {
            this.dialog = dialog;
        }

        @NonNull
        @Override
        public PendingActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_feature_action_row, parent, false);
            return new PendingActionViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(@NonNull PendingActionViewHolder holder, int position) {
            IPendingUse action = dialog.pendingActions.get(position);
            holder.bind(dialog.getMainActivity(), this, action);
        }

        @Override
        public int getItemCount() {
            return dialog.pendingActions.size();
        }


    }

}

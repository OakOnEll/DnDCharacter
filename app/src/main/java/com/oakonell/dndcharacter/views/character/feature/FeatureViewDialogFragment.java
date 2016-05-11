package com.oakonell.dndcharacter.views.character.feature;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Rob on 5/4/2016.
 */
public class FeatureViewDialogFragment extends AbstractCharacterDialogFragment implements FeatureViewInterface {
    public static final String NAME = "name";
    private static final String PENDING_ACTIONS = "pending_actions";
    private static final java.lang.String REMAINING_USES = "remaining_uses";
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
    ArrayList<IPendingUse> pendingActions = new ArrayList<>();

    private ViewGroup pending_actions_group;
    private RecyclerView pending_actions_list;
    private PendingActionAdapter pendingActionsAdapter;
    private String savedRemainingUses;


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


        if (savedInstanceState != null) {
            pendingActions = savedInstanceState.getParcelableArrayList(PENDING_ACTIONS);
            savedRemainingUses = savedInstanceState.getString(REMAINING_USES);
        }

        return view;
    }

    @Override
    public void onCharacterLoaded(com.oakonell.dndcharacter.model.character.Character character) {
        super.onCharacterLoaded(character);
        viewHelper = new FeatureViewHelper(getMainActivity(), this);
        FeatureInfo info = character.getFeatureNamed(getNameArgument());
        viewHelper.bind(info);
        if (savedRemainingUses != null) {
            final int maxUses = info.evaluateMaxUses(getCharacter());
            uses_remaining.setText(savedRemainingUses);
            uses_remaining_readonly.setText(savedRemainingUses);
            try {
                int remainingUses = Integer.parseInt(savedRemainingUses);
                viewHelper.updateView(maxUses, remainingUses);
            } catch (NumberFormatException e) {
                // ignore
            }
            savedRemainingUses = null;
        }

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
            each.apply(getCharacter(), info);
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
        if (pendingActions == null || pendingActions.isEmpty()) {
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
    public void useAction(final CharacterActivity context, FeatureInfo info, IFeatureAction action, Map<String, String> values) {
        final int maxUses = info.evaluateMaxUses(context.getCharacter());
        if (maxUses > 0) {
            int value = Integer.parseInt(uses_remaining.getText().toString());
            value = value - action.getCost();
            uses_remaining.setText(NumberUtils.formatNumber(value));
            uses_remaining_readonly.setText(NumberUtils.formatNumber(value));
            viewHelper.updateView(maxUses, value);
        }

        PendingAction pendingAction = new PendingAction(action.getName(), values);
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
        PendingFeatureUse pendingUse = new PendingFeatureUse(value);
        pendingActions.add(pendingUse);
        pendingActionsAdapter.notifyDataSetChanged();
        updateViews();
    }


    @Override
    public int getUsesRemaining(CharacterActivity context, FeatureInfo info) {
        try {
            int remaining = Integer.parseInt(uses_remaining.getText().toString());
            return remaining;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getSpellSlotsAvailable(Character.SpellLevelInfo levelInfo) {
        //  adjust this for pending spell slot uses
        int used = 0;
        for (IPendingUse each : pendingActions) {
            if (each instanceof PendingSpellSlotUse) {
                final PendingSpellSlotUse pendingSpellSlotUse = (PendingSpellSlotUse) each;
                if (pendingSpellSlotUse.spellLevel == levelInfo.getLevel()) used++;
            }
        }
        return levelInfo.getSlotsAvailable() - used;
    }

    @Override
    public void useSpellSlot(CharacterActivity context, Character.SpellLevelInfo levelInfo) {
        PendingSpellSlotUse pendingUse = new PendingSpellSlotUse(levelInfo.getLevel());
        pendingActions.add(pendingUse);
        pendingActionsAdapter.notifyDataSetChanged();

        FeatureInfo info = getCharacter().getFeatureNamed(getNameArgument());
        viewHelper.bindSpellSlotViews(info);

        updateViews();

    }

    public static class PendingSpellSlotUse implements IPendingUse {
        // Method to recreate a HpRow from a Parcel
        @NonNull
        public static Creator<PendingSpellSlotUse> CREATOR = new Creator<PendingSpellSlotUse>() {

            @NonNull
            @Override
            public PendingSpellSlotUse createFromParcel(@NonNull Parcel source) {
                return new PendingSpellSlotUse(source);
            }

            @NonNull
            @Override
            public PendingSpellSlotUse[] newArray(int size) {
                return new PendingSpellSlotUse[size];
            }

        };

        private final int spellLevel;

        PendingSpellSlotUse(int spellLevel) {
            this.spellLevel = spellLevel;
        }

        @Override
        public void apply(Character character, FeatureInfo info) {
            character.useSpellSlot(spellLevel);
        }

        @Override
        public String getText(Resources resources, FeatureInfo info) {
            return "Use spell slot level " + spellLevel;
        }

        @Override
        public void undo(FeatureViewDialogFragment dialog, FeatureInfo info) {
            dialog.viewHelper.bindSpellSlotViews(info);
        }


        // Parcelable methods
        public PendingSpellSlotUse(Parcel source) {
            this.spellLevel = source.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(spellLevel);
        }
    }

    public static class PendingAction implements IPendingUse {
        // Method to recreate a HpRow from a Parcel
        @NonNull
        public static Creator<PendingAction> CREATOR = new Creator<PendingAction>() {

            @NonNull
            @Override
            public PendingAction createFromParcel(@NonNull Parcel source) {
                return new PendingAction(source);
            }

            @NonNull
            @Override
            public PendingAction[] newArray(int size) {
                return new PendingAction[size];
            }

        };

        private final String actionName;
        private final Map<String, String> values;

        PendingAction(String actionName, Map<String, String> values) {
            this.actionName = actionName;
            this.values = values;
        }

        @Override
        public void apply(Character character, FeatureInfo info) {
            IFeatureAction action = info.getActionNamed(actionName);
            character.useFeatureAction(info, action, values);
        }

        @Override
        public String getText(Resources resources, FeatureInfo info) {
            if (values != null && values.size() > 0) {
                StringBuilder builder = new StringBuilder("(");
                for (Iterator<Map.Entry<String, String>> iter = values.entrySet().iterator(); iter.hasNext(); ) {
                    Map.Entry<String, String> each = iter.next();
                    String value = each.getValue();
                    String name = each.getKey();
                    builder.append(name);
                    builder.append("='");
                    builder.append(value);
                    builder.append("'");
                    if (iter.hasNext()) {
                        builder.append("'");
                    }
                }
                builder.append(")");
                return "Use " + actionName + " with " + builder.toString();
            }
            return "Use " + actionName;
        }

        @Override
        public void undo(FeatureViewDialogFragment dialog, FeatureInfo info) {
            final int maxUses = info.evaluateMaxUses(dialog.getCharacter());
            int value = 0;
            if (maxUses > 0) {
                IFeatureAction action = info.getActionNamed(actionName);
                value = Integer.parseInt(dialog.uses_remaining.getText().toString());
                value = Math.min(value + action.getCost(), maxUses);
                dialog.uses_remaining.setText(NumberUtils.formatNumber(value));
                dialog.uses_remaining_readonly.setText(NumberUtils.formatNumber(value));
            }
            dialog.viewHelper.updateView(maxUses, value);
        }


        // Parcelable methods
        public PendingAction(Parcel source) {
            this.actionName = source.readString();
            int size = source.readInt();
            values = new HashMap<>();
            for (int i = 0; i < size; i++) {
                String key = source.readString();
                String value = source.readString();
                values.put(key, value);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(values.size());
            for (Map.Entry<String, String> entry : values.entrySet()) {
                out.writeString(entry.getKey());
                out.writeString(entry.getValue());
            }
        }
    }

    public static class PendingFeatureUse implements IPendingUse {
        // Method to recreate a HpRow from a Parcel
        @NonNull
        public static Creator<PendingFeatureUse> CREATOR = new Creator<PendingFeatureUse>() {

            @NonNull
            @Override
            public PendingFeatureUse createFromParcel(@NonNull Parcel source) {
                return new PendingFeatureUse(source);
            }

            @NonNull
            @Override
            public PendingFeatureUse[] newArray(int size) {
                return new PendingFeatureUse[size];
            }

        };

        private final int value;

        public PendingFeatureUse(int value) {
            this.value = value;
        }

        @Override
        public void apply(Character character, FeatureInfo info) {
            character.useFeature(info, value);
        }

        @Override
        public String getText(Resources resources, FeatureInfo info) {
            if (info.getUseType() == UseType.POOL) {
                return "Use " + info.getName() + " for " + value;
            }
            return "Use " + info.getName();
        }

        @Override
        public void undo(FeatureViewDialogFragment dialog, FeatureInfo info) {
            int remaining = Integer.parseInt(dialog.uses_remaining.getText().toString());
            final int maxUses = info.evaluateMaxUses(dialog.getCharacter());

            remaining = Math.min(remaining + value, maxUses);
            dialog.uses_remaining.setText(NumberUtils.formatNumber(remaining));
            dialog.uses_remaining_readonly.setText(NumberUtils.formatNumber(remaining));

            dialog.viewHelper.updateView(maxUses, remaining);
        }

        // Parcelable methods
        public PendingFeatureUse(Parcel source) {
            this.value = source.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeInt(value);
        }

    }


    interface IPendingUse extends Parcelable {
        void apply(Character character, FeatureInfo info);

        String getText(Resources resources, FeatureInfo info);

        void undo(FeatureViewDialogFragment dialog, FeatureInfo info);
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
            final FeatureInfo info = adapter.dialog.getCharacter().getFeatureNamed(adapter.dialog.getNameArgument());
            pending_action.setText(action.getText(context.getResources(), info));
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.dialog.pendingActions.remove(action);
                    action.undo(adapter.dialog, info);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(PENDING_ACTIONS, pendingActions);
        outState.putString(REMAINING_USES, uses_remaining.getText().toString());
    }
}

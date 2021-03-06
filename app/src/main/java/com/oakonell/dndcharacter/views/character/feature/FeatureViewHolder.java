package com.oakonell.dndcharacter.views.character.feature;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.model.character.FeatureInfo;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.components.IFeatureAction;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.character.CharacterActivity;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Rob on 1/4/2016.
 */
public class FeatureViewHolder extends BindableComponentViewHolder<FeatureInfo, CharacterActivity, RecyclerView.Adapter<?>> implements FeatureViewInterface {
    boolean isForCompanion;
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
    private final Button add_use;
    private final Button subtract_use;
    @NonNull
    public final EditText uses_remaining;
    private final TextView uses_remaining_readonly;
    private final TextView uses_total;

    @NonNull
    public final TextView refreshes_label;

    private final Button use_spell_slot;
    private final Spinner spell_slot_level;
    private final ViewGroup spell_slot_use_group;

    private Set<FeatureContextArgument> filter;

    @NonNull
    public final RecyclerView action_list;
    private ArrayList<String> spellLevels = new ArrayList<>();
    private RecyclerView.Adapter<?> adapter;


    public FeatureViewHolder(@NonNull View view, boolean isForCompanion) {
        super(view);
        this.isForCompanion = isForCompanion;
        name = (TextView) view.findViewById(R.id.name);
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
    }

    public FeatureViewHolder(@NonNull View view, Set<FeatureContextArgument> filter, boolean isForCompanion) {
        this(view, isForCompanion);
        this.filter = filter;
    }

    @Override
    public void bind(@NonNull final CharacterActivity context, final RecyclerView.Adapter<?> adapter, @NonNull final FeatureInfo info) {
        final int position = getAdapterPosition();
        this.adapter = adapter;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeatureViewDialogFragment dialog = FeatureViewDialogFragment.createDialog(info, isForCompanion);
                dialog.show(context.getSupportFragmentManager(), "feature");
            }
        });


        name.setText(info.getName());
        FeatureViewHelper viewHelper = new FeatureViewHelper(context, this, isForCompanion);
        viewHelper.bind(info);

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
    public TextView getUsesTotalTextView() {
        return uses_total;
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
    public TextView getRefreshesLabelTextView() {
        return refreshes_label;
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
        return filter;
    }

    @Override
    public int getUsesRemaining(CharacterActivity context, FeatureInfo info) {
        return getCharacter(context).getUsesRemaining(info);
    }

    @Override
    public int getSpellSlotsAvailable(Character.SpellLevelInfo each) {
        return each.getSlotsAvailable();
    }

    @Override
    public void useAction(final CharacterActivity context, FeatureInfo info, IFeatureAction action, Map<String, String> values) {
        CharacterEffect effect = getCharacter(context).useFeatureAction(info, action, values);
        if (effect != null) {
            // TODO open effect dialog, if one was added
            ViewEffectDialogFragment dialog = ViewEffectDialogFragment.createDialog(effect);
            dialog.show(context.getSupportFragmentManager(), "effect_dialog");
        }
        shortDescription.postDelayed(new Runnable() {
            @Override
            public void run() {
                context.updateViews();
                context.saveCharacter();
            }
        }, 10);

    }

    @Override
    public void useFeature(final CharacterActivity context, FeatureInfo info, int value) {
        getCharacter(context).useFeature(info, value);
        // TODO open effect dialog, if one was added
        shortDescription.postDelayed(new Runnable() {
            @Override
            public void run() {
                context.updateViews();
                context.saveCharacter();
            }
        }, 10);
    }

    private AbstractCharacter getCharacter(CharacterActivity context) {
        Character character = context.getCharacter();
        if (isForCompanion) {
            return character.getDisplayedCompanion();
        }
        return character;
    }


    @Override
    public void useSpellSlot(final CharacterActivity context, Character.SpellLevelInfo levelInfo) {
        ((Character) getCharacter(context)).useSpellSlot(levelInfo.getLevel());
        shortDescription.postDelayed(new Runnable() {
            @Override
            public void run() {
                context.updateViews();
                context.saveCharacter();
            }
        }, 10);
    }

    public boolean isReadOnly() {
        return true;
    }


}




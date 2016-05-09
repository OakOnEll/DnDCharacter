package com.oakonell.dndcharacter.views.character.feature;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.*;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

/**
 * Created by Rob on 5/4/2016.
 */
public class FeatureViewDialogFragment extends AbstractCharacterDialogFragment implements FeatureViewInterface {
    public static final String NAME = "name";
    private TextView source;
    private ViewGroup limited_uses_group;
    private RecyclerView action_list;
    private TextView uses_label;
    private TextView uses_remaining;
    private TextView shortDescription;
    private TextView refreshes_label;
    private ViewGroup spell_slot_use_group;
    private Spinner spell_slot_level;
    private Button use_spell_slot;

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
        uses_remaining = (TextView) view.findViewById(R.id.remaining);
        shortDescription = (TextView) view.findViewById(R.id.description);
        refreshes_label = (TextView) view.findViewById(R.id.refreshes_label);

        spell_slot_use_group = (ViewGroup) view.findViewById(R.id.spell_slot_use_group);
        spell_slot_level = (Spinner) view.findViewById(R.id.spell_slot_level);
        use_spell_slot = (Button) view.findViewById(R.id.use_spell_slot);

        return view;
    }

    @Override
    public void onCharacterLoaded(com.oakonell.dndcharacter.model.character.Character character) {
        super.onCharacterLoaded(character);
        FeatureViewHelper helper = new FeatureViewHelper(getMainActivity(), this);
        FeatureInfo info = character.getFeatureNamed(getNameArgument());
        helper.bind(info);

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
}

package com.oakonell.dndcharacter.views.character.feature;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.*;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

import java.util.ArrayList;

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
            getCharacter().setUsesRemaining(info, value);
        }
        return isValid;
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

}

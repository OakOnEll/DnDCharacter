package com.oakonell.dndcharacter.views.character.feature;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rob on 1/4/2016.
 */
public class ViewEffectDialogFragment extends AbstractCharacterDialogFragment {
    public static final String NAME = "name";
    private static final String ID = "id";
    private TextView description;
    private Button endEffectButton;

    @NonNull
    public static ViewEffectDialogFragment createDialog(@NonNull CharacterEffect effect) {
        ViewEffectDialogFragment frag = new ViewEffectDialogFragment();
        String name = effect.getName();
        String id = effect.getId();
        Bundle args = new Bundle();
        args.putString(NAME, name);
        args.putString(ID, id);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.effect_dialog, container);

        description = (TextView) view.findViewById(R.id.description);
        endEffectButton = (Button) view.findViewById(R.id.end_effect);


        return view;
    }

    @Nullable
    private String getNameArgument() {
        return getArguments().getString(NAME);
    }

    @Nullable
    private String getIdArgument() {
        return getArguments().getString(ID);
    }


    @Override
    public void onCharacterLoaded(@NonNull final Character character) {
        super.onCharacterLoaded(character);
        updateViews(character);
    }

    protected void updateViews(@NonNull final Character character) {
        final CharacterEffect effect = character.getEffectNamed(getNameArgument());
        if (effect == null) {
            description.setText(R.string.effect_not_active);
            endEffectButton.setOnClickListener(null);
            endEffectButton.setEnabled(false);
        } else {
            description.setText(effect.getDescription());
            endEffectButton.setEnabled(true);
            endEffectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    character.removeEffect(effect);
                    getMainActivity().updateViews();
                    getMainActivity().saveCharacter();
                    dismiss();
                }
            });
        }
    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        super.onCharacterChanged(character);
        updateViews(character);
    }

    @Nullable
    @Override
    protected String getTitle() {
        return getNameArgument();
    }

    @NonNull
    @Override
    protected Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.EFFECT, getIdArgument()));
        return filter;
    }


}
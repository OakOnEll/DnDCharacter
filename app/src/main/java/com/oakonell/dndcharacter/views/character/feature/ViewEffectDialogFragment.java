package com.oakonell.dndcharacter.views.character.feature;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

/**
 * Created by Rob on 1/4/2016.
 */
public class ViewEffectDialogFragment extends AbstractCharacterDialogFragment {
    private TextView description;
    private Button endEffectButton;

    public static ViewEffectDialogFragment createDialog(CharacterEffect effect) {
        ViewEffectDialogFragment frag = new ViewEffectDialogFragment();
        String name = effect.getName();
        Bundle args = new Bundle();
        args.putString("name", name);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.effect_dialog, container);

        description = (TextView) view.findViewById(R.id.description);
        endEffectButton = (Button) view.findViewById(R.id.end_effect);


        return view;
    }

    private String getNameArgument() {
        return getArguments().getString("name");
    }


    @Override
    public void onCharacterLoaded(final Character character) {
        super.onCharacterLoaded(character);
        updateViews(character);
    }

    protected void updateViews(final Character character) {
        final CharacterEffect effect = character.getEffectNamed(getNameArgument());
        if (effect == null) {
            description.setText("Not active");
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
    public void onCharacterChanged(Character character) {
        super.onCharacterChanged(character);
        updateViews(character);
    }

    @Override
    protected String getTitle() {
        return getNameArgument();
    }
}
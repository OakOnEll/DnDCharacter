package com.oakonell.dndcharacter.views.character;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.*;

/**
 * Created by Rob on 2/7/2016.
 */
public class NameDialog extends AbstractCharacterDialogFragment {

    private TextView name;

    @NonNull
    public static NameDialog create() {
        return new NameDialog();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.choose_name);
    }

    @Override
    protected View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.name_dialog, container);

        name = (TextView) view.findViewById(R.id.name);
        return view;
    }

    @Override
    public void onCharacterLoaded(com.oakonell.dndcharacter.model.character.Character character) {
        super.onCharacterLoaded(character);
        name.setText(character.getName());
        updateViews();
    }

//    @Override
//    public void onCharacterChanged(com.oakonell.dndcharacter.model.character.Character character) {
//        super.onCharacterChanged(character);
//        updateViews();
//    }

    protected void updateViews() {
    }

    @Override
    protected boolean onDone() {
        String newName = name.getText().toString();
        if (newName == null || newName.trim().length() == 0) {
            name.setError(getString(R.string.choose_name));
            Animation shake = AnimationUtils.loadAnimation(getMainActivity(), R.anim.shake);
            name.startAnimation(shake);
            return false;
        }
        getCharacter().setName(newName);

        return super.onDone();
    }
}

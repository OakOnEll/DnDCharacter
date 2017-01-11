package com.oakonell.dndcharacter.views.character.item;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.MoneyValue;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

/**
 * Created by Rob on 3/22/2016.
 */
public class AddGemDialog extends AbstractCharacterDialogFragment {
    private EditText name;
    private EditText value;
    private AddGemListener addGemListener;

    public void setAddGemListener(AddGemListener addGemListener) {
        this.addGemListener = addGemListener;
    }

    public AddGemListener getAddGemListener() {
        return addGemListener;
    }

    public interface AddGemListener {
        void addGem(Character.Gem gem);
    }

    @NonNull
    public static AddGemDialog createDialog(AddGemListener listener) {
        AddGemDialog dialog = new AddGemDialog();
        dialog.setAddGemListener(listener);
        return dialog;
    }


    @Nullable
    @Override
    protected String getTitle() {
        return getString(R.string.add_a_gem);
    }

    protected boolean preventAutoSoftKeyboard() {
        return false;
    }

    public View onCreateTheView(@NonNull LayoutInflater inflater, final ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_gem_dialog, container);

        name = (EditText) view.findViewById(R.id.name);
        value = (EditText) view.findViewById(R.id.value);

        value.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    pressDone();
                }
                return true;
            }
        });

        return view;
    }

    @Override
    protected boolean onDone() {
        final String nameString = name.getText().toString().trim();
        boolean isValid = true;
        if (nameString.length() == 0) {
            name.setError(getString(R.string.enter_a_name));
            Animation shake = AnimationUtils.loadAnimation(name.getContext(), R.anim.shake);
            name.startAnimation(shake);
            isValid = false;
        }
        final String valueString = value.getText().toString();
        MoneyValue moneyValue = null;
        if (valueString.length() == 0) {
            value.setError(getString(R.string.enter_a_value));
            Animation shake = AnimationUtils.loadAnimation(value.getContext(), R.anim.shake);
            value.startAnimation(shake);
            isValid = false;
        } else {
            try {
                moneyValue = MoneyValue.fromString(getActivity(), valueString);
            } catch (MoneyValue.InvalidMoneyFormatException e) {
                value.setError(e.getMessage());
                Animation shake = AnimationUtils.loadAnimation(value.getContext(), R.anim.shake);
                value.startAnimation(shake);
                isValid = false;
            }
        }
        if (isValid) {
            if (addGemListener != null) {
                addGemListener.addGem(new Character.Gem(nameString, moneyValue));
            }
        }
        return super.onDone() && isValid;
    }
}
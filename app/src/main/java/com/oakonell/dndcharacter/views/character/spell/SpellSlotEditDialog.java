package com.oakonell.dndcharacter.views.character.spell;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.*;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

import java.util.List;

/**
 * Created by Rob on 3/14/2016.
 */
public class SpellSlotEditDialog extends AbstractCharacterDialogFragment {

    private static final String SPELL_LEVEL = "spell_level";
    private int level;
    private EditText spell_slots;
    private TextView max_spell_slots;
    private Button increment;
    private Button decrement;

    public static SpellSlotEditDialog createDialog(int level) {
        SpellSlotEditDialog newMe = new SpellSlotEditDialog();
        Bundle args = new Bundle();
        args.putInt(SPELL_LEVEL, level);
        newMe.setArguments(args);

        return newMe;

    }

    @Nullable
    @Override
    protected String getTitle() {
        return getString(R.string.spell_slots_for_level, level);
    }

    @Nullable
    @Override
    protected View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.spell_slots_dialog, container);

        level = getArguments().getInt(SPELL_LEVEL, -1);
        if (level < 0) {
            throw new RuntimeException();
        }

        spell_slots = (EditText) rootView.findViewById(R.id.spell_slots);
        max_spell_slots = (TextView) rootView.findViewById(R.id.max_spell_slots);
        increment = (Button) rootView.findViewById(R.id.increment);
        decrement = (Button) rootView.findViewById(R.id.decrement);

        spell_slots.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int slots = getSpellSlots();
                int max = getMaxSpellSlots();
                if (slots > max || slots < 0) {
                    spell_slots.setError(getString(R.string.enter_spell_slots_between_0_n, max));
                    Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                    spell_slots.startAnimation(shake);

                }
            }
        });

        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int slots = getSpellSlots();
                slots--;
                spell_slots.setText(NumberUtils.formatNumber(slots));
                updateView();
            }
        });
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int slots = getSpellSlots();
                slots++;
                spell_slots.setText(NumberUtils.formatNumber(slots));
                updateView();
            }
        });
        return rootView;
    }

    private int getSpellSlots() {
        String slotString = spell_slots.getText().toString();
        if (slotString == null || slotString.trim().length() == 0) {
            return 0;
        }
        return Integer.parseInt(slotString);
    }

    private int getMaxSpellSlots() {
        String slotString = max_spell_slots.getText().toString();
        if (slotString == null || slotString.trim().length() == 0) {
            return 0;
        }
        return Integer.parseInt(slotString);
    }

    @Override
    public void onCharacterLoaded(@NonNull com.oakonell.dndcharacter.model.character.Character character) {
        final List<Character.SpellLevelInfo> spellInfos = getCharacter().getSpellInfos();
        if (level > spellInfos.size() + 1) {
            max_spell_slots.setText("0");
            spell_slots.setText("0");
            return;
        }
        final Character.SpellLevelInfo levelInfo = spellInfos.get(level);
        int maxSlots = levelInfo.getMaxSlots();
        max_spell_slots.setText(NumberUtils.formatNumber(maxSlots));
        // TODO allow saved state to override this..
        spell_slots.setText(NumberUtils.formatNumber(levelInfo.getSlotsAvailable()));
        updateView();
    }

    private void updateView() {
        int slots = getSpellSlots();
        int max = getMaxSpellSlots();

        increment.setEnabled(slots < max);
        decrement.setEnabled(slots > 0);
    }


    @Override
    protected boolean onDone() {
        int slots = getSpellSlots();
        int max = getMaxSpellSlots();
        if (slots > max || slots < 0) {
            spell_slots.setError(getString(R.string.enter_spell_slots_between_0_n, max));
            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            spell_slots.startAnimation(shake);
            return false;
        }
        getCharacter().setSpellSlots(level, max-slots);
        return super.onDone();
    }
}

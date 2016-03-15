package com.oakonell.dndcharacter.views.character.persona;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Alignment;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 3/15/2016.
 */
public class AlignmentDialogFragment extends AbstractCharacterDialogFragment {


    private Alignment selected;
    Map<Alignment, View> viewByAlignment = new HashMap<>();

    @NonNull
    public static AlignmentDialogFragment create() {
        return new AlignmentDialogFragment();
    }


    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alignment_dialog, container);

        viewByAlignment.put(Alignment.LAWFUL_GOOD, view.findViewById(R.id.lawful_good));
        viewByAlignment.put(Alignment.NEUTRAL_GOOD, view.findViewById(R.id.neutral_good));
        viewByAlignment.put(Alignment.CHAOTIC_GOOD, view.findViewById(R.id.chaotic_good));

        viewByAlignment.put(Alignment.LAWFUL_NEUTRAL, view.findViewById(R.id.lawful_neutral));
        viewByAlignment.put(Alignment.TRUE_NEUTRAL, view.findViewById(R.id.true_neutral));
        viewByAlignment.put(Alignment.CHATOIC_NEUTRAL, view.findViewById(R.id.chaotic_neutral));

        viewByAlignment.put(Alignment.LAWFUL_EVIL, view.findViewById(R.id.lawful_evil));
        viewByAlignment.put(Alignment.NEUTRAL_EVIL, view.findViewById(R.id.neutral_evil));
        viewByAlignment.put(Alignment.CHAOTIC_EVIL, view.findViewById(R.id.chaotic_evil));


        return view;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.alignment_title);
    }


    @Override
    public void onCharacterLoaded(com.oakonell.dndcharacter.model.character.Character character) {
        super.onCharacterLoaded(character);
        selected = character.getAlignment();
        if (selected != null) {
            final View view = viewByAlignment.get(selected);
            view.setBackgroundResource(R.drawable.ac_block);
        }
        for (Map.Entry<Alignment, View> each : viewByAlignment.entrySet()) {
            final Alignment alignment = each.getKey();
            final View view = each.getValue();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selected != null) {
                        final View currentSelectedView = viewByAlignment.get(selected);
                        if (currentSelectedView != null) {
                            currentSelectedView.setBackgroundResource(0);
                        }
                    }
                    if (selected == alignment) {
                        // allow deselect, no alignment
                        selected = null;
                    } else {
                        selected = alignment;
                        view.setBackgroundResource(R.drawable.ac_block);
                    }
                }
            });
        }
    }

    @Override
    protected boolean onDone() {
        getCharacter().setAlignment(selected);
        return super.onDone();
    }
}

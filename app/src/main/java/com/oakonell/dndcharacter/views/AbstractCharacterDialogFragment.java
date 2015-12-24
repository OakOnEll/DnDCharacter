package com.oakonell.dndcharacter.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.oakonell.dndcharacter.MainActivity;
import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 12/24/2015.
 */
public abstract class AbstractCharacterDialogFragment extends AppCompatDialogFragment implements OnCharacterLoaded {


    private Button done;

    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        View view = onCreateTheView(inflater, container, savedInstanceState);

        done = (Button) view.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDone();
                dismiss();
                getMainActivity().updateViews();
            }
        });

        getMainActivity().addCharacterLoadLister(this);

        return view;
    }

    protected void enableDone(boolean enabled) {
        done.setEnabled(enabled);
    }


    protected abstract View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);


    protected final MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    protected final com.oakonell.dndcharacter.model.Character getCharacter() {
        return getMainActivity().character;
    }

    public void onCharacterLoaded(com.oakonell.dndcharacter.model.Character character) {

    }

    protected void onDone() {

    }

}

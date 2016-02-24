package com.oakonell.dndcharacter.views.character;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.DndCharacterApp;
import com.oakonell.dndcharacter.R;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by Rob on 2/22/2016.
 */
public abstract class AbstractDialogFragment extends AppCompatDialogFragment {
    private Button done;

    protected void extraDoneActions() {

    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = onCreateTheView(inflater, container, savedInstanceState);
        done = (Button) view.findViewById(R.id.done);
        if (done != null) {
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean valid = onDone();
                    if (!valid) return;

                    extraDoneActions();

                    dismiss();
                }
            });
        }
        Button cancel = (Button) view.findViewById(R.id.cancel);
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                }
            });
        }
        setCancelable(isCancelable(cancel != null));


        if (getDialog() != null) {
            getDialog().setTitle(getTitle());
        }
        return view;
    }

    @Nullable
    protected abstract String getTitle();

    protected void hideKeyboardFrom(@NonNull TextView v) {
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected boolean isCancelable(boolean hasCancelButton) {
        return !hasCancelButton;
    }

    protected void enableDone(boolean enabled) {
        if (done != null) {
            done.setEnabled(enabled);
        }
    }


    @Nullable
    protected abstract View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected boolean onDone() {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = DndCharacterApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}

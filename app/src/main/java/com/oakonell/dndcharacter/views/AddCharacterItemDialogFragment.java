package com.oakonell.dndcharacter.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 12/24/2015.
 */
public class AddCharacterItemDialogFragment extends AbstractCharacterDialogFragment{
    @Nullable
    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_search_dialog, container);

        return super.onCreateView(inflater, container, savedInstanceState);
    }



}

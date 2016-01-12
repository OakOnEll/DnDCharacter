package com.oakonell.dndcharacter.views.character.item;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

/**
 * Created by Rob on 12/24/2015.
 */
public class AddCharacterItemDialogFragment extends AbstractCharacterDialogFragment {
    @Nullable
    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_search_dialog, container);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected String getTitle() {
        return "Add an Item";
    }

}

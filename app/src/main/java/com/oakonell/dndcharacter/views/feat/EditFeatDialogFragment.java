package com.oakonell.dndcharacter.views.feat;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.feat.Feat;
import com.oakonell.dndcharacter.views.AbstractEditComponentDialogFragment;

/**
 * Created by Rob on 11/19/2015.
 */
public class EditFeatDialogFragment extends AbstractEditComponentDialogFragment<Feat> {

    @NonNull
    public static EditFeatDialogFragment create(Feat feat) {
        EditFeatDialogFragment frag = new EditFeatDialogFragment();
        frag.setModel(feat);
        return frag;
    }

}

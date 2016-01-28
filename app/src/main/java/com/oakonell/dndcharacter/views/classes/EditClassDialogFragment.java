package com.oakonell.dndcharacter.views.classes;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.classes.AClass;
import com.oakonell.dndcharacter.views.AbstractEditComponentDialogFragment;

/**
 * Created by Rob on 11/19/2015.
 */
public class EditClassDialogFragment extends AbstractEditComponentDialogFragment<AClass> {

    @NonNull
    public static EditClassDialogFragment create(AClass aClass) {
        EditClassDialogFragment frag = new EditClassDialogFragment();
        frag.setModel(aClass);
        return frag;
    }
}

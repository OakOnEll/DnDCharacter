package com.oakonell.dndcharacter.views.effect;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.effect.Effect;
import com.oakonell.dndcharacter.views.AbstractEditComponentDialogFragment;

/**
 * Created by Rob on 11/19/2015.
 */
public class EditEffectDialogFragment extends AbstractEditComponentDialogFragment<Effect> {

    @NonNull
    public static EditEffectDialogFragment create(Effect effect) {
        EditEffectDialogFragment frag = new EditEffectDialogFragment();
        frag.setModel(effect);
        return frag;
    }

}

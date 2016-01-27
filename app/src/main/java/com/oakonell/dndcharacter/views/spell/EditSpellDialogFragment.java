package com.oakonell.dndcharacter.views.spell;

import com.oakonell.dndcharacter.model.spell.Spell;
import com.oakonell.dndcharacter.views.AbstractEditComponentDialogFragment;

/**
 * Created by Rob on 1/13/2016.
 */
public class EditSpellDialogFragment extends AbstractEditComponentDialogFragment<Spell> {

    public static EditSpellDialogFragment create(Spell spell) {
        EditSpellDialogFragment frag = new EditSpellDialogFragment();
        frag.setModel(spell);
        return frag;
    }

}

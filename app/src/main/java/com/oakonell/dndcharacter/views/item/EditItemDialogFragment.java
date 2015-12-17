package com.oakonell.dndcharacter.views.item;

import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.views.AbstractEditComponentDialogFragment;

/**
 * Created by Rob on 11/10/2015.
 */
public class EditItemDialogFragment extends AbstractEditComponentDialogFragment<ItemRow> {

    public static EditItemDialogFragment create(ItemRow itemRow) {
        EditItemDialogFragment frag = new EditItemDialogFragment();
        frag.setModel(itemRow);
        return frag;
    }

}

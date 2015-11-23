package com.oakonell.dndcharacter.views.item;

import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.AbstractEditComponentDialogFragment;

import org.w3c.dom.Document;

/**
 * Created by Rob on 11/10/2015.
 */
public class EditItemDialogFragment extends AbstractEditComponentDialogFragment<ItemRow> {

    public static EditItemDialogFragment create(ItemRow itemRow) {
        EditItemDialogFragment frag = new EditItemDialogFragment();
        frag.setModel(itemRow);
        return frag;
    }

    @Override
    protected void updateModel(ItemRow model, Document doc) {
        String category = XmlUtils.getElementText(doc.getDocumentElement(), "category");
        model.setCategory(category);
    }
}

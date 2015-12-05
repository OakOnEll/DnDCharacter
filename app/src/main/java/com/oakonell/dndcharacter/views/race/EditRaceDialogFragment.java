package com.oakonell.dndcharacter.views.race;

import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.AbstractEditComponentDialogFragment;

import org.w3c.dom.Document;

/**
 * Created by Rob on 11/19/2015.
 */
public class EditRaceDialogFragment extends AbstractEditComponentDialogFragment<Race> {

    public static EditRaceDialogFragment create(Race race) {
        EditRaceDialogFragment frag = new EditRaceDialogFragment();
        frag.setModel(race);
        return frag;
    }

    @Override
    protected void updateModel(Race model, Document doc) {
        if (doc==null || doc.getDocumentElement()==null) {
            model.setParentRace("XmlParseError");
        } else {
            model.setParentRace(XmlUtils.getElementText(doc.getDocumentElement(), "parent"));
        }

    }
}

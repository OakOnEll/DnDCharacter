package com.oakonell.dndcharacter.model.race;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.AbstractChoiceComponentVisitor;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 11/9/2015.
 */
public abstract class AbstractRaceVisitor extends AbstractChoiceComponentVisitor {

    public void visit(@NonNull Race race) {
        visit(XmlUtils.getDocument(race.getXml()).getDocumentElement());
    }

    @Override
    protected void visit(@NonNull Element element) {
        boolean wasVisited = true;
        String name = element.getTagName();
        if (name.equals("race")) {
            visitRace(element);
        } else {
            wasVisited = false;
        }
        if (!wasVisited) {
            super.visit(element);
        }
    }


    protected void visitRace(@NonNull Element element) {
        visitChildren(element);
    }

}

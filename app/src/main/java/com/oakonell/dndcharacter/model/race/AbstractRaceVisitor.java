package com.oakonell.dndcharacter.model.race;

import com.oakonell.dndcharacter.model.AbstractComponentVisitor;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 11/9/2015.
 */
public abstract class AbstractRaceVisitor extends AbstractComponentVisitor {

    public void visit(Race race) {
        visit(XmlUtils.getDocument(race.getXml()).getDocumentElement());
    }

    protected boolean subVisit(Element element, String name) {
        if (name.equals("race")) {
            visitRace(element);
        } else {
            return false;
        }
        return true;
    }


    protected void visitRace(Element element) {
        visitChildren(element);
    }

}

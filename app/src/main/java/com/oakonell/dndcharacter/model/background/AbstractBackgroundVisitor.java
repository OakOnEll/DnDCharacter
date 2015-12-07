package com.oakonell.dndcharacter.model.background;

import com.oakonell.dndcharacter.model.AbstractComponentVisitor;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 11/9/2015.
 */
public abstract class AbstractBackgroundVisitor extends AbstractComponentVisitor {

    public void visit(Background background) {
        visit(XmlUtils.getDocument(background.getXml()).getDocumentElement());
    }

    protected boolean subVisit(Element element, String name) {
        if (name.equals("background")) {
            visitBackground(element);
        } else if (name.equals("specialties")) {
            visitSpecialties(element);
        } else if (name.equals("specialty")) {
            visitSpecialty(element);
        } else if (name.equals("personalityTraits")) {
            visitTraits(element);
        } else if (name.equals("trait")) {
            visitTrait(element);
        } else if (name.equals("ideals")) {
            visitIdeals(element);
        } else if (name.equals("ideal")) {
            visitIdeal(element);
        } else if (name.equals("bonds")) {
            visitBonds(element);
        } else if (name.equals("bond")) {
            visitBond(element);
        } else if (name.equals("flaws")) {
            visitFlaws(element);
        } else if (name.equals("flaw")) {
            visitFlaw(element);
        } else {
            return false;
        }
        return true;
    }

    protected void visitFlaw(Element element) {
        visitChildren(element);
    }

    protected void visitFlaws(Element element) {
        visitChildren(element);
    }

    protected void visitBond(Element element) {
        visitChildren(element);
    }

    protected void visitBonds(Element element) {
        visitChildren(element);
    }

    protected void visitIdeal(Element element) {
        visitChildren(element);
    }

    protected void visitIdeals(Element element) {
        visitChildren(element);
    }

    protected void visitTrait(Element element) {
        visitChildren(element);
    }

    protected void visitTraits(Element element) {
        visitChildren(element);
    }

    protected void visitSpecialty(Element element) {
        visitChildren(element);
    }

    protected void visitSpecialties(Element element) {
        VisitState oldState = state;
        state = VisitState.SPECIALTIES;
        visitGroup(element);
        state = oldState;
    }

    protected void visitBackground(Element element) {
        visitChildren(element);
    }

}

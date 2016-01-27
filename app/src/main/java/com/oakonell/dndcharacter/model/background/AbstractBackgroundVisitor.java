package com.oakonell.dndcharacter.model.background;

import com.oakonell.dndcharacter.model.AbstractChoiceComponentVisitor;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 11/9/2015.
 */
public abstract class AbstractBackgroundVisitor extends AbstractChoiceComponentVisitor {

    public void visit(Background background) {
        visit(XmlUtils.getDocument(background.getXml()).getDocumentElement());
    }

    @Override
    protected void visit(Element element) {
        boolean wasVisited = true;
        String name = element.getTagName();
        switch (name) {
            case "background":
                visitBackground(element);
                break;
            case "specialties":
                visitSpecialties(element);
                break;
            case "specialty":
                visitSpecialty(element);
                break;
            case "personalityTraits":
                visitTraits(element);
                break;
            case "trait":
                visitTrait(element);
                break;
            case "ideals":
                visitIdeals(element);
                break;
            case "ideal":
                visitIdeal(element);
                break;
            case "bonds":
                visitBonds(element);
                break;
            case "bond":
                visitBond(element);
                break;
            case "flaws":
                visitFlaws(element);
                break;
            case "flaw":
                visitFlaw(element);
                break;
            default:
                wasVisited = false;
        }
        if (!wasVisited) {
            super.visit(element);
        }
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

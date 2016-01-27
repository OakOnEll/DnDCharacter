package com.oakonell.dndcharacter.model;

import org.w3c.dom.Element;

/**
 * Created by Rob on 1/14/2016.
 */
public class AbstractChoiceComponentVisitor extends AbstractComponentVisitor {


    @Override
    protected void visit(Element element) {
        boolean wasVisited = true;
        String name = element.getTagName();
        switch (name) {
            case "choose":
                visitChoose(element);
                break;
            case "or":
                visitOr(element);
                break;
            default:
                wasVisited = false;
        }
        if (!wasVisited) {
            super.visit(element);
        }
    }

    protected void visitOr(Element element) {
        visitChildren(element);
    }

    protected void visitChoose(Element element) {
        visitChildren(element);
    }

}

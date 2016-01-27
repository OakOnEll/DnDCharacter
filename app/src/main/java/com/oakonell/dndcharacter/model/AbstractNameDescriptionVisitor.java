package com.oakonell.dndcharacter.model;

import org.w3c.dom.Element;

/**
 * Created by Rob on 1/14/2016.
 */
public class AbstractNameDescriptionVisitor extends AbstractXmlVisitor {

    protected void visit(Element element) {
        String name = element.getTagName();
        boolean found = true;
        switch (name) {
            case "name":
                visitName(element);
                break;
            case "shortDescription":
                visitShortDescription(element);
                break;
            case "description":
                visitDescription(element);
                break;
            default:
                found = false;
        }
        if (!found) {
            elementNotHandled(element);
        }
    }

    protected void elementNotHandled(Element element) {

    }

    protected void visitShortDescription(Element element) {
        visitChildren(element);
    }

    protected void visitDescription(Element element) {
        visitChildren(element);
    }

    protected void visitName(Element element) {
        visitChildren(element);
    }
}

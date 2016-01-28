package com.oakonell.dndcharacter.model;

import android.support.annotation.NonNull;

import org.w3c.dom.Element;

/**
 * Created by Rob on 1/14/2016.
 */
public class AbstractNameDescriptionVisitor extends AbstractXmlVisitor {

    protected void visit(@NonNull Element element) {
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

    @SuppressWarnings("EmptyMethod")
    protected void elementNotHandled(Element element) {

    }

    protected void visitShortDescription(@NonNull Element element) {
        visitChildren(element);
    }

    protected void visitDescription(@NonNull Element element) {
        visitChildren(element);
    }

    protected void visitName(@NonNull Element element) {
        visitChildren(element);
    }
}

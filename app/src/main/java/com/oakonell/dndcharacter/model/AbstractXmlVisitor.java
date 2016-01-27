package com.oakonell.dndcharacter.model;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by Rob on 1/14/2016.
 */
public class AbstractXmlVisitor {

    protected void visit(Element element) {
        // by default, do nothing
    }

    protected void visitGroup(Element element) {
        visitChildren(element);
    }

    protected void visitSimpleItem(Element element) {
        visitChildren(element);
    }

    protected void visitChildren(Element element) {
        Node child = element.getFirstChild();

        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                visit((Element) child);
            }
            child = child.getNextSibling();
        }
    }
}

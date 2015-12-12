package com.oakonell.dndcharacter.model.classes;

import com.oakonell.dndcharacter.model.AbstractComponentVisitor;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 11/9/2015.
 */
public abstract class AbstractClassVisitor extends AbstractComponentVisitor {

    public void visit(AClass aClass) {
        visit(XmlUtils.getDocument(aClass.getXml()).getDocumentElement());
    }

    protected boolean subVisit(Element element, String name) {
        if (name.equals("class")) {
            visitClass(element);
        } else if (name.equals("level")) {
            visitLevel(element);
        } else {
            return false;
        }
        return true;
    }

    protected void visitClass(Element element) {
        visitChildren(element);
    }

    protected void visitLevel(Element element) {
        visitChildren(element);
    }

}

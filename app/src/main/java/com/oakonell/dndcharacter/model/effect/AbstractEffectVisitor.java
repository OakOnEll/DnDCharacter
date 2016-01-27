package com.oakonell.dndcharacter.model.effect;

import com.oakonell.dndcharacter.model.AbstractComponentVisitor;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 11/9/2015.
 */
public abstract class AbstractEffectVisitor extends AbstractComponentVisitor {

    public void visit(Effect effect) {
        visit(XmlUtils.getDocument(effect.getXml()).getDocumentElement());
    }

    @Override
    protected void visit(Element element) {
        boolean wasVisited = true;
        String name = element.getTagName();
        switch (name) {
            case "effect":
                visitEffect(element);
                break;
            default:
                wasVisited = false;
        }
        if (!wasVisited) {
            super.visit(element);
        }
    }

    protected void visitEffect(Element element) {
        visitChildren(element);
    }

}

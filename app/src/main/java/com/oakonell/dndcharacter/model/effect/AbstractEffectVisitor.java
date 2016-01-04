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

    protected boolean subVisit(Element element, String name) {
        if (name.equals("effect")) {
            visitEffect(element);
        } else {
            return false;
        }
        return true;
    }


    protected void visitEffect(Element element) {
        visitChildren(element);
    }

}

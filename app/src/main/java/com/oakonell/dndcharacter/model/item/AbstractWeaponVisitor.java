package com.oakonell.dndcharacter.model.item;

import com.oakonell.dndcharacter.model.AbstractComponentVisitor;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 12/29/2015.
 */
public class AbstractWeaponVisitor extends AbstractComponentVisitor {

    public void visit(ItemRow anItem) {
        visit(XmlUtils.getDocument(anItem.getXml()).getDocumentElement());
    }

    protected boolean subVisit(Element element, String name) {
        if (name.equals("damage")) {
            visitDamage(element);
        } else if (name.equals("properties")) {
            visitProperties(element);
        } else if (name.equals("range")) {
            visitRange(element);
        } else {
            return false;
        }
        return true;
    }

    protected void visitDamage(Element element) {
        visitSimpleItem(element);
    }

    protected void visitProperties(Element element) {
        visitSimpleItem(element);
    }

    protected void visitRange(Element element) {
        visitSimpleItem(element);
    }


}

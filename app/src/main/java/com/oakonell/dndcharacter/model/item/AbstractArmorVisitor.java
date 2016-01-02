package com.oakonell.dndcharacter.model.item;

import com.oakonell.dndcharacter.model.AbstractComponentVisitor;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 12/29/2015.
 */
public class AbstractArmorVisitor extends AbstractComponentVisitor {
    /*
        <disadvantage>stealth</disadvantage>
        <strengthMin>13</strengthMin>

     */
    public void visit(ItemRow anItem) {
        visit(XmlUtils.getDocument(anItem.getXml()).getDocumentElement());
    }

    protected boolean subVisit(Element element, String name) {
        switch (name) {
            case "strengthMin":
                visitStrengthMin(element);
                break;
            case "disadvantage":
                visitDisadvantage(element);
                break;
            default:
                return false;
        }
        return true;
    }


    protected void visitStrengthMin(Element element) {
        visitSimpleItem(element);
    }

    protected void visitDisadvantage(Element element) {
        visitSimpleItem(element);
    }

}

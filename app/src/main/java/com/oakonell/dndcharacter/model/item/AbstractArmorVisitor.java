package com.oakonell.dndcharacter.model.item;

import android.support.annotation.NonNull;

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
    public void visit(@NonNull ItemRow anItem) {
        visit(XmlUtils.getDocument(anItem.getXml()).getDocumentElement());
    }

    @Override
    protected void visit(@NonNull Element element) {
        boolean wasVisited = true;
        String name = element.getTagName();
        switch (name) {
            case "strengthMin":
                visitStrengthMin(element);
                break;
            case "disadvantage":
                visitDisadvantage(element);
                break;
            case "category":
                visitCategory(element);
                break;
            default:
                wasVisited = false;
        }
        if (!wasVisited) {
            super.visit(element);
        }
    }

    protected void visitCategory(Element element) {
        visitSimpleItem(element);
    }

    protected void visitStrengthMin(Element element) {
        visitSimpleItem(element);
    }

    protected void visitDisadvantage(Element element) {
        visitSimpleItem(element);
    }

}

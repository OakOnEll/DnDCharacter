package com.oakonell.dndcharacter.model.item;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.AbstractComponentVisitor;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 12/29/2015.
 */
public class AbstractWeaponVisitor extends AbstractComponentVisitor {

    public void visit(@NonNull ItemRow anItem) {
        visit(XmlUtils.getDocument(anItem.getXml()).getDocumentElement());
    }

    @Override
    protected void visit(@NonNull Element element) {
        boolean wasVisited = true;
        String name = element.getTagName();
        switch (name) {
            case "damage":
                visitDamage(element);
                break;
            case "versatileDamage":
                visitVersatileDamage(element);
                break;
            case "properties":
                visitProperties(element);
                break;
            case "category":
                visitCategory(element);
                break;
            case "range":
                visitRange(element);
                break;
            case "ranged":
                visitRanged(element);
            case "ammunition":
                visitAmmunition(element);
                break;
            default:
                wasVisited = false;
        }
        if (!wasVisited) {
            super.visit(element);
        }
    }

    protected void visitDamage(Element element) {
        visitSimpleItem(element);
    }

    protected void visitVersatileDamage(Element element) {
        visitSimpleItem(element);
    }

    protected void visitProperties(Element element) {
        visitSimpleItem(element);
    }

    protected void visitRange(Element element) {
        visitSimpleItem(element);
    }

    protected void visitAmmunition(Element element) {
        visitSimpleItem(element);
    }

    protected void visitRanged(Element element) {
        visitSimpleItem(element);
    }

    protected void visitCategory(Element element) {
        visitSimpleItem(element);
    }


}

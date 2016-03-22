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
                break;
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

    protected void visitDamage(@NonNull Element element) {
        visitSimpleItem(element);
    }

    protected void visitVersatileDamage(@NonNull Element element) {
        visitSimpleItem(element);
    }

    protected void visitProperties(@NonNull Element element) {
        visitSimpleItem(element);
    }

    protected void visitRange(@NonNull Element element) {
        visitSimpleItem(element);
    }

    protected void visitAmmunition(@NonNull Element element) {
        visitSimpleItem(element);
    }

    protected void visitRanged(@NonNull Element element) {
        visitSimpleItem(element);
    }

    protected void visitCategory(@NonNull Element element) {
        visitSimpleItem(element);
    }


}

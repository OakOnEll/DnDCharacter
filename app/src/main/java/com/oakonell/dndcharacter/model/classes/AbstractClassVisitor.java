package com.oakonell.dndcharacter.model.classes;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.AbstractChoiceComponentVisitor;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 11/9/2015.
 */
public abstract class AbstractClassVisitor extends AbstractChoiceComponentVisitor {

    public void visit(@NonNull AClass aClass) {
        visit(XmlUtils.getDocument(aClass.getXml()).getDocumentElement());
    }

    @Override
    protected void visit(@NonNull Element element) {
        boolean wasVisited = true;
        String name = element.getTagName();
        switch (name) {
            case "subclass":
                visitSubClass(element);
                break;
            case "class":
                visitClass(element);
                break;
            case "spellCastingStat":
                visitSpellCastingStat(element);
                break;
            case "multiclassCasterFactor":
                visitMulticlassCasterFactor(element);
                break;
            case "preparedSpellsFormula":
                visitPreparedSpells(element);
                break;
            case "level":
                visitLevel(element);
                break;
            case "cantrips":
                visitCantrips(element);
                break;
            case "spells":
                visitSpells(element);
                break;
            default:
                wasVisited = false;
        }
        if (!wasVisited) {
            super.visit(element);
        }
    }

    /**
     * <spells>
     * <known>4</known>
     * <slots>
     * <level value="1">4</level>
     * <level value="2">2</level>
     * </slots>
     * </spells>
     *
     * @param element
     */
    protected void visitSpells(Element element) {
        visitGroup(element);
    }

    /**
     * <cantrips>
     * <known>4</known>
     * </cantrips>
     *
     * @param element
     */
    protected void visitCantrips(Element element) {
        visitGroup(element);
    }

    protected void visitMulticlassCasterFactor(Element element) {
        visitSimpleItem(element);
    }

    protected void visitSpellCastingStat(Element element) {
        visitSimpleItem(element);
    }

    protected void visitPreparedSpells(Element element) {
        visitSimpleItem(element);
    }

    protected void visitSubClass(Element element) {
        visitChildren(element);
    }

    protected void visitClass(Element element) {
        visitChildren(element);
    }

    protected void visitLevel(Element element) {
        visitChildren(element);
    }

}

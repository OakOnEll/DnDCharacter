package com.oakonell.dndcharacter.model.spell;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.AbstractNameDescriptionVisitor;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 1/13/2016.
 */
public class AbstractSpellVisitor extends AbstractNameDescriptionVisitor {

    public void visit(@NonNull Spell spell) {
        visit(XmlUtils.getDocument(spell.getXml()).getDocumentElement());
    }

    @Override
    protected void visit(@NonNull Element element) {
        String name = element.getTagName();
        boolean wasVisited = true;
        switch (name) {
            case "spell":
                visitSpell(element);
                break;
            default:
                wasVisited = false;
                break;
        }
        if (!wasVisited) {
            super.visit(element);
        }
    }

    protected void visitSpell(@NonNull Element element) {
        visitGroup(element);
    }


}

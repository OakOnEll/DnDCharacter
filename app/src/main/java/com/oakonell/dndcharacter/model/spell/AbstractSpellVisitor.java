package com.oakonell.dndcharacter.model.spell;

import com.oakonell.dndcharacter.model.AbstractNameDescriptionVisitor;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 1/13/2016.
 */
public class AbstractSpellVisitor extends AbstractNameDescriptionVisitor {

    public void visit(Spell spell) {
        visit(XmlUtils.getDocument(spell.getXml()).getDocumentElement());
    }

    @Override
    protected void visit(Element element) {
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

    protected void visitSpell(Element element) {
        visitGroup(element);
    }


}

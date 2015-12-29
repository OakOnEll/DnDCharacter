package com.oakonell.dndcharacter.model;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by Rob on 11/18/2015.
 */
public class AbstractComponentVisitor {
    protected VisitState state;

    protected void visit(Element element) {
        String name = element.getTagName();
        if (name.equals("choose")) {
            visitChoose(element);
        } else if (name.equals("or")) {
            visitOr(element);
        } else if (name.equals("skills")) {
            visitSkills(element);
        } else if (name.equals("savingThrows")) {
            visitSavingThrows(element);
        } else if (name.equals("tools")) {
            visitTools(element);
        } else if (name.equals("armor")) {
            visitArmor(element);
        } else if (name.equals("weapons")) {
            visitWeapons(element);
        } else if (name.equals("tool")) {
            visitTool(element);
        } else if (name.equals("equipment")) {
            visitEquipment(element);
        } else if (name.equals("proficiency")) {
            visitProficiency(element);
        } else if (name.equals("languages")) {
            visitLanguages(element);
        } else if (name.equals("language")) {
            visitLanguage(element);
        } else if (name.equals("feature")) {
            visitFeature(element);
        } else if (name.equals("item")) {
            visitItem(element);
        } else if (name.equals("money")) {
            visitMoney(element);
        } else if (name.equals("name")) {
            visitName(element);
        } else if (name.equals("shortDescription")) {
            visitShortDescription(element);
        } else if (name.equals("description")) {
            visitDescription(element);
        } else if (name.equals("stat")) {
            visitStat(element);
        } else if (name.equals("increase")) {
            visitIncrease(element);
        } else {
            boolean wasVisited = subVisit(element, name);
        }

    }

    protected void visitIncrease(Element element) {
        visitSimpleItem(element);
    }

    protected void visitStat(Element element) {
        VisitState oldState = state;
        state = VisitState.STATS;
        visitGroup(element);
        state = oldState;
    }

    protected boolean subVisit(Element element, String name) {
        return false;
    }

    protected void visitLanguage(Element element) {
        visitSimpleItem(element);
    }

    protected void visitEquipment(Element element) {
        VisitState oldState = state;
        state = VisitState.EQUIPMENT;
        visitGroup(element);
        state = oldState;
    }

    protected void visitShortDescription(Element element) {
        visitChildren(element);
    }

    protected void visitDescription(Element element) {
        visitChildren(element);
    }

    protected void visitName(Element element) {
        visitChildren(element);
    }

    protected void visitProficiency(Element element) {
        visitSimpleItem(element);
    }

    protected void visitMoney(Element element) {
        visitSimpleItem(element);
    }

    protected void visitTool(Element element) {
        visitSimpleItem(element);
    }

    protected void visitTools(Element element) {
        VisitState oldState = state;
        state = VisitState.TOOLS;
        visitGroup(element);
        state = oldState;
    }


    protected void visitArmor(Element element) {
        VisitState oldState = state;
        state = VisitState.ARMOR;
        visitGroup(element);
        state = oldState;
    }

    protected void visitWeapons(Element element) {
        VisitState oldState = state;
        state = VisitState.WEAPONS;
        visitGroup(element);
        state = oldState;
    }

    protected void visitItem(Element element) {
        visitSimpleItem(element);
    }


    protected void visitOr(Element element) {
        visitChildren(element);
    }

    protected void visitChoose(Element element) {
        visitChildren(element);
    }

    protected void visitFeature(Element element) {
        VisitState oldState = state;
        state = VisitState.FEATURE;
        visitChildren(element);
        state = oldState;
    }

    protected void visitLanguages(Element element) {
        VisitState oldState = state;
        state = VisitState.LANGUAGES;
        visitGroup(element);
        state = oldState;
    }

    protected void visitSkills(Element element) {
        VisitState oldState = state;
        state = VisitState.SKILLS;
        visitGroup(element);
        state = oldState;
    }

    protected void visitSavingThrows(Element element) {
        VisitState oldState = state;
        state = VisitState.SAVING_THROWS;
        visitGroup(element);
        state = oldState;
    }

    protected void visitGroup(Element element) {
        visitChildren(element);
    }

    protected void visitSimpleItem(Element element) {
        visitChildren(element);
    }

    protected void visitChildren(Element element) {
        Node child = element.getFirstChild();

        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                visit((Element) child);
            }
            child = child.getNextSibling();
        }
    }

    public enum VisitState {
        SAVING_THROWS, SKILLS, TOOLS, LANGUAGES, FEATURE, STATS, WEAPONS, ARMOR, EQUIPMENT, SPECIALTIES
    }
}

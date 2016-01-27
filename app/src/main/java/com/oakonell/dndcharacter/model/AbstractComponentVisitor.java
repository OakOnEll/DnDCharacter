package com.oakonell.dndcharacter.model;

import org.w3c.dom.Element;

/**
 * Created by Rob on 11/18/2015.
 */
public class AbstractComponentVisitor extends AbstractNameDescriptionVisitor {
    protected VisitState state;

    protected void visit(Element element) {
        String name = element.getTagName();
        boolean wasVisited = true;
        switch (name) {
            case "charLevel":
                visitCharacterLevel(element);
                break;
            case "cantrip":
                visitCantrip(element);
                break;
            case "skills":
                visitSkills(element);
                break;
            case "savingThrows":
                visitSavingThrows(element);
                break;
            case "tools":
                visitTools(element);
                break;
            case "armor":
                visitArmor(element);
                break;
            case "weapons":
                visitWeapons(element);
                break;
            case "tool":
                visitTool(element);
                break;
            case "equipment":
                visitEquipment(element);
                break;
            case "proficiency":
                visitProficiency(element);
                break;
            case "languages":
                visitLanguages(element);
                break;
            case "language":
                visitLanguage(element);
                break;
            case "feature":
                visitFeature(element);
                break;
            case "item":
                visitItem(element);
                break;
            case "money":
                visitMoney(element);
                break;
            case "stat":
                visitStat(element);
                break;
            case "increase":
                visitIncrease(element);
                break;
            default:
                wasVisited = false;
                break;
        }
        if (!wasVisited) {
            super.visit(element);
        }
    }

    protected void visitCharacterLevel(Element element) {
        visitGroup(element);
    }

    protected void visitCantrip(Element element) {
        visitSimpleItem(element);
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


    public enum VisitState {
        SAVING_THROWS, SKILLS, TOOLS, LANGUAGES, FEATURE, STATS, WEAPONS, ARMOR, EQUIPMENT, SPECIALTIES
    }
}

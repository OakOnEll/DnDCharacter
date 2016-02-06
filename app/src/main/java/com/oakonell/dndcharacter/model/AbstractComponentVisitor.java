package com.oakonell.dndcharacter.model;

import android.support.annotation.NonNull;

import org.w3c.dom.Element;

/**
 * Created by Rob on 11/18/2015.
 */
public class AbstractComponentVisitor extends AbstractNameDescriptionVisitor {
    protected VisitState state;

    protected void visit(@NonNull Element element) {
        String name = element.getTagName();
        boolean wasVisited = true;
        switch (name) {
            case "ref":
                visitReference(element);
                break;
            case "speed":
                visitSpeed(element);
                break;
            case "initiative":
                visitInitiative(element);
                break;
            case "cantrips":
                visitCantrips(element);
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
            case "feats":
                visitFeats(element);
                break;
            case "feat":
                visitFeat(element);
                break;
            default:
                wasVisited = false;
                break;
        }
        if (!wasVisited) {
            super.visit(element);
        }
    }

    protected void visitReference(@NonNull Element element) {
        visitSimpleItem(element);
    }

    protected void visitSpeed(@NonNull Element element) {
        visitSimpleItem(element);
    }

    protected void visitInitiative(@NonNull Element element) {
        visitSimpleItem(element);
    }

    protected void visitCantrips(@NonNull Element element) {
        visitGroup(element);
    }

    protected void visitCantrip(@NonNull Element element) {
        visitSimpleItem(element);
    }


    protected void visitIncrease(@NonNull Element element) {
        visitSimpleItem(element);
    }

    protected void visitStat(@NonNull Element element) {
        VisitState oldState = state;
        state = VisitState.STATS;
        visitGroup(element);
        state = oldState;
    }

    protected void visitLanguage(@NonNull Element element) {
        visitSimpleItem(element);
    }

    protected void visitEquipment(@NonNull Element element) {
        VisitState oldState = state;
        state = VisitState.EQUIPMENT;
        visitGroup(element);
        state = oldState;
    }

    protected void visitShortDescription(@NonNull Element element) {
        visitChildren(element);
    }

    protected void visitDescription(@NonNull Element element) {
        visitChildren(element);
    }

    protected void visitName(@NonNull Element element) {
        visitChildren(element);
    }

    protected void visitProficiency(@NonNull Element element) {
        visitSimpleItem(element);
    }

    protected void visitMoney(@NonNull Element element) {
        visitSimpleItem(element);
    }

    protected void visitTool(@NonNull Element element) {
        visitSimpleItem(element);
    }

    protected void visitTools(@NonNull Element element) {
        VisitState oldState = state;
        state = VisitState.TOOLS;
        visitGroup(element);
        state = oldState;
    }


    protected void visitArmor(@NonNull Element element) {
        VisitState oldState = state;
        state = VisitState.ARMOR;
        visitGroup(element);
        state = oldState;
    }

    protected void visitWeapons(@NonNull Element element) {
        VisitState oldState = state;
        state = VisitState.WEAPONS;
        visitGroup(element);
        state = oldState;
    }

    protected void visitItem(@NonNull Element element) {
        visitSimpleItem(element);
    }

    protected void visitFeature(@NonNull Element element) {
        VisitState oldState = state;
        state = VisitState.FEATURE;
        visitChildren(element);
        state = oldState;
    }

    protected void visitFeats(@NonNull Element element) {
        VisitState oldState = state;
        state = VisitState.FEAT;
        visitChildren(element);
        state = oldState;
    }

    protected void visitFeat(@NonNull Element element) {
        visitChildren(element);
    }

    protected void visitLanguages(@NonNull Element element) {
        VisitState oldState = state;
        state = VisitState.LANGUAGES;
        visitGroup(element);
        state = oldState;
    }

    protected void visitSkills(@NonNull Element element) {
        VisitState oldState = state;
        state = VisitState.SKILLS;
        visitGroup(element);
        state = oldState;
    }

    protected void visitSavingThrows(@NonNull Element element) {
        VisitState oldState = state;
        state = VisitState.SAVING_THROWS;
        visitGroup(element);
        state = oldState;
    }


    public enum VisitState {
        SAVING_THROWS, SKILLS, TOOLS, LANGUAGES, FEATURE, STATS, WEAPONS, ARMOR, EQUIPMENT, FEAT, SPECIALTIES
    }
}

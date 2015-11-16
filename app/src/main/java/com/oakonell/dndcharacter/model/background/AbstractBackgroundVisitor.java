package com.oakonell.dndcharacter.model.background;

import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by Rob on 11/9/2015.
 */
public abstract class AbstractBackgroundVisitor {
    public enum BackgroundState {
        SKILLS, TOOLS, LANGUAGES, FEATURE, SPECIALTIES
    }

    protected BackgroundState state;

    public void visit(Background background) {
        visit(XmlUtils.getDocument(background.getXml()).getDocumentElement());
    }

    void visit(Element element) {
        String name = element.getTagName();
        if (name.equals("choose")) {
            visitChoose(element);
        } else if (name.equals("or")) {
            visitOr(element);
        } else if (name.equals("background")) {
            visitBackground(element);
        } else if (name.equals("skills")) {
            visitSkills(element);
        } else if (name.equals("tools")) {
            visitTools(element);
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
        } else if (name.equals("specialties")) {
            visitSpecialties(element);
        } else if (name.equals("specialty")) {
            visitSpecialty(element);
        } else if (name.equals("name")) {
            visitName(element);
        } else if (name.equals("shortDescription")) {
            visitShortDescription(element);
        } else if (name.equals("description")) {
            visitDescription(element);
        } else if (name.equals("personalityTraits")) {
            visitTraits(element);
        } else if (name.equals("trait")) {
            visitTrait(element);
        } else if (name.equals("ideals")) {
            visitIdeals(element);
        } else if (name.equals("ideal")) {
            visitIdeal(element);
        } else if (name.equals("bonds")) {
            visitBonds(element);
        } else if (name.equals("bond")) {
            visitBond(element);
        } else if (name.equals("flaws")) {
            visitFlaws(element);
        } else if (name.equals("flaw")) {
            visitFlaw(element);
        }

    }

    protected void visitFlaw(Element element) {
        visitChildren(element);
    }

    protected void visitFlaws(Element element) {
        visitChildren(element);
    }

    protected void visitBond(Element element) {
        visitChildren(element);
    }

    protected void visitBonds(Element element) {
        visitChildren(element);
    }

    protected void visitIdeal(Element element) {
        visitChildren(element);
    }

    protected void visitIdeals(Element element) {
        visitChildren(element);
    }

    protected void visitTrait(Element element) {
        visitChildren(element);
    }

    protected void visitTraits(Element element) {
        visitChildren(element);
    }

    protected void visitLanguage(Element element) {
        visitSimpleItem(element);
    }

    protected void visitEquipment(Element element) {
        visitGroup(element);
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

    protected void visitSpecialty(Element element) {
        visitChildren(element);
    }

    protected void visitSpecialties(Element element) {
        state = BackgroundState.SPECIALTIES;
        visitGroup(element);
    }

    protected void visitProficiency(Element element) {
        visitSimpleItem(element);
    }

    protected void visitMoney(Element element) {
        visitSimpleItem(element);
    }

    protected void visitTools(Element element) {
        state = BackgroundState.TOOLS;
        visitSimpleItem(element);
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
        state = BackgroundState.FEATURE;
        visitChildren(element);
    }

    protected void visitLanguages(Element element) {
        state = BackgroundState.LANGUAGES;
        visitGroup(element);
    }

    protected void visitSkills(Element element) {
        state = BackgroundState.SKILLS;
        visitGroup(element);
    }


    protected void visitBackground(Element element) {
        visitChildren(element);
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
}

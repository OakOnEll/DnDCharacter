package com.oakonell.dndcharacter.model;

import com.oakonell.dndcharacter.model.components.Feature;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by Rob on 11/18/2015.
 */
public class ApplyChangesToGenericComponent<C extends BaseCharacterComponent> extends AbstractComponentVisitor {
    private final C component;
    private final SavedChoices savedChoices;
    String currentChoiceName;

    public static <C extends BaseCharacterComponent> void applyToCharacter(Element element, SavedChoices savedChoices, C component) {
        ApplyChangesToGenericComponent<C> newMe = new ApplyChangesToGenericComponent<>(savedChoices, component);
        newMe.visitChildren(element);
    }

    public ApplyChangesToGenericComponent(SavedChoices savedChoices, C component) {
        this.component = component;
        this.savedChoices = savedChoices;
    }


    @Override
    protected void visitFeature(Element element) {
        Feature feature = new Feature();
        feature.setName(XmlUtils.getElementText(element, "name"));
        feature.setDescription(XmlUtils.getElementText(element, "shortDescription"));
        // TODO handle refreshes, and other data in XML
        component.addFeature(feature);
        super.visitFeature(element);
    }

    @Override
    protected void visitProficiency(Element element) {
        if (state == AbstractComponentVisitor.VisitState.SKILLS) {
            // TODO how to endode expert, or half prof- via attribute?
            String skillName = element.getTextContent();
            skillName = skillName.replaceAll(" ", "_");
            skillName = skillName.toUpperCase();
            SkillType type = SkillType.valueOf(SkillType.class, skillName);
            // TODO handle error
            component.addSkill(type, Proficient.PROFICIENT);
        }
        super.visitProficiency(element);
    }

    @Override
    protected void visitLanguage(Element element) {
        String language = element.getTextContent();
        component.getLanguages().add(language);
        super.visitLanguage(element);
    }

    @Override
    protected void visitChoose(Element element) {
        String oldChoiceName = currentChoiceName;

        currentChoiceName = element.getAttribute("name");

        List<Element> childOrElems = XmlUtils.getChildElements(element, "or");
        if (childOrElems.size() == 0) {
            // category, context sensitive choices ?
            categoryChoices();
        } else {
            super.visitChoose(element);
        }

        currentChoiceName = oldChoiceName;
    }

    private void categoryChoices() {
        List<String> selections = savedChoices.getChoicesFor(currentChoiceName);
        for (String selection : selections) {
            switch (state) {
                case LANGUAGES:
                    component.getLanguages().add(selection);
                    break;
            }
        }

    }

    @Override
    protected void visitOr(Element element) {
        String optionName = element.getAttribute("name");
        List<String> selections = savedChoices.getChoicesFor(currentChoiceName);
        if (selections.contains(optionName)) {
            super.visitOr(element);
        }
    }


}

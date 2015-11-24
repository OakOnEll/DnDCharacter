package com.oakonell.dndcharacter.model;

import com.oakonell.dndcharacter.model.components.Feature;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
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
        String skillName = element.getTextContent();
        String category = element.getAttribute("category");
        if (state == AbstractComponentVisitor.VisitState.SKILLS) {
            // TODO how to endode expert, or half prof- via attribute?
            skillName = skillName.replaceAll(" ", "_");
            skillName = skillName.toUpperCase();
            SkillType type = SkillType.valueOf(SkillType.class, skillName);
            // TODO handle error
            component.addSkill(type, Proficient.PROFICIENT);
        } else if (state == VisitState.TOOLS || state == VisitState.ARMOR || state == VisitState.WEAPONS) {
            ProficiencyType type;
            if (state == VisitState.TOOLS) {
                type = ProficiencyType.TOOL;
            } else if (state == VisitState.ARMOR) {
                type = ProficiencyType.ARMOR;
            } else if (state == VisitState.WEAPONS) {
                type = ProficiencyType.WEAPON;
            } else {
                throw new RuntimeException("Unexpected state " + state);
            }

            // TODO how to endode expert, or half prof- via attribute?
            // TODO handle error
            if (category != null && category.trim().length() > 0) {
                component.addToolCategoryProficiency(type, category, Proficient.PROFICIENT);
            } else {
                component.addToolProficiency(type, skillName, Proficient.PROFICIENT);
            }
        }
        super.visitProficiency(element);
    }

    @Override
    protected void visitIncrease(Element element) {
        String statName = element.getAttribute("name");
        String amountStr = element.getTextContent();

        statName = statName.replaceAll(" ", "_");
        statName = statName.toUpperCase();
        StatType type = StatType.valueOf(StatType.class, statName);

        int amount = Integer.parseInt(amountStr);

        component.addModifier(type, amount);
        super.visitIncrease(element);
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
                case TOOLS:
                    component.addToolProficiency(ProficiencyType.TOOL, selection, Proficient.PROFICIENT);
                    break;
                case ARMOR:
                    component.addToolProficiency(ProficiencyType.ARMOR, selection, Proficient.PROFICIENT);
                    break;
                case WEAPONS:
                    component.addToolCategoryProficiency(ProficiencyType.WEAPON, selection, Proficient.PROFICIENT);
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

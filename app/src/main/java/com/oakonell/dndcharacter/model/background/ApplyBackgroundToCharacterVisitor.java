package com.oakonell.dndcharacter.model.background;

import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterBackground;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class ApplyBackgroundToCharacterVisitor extends AbstractBackgroundVisitor {
    private final Map<String, String> customChoices;
    private final CharacterBackground charBackground;
    private final SavedChoices savedChoices;
    private String currentChoiceName;
    private int traitIndex;

    private ApplyBackgroundToCharacterVisitor(SavedChoices savedChoices, Map<String, String> customChoices, CharacterBackground charBackground) {
        this.charBackground = charBackground;
        this.savedChoices = savedChoices;
        this.customChoices = customChoices;
    }

    public static void applyToCharacter(Background background, SavedChoices savedChoices, Map<String, String> customChoices, Character character) {
        CharacterBackground charBackground = new CharacterBackground();
        charBackground.setSavedChoices(savedChoices);
        // apply common changes
        Element element = XmlUtils.getDocument(background.getXml()).getDocumentElement();

        ApplyChangesToGenericComponent.applyToCharacter(element, savedChoices, charBackground, character, true);

        ApplyBackgroundToCharacterVisitor newMe = new ApplyBackgroundToCharacterVisitor(savedChoices, customChoices, charBackground);
        newMe.visit(element);
        character.setBackground(charBackground);
    }

    private void applyTraits(String choiceName, Runnable superVisit, ApplyTrait applyTrait) {
        traitIndex = 1;
        String oldChoiceName = currentChoiceName;
        currentChoiceName = choiceName;

        superVisit.run();

        if (savedChoices.getChoicesFor(choiceName).contains("custom")) {
            String text = customChoices.get(choiceName);
            applyTrait.applyTrait(text);
        }

        currentChoiceName = oldChoiceName;
    }

    private void applyTrait(Element element, ApplyTrait applyTrait) {
        String optionName = traitIndex + "";
        if (savedChoices.getChoicesFor(currentChoiceName).contains(optionName)) {
            applyTrait.applyTrait(element.getTextContent());
        }
        traitIndex++;
    }


    @Override
    protected void visitBackground(Element element) {
        charBackground.setName(XmlUtils.getElementText(element, "name"));
        super.visitBackground(element);
    }

    @Override
    protected void visitSpecialties(final Element element) {
        String choiceName = "specialties";
        traitIndex = 1;
        String oldChoiceName = currentChoiceName;
        currentChoiceName = choiceName;

        String specialtyTitle = element.getAttribute("name");
        charBackground.setSpecialtyTitle(specialtyTitle);

        super.visitSpecialties(element);

        currentChoiceName = oldChoiceName;
    }

    @Override
    protected void visitSpecialty(final Element element) {
        applyTrait(element, new ApplyTrait() {
            @Override
            public void applyTrait(String value) {
                charBackground.setSpecialty(element.getTextContent());
            }
        });
    }

    @Override
    protected void visitTraits(final Element element) {
        String choiceName = "traits";
        Runnable superVisit = new Runnable() {
            @Override
            public void run() {
                ApplyBackgroundToCharacterVisitor.super.visitTraits(element);
            }
        };

        applyTraits(choiceName, superVisit, new ApplyTrait() {
            @Override
            public void applyTrait(String value) {
                charBackground.setPersonalityTrait(value);
            }
        });
    }

    @Override
    protected void visitBonds(final Element element) {
        String choiceName = "bonds";
        Runnable superVisit = new Runnable() {
            @Override
            public void run() {
                ApplyBackgroundToCharacterVisitor.super.visitBonds(element);
            }
        };

        applyTraits(choiceName, superVisit, new ApplyTrait() {
            @Override
            public void applyTrait(String value) {
                charBackground.setBond(value);
            }
        });
    }

    @Override
    protected void visitFlaws(final Element element) {
        String choiceName = "flaws";
        Runnable superVisit = new Runnable() {
            @Override
            public void run() {
                ApplyBackgroundToCharacterVisitor.super.visitFlaws(element);
            }
        };

        applyTraits(choiceName, superVisit, new ApplyTrait() {
            @Override
            public void applyTrait(String value) {
                charBackground.setFlaw(value);
            }
        });
    }

    @Override
    protected void visitIdeals(final Element element) {
        String choiceName = "ideals";
        Runnable superVisit = new Runnable() {
            @Override
            public void run() {
                ApplyBackgroundToCharacterVisitor.super.visitIdeals(element);
            }
        };

        applyTraits(choiceName, superVisit, new ApplyTrait() {
            @Override
            public void applyTrait(String value) {
                charBackground.setIdeal(value);
            }
        });

    }

    @Override
    protected void visitTrait(Element element) {
        applyTrait(element, new ApplyTrait() {
            @Override
            public void applyTrait(String value) {
                charBackground.setPersonalityTrait(value);
            }
        });
    }

    @Override
    protected void visitBond(Element element) {
        applyTrait(element, new ApplyTrait() {
            @Override
            public void applyTrait(String value) {
                charBackground.setBond(value);
            }
        });
    }

    @Override
    protected void visitFlaw(Element element) {
        applyTrait(element, new ApplyTrait() {
            @Override
            public void applyTrait(String value) {
                charBackground.setFlaw(value);
            }
        });
    }

    @Override
    protected void visitIdeal(Element element) {
        applyTrait(element, new ApplyTrait() {
            @Override
            public void applyTrait(String value) {
                charBackground.setIdeal(value);
            }
        });
    }

    public interface ApplyTrait {
        void applyTrait(String value);
    }
}

package com.oakonell.dndcharacter.model.background;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.oakonell.dndcharacter.background.BackgroundViewCreatorVisitor;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterBackground;
import com.oakonell.dndcharacter.model.Proficient;
import com.oakonell.dndcharacter.model.SkillType;
import com.oakonell.dndcharacter.model.components.Feature;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class ApplyBackgroundToCharacterVisitor extends AbstractBackgroundVisitor {
    View view;
    private final Map<String, BackgroundViewCreatorVisitor.ChooseMD> chooseMDs;
    private final Character character;
    CharacterBackground charBackground = new CharacterBackground();
    private final SavedChoices savedChoices = charBackground.getSavedChoices();

    BackgroundViewCreatorVisitor.ChooseMD currentChooseMD;
    int traitIndex;

    public ApplyBackgroundToCharacterVisitor(View view, Map<String, BackgroundViewCreatorVisitor.ChooseMD> chooseMDs, Character character) {
        this.chooseMDs = chooseMDs;
        this.character = character;
        this.view = view;
    }


    private void applyTraits(String choiceName, Runnable superVisit, ApplyTrait applyTrait) {
        traitIndex = 1;
        BackgroundViewCreatorVisitor.ChooseMD oldChooseMD = currentChooseMD;
        currentChooseMD = chooseMDs.get(choiceName);

        superVisit.run();


        BackgroundViewCreatorVisitor.CustomCheckOptionMD customMD = (BackgroundViewCreatorVisitor.CustomCheckOptionMD) currentChooseMD.findOrOptionNamed("custom");
        CheckBox checkView = (CheckBox) view.findViewById(customMD.uiId);
        if (checkView.isChecked()) {
            EditText customText = (EditText) view.findViewById(customMD.textUiId);
            String text = customText.getText().toString();
            applyTrait.applyTrait(text);
            List<String> selections = savedChoices.getChoicesFor(choiceName);
            selections.add("custom");
        }

        currentChooseMD = oldChooseMD;
    }

    private void applyTrait(Element element, ApplyTrait applyTrait) {
        String optionName = traitIndex + "";
        BackgroundViewCreatorVisitor.CheckOptionMD optionMD = currentChooseMD.findOrOptionNamed(optionName);
        if (optionMD == null) {
            Log.e(getClass().getName(), "No check/or optionMD found named " + optionName + " under choose '" + currentChooseMD.choiceName + "'");
            return;
        }

        CheckBox checkView = (CheckBox) view.findViewById(optionMD.uiId);
        if (checkView.isChecked()) {
            List<String> selections = savedChoices.getChoicesFor(currentChooseMD.choiceName);
            selections.add(optionName);
            applyTrait.applyTrait(element.getTextContent());
        }
        traitIndex++;
    }


    @Override
    protected void visitBackground(Element element) {
        charBackground.setName(XmlUtils.getElementText(element, "name"));
        super.visitBackground(element);
        character.setBackground(charBackground);
    }


    @Override
    protected void visitFeature(Element element) {
        Feature feature = new Feature();
        feature.setName(XmlUtils.getElementText(element, "name"));
        feature.setDescription(XmlUtils.getElementText(element, "shortDescription"));
        // TODO handle refreshes, and other data in XML
        charBackground.addFeature(feature);
        super.visitFeature(element);
    }

    @Override
    protected void visitProficiency(Element element) {
        if (state == BackgroundState.SKILLS) {
            // TODO how to endode expert, or half prof- via attribute?
            String skillName = element.getTextContent();
            skillName = skillName.replaceAll(" ", "_");
            skillName = skillName.toUpperCase();
            SkillType type = SkillType.valueOf(SkillType.class, skillName);
            // TODO handle error
            charBackground.addSkill(type, Proficient.PROFICIENT);
        }
        super.visitProficiency(element);
    }

    @Override
    protected void visitLanguage(Element element) {
        String language = element.getTextContent();
        charBackground.getLanguages().add(language);
        super.visitLanguage(element);
    }

    @Override
    protected void visitChoose(Element element) {
        BackgroundViewCreatorVisitor.ChooseMD oldChooseMD = currentChooseMD;

        String choiceName = element.getAttribute("name");
        currentChooseMD = chooseMDs.get(choiceName);
        if (currentChooseMD == null) {
            Log.e(ApplyBackgroundToCharacterVisitor.class.getName(), "There is no Choice MD for " + choiceName);
            return;
        }

        List<Element> childOrElems = XmlUtils.getChildElements(element, "or");
        if (childOrElems.size() == 0) {
            // category, context sensitive choices ?
            categoryChoices(currentChooseMD.maxChoices);
        } else {
            super.visitChoose(element);
        }

        currentChooseMD = oldChooseMD;
    }

    private void categoryChoices(int maxChoices) {
        List<BackgroundViewCreatorVisitor.OptionMD> optionMDs = currentChooseMD.options;
        for (BackgroundViewCreatorVisitor.OptionMD each : optionMDs) {
            Spinner dropdown = (Spinner) view.findViewById(each.uiId);
            String selection = (String) dropdown.getSelectedItem();

            switch (state) {
                case LANGUAGES:
                    charBackground.getLanguages().add(selection);
                    break;
            }


            List<String> selections = savedChoices.getChoicesFor(currentChooseMD.choiceName);
            selections.add(selection);
        }

    }

    @Override
    protected void visitOr(Element element) {
        String optionName = element.getAttribute("name");
        BackgroundViewCreatorVisitor.CheckOptionMD optionMD = currentChooseMD.findOrOptionNamed(optionName);
        if (optionMD == null) {
            Log.e(getClass().getName(), "No check/or optionMD found named " + optionName + " under choose '" + currentChooseMD.choiceName + "'");
            return;
        }
        CheckBox checkView = (CheckBox) view.findViewById(optionMD.uiId);
        if (checkView.isChecked()) {
            super.visitOr(element);
            List<String> selections = savedChoices.getChoicesFor(currentChooseMD.choiceName);
            selections.add(optionName);
        }
    }

    public interface ApplyTrait {
        void applyTrait(String value);
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
                character.setPersonalityTrait(value);
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
                character.setBonds(value);
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
                character.setFlaws(value);
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
                character.setIdeals(value);
            }
        });

    }

    @Override
    protected void visitTrait(Element element) {
        applyTrait(element, new ApplyTrait() {
            @Override
            public void applyTrait(String value) {
                character.setPersonalityTrait(value);
            }
        });
    }

    @Override
    protected void visitBond(Element element) {
        applyTrait(element, new ApplyTrait() {
            @Override
            public void applyTrait(String value) {
                character.setBonds(value);
            }
        });
    }

    @Override
    protected void visitFlaw(Element element) {
        applyTrait(element, new ApplyTrait() {
            @Override
            public void applyTrait(String value) {
                character.setFlaws(value);
            }
        });
    }

    @Override
    protected void visitIdeal(Element element) {
        applyTrait(element, new ApplyTrait() {
            @Override
            public void applyTrait(String value) {
                character.setIdeals(value);
            }
        });
    }
}

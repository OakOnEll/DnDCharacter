package com.oakonell.dndcharacter.background;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.background.AbstractBackgroundVisitor;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.model.background.SavedChoices;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class BackgroundViewCreatorVisitor extends AbstractBackgroundVisitor {

    private ViewGroup view;
    private ViewGroup parent;
    private Map<String, ChooseMD> choicesMD = new HashMap<>();
    SavedChoices choices;
    int uiIdCounter;
    int traitIndex;
    ChooseMD currentChooseMD;
    Character character;

    public static abstract class OptionMD {
        public int uiId;
    }

    public static class CustomCheckOptionMD extends CheckOptionMD {
        public int textUiId;
    }

    public static class CheckOptionMD extends OptionMD {
        public String name;
    }

    public static class DropdownOptionMD extends OptionMD {

    }

    public static class ChooseMD {
        public String choiceName;
        public int maxChoices;
        public List<OptionMD> options = new ArrayList<>();

        public CheckOptionMD findOrOptionNamed(String optionName) {
            for (OptionMD each : options) {
                if (!(each instanceof CheckOptionMD)) continue;
                CheckOptionMD checkMD = (CheckOptionMD) each;
                if (checkMD.name.equals(optionName)) return checkMD;
            }
            return null;
        }
    }


    private void createGroup(String title) {
        // why isn't the style being applied?
//        LinearLayout layout = new LinearLayout(parent.getContext(),null, R.style.component_group_style);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        parent.addView(layout);
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_component_group, null);
        parent.addView(layout);
        parent = layout;

        TextView titleView = new TextView(parent.getContext());
        titleView.setTextAppearance(parent.getContext(), android.support.v7.appcompat.R.style.TextAppearance_AppCompat_Large);
        parent.addView(titleView);
        titleView.setText(title);
    }

    public interface TraitRetriever {
        String getTrait();
    }

    private void createCharacterTraitSelection(String groupTitle, String choiceName, Runnable superVisit, TraitRetriever traitRetriever) {
        ViewGroup oldParent = parent;
        ChooseMD oldChooseMD = currentChooseMD;

        createGroup(groupTitle);

        traitIndex = 1;
        currentChooseMD = new ChooseMD();
        currentChooseMD.choiceName = choiceName;
        currentChooseMD.maxChoices = 1;
        choicesMD.put(currentChooseMD.choiceName, currentChooseMD);

        superVisit.run();

        LinearLayout customLayout = new LinearLayout(parent.getContext());
        customLayout.setOrientation(LinearLayout.HORIZONTAL);
        parent.addView(customLayout);
        parent = customLayout;

        final CheckBox customCheck = new CheckBox(parent.getContext());
        parent.addView(customCheck);
        customCheck.setId(++uiIdCounter);

        final EditText customText = new EditText(parent.getContext());
        customText.setHint("Custom");
        parent.addView(customText);
        customText.setId(++uiIdCounter);

        CustomCheckOptionMD optionMD = new CustomCheckOptionMD();
        currentChooseMD.options.add(optionMD);
        optionMD.name = "custom";
        optionMD.uiId = customCheck.getId();
        optionMD.textUiId = customText.getId();

        List<String> selections = choices.getChoicesFor(currentChooseMD.choiceName);
        if (selections.contains(optionMD.name)) {
            customText.setEnabled(true);
            customCheck.setChecked(true);
            customText.setText(traitRetriever.getTrait());
        } else {
            customText.setEnabled(false);
        }
        final ChooseMD myTraitChoices = currentChooseMD;
        customCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                customText.setEnabled(isChecked);
                configureTraitChecks(myTraitChoices, isChecked);
            }
        });
        configureTraitChecks(myTraitChoices, !selections.isEmpty());

        currentChooseMD = oldChooseMD;
        parent = oldParent;
    }

    private void createTraitOption(Element element) {
        CheckBox checkbox = new CheckBox(parent.getContext());
        checkbox.setText(element.getTextContent());
        parent.addView(checkbox);
        checkbox.setId(++uiIdCounter);

        // create the MD
        CheckOptionMD optionMD = new CheckOptionMD();
        currentChooseMD.options.add(optionMD);
        optionMD.name = "" + traitIndex;
        optionMD.uiId = checkbox.getId();

        // select the current state
        List<String> selections = choices.getChoicesFor(currentChooseMD.choiceName);
        checkbox.setChecked(selections.contains(optionMD.name));

        final ChooseMD myTraitChoices = currentChooseMD;
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configureTraitChecks(myTraitChoices, isChecked);
            }
        });

        traitIndex++;
    }

    private void configureTraitChecks(ChooseMD myTraitChoices, boolean isChecked) {
        for (OptionMD each : myTraitChoices.options) {
            // we know a trait only contains checkMDs
            //CheckOptionMD checkMD = (CheckOptionMD) each;
            CheckBox checkbox = (CheckBox) view.findViewById(each.uiId);
            if (checkbox == null) {
                Log.e(BackgroundViewCreatorVisitor.class.getName(), "Could not find check box with id " + each.uiId + " for choices " + myTraitChoices.choiceName);
            }
            if (isChecked) {
                checkbox.setEnabled(checkbox.isChecked());
            } else {
                checkbox.setEnabled(true);
            }
        }
    }

    public Map<String, ChooseMD> appendToLayout(Background background, ViewGroup parent, Character character, SavedChoices choices) {
        this.parent = parent;
        this.view = parent;
        this.choices = choices;
        this.character = character;
        visit(background);
        return choicesMD;
    }

    @Override
    protected void visitFeature(Element element) {
        ViewGroup oldParent = parent;
        String name = XmlUtils.getElementText(element, "name");
        String description = XmlUtils.getElementText(element, "shortDescription");

        createGroup("Feature: " + name);
        TextView featureText = new TextView(parent.getContext());
        parent.addView(featureText);
        featureText.setText(description);

        parent = oldParent;
    }

    @Override
    protected void visitSkills(Element element) {
        ViewGroup oldParent = parent;
        createGroup("Skill proficiencies");
        super.visitSkills(element);
        parent = oldParent;
    }

    @Override
    protected void visitLanguages(Element element) {
        ViewGroup oldParent = parent;
        createGroup("Languages");
        super.visitLanguages(element);

        parent = oldParent;
    }

    @Override
    protected void visitTools(Element element) {
        ViewGroup oldParent = parent;
        createGroup("Tool proficiencies");
        super.visitTools(element);
        parent = oldParent;
    }

    protected void visitSpecialties(Element element) {
        ViewGroup oldParent = parent;

        createGroup("Specialties");
        super.visitSpecialties(element);

        parent = oldParent;
    }

    @Override
    protected void visitEquipment(Element element) {
        ViewGroup oldParent = parent;
        createGroup("Equipment");
        super.visitEquipment(element);

        parent = oldParent;
    }


    @Override
    protected void visitSpecialty(Element element) {
        TextView textView = new TextView(parent.getContext());
        parent.addView(textView);
        textView.setText(element.getTextContent());

        super.visitSpecialty(element);
    }


    @Override
    protected void visitSimpleItem(Element element) {
        TextView text = new TextView(parent.getContext());
        parent.addView(text);
        text.setText(" *  " + element.getTextContent());
    }
/*
    @Override
    protected void visitMoney(Element element) {
        TextView text = new TextView(parent.getContext());
        parent.addView(text);
        text.setText(" *  " + element.getTextContent());

        super.visitItem(element);
    }

    @Override
    protected void visitItem(Element element) {
        TextView text = new TextView(parent.getContext());
        parent.addView(text);
        text.setText(" *  " + element.getTextContent());

        super.visitItem(element);
    }

    @Override
    protected void visitLanguage(Element element) {
        TextView text = new TextView(parent.getContext());
        parent.addView(text);
        text.setText(" *  " + element.getTextContent());

        super.visitItem(element);
    }

    @Override
    protected void visitProficiency(Element element) {
        TextView proficiencyText = new TextView(parent.getContext());
        parent.addView(proficiencyText);
        proficiencyText.setText(" *  " + element.getTextContent());

        super.visitProficiency(element);
    }
*/

    @Override
    protected void visitChoose(Element element) {
        ChooseMD oldChooseMD = currentChooseMD;
        ViewGroup oldParent = parent;

        int numChoices = 1;
        String choiceName = element.getAttribute("name");
        String numChoicesString = element.getAttribute("number");
        if (numChoicesString != null && numChoicesString.trim().length() > 0) {
            numChoices = Integer.parseInt(numChoicesString);
        }
        currentChooseMD = new ChooseMD();
        currentChooseMD.choiceName = choiceName;
        currentChooseMD.maxChoices = numChoices;
        choicesMD.put(choiceName, currentChooseMD);

        LinearLayout layout = new LinearLayout(parent.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        parent.addView(layout);
        parent = layout;

        List<Element> childOrElems = XmlUtils.getChildElements(element, "or");
        if (childOrElems.size() == 0) {
            // category, context sensitive choices ?
            categoryChoices(numChoices);
        } else {
            super.visitChoose(element);
        }

        currentChooseMD = oldChooseMD;
        parent = oldParent;
    }

    protected void categoryChoices(int numChoices) {
        if (state == BackgroundState.LANGUAGES) {

            List<String> selections = choices.getChoicesFor(currentChooseMD.choiceName);

            for (int i = 0; i < numChoices; i++) {
                // TODO get the list of languages...
                List<String> languages = new ArrayList<>();
                languages.add("Elvish");
                languages.add("Dwarvish");
                languages.add("Common");
                languages.add("Draconic");


                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(parent.getContext(),
                        android.R.layout.simple_spinner_item, languages);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                Spinner spinner = new Spinner(parent.getContext());
                spinner.setLayoutParams(layoutParams);
                spinner.setAdapter(dataAdapter);
                spinner.setId(++uiIdCounter);

                DropdownOptionMD optionMD = new DropdownOptionMD();
                currentChooseMD.options.add(optionMD);
                optionMD.uiId = spinner.getId();

                // display saved selection
                int selectedIndex = -1;
                if (selections.size() > i) {
                    String selected = selections.get(i);
                    selectedIndex = languages.indexOf(selected);
                }
                spinner.setSelection(selectedIndex);

                parent.addView(spinner);
            }
        }
    }

    @Override
    protected void visitOr(Element element) {
        ViewGroup oldParent = parent;

        LinearLayout layout = new LinearLayout(parent.getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        parent.addView(layout);
        parent = layout;

        String name = element.getAttribute("name");
        CheckBox checkbox = new CheckBox(parent.getContext());
        parent.addView(checkbox);

        // display saved selection state
        List<String> selections = choices.getChoicesFor(currentChooseMD.choiceName);
        checkbox.setChecked(selections.contains(name));
        checkbox.setId(++uiIdCounter);

        CheckOptionMD optionMD = new CheckOptionMD();
        currentChooseMD.options.add(optionMD);
        optionMD.name = name;
        optionMD.uiId = checkbox.getId();


        LinearLayout vertLayout = new LinearLayout(parent.getContext());
        vertLayout.setOrientation(LinearLayout.VERTICAL);
        parent.addView(vertLayout);
        parent = vertLayout;

        super.visitOr(element);

        parent = oldParent;
    }


    protected void visitTraits(final Element element) {

        String groupTitle = "Personality Traits";
        String choiceName = "traits";
        Runnable superVisit = new Runnable() {
            @Override
            public void run() {
                BackgroundViewCreatorVisitor.super.visitTraits(element);
            }
        };
        TraitRetriever traitRetriever = new TraitRetriever() {
            @Override
            public String getTrait() {
                return character.getPersonalityTrait();
            }
        };

        createCharacterTraitSelection(groupTitle, choiceName, superVisit, traitRetriever);
    }

    protected void visitBonds(final Element element) {
        String groupTitle = "Bonds";
        String choiceName = "bonds";
        Runnable superVisit = new Runnable() {
            @Override
            public void run() {
                BackgroundViewCreatorVisitor.super.visitBonds(element);
            }
        };
        TraitRetriever traitRetriever = new TraitRetriever() {
            @Override
            public String getTrait() {
                return character.getBonds();
            }
        };

        createCharacterTraitSelection(groupTitle, choiceName, superVisit, traitRetriever);
    }


    protected void visitFlaws(final Element element) {
        String groupTitle = "Flaws";
        String choiceName = "flaws";
        Runnable superVisit = new Runnable() {
            @Override
            public void run() {
                BackgroundViewCreatorVisitor.super.visitFlaws(element);
            }
        };
        TraitRetriever traitRetriever = new TraitRetriever() {
            @Override
            public String getTrait() {
                return character.getFlaws();
            }
        };

        createCharacterTraitSelection(groupTitle, choiceName, superVisit, traitRetriever);
    }

    protected void visitIdeals(final Element element) {
        String groupTitle = "Ideals";
        String choiceName = "ideals";
        Runnable superVisit = new Runnable() {
            @Override
            public void run() {
                BackgroundViewCreatorVisitor.super.visitIdeals(element);
            }
        };
        TraitRetriever traitRetriever = new TraitRetriever() {
            @Override
            public String getTrait() {
                return character.getIdeals();
            }
        };

        createCharacterTraitSelection(groupTitle, choiceName, superVisit, traitRetriever);
    }

    protected void visitFlaw(Element element) {
        createTraitOption(element);
    }

    protected void visitIdeal(Element element) {
        createTraitOption(element);
    }


    protected void visitBond(Element element) {
        createTraitOption(element);
    }

    protected void visitTrait(Element element) {
        createTraitOption(element);
    }

}

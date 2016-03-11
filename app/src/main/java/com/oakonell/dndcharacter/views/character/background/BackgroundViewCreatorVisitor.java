package com.oakonell.dndcharacter.views.character.background;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.model.background.AbstractBackgroundVisitor;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.views.character.md.CheckOptionMD;
import com.oakonell.dndcharacter.views.character.md.ChooseMD;
import com.oakonell.dndcharacter.views.character.md.ChooseMDTreeNode;
import com.oakonell.dndcharacter.views.character.md.CustomCheckOptionMD;
import com.oakonell.dndcharacter.views.character.md.MultipleChoicesMD;
import com.oakonell.dndcharacter.views.character.md.RootChoiceMDNode;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class BackgroundViewCreatorVisitor extends AbstractBackgroundVisitor {
    private SavedChoices choices;
    private int uiIdCounter;
    private int traitIndex;
    private ChooseMD<?> currentChooseMD;

    //private ViewGroup view;
    private ViewGroup parent;
    private ChooseMDTreeNode choicesMD = new RootChoiceMDNode();
    private List<String> traitNames;
    private Map<String, String> customChoices;


    @NonNull
    private TextView createGroup(String title) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_component_group, parent, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        parent.addView(layout);
        parent = layout;

        TextView titleView = new TextView(parent.getContext());
        titleView.setTextAppearance(parent.getContext(), android.support.v7.appcompat.R.style.TextAppearance_AppCompat_Large);
        parent.addView(titleView);
        titleView.setText(title);
        return titleView;
    }


    private void createCharacterTraitSelection(String groupTitle, String choiceName, @NonNull Runnable superVisit, boolean allowCustom) {
        if (!traitNames.contains(choiceName)) return;
        ViewGroup oldParent = parent;
        ChooseMD oldChooseMD = currentChooseMD;

        TextView titleText = createGroup(groupTitle);

        traitIndex = 1;
        final MultipleChoicesMD categoryChoicesMD = new MultipleChoicesMD(titleText, choiceName, 1, 0);
        currentChooseMD = categoryChoicesMD;
        choicesMD.addChildChoice(currentChooseMD);

        superVisit.run();

        List<String> selections = choices.getChoicesFor(categoryChoicesMD.getChoiceName());
        if (allowCustom) {
            LinearLayout customLayout = new LinearLayout(parent.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            customLayout.setLayoutParams(params);
            customLayout.setOrientation(LinearLayout.HORIZONTAL);
            parent.addView(customLayout);
            parent = customLayout;

            final CheckBox customCheck = new CheckBox(parent.getContext());
            parent.addView(customCheck);
            customCheck.setId(++uiIdCounter);

            final EditText customText = new EditText(parent.getContext());
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            customText.setLayoutParams(params);
            customText.setHint("Custom " + groupTitle);

            String customString = customChoices.get(categoryChoicesMD.getChoiceName());
            if (customString != null) {
                customText.setText(customString);
            }

            parent.addView(customText);
            customText.setId(++uiIdCounter);

            CustomCheckOptionMD optionMD = new CustomCheckOptionMD(categoryChoicesMD, customCheck, "custom", customText);
            categoryChoicesMD.addOption(optionMD);

            if (selections.contains(optionMD.getOptionName())) {
                customText.setEnabled(true);
                customCheck.setChecked(true);
            } else {
                customText.setEnabled(false);
            }
            customCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    customText.setEnabled(isChecked);
                    configureTraitChecks(categoryChoicesMD, isChecked);
                }
            });
        }

        configureTraitChecks(categoryChoicesMD, !selections.isEmpty());

        currentChooseMD = oldChooseMD;
        parent = oldParent;
    }

    private void createTraitOption(@NonNull Element element) {
        CheckBox checkbox = new CheckBox(parent.getContext());
        checkbox.setText(element.getTextContent());
        parent.addView(checkbox);
        checkbox.setId(++uiIdCounter);

        final MultipleChoicesMD myTraitChoices = (MultipleChoicesMD) currentChooseMD;

        // create the MD
        CheckOptionMD optionMD = new CheckOptionMD(myTraitChoices, checkbox, "" + traitIndex);
        myTraitChoices.addOption(optionMD);

        ChooseMDTreeNode oldCheck = choicesMD;
        choicesMD = optionMD;


        // select the current state
        List<String> selections = choices.getChoicesFor(currentChooseMD.getChoiceName());
        checkbox.setChecked(selections.contains(optionMD.getOptionName()));

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configureTraitChecks(myTraitChoices, isChecked);
            }
        });

        choicesMD = oldCheck;
        traitIndex++;
    }

    private void configureTraitChecks(@NonNull MultipleChoicesMD myTraitChoices, boolean isChecked) {
        for (CheckOptionMD each : myTraitChoices.getOptions()) {
            // we know a trait only contains checkMDs
            //CheckOptionMD checkMD = (CheckOptionMD) each;
            CheckBox checkbox = each.getCheckbox();
            if (isChecked) {
                checkbox.setEnabled(checkbox.isChecked());
            } else {
                checkbox.setEnabled(true);
            }
        }
    }

    public ChooseMDTreeNode appendToLayout(@NonNull Background background, ViewGroup parent, SavedChoices choices, Map<String, String> customChoices, String trait) {
        List<String> list = new ArrayList<>();
        list.add(trait);
        return appendToLayout(background, parent, choices, customChoices, list);
    }

    public ChooseMDTreeNode appendToLayout(@NonNull Background background, ViewGroup parent, SavedChoices choices, Map<String, String> customChoices, List<String> traitNames) {
        this.parent = parent;
        //this.view = parent;
        this.choices = choices;
        this.customChoices = customChoices;
        this.traitNames = traitNames;
        visit(background);
        return choicesMD;
    }

    protected void visitSpecialties(@NonNull final Element element) {
        String groupTitle = parent.getContext().getString(R.string.specialties);
        String choiceName = "specialties";
        Runnable superVisit = new Runnable() {
            @Override
            public void run() {
                BackgroundViewCreatorVisitor.super.visitSpecialties(element);
            }
        };

        createCharacterTraitSelection(groupTitle, choiceName, superVisit, false);
    }


    protected void visitTraits(@NonNull final Element element) {
        String groupTitle = parent.getContext().getString(R.string.personality_traits);
        String choiceName = "traits";
        Runnable superVisit = new Runnable() {
            @Override
            public void run() {
                BackgroundViewCreatorVisitor.super.visitTraits(element);
            }
        };


        createCharacterTraitSelection(groupTitle, choiceName, superVisit, true);
    }

    protected void visitBonds(@NonNull final Element element) {
        String choiceName = "bonds";
        String groupTitle = parent.getContext().getString(R.string.bonds);
        Runnable superVisit = new Runnable() {
            @Override
            public void run() {
                BackgroundViewCreatorVisitor.super.visitBonds(element);
            }
        };

        createCharacterTraitSelection(groupTitle, choiceName, superVisit, true);
    }


    protected void visitFlaws(@NonNull final Element element) {
        String groupTitle = parent.getContext().getString(R.string.flaws);
        String choiceName = "flaws";
        Runnable superVisit = new Runnable() {
            @Override
            public void run() {
                BackgroundViewCreatorVisitor.super.visitFlaws(element);
            }
        };

        createCharacterTraitSelection(groupTitle, choiceName, superVisit, true);
    }

    protected void visitIdeals(@NonNull final Element element) {
        String groupTitle = parent.getContext().getString(R.string.ideals);
        String choiceName = "ideals";
        Runnable superVisit = new Runnable() {
            @Override
            public void run() {
                BackgroundViewCreatorVisitor.super.visitIdeals(element);
            }
        };


        createCharacterTraitSelection(groupTitle, choiceName, superVisit, true);
    }


    @Override
    protected void visitSpecialty(@NonNull Element element) {
        createTraitOption(element);
    }

    protected void visitFlaw(@NonNull Element element) {
        createTraitOption(element);
    }

    protected void visitIdeal(@NonNull Element element) {
        createTraitOption(element);
    }

    protected void visitBond(@NonNull Element element) {
        createTraitOption(element);
    }

    protected void visitTrait(@NonNull Element element) {
        createTraitOption(element);
    }

}

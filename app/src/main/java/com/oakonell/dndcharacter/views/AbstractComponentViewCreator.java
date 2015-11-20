package com.oakonell.dndcharacter.views;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.AbstractComponentVisitor;
import com.oakonell.dndcharacter.model.SavedChoices;
import com.oakonell.dndcharacter.views.md.CheckOptionMD;
import com.oakonell.dndcharacter.views.md.ChooseMD;
import com.oakonell.dndcharacter.views.md.DropdownOptionMD;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/18/2015.
 */
public class AbstractComponentViewCreator extends AbstractComponentVisitor {
    private ViewGroup parent;
    private Map<String, ChooseMD> choicesMD = new HashMap<>();
    SavedChoices choices;
    int uiIdCounter;
    ChooseMD currentChooseMD;


    protected void createGroup(String title) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_component_group, null);
        parent.addView(layout);
        parent = layout;

        TextView titleView = new TextView(parent.getContext());
        titleView.setTextAppearance(parent.getContext(), android.support.v7.appcompat.R.style.TextAppearance_AppCompat_Large);
        parent.addView(titleView);
        titleView.setText(title);
    }


    public Map<String, ChooseMD> appendToLayout(Element element, ViewGroup parent, SavedChoices choices) {
        this.parent = parent;
        this.choices = choices;
        visitChildren(element);
        Log.i("", "Testing");
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

    @Override
    protected void visitEquipment(Element element) {
        ViewGroup oldParent = parent;
        createGroup("Equipment");
        super.visitEquipment(element);

        parent = oldParent;
    }


    @Override
    protected void visitStat(Element element) {
        ViewGroup oldParent = parent;
        createGroup("Stats");
        super.visitStat(element);

        parent = oldParent;
    }

    @Override
    protected void visitIncrease(Element element) {
        TextView text = new TextView(parent.getContext());
        parent.addView(text);
        String string = "Increase " + element.getAttribute("name") + " by " + element.getTextContent();
        text.setText(" *  " + string);
    }

    @Override
    protected void visitSimpleItem(Element element) {
        TextView text = new TextView(parent.getContext());
        parent.addView(text);
        text.setText(" *  " + element.getTextContent());
    }

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
        if (state == VisitState.LANGUAGES) {

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
                Spinner spinner = new NoDefaultSpinner(parent.getContext());
                spinner.setPrompt("[Language]");
                spinner.setLayoutParams(layoutParams);
                spinner.setAdapter(dataAdapter);
                spinner.setId(++uiIdCounter);

                DropdownOptionMD optionMD = new DropdownOptionMD(currentChooseMD, spinner.getId());
                currentChooseMD.options.add(optionMD);

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

        CheckOptionMD optionMD = new CheckOptionMD(currentChooseMD, checkbox.getId(), name);
        currentChooseMD.options.add(optionMD);


        LinearLayout vertLayout = new LinearLayout(parent.getContext());
        vertLayout.setOrientation(LinearLayout.VERTICAL);
        parent.addView(vertLayout);
        parent = vertLayout;

        super.visitOr(element);

        parent = oldParent;
    }


}

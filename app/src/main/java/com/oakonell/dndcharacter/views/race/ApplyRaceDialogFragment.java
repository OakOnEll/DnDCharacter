package com.oakonell.dndcharacter.views.race;

import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.SavedChoices;
import com.oakonell.dndcharacter.model.race.ApplyRaceToCharacterVisitor;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.AbstractComponentViewCreator;
import com.oakonell.dndcharacter.views.ApplyAbstractComponentDialogFragment;
import com.oakonell.dndcharacter.views.NoDefaultSpinner;
import com.oakonell.dndcharacter.views.md.ChooseMD;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class ApplyRaceDialogFragment extends ApplyAbstractComponentDialogFragment<Race> {
    private final Map<String, SavedChoices> savedChoicesByModel = new HashMap<>();
    private final Map<String, Map<String, String>> customChoicesByModel = new HashMap<>();
    Race subrace;
    List<Race> subraces;
    private NoDefaultSpinner subraceSpinner;
    private Map<String, ChooseMD> subRaceChooseMDs;

    public static ApplyRaceDialogFragment createDialog(Character character) {
        Race race = new Select().from(Race.class).where("name = ?", character.getRaceName()).executeSingle();

        ApplyRaceDialogFragment newMe = new ApplyRaceDialogFragment();
        newMe.setModel(race);
        newMe.setCharacter(character);
        return newMe;
    }

    @Override
    protected List<Page<Race>> createPages() {
        List<Page<Race>> result = new ArrayList<Page<Race>>();
        Page main = new Page<Race>() {
            @Override
            public Map<String, ChooseMD> appendToLayout(Race model, final ViewGroup dynamicView, SavedChoices savedChoices, Map<String, String> customChoices) {
                Race race = getModel();

                List<String> list = new ArrayList<String>();
                From nameSelect = new Select()
                        .from(getModelClass()).orderBy("name");
                nameSelect = nameSelect.where("parentRace = ?", race.getName());
                subraces = nameSelect.execute();

                if (subraces.size() > 0) {
                    subraceSpinner = new NoDefaultSpinner(getContext());
                    subraceSpinner.setPrompt("[SubRace]");

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                    subraceSpinner.setLayoutParams(layoutParams);
                    dynamicView.addView(subraceSpinner);

                    int index = 0;
                    int current = -1;
                    for (Race each : subraces) {
                        if (subrace != null && each.getName().equals(subrace.getName())) {
                            current = index;
                        }
                        list.add(each.getName());
                        index++;
                    }

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                            R.layout.large_spinner_text, list);
                    dataAdapter.setDropDownViewResource(R.layout.large_spinner_text);
                    subraceSpinner.setAdapter(dataAdapter);

                    subraceSpinner.setSelection(current);

                    subraceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Race newModel = subraces.get(position);
                            if (newModel == subrace) return;
                            subrace = newModel;
                            dynamicView.removeAllViews();

                            String name = subrace.getName();
                            SavedChoices savedChoices = savedChoicesByModel.get(name);
                            if (savedChoices == null) {
                                savedChoices = new SavedChoices();
                                savedChoicesByModel.put(name, savedChoices);
                            }
                            Map<String, String> customChoices = customChoicesByModel.get(name);
                            if (customChoices == null) {
                                customChoices = new HashMap<String, String>();
                                customChoicesByModel.put(name, customChoices);
                            }

                            displayPage(dynamicView);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


                } else {
                    subraceSpinner = null;
                    subrace = null;
                }


                AbstractComponentViewCreator visitor = new AbstractComponentViewCreator();
                Element element = XmlUtils.getDocument(model.getXml()).getDocumentElement();
                Map<String, ChooseMD> chooseMDs = visitor.appendToLayout(element, dynamicView, savedChoices);

                if (subrace != null) {
                    Element subRaceElement = XmlUtils.getDocument(subrace.getXml()).getDocumentElement();
                    subRaceChooseMDs = visitor.appendToLayout(subRaceElement, dynamicView, savedChoices);
                }

                return chooseMDs;
            }
        };


        result.add(main);
        return result;
    }

    protected void applyToCharacter(SavedChoices savedChoices, Map<String, String> customChoices) {
        SavedChoices subraceChoices = null;
        Map<String, String> subraceCustom = null;
        if (subrace != null) {
            subraceChoices = savedChoicesByModel.get(subrace.getName());
            subraceCustom = customChoicesByModel.get(subrace.getName());
        }

        ApplyRaceToCharacterVisitor.applyToCharacter(getModel(), savedChoices, customChoices, subrace, subraceChoices, subraceCustom, getCharacter());
    }


    @NonNull
    protected Class<? extends Race> getModelClass() {
        return Race.class;
    }

    protected SavedChoices getSavedChoicesFromCharacter(com.oakonell.dndcharacter.model.Character character) {
        return character.getRaceChoices();
    }

    protected String getCurrentName() {
        return getCharacter().getRaceName();
    }

    protected From filter(From nameSelect) {
        return nameSelect.where("parentRace is null OR trim(parentRace) = ''");
    }

    @Override
    public void setCharacter(Character character) {
        super.setCharacter(character);
        String subraceName = character.getSubRaceName();
        if (subraceName != null) {
            subrace = new Select().from(Race.class).where("name = ?", subraceName).executeSingle();
            savedChoicesByModel.put(getCurrentName(), character.getSubRaceChoices());
            customChoicesByModel.put(getCurrentName(), new HashMap<String, String>());
            ;
        }
    }

    @Override
    public String getModelSpinnerPrompt() {
        return "[Race]";
    }


    protected void saveChoices(ViewGroup dynamicView) {
        super.saveChoices(dynamicView);
        if (subrace == null) return;
        String name = subrace.getName();
        SavedChoices savedChoices = savedChoicesByModel.get(name);
        Map<String, String> customChoices = customChoicesByModel.get(name);

        for (ChooseMD each : subRaceChooseMDs.values()) {
            each.saveChoice(dynamicView, savedChoices, customChoices);
        }
    }
}
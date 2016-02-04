package com.oakonell.dndcharacter.views.character.race;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.model.race.ApplyRaceToCharacterVisitor;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.character.AbstractComponentViewCreator;
import com.oakonell.dndcharacter.views.character.ApplyAbstractComponentDialogFragment;
import com.oakonell.dndcharacter.views.NoDefaultSpinner;
import com.oakonell.dndcharacter.views.character.md.ChooseMD;
import com.oakonell.dndcharacter.views.character.md.ChooseMDTreeNode;
import com.oakonell.dndcharacter.views.character.md.RootChoiceMDNode;
import com.oakonell.dndcharacter.views.character.persona.RaceLooksDialogFragment;

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
    @Nullable
    private Race subrace;
    private List<Race> subraces;
    @Nullable
    private NoDefaultSpinner subraceSpinner;
    private ChooseMDTreeNode subRaceChooseMDs;
    private TextView subRaceErrorView;

    @NonNull
    public static ApplyRaceDialogFragment createDialog() {
        return new ApplyRaceDialogFragment();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.choose_a_race);
    }


    @Override
    protected boolean validate(@NonNull ViewGroup dynamicView, int pageIndex) {
        boolean subraceValid = true;
        if (pageIndex == 0 && subraceSpinner != null) {
            if (subraceSpinner.getSelectedItemPosition() < 0) {
                subRaceErrorView.setError(getString(R.string.choose_subrace_error));
                Animation shake = AnimationUtils.loadAnimation(dynamicView.getContext(), R.anim.shake);
                subraceSpinner.startAnimation(shake);
                subraceValid = false;
            }
        }
        return super.validate(dynamicView, pageIndex) && subraceValid;
    }

    @NonNull
    @Override
    protected List<Page<Race>> createPages() {
        List<Page<Race>> result = new ArrayList<>();
        Page<Race> main = new Page<Race>() {
            @Override
            public ChooseMDTreeNode appendToLayout(@NonNull Race model, @NonNull final ViewGroup dynamicView, SavedChoices savedChoices, Map<String, String> customChoices) {
                Race race = getModel();

                List<String> list = new ArrayList<>();
                From nameSelect = new Select()
                        .from(getModelClass()).orderBy("name");
                nameSelect = nameSelect.where("parentRace = ?", race.getName());
                subraces = nameSelect.execute();

                if (subraces.size() > 0) {

                    ViewGroup layout = (ViewGroup) LayoutInflater.from(dynamicView.getContext()).inflate(R.layout.drop_down_layout, dynamicView);
                    subraceSpinner = (NoDefaultSpinner) layout.findViewById(R.id.spinner);
                    subRaceErrorView = (TextView) layout.findViewById(R.id.tvInvisibleError);

                    subraceSpinner.setPromptId(R.string.subrace_prompt);

                    int index = 0;
                    int current = -1;
                    for (Race each : subraces) {
                        if (subrace != null && each.getName().equals(subrace.getName())) {
                            current = index;
                        }
                        list.add(each.getName());
                        index++;
                    }

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
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
                                customChoices = new HashMap<>();
                                customChoicesByModel.put(name, customChoices);
                            }

                            displayPage();
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
                ChooseMDTreeNode chooseMDs = visitor.appendToLayout(element, getMainActivity(), dynamicView, savedChoices);

                if (subrace != null) {
                    Element subRaceElement = XmlUtils.getDocument(subrace.getXml()).getDocumentElement();
                    subRaceChooseMDs = visitor.appendToLayout(subRaceElement, getMainActivity(), dynamicView, savedChoices);
                }

                return chooseMDs;
            }
        };


        result.add(main);

        Page<Race> raceLookPage = new Page<Race>() {

            private RaceLooksDialogFragment frag;

            @Override
            public ChooseMDTreeNode appendToLayout(Race model, ViewGroup dynamicView, SavedChoices savedChoices, Map<String, String> customChoices) {
                //ViewGroup layout = (ViewGroup) LayoutInflater.from(dynamicView.getContext()).inflate(R.layout.race_looks_layout, dynamicView);

                Element raceElement = XmlUtils.getDocument(model.getXml()).getDocumentElement();
                if (subrace != null) {
                    Element subRaceElement = XmlUtils.getDocument(subrace.getXml()).getDocumentElement();
                }
                //dynamicView.setId(R.id.unique_frag_container);

                String subraceName = null;
                if (subrace != null) {
                    subraceName = subrace.getName();
                }
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                frag = RaceLooksDialogFragment.create(getCurrentName(), subraceName, customChoices);
                ft.add(R.id.dynamic_content, frag);
                ft.commit();


                return new RootChoiceMDNode();
            }

            @Override
            public void saveChoices(Race model, ViewGroup dynamic, SavedChoices savedChoices, Map<String, String> customChoices) {
                super.saveChoices(model, dynamic, savedChoices, customChoices);
                customChoices.putAll(frag.getData());
            }
        };
        result.add(raceLookPage);


        return result;
    }

    protected void applyToCharacter(SavedChoices savedChoices, Map<String, String> customChoices) {
        SavedChoices subraceChoices = null;
        Map<String, String> subraceCustom = null;
        if (subrace != null) {
            subraceChoices = savedChoicesByModel.get(subrace.getName());
            subraceCustom = customChoicesByModel.get(subrace.getName());
        }

        ApplyRaceToCharacterVisitor.applyToCharacter(getActivity(), getModel(), savedChoices, customChoices, subrace, subraceChoices, subraceCustom, getCharacter());

        String ageString = customChoices.get("age");
        int age = 0;
        if (ageString != null) {
            age = Integer.parseInt(ageString);
        }
        String weightString = customChoices.get("weight");
        int weight = 0;
        if (weightString != null) {
            weight = Integer.parseInt(weightString);
        }
        String height = customChoices.get("height");
        String hair = customChoices.get("hair");
        String skin = customChoices.get("skin");
        String eyes = customChoices.get("eyes");

        getCharacter().setAge(age);
        getCharacter().setWeight(weight);
        getCharacter().setHeight(height);
        getCharacter().setSkin(skin);
        getCharacter().setHair(hair);
        getCharacter().setEyes(eyes);
    }


    @NonNull
    protected Class<? extends Race> getModelClass() {
        return Race.class;
    }

    protected SavedChoices getSavedChoicesFromCharacter(@NonNull Character character) {
        return character.getRaceChoices();
    }

    protected String getCurrentName() {
        return getCharacter().getRaceName();
    }

    @NonNull
    protected From filter(@NonNull From nameSelect) {
        return nameSelect.where("parentRace is null OR trim(parentRace) = ''");
    }


    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        Race race = new Select().from(Race.class).where("name = ?", character.getRaceName()).executeSingle();
        setModel(race);


        String subraceName = character.getSubRaceName();
        if (subraceName != null) {
            subrace = new Select().from(Race.class).where("name = ?", subraceName).executeSingle();
            savedChoicesByModel.put(subraceName, character.getSubRaceChoices());
            customChoicesByModel.put(subraceName, new HashMap<String, String>());
        }
        super.onCharacterLoaded(character);
    }

    @Override
    public String getModelSpinnerPrompt() {
        return getString(R.string.race_prompt);
    }


    protected void saveChoices(ViewGroup dynamicView) {
        super.saveChoices(dynamicView);
        if (subrace == null) return;
        String name = subrace.getName();
        SavedChoices subSavedChoices = savedChoicesByModel.get(name);
        Map<String, String> subCustomChoices = customChoicesByModel.get(name);

        for (ChooseMD each : subRaceChooseMDs.getChildChoiceMDs()) {
            each.saveChoice(dynamicView, subSavedChoices, subCustomChoices);
        }
    }

    @NonNull
    @Override
    protected Map<String, String> getCustomChoicesFromCharacter(Character character) {
        Map<String, String> result = super.getCustomChoicesFromCharacter(character);

        result.put("age", NumberUtils.formatNumber(character.getAge()));
        result.put("weight", NumberUtils.formatNumber(character.getWeight()));
        result.put("height", character.getHeight());
        result.put("hair", character.getHair());
        result.put("skin", character.getSkin());
        result.put("eyes", character.getEyes());

        return result;
    }
}
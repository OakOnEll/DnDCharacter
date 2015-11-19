package com.oakonell.dndcharacter.views.race;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.MainActivity;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.SavedChoices;
import com.oakonell.dndcharacter.model.race.ApplyRaceToCharacterVisitor;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.AbstractComponentViewCreator;
import com.oakonell.dndcharacter.views.md.ChooseMD;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class ApplyRaceDialogFragment extends DialogFragment {
    private Race race;
    private Map<String, ChooseMD> chooseMDs;
    private Character character;

    private final Map<String, SavedChoices> savedChoicesByRace = new HashMap<>();
    private final Map<String, Map<String, String>> customChoicesByRace = new HashMap<>();

    private Button doneButton;
    private Button previousButton;
    private Button nextButton;
    private Spinner nameSpinner;
    private ViewGroup dynamicView;

    public static ApplyRaceDialogFragment createDialog(Character character, Race race) {
        ApplyRaceDialogFragment newMe = new ApplyRaceDialogFragment();
        newMe.setRace(race);
        newMe.setCharacter(character);
        return newMe;
    }


    private static abstract class Page {
        public abstract Map<String, ChooseMD> appendToLayout(Race race, ViewGroup dynamic, SavedChoices raceChoices, Map<String, String> customChoices);
    }

    int pageIndex = 0;
    List<Page> pages = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.race_dialog, container);

        dynamicView = (ViewGroup) view.findViewById(R.id.dynamic_content);
        doneButton = (Button) view.findViewById(R.id.done);
        previousButton = (Button) view.findViewById(R.id.previous);
        nextButton = (Button) view.findViewById(R.id.next);
        nameSpinner = (Spinner) view.findViewById(R.id.name);

        List<String> list = new ArrayList<String>();
        final List<Race> races = new Select()
                .from(Race.class).orderBy("name").execute();
        int index = 0;
        int current = -1;
        for (Race each : races) {
            if (each.name.equals(character.getRaceName())) {
                current = index;
            }
            list.add(each.name);
            index++;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.large_spinner_text, list);
        dataAdapter.setDropDownViewResource(R.layout.large_spinner_text);
        nameSpinner.setAdapter(dataAdapter);

        nameSpinner.setSelection(current);

        nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Race newRace = races.get(position);
                if (newRace == race) return;
                race = newRace;
                dynamicView.removeAllViews();

                String raceName = race.getName();
                SavedChoices savedChoices = savedChoicesByRace.get(raceName);
                if (savedChoices == null) {
                    savedChoices = new SavedChoices();
                    savedChoicesByRace.put(raceName, savedChoices);
                }
                Map<String, String> customChoices = customChoicesByRace.get(raceName);
                if (customChoices == null) {
                    customChoices = new HashMap<>();
                    customChoicesByRace.put(raceName, customChoices);
                }

                createPages();

                displayPage(dynamicView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createPages();

        displayPage(dynamicView);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageIndex++;
                saveChoices();
                dynamicView.removeAllViews();
                displayPage(dynamicView);
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageIndex--;
                saveChoices();
                dynamicView.removeAllViews();
                displayPage(dynamicView);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChoices();

                int selectRaceIndex = nameSpinner.getSelectedItemPosition();
                Race race = races.get(selectRaceIndex);

                String backgroundName = race.getName();
                SavedChoices savedChoices = savedChoicesByRace.get(backgroundName);
                Map<String, String> customChoices = customChoicesByRace.get(backgroundName);

                ApplyRaceToCharacterVisitor.applyToCharacter(race, savedChoices, customChoices, character);
                dismiss();
                ((MainActivity) getActivity()).updateViews();
            }
        });

        return view;
    }

    private void createPages() {
        pages.clear();
        Page main = new Page() {
            @Override
            public Map<String, ChooseMD> appendToLayout(Race race, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                AbstractComponentViewCreator visitor = new AbstractComponentViewCreator();
                Element element = XmlUtils.getDocument(race.getXml()).getDocumentElement();
                return visitor.appendToLayout(element, dynamic, backgroundChoices);
            }
        };


        pages.add(main);
    }

    private void saveChoices() {
        String backgroundName = race.getName();
        SavedChoices savedChoices = savedChoicesByRace.get(backgroundName);
        Map<String, String> customChoices = customChoicesByRace.get(backgroundName);

        for (ChooseMD each : chooseMDs.values()) {
            each.saveChoice(dynamicView, savedChoices, customChoices);
        }
    }

    private void displayPage(ViewGroup dynamic) {
        String backgroundName = race.getName();
        SavedChoices savedChoices = savedChoicesByRace.get(backgroundName);
        Map<String, String> customChoices = customChoicesByRace.get(backgroundName);

        Page page = pages.get(pageIndex);
        chooseMDs = page.appendToLayout(race, dynamic, savedChoices, customChoices);

        nameSpinner.setEnabled(pageIndex == 0);

        boolean isLast = pageIndex == pages.size() - 1;
        boolean isFirst = pageIndex == 0;


        if (isFirst) {
            previousButton.setVisibility(View.GONE);
        } else {
            previousButton.setVisibility(View.VISIBLE);
        }

        if (isLast) {
            doneButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
        } else {
            doneButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
        }
    }


    public void setRace(Race race) {
        this.race = race;
    }

    public void setCharacter(Character character) {
        this.character = character;

        SavedChoices savedChoices = character.getRaceChoices().copy();
        Map<String, String> customChoices = new HashMap<>();

        savedChoicesByRace.put(character.getRaceName(), savedChoices);
        customChoicesByRace.put(character.getRaceName(), customChoices);
    }
}
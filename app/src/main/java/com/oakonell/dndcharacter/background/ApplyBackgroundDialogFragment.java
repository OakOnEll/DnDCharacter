package com.oakonell.dndcharacter.background;

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
import com.oakonell.dndcharacter.model.AbstractComponentViewCreator;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.background.ApplyBackgroundToCharacterVisitor;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.model.background.SavedChoices;
import com.oakonell.dndcharacter.model.md.ChooseMD;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class ApplyBackgroundDialogFragment extends DialogFragment {
    private Background background;
    private Map<String, ChooseMD> chooseMDs;
    private Character character;

    private final Map<String, SavedChoices> savedChoicesByBackground = new HashMap<>();
    private final Map<String, Map<String, String>> customChoicesByBackground = new HashMap<>();

    private Button doneButton;
    private Button previousButton;
    private Button nextButton;
    private Spinner nameSpinner;
    private ViewGroup dynamicView;

    public static ApplyBackgroundDialogFragment createDialog(Character character, Background background) {
        ApplyBackgroundDialogFragment newMe = new ApplyBackgroundDialogFragment();
        newMe.setBackground(background);
        newMe.setCharacter(character);
        return newMe;
    }

    private static abstract class Page {
        public abstract Map<String, ChooseMD> appendToLayout(Background background, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices);
    }

    int pageIndex = 0;
    List<Page> pages = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.background_dialog, container);

        dynamicView = (ViewGroup) view.findViewById(R.id.dynamic_content);
        doneButton = (Button) view.findViewById(R.id.done);
        previousButton = (Button) view.findViewById(R.id.previous);
        nextButton = (Button) view.findViewById(R.id.next);
        nameSpinner = (Spinner) view.findViewById(R.id.name);

        List<String> list = new ArrayList<String>();
        final List<Background> backgrounds = new Select()
                .from(Background.class).orderBy("name").execute();
        int index = 0;
        int current = -1;
        for (Background each : backgrounds) {
            if (each.name.equals(character.getBackgroundName())) {
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
                Background newBackground = backgrounds.get(position);
                if (newBackground == background) return;
                background = newBackground;
                dynamicView.removeAllViews();

                String backgroundName = background.getName();
                SavedChoices savedChoices = savedChoicesByBackground.get(backgroundName);
                if (savedChoices == null) {
                    savedChoices = new SavedChoices();
                    savedChoicesByBackground.put(backgroundName, savedChoices);
                }
                Map<String, String> customChoices = customChoicesByBackground.get(backgroundName);
                if (customChoices == null) {
                    customChoices = new HashMap<>();
                    customChoicesByBackground.put(backgroundName, customChoices);
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

                int selectBackgroundIndex = nameSpinner.getSelectedItemPosition();
                Background background = backgrounds.get(selectBackgroundIndex);

                String backgroundName = background.getName();
                SavedChoices savedChoices = savedChoicesByBackground.get(backgroundName);
                Map<String, String> customChoices = customChoicesByBackground.get(backgroundName);

                ApplyBackgroundToCharacterVisitor.applyToCharacter(background, savedChoices, customChoices, character);
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
            public Map<String, ChooseMD> appendToLayout(Background background, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                AbstractComponentViewCreator visitor = new AbstractComponentViewCreator();
                Element element = XmlUtils.getDocument(background.getXml()).getDocumentElement();
                return visitor.appendToLayout(element, dynamic, backgroundChoices);
            }
        };

        Page trait = new Page() {
            @Override
            public Map<String, ChooseMD> appendToLayout(Background background, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                BackgroundViewCreatorVisitor visitor = new BackgroundViewCreatorVisitor();
                return visitor.appendToLayout(background, dynamic, character, backgroundChoices, customChoices, "traits");
            }
        };
        Page ideal = new Page() {
            @Override
            public Map<String, ChooseMD> appendToLayout(Background background, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                BackgroundViewCreatorVisitor visitor = new BackgroundViewCreatorVisitor();
                return visitor.appendToLayout(background, dynamic, character, backgroundChoices, customChoices, "ideals");
            }
        };
        Page bond = new Page() {
            @Override
            public Map<String, ChooseMD> appendToLayout(Background background, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                BackgroundViewCreatorVisitor visitor = new BackgroundViewCreatorVisitor();
                return visitor.appendToLayout(background, dynamic, character, backgroundChoices, customChoices, "bonds");
            }
        };
        Page flaw = new Page() {
            @Override
            public Map<String, ChooseMD> appendToLayout(Background background, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                BackgroundViewCreatorVisitor visitor = new BackgroundViewCreatorVisitor();
                return visitor.appendToLayout(background, dynamic, character, backgroundChoices, customChoices, "flaws");
            }
        };
        Element documentElement = XmlUtils.getDocument(background.getXml()).getDocumentElement();
        Page extra = null;
        if (!XmlUtils.getChildElements(documentElement, "specialties").isEmpty()) {
            extra = new Page() {
                @Override
                public Map<String, ChooseMD> appendToLayout(Background background, ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                    BackgroundViewCreatorVisitor visitor = new BackgroundViewCreatorVisitor();
                    return visitor.appendToLayout(background, dynamic, character, backgroundChoices, customChoices, "specialties");
                }
            };
        }
        pages.add(main);
        if (extra != null) {
            pages.add(extra);
        }
        pages.add(trait);
        pages.add(ideal);
        pages.add(bond);
        pages.add(flaw);
    }

    private void saveChoices() {
        String backgroundName = background.getName();
        SavedChoices savedChoices = savedChoicesByBackground.get(backgroundName);
        Map<String, String> customChoices = customChoicesByBackground.get(backgroundName);

        for (ChooseMD each : chooseMDs.values()) {
            each.saveChoice(dynamicView, savedChoices, customChoices);
        }
    }

    private void displayPage(ViewGroup dynamic) {
        String backgroundName = background.getName();
        SavedChoices savedChoices = savedChoicesByBackground.get(backgroundName);
        Map<String, String> customChoices = customChoicesByBackground.get(backgroundName);

        Page page = pages.get(pageIndex);
        chooseMDs = page.appendToLayout(background, dynamic, savedChoices, customChoices);

        nameSpinner.setEnabled(pageIndex == 0);
        if (pageIndex == 0) {
            previousButton.setVisibility(View.GONE);
            doneButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
        } else if (pageIndex == pages.size() - 1) {
            previousButton.setVisibility(View.VISIBLE);
            doneButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
        } else {
            previousButton.setVisibility(View.VISIBLE);
            doneButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
        }
    }

    public void setBackground(Background background) {
        this.background = background;
    }

    public void setCharacter(Character character) {
        this.character = character;

        SavedChoices savedChoices = character.getBackgroundChoices().copy();
        Map<String, String> customChoices = new HashMap<>();
        if (savedChoices.getChoicesFor("traits").contains("custom")) {
            customChoices.put("traits", character.getPersonalityTrait());
        }
        if (savedChoices.getChoicesFor("ideals").contains("custom")) {
            customChoices.put("ideals", character.getIdeals());
        }
        if (savedChoices.getChoicesFor("bonds").contains("custom")) {
            customChoices.put("bonds", character.getBonds());
        }
        if (savedChoices.getChoicesFor("flaws").contains("custom")) {
            customChoices.put("flaws", character.getFlaws());
        }
        savedChoicesByBackground.put(character.getBackgroundName(), savedChoices);
        customChoicesByBackground.put(character.getBackgroundName(), customChoices);
    }
}
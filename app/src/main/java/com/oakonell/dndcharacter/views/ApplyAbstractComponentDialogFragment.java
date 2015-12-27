package com.oakonell.dndcharacter.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.SavedChoices;
import com.oakonell.dndcharacter.views.md.ChooseMD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/20/2015.
 */
public abstract class ApplyAbstractComponentDialogFragment<M extends AbstractComponentModel> extends AbstractCharacterDialogFragment {

    private final Map<String, SavedChoices> savedChoicesByModel = new HashMap<>();
    private final Map<String, Map<String, String>> customChoicesByModel = new HashMap<>();
    int pageIndex = 0;
    List<Page<M>> pages = new ArrayList<>();
    private M model;
    private Map<String, ChooseMD> chooseMDs;

    private Button doneButton;
    private Button previousButton;
    private Button nextButton;
    private Spinner nameSpinner;
    private ViewGroup dynamicView;

    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.race_dialog, container);

        dynamicView = (ViewGroup) view.findViewById(R.id.dynamic_content);
        doneButton = (Button) view.findViewById(R.id.done);
        previousButton = (Button) view.findViewById(R.id.previous);
        nextButton = (Button) view.findViewById(R.id.next);
        nameSpinner = (Spinner) view.findViewById(R.id.name);
        nameSpinner.setPrompt(getModelSpinnerPrompt());

        List<String> list = new ArrayList<>();
        From nameSelect = new Select()
                .from(getModelClass()).orderBy("name");
        nameSelect = filter(nameSelect);
        final List<M> selections = nameSelect.execute();
        for (M each : selections) {
            list.add(each.getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                R.layout.large_spinner_text, list);
        dataAdapter.setDropDownViewResource(R.layout.large_spinner_text);
        nameSpinner.setAdapter(dataAdapter);

        nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                M newModel = selections.get(position);
                if (newModel == model) return;
                model = newModel;
                dynamicView.removeAllViews();

                String name = model.getName();
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

                modelChanged();

                pages = createPages();

                displayPage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        pages = createPages();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate(dynamicView, pageIndex)) return;
                pageIndex++;
                saveChoices(dynamicView);
                dynamicView.removeAllViews();
                displayPage();
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageIndex--;
                saveChoices(dynamicView);
                dynamicView.removeAllViews();
                displayPage();
            }
        });

        return view;
    }

    @Override
    protected boolean onDone() {
        if (!validate(dynamicView, pageIndex)) return false;

        saveChoices(dynamicView);

        String name = model.getName();
        SavedChoices savedChoices = savedChoicesByModel.get(name);
        Map<String, String> customChoices = customChoicesByModel.get(name);

        applyToCharacter(savedChoices, customChoices);

        return super.onDone();

    }

    protected boolean validate(ViewGroup dynamicView, int pageIndex) {
        int invalid = 0;

        for (ChooseMD each : chooseMDs.values()) {
            if (!each.validate(dynamicView)) {
                invalid++;
            }
        }
        return invalid == 0;
    }

    protected void modelChanged() {

    }

    @Override
    public void onCharacterChanged(Character character) {
        // TODO
        onCharacterLoaded(character);
    }


    protected From filter(From nameSelect) {
        return nameSelect;
    }

    protected void saveChoices(ViewGroup dynamicView) {
        String name = model.getName();
        SavedChoices savedChoices = savedChoicesByModel.get(name);
        Map<String, String> customChoices = customChoicesByModel.get(name);

        for (ChooseMD each : chooseMDs.values()) {
            each.saveChoice(dynamicView, savedChoices, customChoices);
        }
    }

    protected void displayPage() {
        if (model != null) {
            String name = model.getName();
            SavedChoices savedChoices = savedChoicesByModel.get(name);
            Map<String, String> customChoices = customChoicesByModel.get(name);

            Page page = pages.get(pageIndex);
            chooseMDs = page.appendToLayout(model, dynamicView, savedChoices, customChoices);
        }
        nameSpinner.setEnabled(pageIndex == 0 && allowMainComponentChange());

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

    protected boolean allowMainComponentChange() {
        return true;
    }

    @NonNull
    protected Map<String, String> getCustomChoicesFromCharacter(Character character) {
        return new HashMap<>();
    }

    protected SavedChoices getSavedChoicesFromCharacter(Character character) {
        return new SavedChoices();
    }

    abstract protected List<Page<M>> createPages();

    abstract protected void applyToCharacter(SavedChoices savedChoices, Map<String, String> customChoices);

    abstract protected String getCurrentName();

    @NonNull
    abstract protected Class<? extends M> getModelClass();

    public M getModel() {
        return model;
    }

    public void setModel(M model) {
        this.model = model;
    }


    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);


        int max = nameSpinner.getAdapter().getCount();

        for (int i = 0; i < max; i++) {
            String each = (String) nameSpinner.getAdapter().getItem(i);
            if (each.equals(getCurrentName())) {
                nameSpinner.setSelection(i);
                break;
            }
        }


        SavedChoices savedChoices = getSavedChoicesFromCharacter(character).copy();
        Map<String, String> customChoices = getCustomChoicesFromCharacter(character);

        savedChoicesByModel.put(getCurrentName(), savedChoices);
        customChoicesByModel.put(getCurrentName(), customChoices);

        displayPage();

    }

    public abstract String getModelSpinnerPrompt();

    protected static abstract class Page<M extends AbstractComponentModel> {
        public abstract Map<String, ChooseMD> appendToLayout(M model, ViewGroup dynamic, SavedChoices savedChoices, Map<String, String> customChoices);
    }
}
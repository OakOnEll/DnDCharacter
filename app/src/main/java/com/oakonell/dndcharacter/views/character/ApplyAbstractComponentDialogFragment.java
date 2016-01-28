package com.oakonell.dndcharacter.views.character;

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
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.views.character.md.ChooseMD;
import com.oakonell.dndcharacter.views.character.md.ChooseMDTreeNode;

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
    private int pageIndex = 0;
    @NonNull
    private List<Page<M>> pages = new ArrayList<>();
    private M model;
    private ChooseMDTreeNode chooseMDs;

    private Button doneButton;
    private Button previousButton;
    private Button nextButton;
    private Spinner nameSpinner;
    private ViewGroup dynamicView;

    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.dynamic_apply_component_dialog, container);

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

                recreatePages();

                displayPage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recreatePages();

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

    protected void recreatePages() {
        pages = createPages();
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

        for (ChooseMD each : chooseMDs.getChildChoiceMDs()) {
            if (!each.validate(dynamicView)) {
                invalid++;
            }
        }
        return invalid == 0;
    }

    protected void modelChanged() {

    }

    protected From filter(From nameSelect) {
        return nameSelect;
    }

    protected void saveChoices(ViewGroup dynamicView) {
        String name = model.getName();
        SavedChoices savedChoices = savedChoicesByModel.get(name);
        Map<String, String> customChoices = customChoicesByModel.get(name);

        for (ChooseMD each : chooseMDs.getChildChoiceMDs()) {
            each.saveChoice(dynamicView, savedChoices, customChoices);
        }
    }

    protected void displayPage() {
        if (model != null && pages.size() > 0) {
            String name = model.getName();
            SavedChoices savedChoices = savedChoicesByModel.get(name);
            Map<String, String> customChoices = customChoicesByModel.get(name);

            Page<M> page = pages.get(pageIndex);
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

    @NonNull
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

        recreatePages();
        displayPage();
    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        // TODO
        super.onCharacterChanged(character);
        onCharacterLoaded(character);
    }

    public abstract String getModelSpinnerPrompt();

    protected static abstract class Page<M extends AbstractComponentModel> {
        public abstract ChooseMDTreeNode appendToLayout(M model, ViewGroup dynamic, SavedChoices savedChoices, Map<String, String> customChoices);
    }
}
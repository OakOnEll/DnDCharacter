package com.oakonell.dndcharacter.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.MainActivity;
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
public abstract class ApplyAbstractComponentDialogFragment<M extends AbstractComponentModel> extends DialogFragment {
    ComponentLaunchHelper.OnDialogDone onDone;

    private final Map<String, SavedChoices> savedChoicesByModel = new HashMap<>();
    private final Map<String, Map<String, String>> customChoicesByModel = new HashMap<>();
    int pageIndex = 0;
    List<Page<M>> pages = new ArrayList<>();
    private M model;
    private Map<String, ChooseMD> chooseMDs;
    private com.oakonell.dndcharacter.model.Character character;
    private Button doneButton;
    private Button previousButton;
    private Button nextButton;
    private Spinner nameSpinner;
    private String modelSpinnerPrompt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.race_dialog, container);

        final ViewGroup dynamicView = (ViewGroup) view.findViewById(R.id.dynamic_content);
        doneButton = (Button) view.findViewById(R.id.done);
        previousButton = (Button) view.findViewById(R.id.previous);
        nextButton = (Button) view.findViewById(R.id.next);
        nameSpinner = (Spinner) view.findViewById(R.id.name);
        nameSpinner.setPrompt(getModelSpinnerPrompt());

        List<String> list = new ArrayList<String>();
        From nameSelect = new Select()
                .from(getModelClass()).orderBy("name");
        nameSelect = filter(nameSelect);
        final List<M> selections = nameSelect.execute();
        int index = 0;
        int current = -1;
        for (M each : selections) {
            if (each.getName().equals(getCurrentName())) {
                current = index;
            }
            list.add(each.getName());
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
                    customChoices = new HashMap<String, String>();
                    customChoicesByModel.put(name, customChoices);
                }

                modelChanged();

                pages = createPages();

                displayPage(dynamicView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        pages = createPages();

        displayPage(dynamicView);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageIndex++;
                saveChoices(dynamicView);
                dynamicView.removeAllViews();
                displayPage(dynamicView);
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageIndex--;
                saveChoices(dynamicView);
                dynamicView.removeAllViews();
                displayPage(dynamicView);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChoices(dynamicView);

                String name = model.getName();
                SavedChoices savedChoices = savedChoicesByModel.get(name);
                Map<String, String> customChoices = customChoicesByModel.get(name);

                applyToCharacter(savedChoices, customChoices);
                dismiss();
                ((MainActivity) getActivity()).updateViews();
                onDone.done(true);
            }
        });

        return view;
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

        for (ChooseMD each : chooseMDs.values()) {
            each.saveChoice(dynamicView, savedChoices, customChoices);
        }
    }

    protected void displayPage(ViewGroup dynamic) {
        if (model != null) {
            String name = model.getName();
            SavedChoices savedChoices = savedChoicesByModel.get(name);
            Map<String, String> customChoices = customChoicesByModel.get(name);

            Page page = pages.get(pageIndex);
            chooseMDs = page.appendToLayout(model, dynamic, savedChoices, customChoices);
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

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;

        SavedChoices savedChoices = getSavedChoicesFromCharacter(character).copy();
        Map<String, String> customChoices = getCustomChoicesFromCharacter(character);

        savedChoicesByModel.put(getCurrentName(), savedChoices);
        customChoicesByModel.put(getCurrentName(), customChoices);
    }

    public abstract String getModelSpinnerPrompt();

    public void setOnDone(ComponentLaunchHelper.OnDialogDone onDone) {
        this.onDone = onDone;
    }

    protected static abstract class Page<M extends AbstractComponentModel> {
        public abstract Map<String, ChooseMD> appendToLayout(M model, ViewGroup dynamic, SavedChoices savedChoices, Map<String, String> customChoices);
    }
}
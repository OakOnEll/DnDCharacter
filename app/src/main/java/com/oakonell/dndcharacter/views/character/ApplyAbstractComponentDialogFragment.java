package com.oakonell.dndcharacter.views.character;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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

    private static final String PAGE_INDEX = "pageIndex";
    private static final String DONE_LABEL = "doneLabel";
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
    private TextView nameError;
    private ViewGroup dynamicView;
    private List<M> modelSelections;

    private DoneListener doneListener;
    @Nullable
    private String doneLabel;

    public interface DoneListener {
        void onDone();
    }

    public void setDoneLabel(@NonNull String doneLabel) {
        this.doneLabel = doneLabel;
    }

    public void setDoneListener(DoneListener listener) {
        this.doneListener = listener;
    }

    @Nullable
    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.dynamic_apply_component_dialog, container);

        if (savedInstanceState != null) {
            pageIndex = savedInstanceState.getInt(PAGE_INDEX, 0);
            doneLabel = savedInstanceState.getString(DONE_LABEL);
        }

        dynamicView = (ViewGroup) view.findViewById(R.id.dynamic_content);
        doneButton = (Button) view.findViewById(R.id.done);
        previousButton = (Button) view.findViewById(R.id.previous);
        nextButton = (Button) view.findViewById(R.id.next);
        nameSpinner = (Spinner) view.findViewById(R.id.name);
        nameSpinner.setPrompt(getModelSpinnerPrompt());
        nameError = (TextView) view.findViewById(R.id.nameError);

        List<String> list = new ArrayList<>();
        From nameSelect = new Select()
                .from(getModelClass()).orderBy("name");
        nameSelect = filter(nameSelect);
        modelSelections = nameSelect.execute();
        for (M each : modelSelections) {
            list.add(each.getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                R.layout.large_spinner_text, list);
        dataAdapter.setDropDownViewResource(R.layout.large_spinner_text);
        nameSpinner.setAdapter(dataAdapter);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate(dynamicView, pageIndex)) return;
                saveChoices(dynamicView);
                pageIndex++;
                dynamicView.removeAllViews();
                displayPage();
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChoices(dynamicView);
                pageIndex--;
                dynamicView.removeAllViews();
                displayPage();
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PAGE_INDEX, pageIndex);
        outState.putString(DONE_LABEL, doneLabel);
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

        if (doneListener != null) {
            doneListener.onDone();
        }

        return super.onDone();

    }

    protected boolean validate(@NonNull ViewGroup dynamicView, int pageIndex) {
        int invalid = 0;
        if (model == null) {
            nameError.setError(getString(R.string.choose_one_error));
            Animation shake = AnimationUtils.loadAnimation(dynamicView.getContext(), R.anim.shake);
            nameSpinner.startAnimation(shake);
            invalid++;
        }

        if (chooseMDs != null) {
            for (ChooseMD each : chooseMDs.getChildChoiceMDs()) {
                if (!each.validate(dynamicView)) {
                    invalid++;
                }
            }
            if (!pages.get(pageIndex).validate(dynamicView, chooseMDs.getChildChoiceMDs())) {
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

        Page<M> page = pages.get(pageIndex);
        page.saveChoices(model, dynamicView, savedChoices, customChoices);


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
            if (doneLabel != null) {
                doneButton.setText(doneLabel);
                //doneButton.setText(R.string.next_button_label);
            } else {
                doneButton.setText(R.string.done_button_label);
            }
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

    @Nullable
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
        if (nameSpinner == null) return;

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

        if (getCurrentName() != null) {
            savedChoicesByModel.put(getCurrentName(), savedChoices);
            customChoicesByModel.put(getCurrentName(), customChoices);
        }

        nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nameError.setError(null);
                M newModel = modelSelections.get(position);
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

        public void saveChoices(M model, ViewGroup dynamic, SavedChoices savedChoices, Map<String, String> customChoices) {
            // can override
        }

        public boolean validate(ViewGroup viewGroup, List<ChooseMD<?>> childChoiceMDs) {
            // can override
            return true;
        }
    }
}
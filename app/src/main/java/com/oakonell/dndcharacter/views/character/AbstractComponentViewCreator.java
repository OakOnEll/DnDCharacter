package com.oakonell.dndcharacter.views.character;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.AbstractChoiceComponentVisitor;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.model.feat.Feat;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.model.spell.Spell;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.NoDefaultSpinner;
import com.oakonell.dndcharacter.views.character.feat.SelectFeatDialogFragment;
import com.oakonell.dndcharacter.views.character.md.CategoryChoicesMD;
import com.oakonell.dndcharacter.views.character.md.CheckOptionMD;
import com.oakonell.dndcharacter.views.character.md.ChooseMD;
import com.oakonell.dndcharacter.views.character.md.ChooseMDTreeNode;
import com.oakonell.dndcharacter.views.character.md.DropdownOptionMD;
import com.oakonell.dndcharacter.views.character.md.MultipleChoicesMD;
import com.oakonell.dndcharacter.views.character.md.RootChoiceMDNode;
import com.oakonell.dndcharacter.views.character.md.SearchOptionMD;
import com.oakonell.dndcharacter.views.character.spell.SelectSpellDialogFragment;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 11/18/2015.
 */
public class AbstractComponentViewCreator extends AbstractChoiceComponentVisitor {
    private static final String SELECT_FEAT_DIALOG = "select_feat_frag";
    private static final String SELECT_SPELL_DIALOG = "select_spell_frag";
    SavedChoices choices;
    int uiIdCounter;
    ChooseMD<?> currentChooseMD;

    private ViewGroup parent;
    private ChooseMDTreeNode choicesMD = new RootChoiceMDNode();


    private AppCompatActivity activity;
    private int featSearchIndex;

    protected void setParent(ViewGroup parent) {
        this.parent = parent;
    }

    protected ViewGroup getParent() {
        return parent;
    }

    protected void createGroup(String title) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_component_group, parent,false);
        parent.addView(layout);
        parent = layout;

        TextView titleView = new TextView(parent.getContext());
        titleView.setTextAppearance(parent.getContext(), android.support.v7.appcompat.R.style.TextAppearance_AppCompat_Large);
        parent.addView(titleView);
        titleView.setText(title);
    }

    protected void setChoices(SavedChoices savedChoices) {
        this.choices = savedChoices;
    }


    public ChooseMDTreeNode appendToLayout(@NonNull Element element, AppCompatActivity activity, ViewGroup parent, SavedChoices choices) {
        this.parent = parent;
        this.choices = choices;
        this.activity = activity;
        visitChildren(element);
        return choicesMD;
    }

    @Override
    protected void visitFeature(@NonNull Element element) {
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
    protected void visitSkills(@NonNull Element element) {
        ViewGroup oldParent = parent;
        createGroup(parent.getContext().getString(R.string.skill_proficiencies));
        super.visitSkills(element);
        parent = oldParent;
    }

    @Override
    protected void visitSavingThrows(@NonNull Element element) {
        ViewGroup oldParent = parent;
        createGroup(parent.getContext().getString(R.string.saving_throw_proficiencies));
        super.visitSavingThrows(element);
        parent = oldParent;
    }

    @Override
    protected void visitLanguages(@NonNull Element element) {
        ViewGroup oldParent = parent;
        createGroup(parent.getContext().getString(R.string.languages_label));
        super.visitLanguages(element);

        parent = oldParent;
    }

    @Override
    protected void visitTools(@NonNull Element element) {
        ViewGroup oldParent = parent;
        createGroup(parent.getContext().getString(R.string.tool_proficiencies));
        super.visitTools(element);
        parent = oldParent;
    }

    @Override
    protected void visitArmor(@NonNull Element element) {
        ViewGroup oldParent = parent;
        createGroup(parent.getContext().getString(R.string.armor_proficiencies));
        super.visitArmor(element);
        parent = oldParent;
    }

    @Override
    protected void visitWeapons(@NonNull Element element) {
        ViewGroup oldParent = parent;
        createGroup(parent.getContext().getString(R.string.weapons_proficiencies));
        super.visitWeapons(element);
        parent = oldParent;
    }

    @Override
    protected void visitEquipment(@NonNull Element element) {
        ViewGroup oldParent = parent;
        createGroup(parent.getContext().getString(R.string.equipment));
        super.visitEquipment(element);

        parent = oldParent;
    }

    @Override
    protected void visitProficiency(@NonNull Element element) {
        String category = element.getAttribute("category");
        if (category != null && category.trim().length() > 0) {
            TextView text = new TextView(parent.getContext());
            parent.addView(text);
            text.setText(" *  [" + category + "]");

        } else {
            super.visitProficiency(element);
        }
    }

    @Override
    protected void visitStat(@NonNull Element element) {
        ViewGroup oldParent = parent;
        createGroup("Stats");
        super.visitStat(element);

        parent = oldParent;
    }

    @Override
    protected void visitIncrease(@NonNull Element element) {
        TextView text = new TextView(parent.getContext());
        parent.addView(text);
        String string = parent.getResources().getString(R.string.increase_statname_by, element.getAttribute("name"), element.getTextContent());
        text.setText(" *  " + string);
    }

    @Override
    protected void visitSimpleItem(@NonNull Element element) {
        TextView text = new TextView(parent.getContext());
        parent.addView(text);
        String name = element.getTextContent();
        final String countString = element.getAttribute("count");
        if (countString != null && countString.trim().length() > 0) {
            name += " (" + countString + ")";
        }
        text.setText(" *  " + name);
    }

    @Override
    protected void visitChoose(@NonNull Element element) {
        ChooseMD oldChooseMD = currentChooseMD;
        ViewGroup oldParent = parent;

        int numChoices = 1;
        String choiceName = element.getAttribute("name");
        String numChoicesString = element.getAttribute("number");
        if (numChoicesString != null && numChoicesString.trim().length() > 0) {
            numChoices = Integer.parseInt(numChoicesString);
        }
        int minChoices = numChoices;
        String minNumChoicesString = element.getAttribute("minNumber");
        if (minNumChoicesString != null && minNumChoicesString.trim().length() > 0) {
            minChoices = Integer.parseInt(minNumChoicesString);
        }
        List<Element> childOrElems = XmlUtils.getChildElements(element, "or");
        if (childOrElems.size() == 0) {
            currentChooseMD = new CategoryChoicesMD(choiceName, numChoices, minChoices);
            choicesMD.addChildChoice(currentChooseMD);

            // category, context sensitive choices ?
            categoryChoices(element, numChoices);
        } else {
            ViewGroup layout = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_layout, parent, false);
            parent.addView(layout);
            parent = (ViewGroup) layout.findViewById(R.id.choices_view);

            TextView numChoicesTextView = (TextView) layout.findViewById(R.id.num_choices);

            MultipleChoicesMD multipleChoicesMD = new MultipleChoicesMD(numChoicesTextView, choiceName, numChoices, minChoices);
            currentChooseMD = multipleChoicesMD;
            choicesMD.addChildChoice(currentChooseMD);

            numChoicesTextView.setText(NumberUtils.formatNumber(numChoices));

            super.visitChoose(element);
            setCheckedEnabledStates(multipleChoicesMD);
        }


        currentChooseMD = oldChooseMD;
        parent = oldParent;
    }

    protected void categoryChoices(@NonNull Element element, int numChoices) {
        if (state == VisitState.LANGUAGES) {
            visitLanguageCategoryChoices(numChoices);
        } else if (state == VisitState.TOOLS || state == VisitState.EQUIPMENT) {
            visitToolCategoryChoices(element, numChoices);
        } else if (state == VisitState.FEAT) {
            visitFeatSearchChoices(numChoices);
        }
    }


    private void visitToolCategoryChoices(@NonNull Element element, int numChoices) {
        CategoryChoicesMD categoryChoicesMD = (CategoryChoicesMD) currentChooseMD;
        List<String> selections = choices.getChoicesFor(categoryChoicesMD.getChoiceName());

        String category = element.getAttribute("category");
        From nameSelect = new Select()
                .from(ItemRow.class).orderBy("name");
        nameSelect = nameSelect.where("UPPER(category)= ?", category.toUpperCase());
        List<ItemRow> toolRows = nameSelect.execute();

        List<String> tools = new ArrayList<>();
        for (ItemRow each : toolRows) {
            tools.add(each.getName());
        }

        appendCategoryDropDowns(numChoices, categoryChoicesMD, selections, tools, category);
    }

    private void visitLanguageCategoryChoices(int numChoices) {
        // TODO get the list of languages...
        List<String> languages = new ArrayList<>();
        languages.add("Common");
        languages.add("Dwarvish");
        languages.add("Elvish");
        languages.add("Giant");
        languages.add("Gnomish");
        languages.add("Goblin");
        languages.add("Halfling");
        languages.add("Orc");

        languages.add("Abyssal");
        languages.add("Celestial");
        languages.add("Draconic");
        languages.add("Deep speech");
        languages.add("Infernal");
        languages.add("Primordial");
        languages.add("Sylvan");
        languages.add("Undercommon");
        CategoryChoicesMD categoryChoicesMD = (CategoryChoicesMD) currentChooseMD;
        List<String> selections = choices.getChoicesFor(categoryChoicesMD.getChoiceName());

        String languagePrompt = parent.getContext().getString(R.string.language);
        appendCategoryDropDowns(numChoices, categoryChoicesMD, selections, languages, languagePrompt);
        ;
    }

    protected void visitFeatSearchChoices(int numChoices) {
        final int searchResId = R.string.search_for_feat;
        final String fragmentId = SELECT_FEAT_DIALOG;
        final SearchDialogCreator dialogCreator = new SearchDialogCreator() {
            @Override
            AbstractSelectComponentDialogFragment createDialog(final SearchOptionMD optionMD) {
                SelectFeatDialogFragment dialog = SelectFeatDialogFragment.createDialog(new SelectFeatDialogFragment.FeatSelectedListener() {
                    @Override
                    public boolean featSelected(long id) {
                        Feat feat = Feat.load(Feat.class, id);
                        String name = feat.getName();
                        // TODO verify if another feat onSearchClickListener same page is the same...
                        optionMD.setSelected(name);
                        return true;
                    }
                });
                return dialog;
            }
        };

        appendSearches(numChoices, searchResId, fragmentId, dialogCreator);
    }

    public abstract static class SearchDialogCreator {
        abstract AbstractSelectComponentDialogFragment createDialog(SearchOptionMD optionMD);
    }

    protected void visitCantripsSearchChoices(String casterClass, int numChoices) {
        final int searchResId = R.string.search_for_cantrip;
        final String fragmentId = SELECT_SPELL_DIALOG;
        final List<String> casterClasses = new ArrayList<>();
        casterClasses.add(casterClass);
        final SearchDialogCreator dialogCreator = new SearchDialogCreator() {
            @Override
            AbstractSelectComponentDialogFragment createDialog(final SearchOptionMD optionMD) {
                return SelectSpellDialogFragment.createDialog(casterClasses, true, new SelectSpellDialogFragment.SpellSelectedListener() {
                    @Override
                    public boolean spellSelected(long id, String className) {
                        Spell spell = Spell.load(Spell.class, id);
                        String name = spell.getName();
                        // TODO verify if another spell onSearchClickListener same page is the same...
                        optionMD.setSelected(name);
                        return true;
                    }
                });
            }
        };

        appendSearches(numChoices, searchResId, fragmentId, dialogCreator);
    }

    protected void visitSpellSearchChoices(final String casterClass, final int maxLevel, int numChoices) {
        final int searchResId = R.string.search_for_spell;
        final String fragmentId = SELECT_SPELL_DIALOG;
        final List<String> casterClasses = new ArrayList<>();
        casterClasses.add(casterClass);
        final SearchDialogCreator dialogCreator = new SearchDialogCreator() {
            @Override
            AbstractSelectComponentDialogFragment createDialog(final SearchOptionMD optionMD) {
                return SelectSpellDialogFragment.createDialog(casterClass, maxLevel, new SelectSpellDialogFragment.SpellSelectedListener() {
                    @Override
                    public boolean spellSelected(long id, String className) {
                        Spell spell = Spell.load(Spell.class, id);
                        String name = spell.getName();
                        // TODO verify if another spell onSearchClickListener same page is the same...
                        optionMD.setSelected(name);
                        return true;
                    }
                });
            }
        };

        appendSearches(numChoices, searchResId, fragmentId, dialogCreator);
    }

    private void appendSearches(int numChoices, int searchResId, final String fragmentId, final SearchDialogCreator dialogCreator) {
        CategoryChoicesMD categoryChoicesMD = (CategoryChoicesMD) currentChooseMD;
        List<String> selections = choices.getChoicesFor(categoryChoicesMD.getChoiceName());

        for (int i = 0; i < numChoices; i++) {
            ViewGroup layout = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.component_search, parent, false);
            parent.addView(layout);
            final ImageView search = (ImageView) layout.findViewById(R.id.search);
            final TextView text = (TextView) layout.findViewById(R.id.text);
            final ImageView delete = (ImageView) layout.findViewById(R.id.delete);

            final SearchOptionMD optionMD = new SearchOptionMD(categoryChoicesMD, search, text, delete);
            categoryChoicesMD.addOption(optionMD);

            text.setHint(parent.getResources().getString(searchResId));

            final int index = i;
            final View.OnClickListener onSearchClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AbstractSelectComponentDialogFragment dialog = dialogCreator.createDialog(optionMD);
                    // TODO how to relate the current feat index, for the restart set listener
                    featSearchIndex = index;
                    dialog.show(activity.getSupportFragmentManager(), fragmentId);
                }
            };
            if (selections.size() > i) {
                String selected = selections.get(i);
                optionMD.setSelected(selected);
            } else {
                optionMD.resetSelection(onSearchClickListener);
            }

            search.setOnClickListener(onSearchClickListener);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    optionMD.resetSelection(onSearchClickListener);
                }
            });
        }
    }

    private void appendCategoryDropDowns(int numChoices, @NonNull CategoryChoicesMD categoryChoicesMD, @NonNull List<String> savedSelections, @NonNull List<String> choices, @NonNull String prompt) {
        for (int i = 0; i < numChoices; i++) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(parent.getContext(),
                    android.R.layout.simple_spinner_item, choices);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            ViewGroup layout = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.drop_down_layout, parent, false);
            parent.addView(layout);
            Spinner spinner = (Spinner) layout.findViewById(R.id.spinner);
            TextView textView = (TextView) layout.findViewById(R.id.tvInvisibleError);

            spinner.setPrompt("[" + prompt + "]");
            spinner.setAdapter(dataAdapter);
            spinner.setId(++uiIdCounter);
            float minWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (prompt.length() + 2) * NoDefaultSpinner.SPINNER_TEXT_SP, spinner.getResources().getDisplayMetrics());
            spinner.setMinimumWidth((int) minWidth);

            final DropdownOptionMD optionMD = new DropdownOptionMD(categoryChoicesMD, spinner, textView);
            categoryChoicesMD.addOption(optionMD);

            // display saved selection
            int selectedIndex = -1;
            if (savedSelections.size() > i) {
                String selected = savedSelections.get(i);
                selectedIndex = choices.indexOf(selected);
            }
            spinner.setSelection(selectedIndex);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    optionMD.clearError();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @Override
    protected void visitOr(@NonNull Element element) {
        ViewGroup oldParent = parent;

        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.or_layout, parent,false);
        parent.addView(layout);


        String name = element.getAttribute("name");
        CheckBox checkbox = (CheckBox) layout.findViewById(R.id.checkBox);

        final MultipleChoicesMD chooseMD = (MultipleChoicesMD) currentChooseMD;

        // display saved selection state
        List<String> selections = choices.getChoicesFor(chooseMD.getChoiceName());
        checkbox.setChecked(selections.contains(name));
        checkbox.setId(++uiIdCounter);

        final CheckOptionMD optionMD = new CheckOptionMD(chooseMD, checkbox, name);
        chooseMD.addOption(optionMD);

        ChooseMDTreeNode oldCheck = choicesMD;
        choicesMD = optionMD;

        parent = (ViewGroup) layout.findViewById(R.id.or_view);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCheckedEnabledStates(chooseMD);
                onOptionCheckChange(optionMD, isChecked);
            }
        });


        super.visitOr(element);

        choicesMD = oldCheck;
        parent = oldParent;
    }

    protected void onOptionCheckChange(CheckOptionMD optionMD, boolean isChecked) {

    }

    private void setCheckedEnabledStates(@NonNull MultipleChoicesMD chooseMD) {
        chooseMD.getUiLabel().setError(null);
        chooseMD.setEnabled(true);
    }

    protected ChooseMD<?> pushChooseMD(ChooseMD<?> newChooseMD) {
        ChooseMD<?> theCurrentChooseMD = currentChooseMD;
        currentChooseMD = newChooseMD;
        choicesMD.addChildChoice(currentChooseMD);
        return theCurrentChooseMD;
    }

    protected void popChooseMD(ChooseMD<?> oldChooseMD) {
        currentChooseMD = oldChooseMD;
    }

    protected ChooseMD<?> getCurrentChooseMD() {
        return currentChooseMD;
    }

    protected void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public ChooseMDTreeNode getChoicesMD() {
        return choicesMD;
    }

}

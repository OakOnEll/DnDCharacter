package com.oakonell.dndcharacter.views.character.classes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterClass;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.classes.AClass;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.RandomUtils;
import com.oakonell.dndcharacter.utils.SoundFXUtils;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.NoDefaultSpinner;
import com.oakonell.dndcharacter.views.character.AbstractComponentViewCreator;
import com.oakonell.dndcharacter.views.character.ApplyAbstractComponentDialogFragment;
import com.oakonell.dndcharacter.views.character.md.CategoryChoicesMD;
import com.oakonell.dndcharacter.views.character.md.ChooseMD;
import com.oakonell.dndcharacter.views.character.md.ChooseMDTreeNode;
import com.oakonell.dndcharacter.views.character.md.RootChoiceMDNode;
import com.oakonell.dndcharacter.views.character.md.SearchOptionMD;

import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 12/17/2015.
 */
public abstract class AbstractClassLevelEditDialogFragment extends ApplyAbstractComponentDialogFragment<AClass> {
    int hp;

    private final Map<String, SavedChoices> savedChoicesByModel = new HashMap<>();
    private List<AClass> subclasses;
    @Nullable
    private NoDefaultSpinner subclassSpinner;
    private TextView subclassErrorView;
    @Nullable
    private AClass subclass;
    private ChooseMDTreeNode subclassChooseMDs;
    @Nullable
    private EditText hpRoll;
    private int maxHp;
    private TextView classErrorTextView;

    @Nullable
    protected AClass getSubClass() {
        return subclass;
    }

    @NonNull
    protected SavedChoices getSubClassChoices() {
        if (subclass == null) return null;
        SavedChoices result = savedChoicesByModel.get(subclass.getName());
        if (result == null) {
            result = new SavedChoices();
            savedChoicesByModel.put(subclass.getName(), result);
        }
        return result;
    }

    protected void setSubClassName(@Nullable String name, SavedChoices choices) {
        if (name != null) {
            subclass = new Select().from(AClass.class).where("name = ?", name).executeSingle();
            if (subclass != null) {
                savedChoicesByModel.put(name, choices);
            } else {
                // TODO report an error!?
            }
        } else {
            subclass = null;
        }
    }

    @NonNull
    @Override
    protected Class<? extends AClass> getModelClass() {
        return AClass.class;
    }

    @Override
    public String getModelSpinnerPrompt() {
        return getString(R.string.class_spinner_hint);
    }

    protected boolean includeHp() {
        return true;
    }


    @NonNull
    protected List<Page<AClass>> createPages() {
        List<Page<AClass>> pages = new ArrayList<>();
        if (getModel() == null) return pages;

        final Element rootClassElement = XmlUtils.getDocument(getModel().getXml()).getDocumentElement();
        final Element levelElement = AClass.findLevelElement(rootClassElement, getClassLevel());

        final boolean isFirstLevel = isFirstCharacterLevel();
        if (isFirstLevel) {
            // first page, show base skills, saving throws, hit dice..
            Page<AClass> main = new Page<AClass>() {
                @Override
                public ChooseMDTreeNode appendToLayout(@NonNull AClass aClass, @NonNull ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                    addClassErrorTextView(dynamic);
                    addClassLevelTextView(dynamic);

                    AbstractComponentViewCreator visitor = new AbstractComponentViewCreator(getCharacter(), false);
                    Element element = XmlUtils.getDocument(aClass.getXml()).getDocumentElement();
                    return visitor.appendToLayout(element, getMainActivity(), dynamic, backgroundChoices, getCharacterClass());
                }
            };
            pages.add(main);

            final int maxHp = AClass.getHitDieSides(rootClassElement);
            hp = maxHp;

            // page two, show equipment?

        } else {
            hp = getCurrentHpRoll();
        }

        if (subclass == null) {
            String charSubclass = getCharacter().getSubclassFor(getModel().getName());
            if (charSubclass == null) {
                subclass = null;
            } else {
                subclass = new Select().from(getModelClass()).where("name = ?", charSubclass).executeSingle();
            }
        }

        // next page, show level specific stuff (if first, show hit die, or just show again)
        // TODO show when proficiency is increased!
        if (levelElement != null) {
            final Element spellCastingStat = XmlUtils.getElement(rootClassElement, "spellCastingStat");
            Page<AClass> level = new Page<AClass>() {
                @Override
                public ChooseMDTreeNode appendToLayout(@NonNull AClass aClass, @NonNull ViewGroup dynamic, SavedChoices savedChoices, Map<String, String> customChoices) {
                    addClassErrorTextView(dynamic);
                    addClassLevelTextView(dynamic);

                    Element subclassElement = XmlUtils.getElement(levelElement, "subclass");
                    if (subclassElement != null) {
                        String label = subclassElement.getAttribute("label");
                        if (label == null) {
                            label = getString(R.string.archtype_hint);
                        }
                        addSubclassSpinner(label, aClass, dynamic, savedChoices);
                    }

                    AbstractComponentViewCreator visitor = new AbstractComponentViewCreator(getCharacter(), false);
                    final ChooseMDTreeNode chooseMDTreeNode = visitor.appendToLayout(levelElement, getMainActivity(), dynamic, savedChoices, getCharacterClass());

                    return chooseMDTreeNode;
                }
            };
            pages.add(level);

            possiblyAddSpellsAndCantripsPage(pages, rootClassElement, levelElement, spellCastingStat, null);
        }

        if (subclass != null) {
            Element subclassRoot = XmlUtils.getDocument(subclass.getXml()).getDocumentElement();
            final Element subclassLevelElement = AClass.findLevelElement(subclassRoot, getClassLevel());
            if (subclassLevelElement != null) {
                // check if there are any elements to display
                List<Element> childElements = XmlUtils.getChildElements(subclassLevelElement);
                boolean display = false;
                for (Element each : childElements) {
                    String displayChild = each.getAttribute("display");
                    if (displayChild == null || !displayChild.equals("false")) {
                        display = true;
                        break;
                    }
                }
                final Element subclassSpellCastingStat = XmlUtils.getElement(subclassRoot, "spellCastingStat");
                final boolean subclassCaster = subclassSpellCastingStat != null;
                if (display) {
                    Page<AClass> subclassPage = new Page<AClass>() {
                        @NonNull
                        @Override
                        public ChooseMDTreeNode appendToLayout(AClass model, @NonNull ViewGroup dynamic, SavedChoices savedChoices, Map<String, String> customChoices) {
                            addSubclassTextView(dynamic);
                            AbstractComponentViewCreator visitor = new AbstractComponentViewCreator(getCharacter(), !subclassCaster);


                            subclassChooseMDs = visitor.appendToLayout(subclassLevelElement, getMainActivity(), dynamic, getSubClassChoices(), getCharacterClass());
                            return new RootChoiceMDNode();
                        }
                    };
                    pages.add(subclassPage);
                }
                if (subclassCaster) {
                    possiblyAddSpellsAndCantripsPage(pages, subclassRoot, subclassLevelElement, subclassSpellCastingStat, getSubClassChoices());
                }
            }
        }


        if (getCharacter().canChooseAbilityScoreImprovement(getModel(), getClassLevel())) {
            Page<AClass> asiOrFeat = new Page<AClass>() {
                @NonNull
                @Override
                public ChooseMDTreeNode appendToLayout(@NonNull AClass aClass, @NonNull ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                    addClassLevelTextView(dynamic);

                    TextView textView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.subtitle, dynamic, false);
                    textView.setText(getResources().getString(R.string.ability_score_improvement_title));
                    dynamic.addView(textView);


                    InputStream in = null;
                    try {
                        in = getActivity().getAssets().open("asi.xml");
                    } catch (IOException e) {
                        Log.e("ClassLevelEditDialog", "Error parsing asi.xml in assets!", e);
                        throw new RuntimeException("Error parsing asi.xml in assets!", e);
                    }
                    final Element root = XmlUtils.getDocument(in).getDocumentElement();
                    AbstractComponentViewCreator visitor = new AbilityScoreImprovementViewCreator(getCharacter());
                    return visitor.appendToLayout(root, getMainActivity(), dynamic, backgroundChoices, getCharacterClass());
                }
            };
            pages.add(asiOrFeat);
        }

        // final page, show Hit points- unless classLevel = 1
        if (!isFirstLevel && includeHp()) {
            Page<AClass> hitPoints = new Page<AClass>() {
                @NonNull
                @Override
                public ChooseMDTreeNode appendToLayout(@NonNull AClass aClass, @NonNull ViewGroup dynamic, SavedChoices backgroundChoices, Map<String, String> customChoices) {
                    addClassLevelTextView(dynamic);

                    ViewGroup hpView = (ViewGroup) LayoutInflater.from(dynamic.getContext()).inflate(R.layout.level_hitpoints_layout, dynamic);
                    Button rollButton = (Button) hpView.findViewById(R.id.roll_button);
                    TextView hitDiceView = (TextView) hpView.findViewById(R.id.hit_dice);
                    final TextView hp_increase = (TextView) hpView.findViewById(R.id.hp_increase);
                    TextView con_mod = (TextView) hpView.findViewById(R.id.con_mod);
                    hpRoll = (EditText) hpView.findViewById(R.id.roll1);

                    final int conModifier = getCharacter().getStatBlock(StatType.CONSTITUTION).getModifier();
                    con_mod.setText(NumberUtils.formatNumber(conModifier));

                    Element rootClassElement = XmlUtils.getDocument(aClass.getXml()).getDocumentElement();
                    String hitDieString = XmlUtils.getElementText(rootClassElement, "hitDice");
                    hitDiceView.setText(hitDieString);
                    maxHp = AClass.getHitDieSides(rootClassElement);

                    // set the current values
                    hpRoll.setText(NumberUtils.formatNumber(hp));
                    hp_increase.setText(NumberUtils.formatNumber(Math.max(hp + conModifier, 1)));


                    rollButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SoundFXUtils.playDiceRoll(getActivity());

                            int rollValue = RandomUtils.random(1, maxHp);
                            hpRoll.setError(null);
                            hpRoll.setText(NumberUtils.formatNumber(rollValue));
                        }
                    });

                    hpRoll.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            hpRoll.setError(null);
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(@NonNull Editable s) {
                            String string = s.toString();
                            int val = 0;
                            if (string.trim().length() > 0) {
                                val = Integer.parseInt(string);
                            }
                            if (val <= 0 || val > maxHp) {
                                hpRoll.setError(getString(R.string.enter_hp_between_1_and_n, maxHp));
                                Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                                hpRoll.startAnimation(shake);
                            } else {
                                hp = val;
                                hp_increase.setText(NumberUtils.formatNumber(hp + conModifier));
                            }
                        }
                    });

                    return new RootChoiceMDNode();
                }
            };
            pages.add(hitPoints);
        }

        return pages;
    }

    private void possiblyAddSpellsAndCantripsPage(@NonNull List<Page<AClass>> pages, @NonNull final Element rootClassElement, @NonNull Element levelElement, @Nullable Element spellCastingStat, @Nullable final SavedChoices overrideChoices) {
        final Element spells = XmlUtils.getElement(levelElement, "spells");
        final Element cantrips = XmlUtils.getElement(levelElement, "cantrips");

        if (spellCastingStat != null) {
            boolean hasPreviouslyKnownSpells = false;
            Character.CastingClassInfo classInfo = getCharacter().getCasterClassInfoFor(getModel().getName(), getCharacterLevel() - 1);
            if (classInfo != null) {
                final String maxKnownSpellsFormula = classInfo.getKnownSpells();
                if (maxKnownSpellsFormula != null && maxKnownSpellsFormula.length() > 0) {
                    int maxKnown = getCharacter().evaluateFormula(maxKnownSpellsFormula, null);
                    if (maxKnown > 0) {
                        hasPreviouslyKnownSpells = true;
                    }
                }
            }
            if (spells != null || cantrips != null || hasPreviouslyKnownSpells) {
                Page<AClass> spellPage = new Page<AClass>() {
                    @Override
                    public boolean validate(ViewGroup viewGroup, List<ChooseMD<?>> childChoiceMDs) {
                        // can override
                        CategoryChoicesMD replacedSpellMD = null;
                        CategoryChoicesMD replaceWithSpellMD = null;
                        for (ChooseMD<?> each : childChoiceMDs) {
                            if (each.getChoiceName().equals("replace_spell")) {
                                replacedSpellMD = (CategoryChoicesMD) each;
                                continue;
                            }
                            if (each.getChoiceName().equals("new_spell")) {
                                replaceWithSpellMD = (CategoryChoicesMD) each;
                                continue;
                            }
                        }
                        if (replacedSpellMD == null || replaceWithSpellMD == null) {
                            return true;
                        }
                        SearchOptionMD replacedSpellSearchMD = (SearchOptionMD) replacedSpellMD.getOptions().get(0);
                        SearchOptionMD replaceWithSpellSearchMD = (SearchOptionMD) replaceWithSpellMD.getOptions().get(0);

                        if (replacedSpellSearchMD.isPopulated() && replaceWithSpellSearchMD.isPopulated()) {
                            return true;
                        }
                        if (!replacedSpellSearchMD.isPopulated() && !replaceWithSpellSearchMD.isPopulated()) {
                            return true;
                        }
                        if (!replacedSpellSearchMD.isPopulated()) {
                            replacedSpellSearchMD.showRequiredError(viewGroup);
                        }
                        if (!replaceWithSpellSearchMD.isPopulated()) {
                            replaceWithSpellSearchMD.showRequiredError(viewGroup);
                        }
                        return false;
                    }

                    @NonNull
                    @Override
                    public ChooseMDTreeNode appendToLayout(AClass model, @NonNull ViewGroup dynamic, SavedChoices savedChoices, Map<String, String> customChoices) {
                        SpellCastingClassInfoViewCreator visitor = new SpellCastingClassInfoViewCreator(getCharacter());
                        if (overrideChoices != null) {
                            addSubclassTextView(dynamic);
                        }
                        final ChooseMDTreeNode treeNode = visitor.appendToLayout(getMainActivity(), dynamic, getClassLevel(), rootClassElement, spells, cantrips, overrideChoices == null ? savedChoices : overrideChoices, getCharacterClass(), getCharacterLevel());
                        if (overrideChoices != null) {
                            subclassChooseMDs = treeNode;
                            return new RootChoiceMDNode();
                        }

                        return treeNode;
                    }
                };
                pages.add(spellPage);
            }
        }
    }

    protected abstract int getCharacterLevel();


    @Nullable
    protected abstract CharacterClass getCharacterClass();

    @Override
    protected void displayPage() {
        hpRoll = null;
        super.displayPage();
    }

    private void addSubclassSpinner(String label, @NonNull AClass aClass, @NonNull final ViewGroup dynamicView, SavedChoices backgroundChoices) {
        List<String> list = new ArrayList<>();
        From nameSelect = new Select()
                .from(getModelClass()).orderBy("name");
        nameSelect = nameSelect.where("parentClass = ?", aClass.getName());
        subclasses = nameSelect.execute();

        if (subclasses.size() > 0) {

            ViewGroup layout = (ViewGroup) LayoutInflater.from(dynamicView.getContext()).inflate(R.layout.drop_down_layout, dynamicView);
            subclassSpinner = (NoDefaultSpinner) layout.findViewById(R.id.spinner);
            subclassErrorView = (TextView) layout.findViewById(R.id.tvInvisibleError);

            subclassSpinner.setPrompt("[" + label + "]");


            int index = 0;
            int current = -1;
            for (AClass each : subclasses) {
                if (subclass != null && each.getName().equals(subclass.getName())) {
                    current = index;
                }
                list.add(each.getName());
                index++;
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                    R.layout.large_spinner_text, list);
            dataAdapter.setDropDownViewResource(R.layout.large_spinner_text);
            subclassSpinner.setAdapter(dataAdapter);

            subclassSpinner.setSelection(current);

            subclassSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    AClass newModel = subclasses.get(position);
                    if (newModel == subclass) return;
                    subclass = newModel;
                    dynamicView.removeAllViews();

                    String name = subclass.getName();
                    SavedChoices savedChoices = savedChoicesByModel.get(name);
                    if (savedChoices == null) {
                        savedChoices = new SavedChoices();
                        savedChoicesByModel.put(name, savedChoices);
                    }
//                    Map<String, String> customChoices = customChoicesByModel.get(name);
//                    if (customChoices == null) {
//                        customChoices = new HashMap<>();
//                        customChoicesByModel.put(name, customChoices);
//                    }

                    recreatePages();
                    displayPage();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            subclassSpinner.setEnabled(canModifySubclass());

        } else {
            subclassSpinner = null;
            subclass = null;
        }


    }

    private void addSubclassTextView(@NonNull ViewGroup dynamic) {
        TextView textView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.class_level_text, dynamic, false);
        textView.setText(subclass.getName());
        dynamic.addView(textView);
    }

    private void addClassLevelTextView(@NonNull ViewGroup dynamic) {
        TextView textView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.class_level_text, dynamic, false);
        textView.setText(getResources().getString(R.string.level_n, getClassLevel()));
        dynamic.addView(textView);
    }

    private void addClassErrorTextView(@NonNull ViewGroup dynamic) {
        classErrorTextView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.class_error_text, dynamic, false);
        dynamic.addView(classErrorTextView);
        setClassError(classErrorText, classErrorError);
    }

    @Nullable
    String classErrorText;
    boolean classErrorError;

    protected void setClassError(@Nullable String text, boolean error) {
        classErrorText = text;
        classErrorError = error;
        if (classErrorTextView == null) return;

        if (text == null) {
            classErrorTextView.setVisibility(View.GONE);
            classErrorTextView.setText("");
        } else {
            classErrorTextView.setVisibility(View.VISIBLE);
            if (error) {
                classErrorTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            } else {
                classErrorTextView.setTextColor(getResources().getColor(android.R.color.black));
            }
            classErrorTextView.setText(text);
        }
    }

    protected void shakeClassError() {
        if (classErrorTextView == null) return;
        Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        classErrorTextView.startAnimation(shake);
    }

    protected abstract int getCurrentHpRoll();

    protected abstract boolean isFirstCharacterLevel();

    protected abstract int getClassLevel();


    @NonNull
    protected From filter(@NonNull From nameSelect) {
        return nameSelect.where("parentClass is null OR trim(parentClass) = ''");
    }


    @Override
    protected boolean validate(@NonNull ViewGroup dynamicView, int pageIndex) {
        boolean subraceValid = true;
        if (subclassSpinner != null) {
            if (subclassSpinner.getSelectedItemPosition() < 0) {
                subclassErrorView.setError(getString(R.string.choose_subclass_error));
                Animation shake = AnimationUtils.loadAnimation(dynamicView.getContext(), R.anim.shake);
                subclassSpinner.startAnimation(shake);
                subraceValid = false;
            }
        }
        boolean hpRollValid = true;
        if (hpRoll != null) {
            hpRoll.setError(null);
            String string = hpRoll.getText().toString();
            int val = 0;
            if (string.trim().length() > 0) {
                val = Integer.parseInt(string);
            }
            if (val <= 0 || val > maxHp) {
                hpRoll.setError(getString(R.string.enter_hp_between_1_and_n, maxHp));
                Animation shake = AnimationUtils.loadAnimation(dynamicView.getContext(), R.anim.shake);
                hpRoll.startAnimation(shake);
                hpRollValid = false;
            }
        }
        return super.validate(dynamicView, pageIndex) && subraceValid && hpRollValid;
    }

    protected abstract boolean canModifySubclass();

    @Override
    protected void saveChoices(ViewGroup dynamicView) {
        super.saveChoices(dynamicView);
        if (subclass == null) return;
        if (subclassChooseMDs == null) return;
        SavedChoices savedChoices = getSubClassChoices();

        // TODO missing asking the page to saveChoices

        for (ChooseMD each : subclassChooseMDs.getChildChoiceMDs()) {
            each.saveChoice(dynamicView, savedChoices, null);
        }
    }

    protected void modelChanged() {
        super.modelChanged();
        subclass = null;
    }
}

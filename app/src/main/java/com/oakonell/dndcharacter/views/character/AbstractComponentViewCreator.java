package com.oakonell.dndcharacter.views.character;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.oakonell.dndcharacter.model.AbstractComponentVisitor;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ComponentSource;
import com.oakonell.dndcharacter.model.character.FeatureExtensionType;
import com.oakonell.dndcharacter.model.character.Proficient;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.model.character.SpeedType;
import com.oakonell.dndcharacter.model.character.stats.SkillType;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.model.feat.Feat;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.model.item.ItemType;
import com.oakonell.dndcharacter.model.spell.Spell;
import com.oakonell.dndcharacter.model.spell.SpellSchool;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.NoDefaultSpinner;
import com.oakonell.dndcharacter.views.character.feat.SelectFeatDialogFragment;
import com.oakonell.dndcharacter.views.character.md.CategoryChoicesMD;
import com.oakonell.dndcharacter.views.character.md.CategoryOptionMD;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created by Rob on 11/18/2015.
 */
public class AbstractComponentViewCreator extends AbstractChoiceComponentVisitor {
    private static final String SELECT_FEAT_DIALOG = "select_feat_frag";
    private static final String SELECT_SPELL_DIALOG = "select_spell_frag";
    private boolean handleSpells;
    SavedChoices choices;
    int uiIdCounter;
    ChooseMD<?> currentChooseMD;

    private ViewGroup parent;
    private ChooseMDTreeNode choicesMD = new RootChoiceMDNode();


    private AppCompatActivity activity;
    private int featSearchIndex;
    private Character character;
    @Nullable
    private ComponentSource currentComponent;

    @Nullable
    private String spellPrefix = "";
    private int spellIndex;
    private int cantripIndex;
    @NonNull
    Set<String> hardCodedSpells = new HashSet<>();
    @NonNull
    Set<CategoryChoicesMD> chosenSpellMDs = new HashSet<>();
    private ChoiceFilters choiceFilters;

    // feat related stuff
    private LinearLayout featLayout;
    ChooseMDTreeNode featMd;
    private ChooseMDTreeNode featMdOwner;


    public AbstractComponentViewCreator(Character character, boolean handleSpells) {
        this.handleSpells = handleSpells;
        this.character = character;
    }

    public AbstractComponentViewCreator(Character character) {
        this(character, true);
    }

    protected Character getCharacter() {
        return character;
    }

    public SavedChoices getChoices() {
        return choices;
    }

    protected void setParent(ViewGroup parent) {
        this.parent = parent;
    }

    protected ViewGroup getParent() {
        return parent;
    }

    protected void setCurrentComponent(@NonNull ComponentSource currentComponent) {
        this.currentComponent = currentComponent;
    }

    @Nullable
    protected ComponentSource getCurrentComponent() {
        return currentComponent;
    }

    protected void createGroup(String title) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_component_group, parent, false);
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


    public ChooseMDTreeNode appendToLayout(@NonNull Element element, AppCompatActivity activity, ViewGroup parent, SavedChoices choices, ComponentSource currentComponent) {
        this.parent = parent;
        this.choices = choices;
        this.activity = activity;
        this.currentComponent = currentComponent;
        visitChildren(element);
        return choicesMD;
    }

    @Override
    protected void visitFeature(@NonNull Element element) {
        if ("false".equals(element.getAttribute("display"))) return;

        ViewGroup oldParent = parent;
        String name = XmlUtils.getElementText(element, "name");
        String displayName = name;
        String extensionDescription = XmlUtils.getElementText(element, "extensionDescription");
        String description;
        if (extensionDescription == null || extensionDescription.trim().length() == 0) {
            description = XmlUtils.getElementText(element, "shortDescription");
        } else {
            description = extensionDescription;
        }

        String extensionTypeString = element.getAttribute("extension");
        FeatureExtensionType extensionType = null;
        if (extensionTypeString != null && extensionTypeString.trim().length() > 0) {
            extensionType = EnumHelper.stringToEnum(extensionTypeString, FeatureExtensionType.class);
        }

        if (extensionType != null) {
            displayName += "(" + parent.getResources().getString(extensionType.getStringResId()) + ")";
        }

        createGroup("Feature: " + displayName);
        TextView featureText = new TextView(parent.getContext());
        parent.addView(featureText);
        featureText.setText(description);

        boolean oldHandleSpells = handleSpells;
        handleSpells = true;
        String oldSpellPrefix = spellPrefix;
        spellPrefix = name;
        super.visitFeature(element);
        spellPrefix = oldSpellPrefix;
        handleSpells = oldHandleSpells;

        parent = oldParent;
    }

    @Override
    protected void visitAction(@NonNull Element element) {
        String actionName = XmlUtils.getElementText(element, "name");
        String actionDescription = XmlUtils.getElementText(element, "shortDescription");

        TextView effectText = new TextView(parent.getContext());
        parent.addView(effectText);
        effectText.setText(actionName + ": " + actionDescription);
    }

    @Override
    protected void visitEffect(@NonNull Element element) {
        String effectName = element.getAttribute("actionName");
        if (effectName != null) {
            String effectDescription = XmlUtils.getElementText(element, "actionDescription");
            if (effectDescription != null) {
                TextView effectText = new TextView(parent.getContext());
                parent.addView(effectText);
                effectText.setText(effectName + ": " + effectDescription);
            }
        }
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
    protected void visitLanguage(@NonNull Element element) {
        String language = element.getTextContent();
        List<Character.LanguageWithSource> languageWithSources = getCharacter().deriveLanguages();
        StringBuilder knownBy = new StringBuilder();
        for (Character.LanguageWithSource each : languageWithSources) {
            if (!each.getLanguage().equals(language)) continue;
            if (each.getSource() != null && each.getSource().equals(currentComponent)) continue;

            if (knownBy.length() > 0) {
                knownBy.append(",");
            }
            knownBy.append(each.getSourceString(parent.getResources()));
        }
        if (knownBy.length() > 0) {
            TextView text = new TextView(parent.getContext());
            parent.addView(text);
            text.setText(" *  " + language + " (" + parent.getResources().getString(R.string.already_known_from, knownBy) + ")");
            return;
        }

        super.visitLanguage(element);
    }

    @Override
    protected void visitProficiency(@NonNull Element element) {
        String category = element.getAttribute("category");
        String level = element.getAttribute("level");
        Proficient proficiency = null;
        if (level != null && level.trim().length() > 0) {
            proficiency = EnumHelper.stringToEnum(level, Proficient.class);
        }

        if (category != null && category.trim().length() > 0) {
            TextView text = new TextView(parent.getContext());
            parent.addView(text);
            if (proficiency == null) {
                text.setText(" *  [" + category + "]");
            } else {
                text.setText(" *  [" + category + "] - " + parent.getResources().getString(proficiency.getStringResId()));
            }

        } else {
            if (state == VisitState.TOOLS) {
                String toolName = element.getTextContent();
                final List<Character.ToolProficiencyWithSource> toolProficiencies = getCharacter().deriveToolProficiencies(ProficiencyType.TOOL);

                if (toolProficiencies.isEmpty()) {
                    super.visitProficiency(element);
                } else {
                    TextView text = new TextView(parent.getContext());
                    parent.addView(text);
                    String name = element.getTextContent();

                    String alreadyProficientString = null;
                    double maxProfMult = 0;
                    for (Character.ToolProficiencyWithSource each : toolProficiencies) {
                        if (each.getSource() != null && each.getSource().originatesFrom(currentComponent))
                            continue;
                        // TODO handle tool category
                        if (each.getProficiency().getName().equalsIgnoreCase(toolName)) {
                            // TODO what about when the current component was actually changed
                            if (each.getProficiency().getProficient().getMultiplier() > maxProfMult) {
                                alreadyProficientString = parent.getResources().getString(R.string.already_proficient_from, parent.getResources().getString(each.getProficiency().getProficient().getStringResId()), each.getSourceString(parent.getResources()));
                                maxProfMult = each.getProficiency().getProficient().getMultiplier();
                            }
                        }
                    }
                    if (alreadyProficientString != null) {
                        name += " (" + alreadyProficientString + ")";
                    }
                    if (proficiency != null) {
                        name = "[" + parent.getResources().getString(proficiency.getStringResId()) + "]" + name;
                    }
                    text.setText(" *  " + name);
                }


            } else if (state == VisitState.SKILLS) {
                String skillName = element.getTextContent();
                SkillType type = EnumHelper.stringToEnum(skillName, SkillType.class);

                List<Character.ProficientWithSource> proficientWithSources = getCharacter().deriveSkillProciencies(type);
                final boolean isPastChoice = choiceFilters != null ? choiceFilters.filters.contains(type.name().toUpperCase()) : false;
                if (proficientWithSources.isEmpty() && !isPastChoice) {
                    super.visitProficiency(element);

                } else {
                    TextView text = new TextView(parent.getContext());
                    parent.addView(text);
                    String name = element.getTextContent();

                    String alreadyProficientString = null;
                    double maxProfMult = 0;
                    for (Character.ProficientWithSource each : proficientWithSources) {
                        if (each.getSource() != null && each.getSource().equals(currentComponent))
                            continue;
                        // TODO what about when the current component was actually changed
                        if (each.getProficient().getMultiplier() > maxProfMult) {
                            alreadyProficientString = parent.getResources().getString(R.string.already_proficient_from, parent.getResources().getString(each.getProficient().getStringResId()), each.getSourceString(parent.getResources()));
                            maxProfMult = each.getProficient().getMultiplier();
                        }
                    }
                    if (alreadyProficientString == null && isPastChoice) {
                        name += " (" + parent.getResources().getString(R.string.already_proficient_from_prev_choice) + ")";
                    }
                    if (alreadyProficientString != null) {
                        name += " (" + alreadyProficientString + ")";
                    }
                    if (proficiency != null) {
                        name = "[" + parent.getResources().getString(proficiency.getStringResId()) + "]" + name;
                    }
                    text.setText(" *  " + name);
                }

            } else {
                if (proficiency == null) {
                    super.visitProficiency(element);
                } else {
                    String name = element.getTextContent();
                    if (proficiency != null) {
                        name = "[" + parent.getResources().getString(proficiency.getStringResId()) + "]" + name;
                    }
                    TextView text = new TextView(parent.getContext());
                    parent.addView(text);
                    text.setText(" *  " + name);
                }
            }
        }
    }


    @Override
    protected void visitSpells(@NonNull Element element) {
        if (!handleSpells) return;
        ChooseMD oldChooseMD = currentChooseMD;
        ViewGroup oldParent = parent;
        createGroup(parent.getContext().getString(R.string.spells_title));
        spellIndex = 0;
        super.visitSpells(element);

        if (XmlUtils.getChildElements(element).isEmpty()) {
            String casterClass = element.getAttribute("casterClass");
            String numString = element.getAttribute("number");
            int num = 1;
            if (numString != null && numString.trim().length() > 0) {
                num = Integer.parseInt(numString);
            }
            currentChooseMD = new CategoryChoicesMD(spellPrefix + "_spells", num, num);
            choicesMD.addChildChoice(currentChooseMD);
            chosenSpellMDs.add((CategoryChoicesMD) currentChooseMD);

            //TODO find max level...
            int maxlevel = 1;
            boolean limitToRitual = false;
            visitSpellSearchChoices(casterClass, maxlevel, num, null, limitToRitual);
        }

        parent = oldParent;
        currentChooseMD = oldChooseMD;
    }

    @Override
    protected void visitCantrips(@NonNull Element element) {
        if (!handleSpells) return;
        ChooseMD oldChooseMD = currentChooseMD;
        ViewGroup oldParent = parent;
        createGroup(parent.getContext().getString(R.string.cantrips_title));
        cantripIndex = 0;
        super.visitCantrips(element);

        if (XmlUtils.getChildElements(element).isEmpty()) {

            String casterClass = element.getAttribute("casterClass");
            String numString = element.getAttribute("number");
            int num = 1;
            if (numString != null && numString.trim().length() > 0) {
                num = Integer.parseInt(numString);
            }
            currentChooseMD = new CategoryChoicesMD(spellPrefix + "_cantrips", num, num);
            choicesMD.addChildChoice(currentChooseMD);
            chosenSpellMDs.add((CategoryChoicesMD) currentChooseMD);
            visitCantripsSearchChoices(casterClass, num);
        }

        parent = oldParent;
        currentChooseMD = oldChooseMD;
    }

    protected void addChosenSpellMD(CategoryChoicesMD newChooseMD) {
        chosenSpellMDs.add(newChooseMD);
    }

    protected void visitSpell(@NonNull Element element) {
        if (element.getTextContent() == null || element.getTextContent().trim().length() == 0) {
            final CategoryChoicesMD newChooseMD = new CategoryChoicesMD(spellPrefix + "_addedSpell" + spellIndex, 1, 1);
            ChooseMD oldChooseMD = pushChooseMD(newChooseMD);

            chosenSpellMDs.add(newChooseMD);
            String maxLevelString = element.getAttribute("maxLevel");
            int maxLevel = 0;
            if (maxLevelString != null && maxLevelString.trim().length() > 0) {
                maxLevel = Integer.parseInt(maxLevelString);
            }
            String ritualString = element.getAttribute("ritual");
            boolean limitToRitual = "true".equals(ritualString);
            String schoolsString = element.getAttribute("schools");
            String overrideCastClassName = element.getAttribute("casterClass");
            List<SpellSchool> schools = EnumHelper.commaListToEnum(schoolsString, SpellSchool.class);
            // TODO tie in all spells known from this component, _addedSpell{index}.. and others
            visitSpellSearchChoices((overrideCastClassName != null && overrideCastClassName.trim().length() > 0) ? overrideCastClassName : "*", maxLevel, 1, schools, limitToRitual);
            popChooseMD(oldChooseMD);
            spellIndex++;
        } else {
            hardCodedSpells.add(element.getTextContent());
            visitSimpleItem(element);
        }
    }

    @Override
    protected void visitCantrip(@NonNull Element element) {
        if (element.getTextContent() == null || element.getTextContent().trim().length() == 0) {
            final CategoryChoicesMD newChooseMD = new CategoryChoicesMD(spellPrefix + "_addedCantrip" + cantripIndex, 1, 1);
            ChooseMD oldChooseMD = pushChooseMD(newChooseMD);
            chosenSpellMDs.add(newChooseMD);
            //String schoolsString = each.getAttribute("schools");
            String overrideCastClassName = element.getAttribute("casterClass");
            //List<SpellSchool> schools = EnumHelper.commaListToEnum(schoolsString, SpellSchool.class);
            visitCantripsSearchChoices((overrideCastClassName != null && overrideCastClassName.trim().length() > 0) ? overrideCastClassName : "*", 1);
            popChooseMD(oldChooseMD);
            cantripIndex++;
        } else {
            hardCodedSpells.add(element.getTextContent());
            super.visitCantrip(element);
        }
    }

    @Override
    protected void visitItem(@NonNull Element element) {
        String name = element.getTextContent();
        if ("true".equals(element.getAttribute("displayProficiency"))) {
            TextView text = new TextView(parent.getContext());
            parent.addView(text);
            final String countString = element.getAttribute("count");
            String display = name;
            if (countString != null && countString.trim().length() > 0) {
                display += " (" + countString + ")";
            }

            String proficientDisplay = parent.getResources().getString(R.string.not_proficient);
            final ItemRow item = new Select().from(ItemRow.class).where("upper(name) = upper(?)", name).executeSingle();
            if (item != null) {
                ProficiencyType profType = ProficiencyType.TOOL;
                switch (item.getItemType()) {
                    case EQUIPMENT:
                        profType = ProficiencyType.TOOL;
                        break;
                    case ARMOR:
                        profType = ProficiencyType.ARMOR;
                        break;
                    case WEAPON:
                        profType = ProficiencyType.WEAPON;
                        break;
                }

                final boolean isProficient = getCharacter().isProficientWithItem(profType, name, item.getCategory());
                if (isProficient) {
                    proficientDisplay = "";
                }

            }

            text.setText(" *  " + display + proficientDisplay);
        } else {
            if (ItemRow.isPack(name)) {
                final ItemRow itemRow = new Select().from(ItemRow.class).where("upper(name) = ?", name.toUpperCase()).executeSingle();
                if (itemRow != null) {
                    final Element root = XmlUtils.getDocument(itemRow.getXml()).getDocumentElement();
                    final Element equipment = XmlUtils.getElement(root, "equipment");

                    TextView text = new TextView(parent.getContext());
                    parent.addView(text);
                    text.setText("------- " + name + " --------");

                    if (equipment != null) {
                        final List<Element> childItems = XmlUtils.getChildElements(equipment, "item");
                        for (Element each : childItems) {
                            visitItem(each);
                        }
                    }
                } else {
                    TextView text = new TextView(parent.getContext());
                    parent.addView(text);
                    text.setText(" *  " + name + "[?]");
                }
            } else {
                super.visitItem(element);
            }
        }
    }

    @Override
    protected void visitReference(@NonNull Element element) {
        TextView text = new TextView(parent.getContext());
        parent.addView(text);
        String value = element.getTextContent();
        String string = parent.getResources().getString(R.string.reference, value);
        text.setText(" *  " + string);
    }

    @Override
    protected void visitSpeed(@NonNull Element element) {
        String speedTypString = element.getAttribute("type");
        SpeedType speedType = SpeedType.WALK;
        if (speedTypString != null && speedTypString.trim().length() > 0) {
            speedType = EnumHelper.stringToEnum(speedTypString, SpeedType.class);
        }
        TextView text = new TextView(parent.getContext());
        parent.addView(text);
        String value = element.getTextContent();
        String string;
        if (speedType != SpeedType.WALK) {
            string = parent.getResources().getString(R.string.other_speed, getParent().getResources().getString(speedType.getStringResId()), value);
        } else {
            string = parent.getResources().getString(R.string.speed, value);
        }
        text.setText(" *  " + string);
    }

    @Override
    protected void visitInitiative(@NonNull Element element) {
        TextView text = new TextView(parent.getContext());
        parent.addView(text);
        String value = element.getTextContent();
        String string = parent.getResources().getString(R.string.initiative, value);
        text.setText(" *  " + string);
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
        int num = Integer.parseInt(element.getTextContent());

        int value = 0;
        String statName = element.getAttribute("name");
        for (Character.ModifierWithSource each : getCharacter().deriveStat(EnumHelper.stringToEnum(statName, StatType.class))) {
            if (each.getSource() != null && each.getSource().equals(getCurrentComponent())) {
                continue;
            }
            value += each.getModifier();
        }
        int newValue = value + num;

        String string = parent.getResources().getString(R.string.increase_statname_by_to, statName, num, newValue);
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
        String oldSpellPrefix = spellPrefix;

        int numChoices = 1;
        String choiceName = element.getAttribute("name");
        spellPrefix = choiceName;

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

            ChoiceFilters oldChoiceFilters = choiceFilters;
            String filtersString = element.getAttribute("filters");
            choiceFilters = new ChoiceFilters(filtersString).invoke();

            TextView numChoicesTextView = (TextView) layout.findViewById(R.id.num_choices);
            TextView chooseLabelView = (TextView) layout.findViewById(R.id.choose_label);

            String title = element.getAttribute("title");
            if (title != null && title.trim().length() > 0) {
                chooseLabelView.setText(title);
                numChoicesTextView.setVisibility(View.GONE);
            }

            MultipleChoicesMD multipleChoicesMD = new MultipleChoicesMD(numChoicesTextView, choiceName, numChoices, minChoices);
            currentChooseMD = multipleChoicesMD;
            choicesMD.addChildChoice(currentChooseMD);

            numChoicesTextView.setText(NumberUtils.formatNumber(numChoices));

            super.visitChoose(element);
            setCheckedEnabledStates(multipleChoicesMD);
            choiceFilters = oldChoiceFilters;
        }


        currentChooseMD = oldChooseMD;
        parent = oldParent;
        spellPrefix = oldSpellPrefix;
    }

    protected void categoryChoices(@NonNull Element element, int numChoices) {
        if (state == VisitState.LANGUAGES) {
            visitLanguageCategoryChoices(numChoices);
        } else if (state == VisitState.TOOLS || state == VisitState.EQUIPMENT) {
            visitToolCategoryChoices(element, numChoices, ItemType.EQUIPMENT);
        } else if (state == VisitState.ARMOR) {
            visitToolCategoryChoices(element, numChoices, ItemType.ARMOR);
        } else if (state == VisitState.WEAPONS) {
            visitToolCategoryChoices(element, numChoices, ItemType.WEAPON);
        } else if (state == VisitState.FEAT) {
            visitFeatSearchChoices(numChoices);
        } else if (state == VisitState.SKILLS) {
            visitSkillCategoryChoices(element, numChoices);
        }
    }

    private void visitSkillCategoryChoices(@NonNull Element element, int numChoices) {
        CategoryChoicesMD categoryChoicesMD = (CategoryChoicesMD) currentChooseMD;
        List<String> selections = choices.getChoicesFor(categoryChoicesMD.getChoiceName());

        String level = element.getAttribute("level");
        Proficient proficient = Proficient.PROFICIENT;
        if (level != null && level.trim().length() > 0) {
            proficient = EnumHelper.stringToEnum(level, Proficient.class);
        }

        final List<SkillType> list = new ArrayList<>();

        String filtersString = element.getAttribute("filters");
        ChoiceFilters choiceFilters = new ChoiceFilters(filtersString).invoke();
        Set<String> filters = choiceFilters.getFilters();
        boolean filterExisting = choiceFilters.isFilterExisting();

        for (SkillType each : SkillType.values()) {
            // TODO existence check should only include up to the class level before this one, if this is a class
            final double multiplier = character.getSkillBlock(each).getProficiency().getMultiplier();
            boolean exists = multiplier > 0 || filters.contains(each.name().toUpperCase());
            if (selections.contains(each.name())) {
                list.add(each);
                continue;
            }
            // if referencing an existing proficient skill, only add if exists and not already same proficiency level?
            if (filterExisting) {
                if (exists && multiplier < proficient.getMultiplier()) {
                    list.add(each);
                }
            } else if (!exists) {
                list.add(each);
            }
        }

        SpinnerToString<SkillType> toString = new SpinnerToString<SkillType>() {
            @NonNull
            @Override
            public String toString(@NonNull SkillType object) {
                return parent.getResources().getString(object.getStringResId());
            }

            @Override
            public String toSaveString(@NonNull SkillType selection, int index) {
                return selection.name();
            }

            @NonNull
            @Override
            public SkillType fromSavedString(String skillName) {
                SkillType type = EnumHelper.stringToEnum(skillName, SkillType.class);
                return type;
            }
        };

        appendCategoryDropDowns(numChoices, categoryChoicesMD, selections, list, toString, parent.getResources().getString(R.string.choose_skill));
    }


    private void visitToolCategoryChoices(@NonNull Element element, int numChoices, ItemType itemType) {
        CategoryChoicesMD categoryChoicesMD = (CategoryChoicesMD) currentChooseMD;
        List<String> selections = choices.getChoicesFor(categoryChoicesMD.getChoiceName());

        String category = element.getAttribute("category");
        String title = element.getAttribute("title");
        if (title == null || title.trim().length() == 0) {
            title = category;
        }
        if (title == null || title.trim().length() == 0) {
            title = activity.getString(R.string.tool);
        }
        String conditional = element.getAttribute("conditional");
        if (conditional != null && conditional.trim().length() == 0) {
            conditional = null;
        }

        From nameSelect = new Select().from(ItemRow.class).orderBy("name");

        boolean limitToProficientable = false;
        if (state == VisitState.TOOLS) limitToProficientable = true;

        // TODO itemType might be armor!?
        if (category != null && category.trim().length() > 0) {
            if (limitToProficientable) {
                nameSelect = nameSelect.where("canBeProficientIn = ? AND UPPER(category)= ? AND itemType = ?", true, category.toUpperCase(), itemType);
            } else {
                nameSelect = nameSelect.where("UPPER(category)= ? AND itemType = ?", category.toUpperCase(), itemType);
            }
        } else {
            if (limitToProficientable) {
                nameSelect = nameSelect.where("canBeProficientIn = ? AND itemType = ?", true, itemType);
            } else {
                nameSelect = nameSelect.where("itemType = ?", itemType);
            }
        }
        List<ItemRow> toolRows = nameSelect.execute();

        String filtersString = element.getAttribute("filters");
        ChoiceFilters choiceFilters = new ChoiceFilters(filtersString).invoke();
        Set<String> filters = choiceFilters.getFilters();
        boolean filterExisting = choiceFilters.isFilterExisting();


        List<Character.ToolProficiencyWithSource> toolProficiencies = Collections.emptyList();
        if (state == VisitState.TOOLS) {
            toolProficiencies = getCharacter().deriveToolProficiencies(ProficiencyType.TOOL);
        }
        if (state == VisitState.WEAPONS) {
            toolProficiencies = getCharacter().deriveToolProficiencies(ProficiencyType.WEAPON);
        }

        List<String> tools = new ArrayList<>();
        for (ItemRow each : toolRows) {
            if (filters.contains(each.getName().toUpperCase())) continue;

            if (conditional != null) {
                XPathFactory factory = XPathFactory.newInstance();
                XPath xPath = factory.newXPath();
                final Element root = XmlUtils.getDocument(each.getXml()).getDocumentElement();
                try {
                    String result = xPath.evaluate(conditional, root);
                    if (result == null || result.trim().length() == 0) continue;
                } catch (XPathExpressionException e) {
                    Log.e(AbstractComponentViewCreator.class.getSimpleName(), "Error applying conditional to item document:" + e.getMessage());
                }
            }

            if (!toolProficiencies.isEmpty()) {
                boolean isProficient = false;
                for (Character.ToolProficiencyWithSource eachProf : toolProficiencies) {
                    if (eachProf.getSource() != null && (eachProf.getSource().originatesFrom(currentComponent))) {
                        continue;
                    }
                    final String profName = eachProf.getProficiency().getName();
                    if (profName != null && profName.equalsIgnoreCase(each.getName())) {
                        isProficient = true;
                        break;
                    }
                    final String profCategory = eachProf.getProficiency().getCategory();
                    if (profCategory != null && profCategory.equals(each.getCategory())) {
                        isProficient = true;
                        break;
                    }
                }
                if (!isProficient) {
                    tools.add(each.getName());
                }
            } else {
                tools.add(each.getName());
            }
        }

        appendCategoryDropDowns(numChoices, categoryChoicesMD, selections, tools, title);
    }

    private void visitLanguageCategoryChoices(int numChoices) {
        // TODO get the list of languages...
        List<String> allLanguages = new ArrayList<>();
        allLanguages.add("Common");
        allLanguages.add("Dwarvish");
        allLanguages.add("Elvish");
        allLanguages.add("Giant");
        allLanguages.add("Gnomish");
        allLanguages.add("Goblin");
        allLanguages.add("Halfling");
        allLanguages.add("Orc");

        allLanguages.add("Abyssal");
        allLanguages.add("Celestial");
        allLanguages.add("Draconic");
        allLanguages.add("Deep speech");
        allLanguages.add("Infernal");
        allLanguages.add("Primordial");
        allLanguages.add("Sylvan");
        allLanguages.add("Undercommon");
        CategoryChoicesMD categoryChoicesMD = (CategoryChoicesMD) currentChooseMD;
        List<String> selections = choices.getChoicesFor(categoryChoicesMD.getChoiceName());

        List<String> dropdownLanguages = new ArrayList<>();

        Set<String> characterLanguages = character.getLanguages();
        for (String each : allLanguages) {
            if (selections.contains(each) || !characterLanguages.contains(each)) {
                dropdownLanguages.add(each);
            }
        }
        // TODO allow adding languages?
        String languagePrompt = parent.getContext().getString(R.string.language);
        appendCategoryDropDowns(numChoices, categoryChoicesMD, selections, dropdownLanguages, languagePrompt);

    }

    @Override
    protected void visitFeats(@NonNull Element element) {
        featMdOwner = getChoicesMD();
        featLayout = (LinearLayout) LayoutInflater.from(getParent().getContext()).inflate(R.layout.empty_component_group, getParent(), false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        featLayout.setLayoutParams(params);

        super.visitFeats(element);

        getParent().addView(featLayout);
    }

    protected void visitFeatSearchChoices(int numChoices) {
        final int searchResId = R.string.search_for_feat;
        final String fragmentId = SELECT_FEAT_DIALOG;
        final SearchDialogCreator dialogCreator = new SearchDialogCreator() {
            @NonNull
            @Override
            AbstractSelectComponentDialogFragment createDialog(@NonNull final SearchOptionMD optionMD) {
                SelectFeatDialogFragment dialog = SelectFeatDialogFragment.createDialog(new SelectFeatDialogFragment.FeatSelectedListener() {
                    @Override
                    public boolean featSelected(long id) {
                        Feat feat = Feat.load(Feat.class, id);
                        String name = feat.getName();
                        // TODO verify if another feat onSearchClickListener same page is the same...
                        optionMD.setSelected(name);
                        AbstractComponentViewCreator.this.featSelected(feat);
                        return true;
                    }
                });
                return dialog;
            }
        };

        //TODO filter out feats already selected previously
        appendSearches(numChoices, searchResId, fragmentId, dialogCreator);
        // draw dynamic UI.. assuming only a single feat per component for now
        List<String> choices = getChoices().getChoicesFor(getCurrentChooseMD().getChoiceName());
        if (choices.size() == 0) return;
        String featName = choices.get(0);

        Feat feat = new Select().from(Feat.class).where("upper(name) = upper(?)", featName).executeSingle();
        featSelected(feat);
    }

    protected void featSelected(Feat feat) {
        featLayout.removeAllViews();
        if (featMd != null) {
            featMdOwner.getChildChoiceMDs().removeAll(featMd.getChildChoiceMDs());
        }

        AbstractComponentViewCreator visitor = new AbstractComponentViewCreator(getCharacter()) {
            @Override
            protected void visitShortDescription(@NonNull Element element) {
                TextView featureText = new TextView(getParent().getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                featureText.setLayoutParams(params);
                featLayout.addView(featureText);
                featureText.setText(element.getTextContent().trim());
            }
        };
        Element element = XmlUtils.getDocument(feat.getXml()).getDocumentElement();

        featMd = visitor.appendToLayout(element, activity, featLayout, getChoices(), getCurrentComponent());
        featMdOwner.getChildChoiceMDs().addAll(featMd.getChildChoiceMDs());

    }

    public abstract static class SearchDialogCreator {
        @NonNull
        abstract AbstractSelectComponentDialogFragment createDialog(SearchOptionMD optionMD);
    }

    protected void visitCantripsSearchChoices(@NonNull final String casterClass, int numChoices) {
        final int searchResId = R.string.search_for_cantrip;
        final String fragmentId = SELECT_SPELL_DIALOG;
        final SearchDialogCreator dialogCreator = new SearchDialogCreator() {
            @NonNull
            @Override
            AbstractSelectComponentDialogFragment createDialog(@NonNull final SearchOptionMD optionMD) {
                // TODO ignore cantrips from the other choices in the same component's dialog (eg, class/subclass..)
                List<String> knownCantrips = new ArrayList<>();
                for (Character.SpellLevelInfo each : character.getSpellInfos()) {
                    if (each.getLevel() != 0) continue;

                    for (Character.CharacterSpellWithSource eachSpell : each.getSpellInfos()) {
                        if (eachSpell.getSource() == null || !eachSpell.getSource().equals(currentComponent)) {
                            knownCantrips.add(eachSpell.getSpell().getName());
                        }
                    }
                }

                for (String each : hardCodedSpells) {
                    knownCantrips.add(each);
                }
                for (CategoryChoicesMD eachChooseMD : chosenSpellMDs) {
                    for (CategoryOptionMD each : eachChooseMD.getOptions()) {
                        String text = each.getText();
                        if (text != null && text.trim().length() > 0) {
                            knownCantrips.add(text);
                        }
                    }
                }

                return SelectSpellDialogFragment.createDialog(casterClass, 0, null, false, new SelectSpellDialogFragment.SpellSelectedListener() {
                    @Override
                    public boolean spellSelected(long id, String className) {
                        Spell spell = Spell.load(Spell.class, id);
                        String name = spell.getName();
                        // TODO verify if another spell onSearchClickListener same page is the same...
                        optionMD.setSelected(name);
                        return true;
                    }
                }, knownCantrips);
            }
        };

        appendSearches(numChoices, searchResId, fragmentId, dialogCreator);
    }

    protected void visitSpellSearchChoices(@NonNull final String casterClass, final int maxLevel, int numChoices, final List<SpellSchool> schoolNames, final boolean limitToRitual) {
        final int searchResId = R.string.search_for_spell;
        final String fragmentId = SELECT_SPELL_DIALOG;

        final SearchDialogCreator dialogCreator = new SearchDialogCreator() {
            @NonNull
            @Override
            AbstractSelectComponentDialogFragment createDialog(@NonNull final SearchOptionMD optionMD) {
                // TODO ignore spells from the other choices in the same component's dialog (eg, class/subclass..)
                List<String> knownSpells = new ArrayList<>();
                for (Character.SpellLevelInfo each : character.getSpellInfos()) {
                    if (each.getLevel() == 0) continue;

                    for (Character.CharacterSpellWithSource eachSpell : each.getSpellInfos()) {
                        if (eachSpell.getSource() == null || !eachSpell.getSource().equals(currentComponent)) {
                            knownSpells.add(eachSpell.getSpell().getName());
                        }
                    }
                }


                for (String each : hardCodedSpells) {
                    knownSpells.add(each);
                }
                for (CategoryChoicesMD eachChooseMD : chosenSpellMDs) {
                    for (CategoryOptionMD each : eachChooseMD.getOptions()) {
                        String text = each.getText();
                        if (text != null && text.trim().length() > 0) {
                            knownSpells.add(text);
                        }
                    }
                }


                return SelectSpellDialogFragment.createDialog(casterClass, maxLevel, schoolNames, limitToRitual, new SelectSpellDialogFragment.SpellSelectedListener() {
                    @Override
                    public boolean spellSelected(long id, String className) {
                        Spell spell = Spell.load(Spell.class, id);
                        String name = spell.getName();
                        // TODO verify if another spell onSearchClickListener same page is the same...
                        optionMD.setSelected(name);
                        return true;
                    }
                }, knownSpells);
            }
        };


        appendSearches(numChoices, searchResId, fragmentId, dialogCreator);
    }

    private void appendSearches(int numChoices, int searchResId, final String fragmentId, @NonNull final SearchDialogCreator dialogCreator) {
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
            final VisitState theState = state;
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    optionMD.resetSelection(onSearchClickListener);
                    onDelete(optionMD, theState);
                }
            });
        }
    }


    protected void onDelete(SearchOptionMD optionMD, VisitState state) {
        if (state == VisitState.FEAT) {
            featLayout.removeAllViews();
            if (featMd != null) {
                featMdOwner.getChildChoiceMDs().removeAll(featMd.getChildChoiceMDs());
            }
        }
    }


    private void appendCategoryDropDowns(int numChoices, @NonNull CategoryChoicesMD categoryChoicesMD, @NonNull List<String> savedSelections, @NonNull List<String> choices, @NonNull String prompt) {
        appendCategoryDropDowns(numChoices, categoryChoicesMD, savedSelections, choices, null, prompt);
    }

    public static abstract class SpinnerToString<T> {
        @NonNull
        public abstract String toString(T object);

        public abstract String toSaveString(T localizedString, int index);

        @NonNull
        public abstract T fromSavedString(String string);
    }

    private <T> void appendCategoryDropDowns(int numChoices, @NonNull CategoryChoicesMD categoryChoicesMD, @NonNull List<String> savedSelections, @NonNull List<T> choices, @Nullable final SpinnerToString<T> toString, @NonNull String prompt) {
        for (int i = 0; i < numChoices; i++) {
            ArrayAdapter<T> dataAdapter;
            if (toString == null) {
                dataAdapter = new ArrayAdapter<>(parent.getContext(),
                        android.R.layout.simple_spinner_item, choices);
            } else {
                dataAdapter = new ArrayAdapter<T>(parent.getContext(),
                        android.R.layout.simple_spinner_item, choices) {
                    @Nullable
                    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                        return createViewFromResource(position, convertView, parent, android.R.layout.simple_spinner_dropdown_item);
                    }

                    @Nullable
                    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                        return createViewFromResource(position, convertView, parent, android.R.layout.simple_spinner_item);
                    }

                    @Nullable
                    private View createViewFromResource(int position, @Nullable View convertView,
                                                        @NonNull ViewGroup parent, int resource) {
                        View view;
                        TextView text;

                        if (convertView == null) {
                            view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
                        } else {
                            view = convertView;
                        }

                        try {
                            text = (TextView) view;
                        } catch (ClassCastException e) {
                            Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
                            throw new IllegalStateException(
                                    "ArrayAdapter requires the resource ID to be a TextView", e);
                        }

                        T item = getItem(position);
                        text.setText(toString.toString(item));

                        return view;
                    }
                };
            }
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

            final DropdownOptionMD optionMD = new DropdownOptionMD(categoryChoicesMD, spinner, textView, toString);
            categoryChoicesMD.addOption(optionMD);

            // display saved selection
            int selectedIndex = -1;
            if (savedSelections.size() > i) {
                String selected = savedSelections.get(i);
                T selectedObject;
                if (toString != null) {
                    selectedObject = toString.fromSavedString(selected);
                } else {
                    selectedObject = (T) selected;
                }
                selectedIndex = choices.indexOf(selectedObject);
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

        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.or_layout, parent, false);
        parent.addView(layout);


        String name = element.getAttribute("name");
        CheckBox checkbox = (CheckBox) layout.findViewById(R.id.checkBox);

        String title = element.getAttribute("title");
        if (title != null && title.trim().length() > 0) {
            checkbox.setText(title);
        }

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


    private class ChoiceFilters {
        private String filtersString;
        private Set<String> filters;
        private boolean filterExisting;

        public ChoiceFilters(String filtersString) {
            this.filtersString = filtersString;
        }

        public Set<String> getFilters() {
            return filters;
        }

        public boolean isFilterExisting() {
            return filterExisting;
        }

        public ChoiceFilters invoke() {
            filters = new HashSet<>();
            filterExisting = false;
            if (filtersString != null && filtersString.trim().length() > 0) {
                String[] filtersArray = filtersString.split("\\s*,\\s*");
                for (String each : filtersArray) {
                    if (each.equals("$existing")) {
                        filterExisting = true;
                        continue;
                    }
                    List<String> choicesFor = choices.getChoicesFor(each);
                    for (String eachChoice : choicesFor) {
                        // TODO convert the choice string to the SkillType name
                        filters.add(eachChoice.toUpperCase());
                    }
                }
            }
            return this;
        }
    }
}

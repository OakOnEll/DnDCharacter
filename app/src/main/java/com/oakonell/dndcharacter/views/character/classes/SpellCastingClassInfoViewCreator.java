package com.oakonell.dndcharacter.views.character.classes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterClass;
import com.oakonell.dndcharacter.model.character.ComponentType;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.model.spell.SpellSchool;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.character.AbstractComponentViewCreator;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.md.CategoryChoicesMD;
import com.oakonell.dndcharacter.views.character.md.ChooseMD;
import com.oakonell.dndcharacter.views.character.md.ChooseMDTreeNode;
import com.oakonell.dndcharacter.views.character.md.ReplaceSpellSearchOptionMD;
import com.oakonell.dndcharacter.views.character.md.SearchOptionMD;
import com.oakonell.dndcharacter.views.character.spell.SelectKnownSpellDialogFragment;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Rob on 1/24/2016.
 */
public class SpellCastingClassInfoViewCreator extends AbstractComponentViewCreator {

    SpellCastingClassInfoViewCreator(Character character) {
        super(character, false);
    }

    public ChooseMDTreeNode appendToLayout(@NonNull CharacterActivity characterActivity, @NonNull ViewGroup parent, int classLevel, @NonNull Element rootClassElement, @Nullable Element spells, @Nullable Element cantrips, @NonNull SavedChoices savedChoices, CharacterClass charClass, int charLevel) {
        setCurrentComponent(charClass);
        setParent(parent);
        setChoices(savedChoices);
        setActivity(characterActivity);

        Element castingStatElem = XmlUtils.getElement(rootClassElement, "spellCastingStat");
        Element preparedSpellsFormulaElement = XmlUtils.getElement(rootClassElement, "preparedSpellsFormula");
        createGroup("Spell casting");

        ViewGroup mainGroup = getParent();
// TODO localization
        {
            String stat = castingStatElem.getTextContent();
            TextView text = new TextView(mainGroup.getContext());
            mainGroup.addView(text);
            text.setText("Spell casting stat: " + stat);
        }

        if (preparedSpellsFormulaElement != null) {
            String prepared = preparedSpellsFormulaElement.getTextContent();

            TextView text = new TextView(mainGroup.getContext());
            mainGroup.addView(text);
            text.setText("Prepared Spells: " + prepared);
        }

        // TODO this is not quite right??
        String ownerClassName = XmlUtils.getElementText(rootClassElement, "name");
        String parentClass = XmlUtils.getElementText(rootClassElement, "parent");
        if (parentClass != null && parentClass.trim().length() > 0) {
            ownerClassName = parentClass;
        }
        String casterClassName = XmlUtils.getElementText(rootClassElement, "spellCastingSpellClass");
        if (casterClassName == null) {
            casterClassName = ownerClassName;
        }

        handleCantrips(characterActivity, classLevel, cantrips, savedChoices, mainGroup, ownerClassName, casterClassName);
        handleSpells(characterActivity, classLevel, spells, mainGroup, ownerClassName, casterClassName, charLevel);

        return getChoicesMD();
    }

    protected void handleSpells(@NonNull CharacterActivity characterActivity, int classLevel, @Nullable Element spells, ViewGroup mainGroup, String ownerClassName, @Nullable String casterClassName, int charLevel) {
        if (spells == null) return;
//            <known>3</known>
//            <slots>
//            <level value="1">3</level>
//            </slots>

//            createGroup("Spells");
//            ViewGroup spellGroup = getParent();
        ViewGroup spellGroup = mainGroup;

        Element slotsElem = XmlUtils.getElement(spells, "slots");
        // TODO can slots not exist, but other stuff be specifiec
        List<Element> levelElems = XmlUtils.getChildElements(slotsElem, "level");
        int maxLevel = 0;
        for (Element each : levelElems) {
            String levelString = each.getAttribute("value");
            int level = Integer.parseInt(levelString);
            if (level > maxLevel) {
                maxLevel = level;
            }
        }

        Element spellsKnownElem = XmlUtils.getElement(spells, "known");
        List<Element> spellList = XmlUtils.getChildElements(spells, "spell");
        if (spellsKnownElem != null) {
            String known = spellsKnownElem.getTextContent();

            TextView text = new TextView(spellGroup.getContext());
            spellGroup.addView(text);
            text.setText("Spells known: " + known);

            if (casterClassName != null && spellList.isEmpty()) {
                // be real fancy, and find actual difference from latest level specifying known spells?
                int lastLevelSpellsKnown = findLastSpellsKnown(characterActivity.getCharacter(), ownerClassName, classLevel);
                int numSpellsCanAdd = Integer.parseInt(known) - lastLevelSpellsKnown;

                final CategoryChoicesMD newChooseMD = new CategoryChoicesMD("_addedSpells", numSpellsCanAdd, numSpellsCanAdd);
                ChooseMD oldChooseMD = pushChooseMD(newChooseMD);
                addChosenSpellMD(newChooseMD);

                boolean limitToRitual = false;
                visitSpellSearchChoices(casterClassName, maxLevel, numSpellsCanAdd, null, limitToRitual);
                // TODO display if this known spell was replaced in a later level
                popChooseMD(oldChooseMD);
            }

        }

        // TODO support replacing spells.. some issues
        // addReplaceKnownSpell(spellGroup, casterClassName, charLevel, maxLevel);

        for (Element each : levelElems) {
            String level = each.getAttribute("value");
            String slots = each.getTextContent();

            TextView text = new TextView(spellGroup.getContext());
            spellGroup.addView(text);

            text.setText("Level " + level + " slots: " + slots);
        }

        if (spellList.size() > 0) {
            TextView text = new TextView(spellGroup.getContext());
            spellGroup.addView(text);
            text.setText("Spells: ");

            int i = 0;
            for (Element each : spellList) {
                if (each.getTextContent() == null || each.getTextContent().trim().length() == 0) {
                    final CategoryChoicesMD newChooseMD = new CategoryChoicesMD("_addedSpell" + i, 1, 1);
                    ChooseMD oldChooseMD = pushChooseMD(newChooseMD);
                    addChosenSpellMD(newChooseMD);
                    String schoolsString = each.getAttribute("schools");
                    String overrideCastClassName = each.getAttribute("casterClass");
                    List<SpellSchool> schools = EnumHelper.commaListToEnum(schoolsString, SpellSchool.class);
                    String ritualString = each.getAttribute("ritual");
                    boolean limitToRitual = "true".equals(ritualString);
                    visitSpellSearchChoices((overrideCastClassName != null && overrideCastClassName.trim().length() > 0) ? overrideCastClassName : casterClassName, maxLevel, 1, schools, limitToRitual);
                    popChooseMD(oldChooseMD);
                } else {
                    visitSpell(each);
//                        TextView spellText = new TextView(spellGroup.getContext());
//                        spellGroup.addView(spellText);
//                        spellText.setText(" * " + each.getTextContent());
                }
                i++;
            }
        }

        setParent(mainGroup);

    }

    private void addReplaceKnownSpell(ViewGroup spellGroup, final String casterClassName, final int charLevel, final int maxSpellLevel) {
        Character.CastingClassInfo classInfo = getCharacter().getCasterClassInfoFor(casterClassName, charLevel - 1);
        if (classInfo == null) return;
        final String maxKnownSpellsFormula = classInfo.getKnownSpells();
        if (maxKnownSpellsFormula == null || maxKnownSpellsFormula.length() == 0) return;

        int maxKnown = getCharacter().evaluateFormula(maxKnownSpellsFormula, null);
        if (maxKnown == 0) return;

        TextView labelText = new TextView(spellGroup.getContext());
        spellGroup.addView(labelText);
        labelText.setText("Optionally replace a previously known spell");


        ChooseMD<?> old = pushChooseMD(new CategoryChoicesMD("replace_spell", 1, 0));
        final SearchDialogCreator dialogCreator = new SearchDialogCreator() {
            @NonNull
            @Override
            public SelectKnownSpellDialogFragment createDialog(@NonNull final SearchOptionMD optionMD) {
                List<SelectKnownSpellDialogFragment.KnownClassSpell> knownSpells = getKnownCharacterSpells(casterClassName, charLevel);

                return SelectKnownSpellDialogFragment.createDialog(knownSpells, new SelectKnownSpellDialogFragment.SpellSelectedListener() {
                    @Override
                    public boolean spellSelected(SelectKnownSpellDialogFragment.KnownClassSpell spell) {
                        // current component could be null, eg on an add :(
                        optionMD.setSelected(spell.levelKnown + "," + spell.levelIndex, getCharacter(), casterClassName);
                        return true;
                    }
                });
            }
        };

        appendSearches(1, R.string.replace_spell, "replace_frag_id", dialogCreator, new SearchOptionMDCreator() {
            @Override
            public SearchOptionMD createSearchOptionMD(CategoryChoicesMD categoryChoicesMD, ImageView search, TextView text, ImageView delete) {
                return new ReplaceSpellSearchOptionMD(categoryChoicesMD, search, text, delete);
            }
        }, casterClassName);
        popChooseMD(old);

        old = pushChooseMD(new CategoryChoicesMD("new_spell", 1, 0));
        visitSpellSearchChoices(casterClassName, maxSpellLevel, 1, null, false);
        popChooseMD(old);

    }

    private List<SelectKnownSpellDialogFragment.KnownClassSpell> getKnownCharacterSpells(String casterClassName, int charLevel) {
        List<SelectKnownSpellDialogFragment.KnownClassSpell> spells = new ArrayList<>();

        for (Character.SpellLevelInfo each : getCharacter().getSpellInfos()) {
            int index = 0;
            for (Character.CharacterSpellWithSource spell : each.getSpellInfos()) {
                if (spell.getSource().getType() != ComponentType.CLASS) continue;
                CharacterClass charClass = (CharacterClass) spell.getSource();
                if (!charClass.getName().equals(casterClassName)) continue;
                if (charClass.getLevel() >= charLevel) continue;

                SelectKnownSpellDialogFragment.KnownClassSpell knownSpell = new SelectKnownSpellDialogFragment.KnownClassSpell();
                knownSpell.spellName = spell.getSpell().getName();
                knownSpell.spellLevel = spell.getSpell().getLevel();
                knownSpell.school = spell.getSpell().getSchool();
                knownSpell.isRitual = spell.getSpell().isRitual();
                knownSpell.levelKnown = charClass.getLevel();
                knownSpell.levelIndex = index;
                spells.add(knownSpell);
                index++;
            }
        }


        return spells;
    }

    protected void handleCantrips(@NonNull CharacterActivity characterActivity, int classLevel, @Nullable Element cantrips, @NonNull SavedChoices savedChoices, @NonNull ViewGroup mainGroup, String ownerClassName, @Nullable String casterClassName) {
        if (cantrips == null) return;


        ViewGroup spellGroup = mainGroup;


        Element cantripsKnownElem = XmlUtils.getElement(cantrips, "known");
        List<Element> cantripsList = XmlUtils.getChildElements(cantrips, "cantrip");
        if (cantripsKnownElem != null) {
            String known = cantripsKnownElem.getTextContent();

            TextView text = new TextView(mainGroup.getContext());
            mainGroup.addView(text);
            text.setText("Cantrips known: " + known);

            if (casterClassName != null && cantripsList.isEmpty()) {
                // be real fancy, and find actual difference from latest level specifying known cantrips?
                int lastLevelCantripsKnown = findLastCantripsKnown(characterActivity.getCharacter(), ownerClassName, classLevel);
                int numCantripsCanAdd = Integer.parseInt(known) - lastLevelCantripsKnown;

                final CategoryChoicesMD newChooseMD = new CategoryChoicesMD("_addedCantrips", numCantripsCanAdd, numCantripsCanAdd);
                ChooseMD oldChooseMD = pushChooseMD(newChooseMD);
                addChosenSpellMD(newChooseMD);

                visitCantripsSearchChoices(casterClassName, numCantripsCanAdd);
                popChooseMD(oldChooseMD);
            }
        }


        if (cantripsList.size() > 0) {
            TextView text = new TextView(spellGroup.getContext());
            spellGroup.addView(text);
            text.setText("Cantrips: ");

            int i = 0;
            for (Element each : cantripsList) {
                if (each.getTextContent() == null || each.getTextContent().trim().length() == 0) {
                    final CategoryChoicesMD newChooseMD = new CategoryChoicesMD("_addedCantrip" + i, 1, 1);
                    ChooseMD oldChooseMD = pushChooseMD(newChooseMD);
                    addChosenSpellMD(newChooseMD);

                    //String schoolsString = each.getAttribute("schools");
                    String overrideCastClassName = each.getAttribute("casterClass");
                    //List<SpellSchool> schools = EnumHelper.commaListToEnum(schoolsString, SpellSchool.class);
                    visitCantripsSearchChoices((overrideCastClassName != null && overrideCastClassName.trim().length() > 0) ? overrideCastClassName : casterClassName, 1);
                    popChooseMD(oldChooseMD);
                } else {
                    visitCantrip(each);
//                        TextView spellText = new TextView(spellGroup.getContext());
//                        spellGroup.addView(spellText);
//                        spellText.setText(" * " + each.getTextContent());
                }
                i++;
            }
        }
    }


    private int findLastCantripsKnown(@NonNull Character character, String className, int classLevel) {
        final List<CharacterClass> classes = new ArrayList<>(character.getClasses());
        Collections.reverse(classes);
        for (CharacterClass each : classes) {
            if (each.getName().equals(className) && each.getLevel() < classLevel) {
                if (each.getCantripsKnownFormula() == null || each.getCantripsKnownFormula().trim().length() == 0) {
                    continue;
                }
                return character.evaluateFormula(each.getCantripsKnownFormula(), null);
            }
        }
        return 0;
    }

    private int findLastSpellsKnown(@NonNull Character character, String className, int classLevel) {
        final List<CharacterClass> classes = new ArrayList<>(character.getClasses());
        Collections.reverse(classes);
        for (CharacterClass each : classes) {
            if (each.getName().equals(className) && each.getLevel() < classLevel) {
                if (each.getSpellsKnownFormula() == null || each.getSpellsKnownFormula().trim().length() == 0) {
                    continue;
                }
                return character.evaluateFormula(each.getSpellsKnownFormula(), null);
            }
        }
        return 0;
    }

}

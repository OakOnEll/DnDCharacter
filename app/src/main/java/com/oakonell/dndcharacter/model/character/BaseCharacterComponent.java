package com.oakonell.dndcharacter.model.character;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.oakonell.dndcharacter.model.character.spell.CharacterSpell;
import com.oakonell.dndcharacter.model.character.stats.SkillType;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.components.Feature;
import com.oakonell.dndcharacter.model.components.Proficiency;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.expression.context.SimpleVariableContext;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 10/24/2015.
 */
public abstract class BaseCharacterComponent {
    @Element(required = false)
    private SavedChoices savedChoices = new SavedChoices();
    @NonNull
    @ElementMap(entry = "statMod", key = "name", value = "mod", required = false)
    private Map<StatType, Integer> statModifiers = new HashMap<>();
    @NonNull
    @ElementMap(entry = "skill", key = "name", value = "proficiency", required = false)
    private Map<SkillType, Proficient> skillProficiencies = new HashMap<>();
    @NonNull
    @ElementMap(entry = "saveMod", key = "name", value = "proficiency", required = false)
    private Map<StatType, Proficient> saveProficiencies = new HashMap<>();
    @NonNull
    @ElementList(required = false)
    private List<Feature> features = new ArrayList<>();
    @NonNull
    @ElementList(required = false)
    private List<String> languages = new ArrayList<>();
    @NonNull
    @ElementMap(entry = "tool", key = "type", value = "proficiency", required = false)
    private Map<ProficiencyType, ToolProficiencies> toolProficiencies = new HashMap<>();
    @Element(required = false)
    private String name;
    @Element(required = false)
    private String acFormula;
    @Element(required = false)
    private String activeFormula;
    @NonNull
    @ElementList(required = false)
    private List<CharacterSpell> cantrips = new ArrayList<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatModifier(StatType type) {
        Integer value = statModifiers.get(type);
        if (value == null) return 0;
        return value;
    }

    public void addModifier(StatType type, int modifier) {
        statModifiers.put(type, modifier);
    }

    public void addSkill(SkillType type, Proficient proficient) {
        skillProficiencies.put(type, proficient);
    }

    public void addSaveThrow(StatType type, Proficient proficient) {
        saveProficiencies.put(type, proficient);
    }

    public Proficient getSkillProficient(SkillType type) {
        Proficient proficient = skillProficiencies.get(type);
        if (proficient == null) return Proficient.NONE;
        return proficient;
    }

    public Proficient getSaveProficient(StatType type) {
        Proficient proficient = saveProficiencies.get(type);
        if (proficient == null) return Proficient.NONE;
        return proficient;
    }

    public abstract ComponentType getType();

    public void addFeature(Feature feature) {
        features.add(feature);
    }

    @NonNull
    public List<FeatureInfo> getFeatures() {
        List<FeatureInfo> result = new ArrayList<>();
        for (Feature each : features) {
            FeatureInfo info = new FeatureInfo();
            info.feature = each;
            info.source = this;
            result.add(info);
        }
        return result;
    }

    @NonNull
    public String getSourceString() {
        return getType().toString() + ": " + getName();
    }

    @NonNull
    public List<String> getLanguages() {
        return languages;
    }

    public SavedChoices getSavedChoices() {
        return savedChoices;
    }

    public void setSavedChoices(SavedChoices savedChoices) {
        this.savedChoices = savedChoices;
    }

    public void addToolProficiency(ProficiencyType type, String name, Proficient proficient) {
        Proficiency proficiency = new Proficiency(type, name, null, proficient);
        ToolProficiencies wrappedList = toolProficiencies.get(type);
        if (wrappedList == null) {
            wrappedList = new ToolProficiencies();
            toolProficiencies.put(type, wrappedList);
        }
        wrappedList.proficiencies.add(proficiency);
    }

    public void addToolCategoryProficiency(ProficiencyType type, String category, Proficient proficient) {
        Proficiency proficiency = new Proficiency(type, null, category, proficient);
        ToolProficiencies wrappedList = toolProficiencies.get(type);
        if (wrappedList == null) {
            wrappedList = new ToolProficiencies();
            toolProficiencies.put(type, wrappedList);
        }
        wrappedList.proficiencies.add(proficiency);
    }

    @NonNull
    public List<Proficiency> getToolProficiencies(ProficiencyType type) {
        ToolProficiencies wrappedList = toolProficiencies.get(type);
        if (wrappedList == null) {
            return Collections.emptyList();
        }
        return wrappedList.proficiencies;
    }

    public void addExtraFormulaVariables(SimpleVariableContext extraVariables) {
        // do nothing, let subclasses override
    }

    public String getActiveFormula() {
        return activeFormula;
    }

    public void setActiveFormula(String activeFormula) {
        this.activeFormula = activeFormula;
    }

    public String getAcFormula() {
        return acFormula;
    }

    public void setAcFormula(String acFormula) {
        this.acFormula = acFormula;
    }

    public boolean isBaseArmor() {
        String formula = getAcFormula();
        if (formula == null) return false;
        return formula.startsWith("=");
    }

    @Nullable
    public String getBaseAcFormula() {
        if (!isBaseArmor()) return null;
        String formula = getAcFormula();
        if (formula == null) return null;
        return formula.substring(1);
    }

    @Nullable
    public String getModifyingAcFormula() {
        if (isBaseArmor()) return null;
        String formula = getAcFormula();
        if (formula == null) return null;

        return formula;
    }

    @NonNull
    public List<CharacterSpell> getCantrips() {
        return cantrips;
    }

    // use a wrapper for SimpleXML serialization
    public static class ToolProficiencies {
        @NonNull
        @ElementList(required = false, type = Proficiency.class, inline = true)
        List<Proficiency> proficiencies = new ArrayList<>();
    }
}
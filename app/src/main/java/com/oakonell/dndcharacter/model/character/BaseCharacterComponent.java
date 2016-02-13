package com.oakonell.dndcharacter.model.character;

import android.content.res.Resources;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 10/24/2015.
 */
public abstract class BaseCharacterComponent implements ICharacterComponent {
    @Element(required = false)
    private String description;
    @Element(required = false)
    private SavedChoices savedChoices = new SavedChoices();
    @NonNull
    @ElementMap(entry = "statMod", key = "name", value = "mod", required = false)
    private Map<StatType, Integer> statModifiers = new HashMap<>();
    @NonNull
    @ElementMap(entry = "skill", key = "name", value = "proficiency", required = false)
    private Map<SkillType, Proficient> skillProficiencies = new HashMap<>();
    @NonNull
    @ElementMap(entry = "speed", key = "type", value = "value", required = false)
    Map<SpeedType, String> speeds = new HashMap<>();
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
    @Element(required = false)
    private String hpFormula;
    @Element(required = false)
    private String initiativeModFormula;
    @ElementList(required = false)
    private List<CharacterSpell> spells = new ArrayList<>();
    @Element(required = false)
    private String passivePerceptionModFormula;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
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

    @Override
    public Proficient getSkillProficient(SkillType type) {
        Proficient proficient = skillProficiencies.get(type);
        if (proficient == null) return Proficient.NONE;
        return proficient;
    }

    @Override
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
    public Collection<FeatureInfo> getFeatures(Character character) {
        Map<String, FeatureInfo> map = new HashMap<>();
        addFeatureInfo(map, character);
        return new ArrayList<>(map.values());
    }

    public void addFeatureInfo(Map<String, FeatureInfo> map, Character character) {
        for (Feature each : features) {
            if (!each.applies(character)) continue;
            FeatureExtensionType type = each.getExtensionType();
            if (type == null) {
                FeatureInfo info = new FeatureInfo(each, this);
                FeatureInfo existing = map.put(each.getName(), info);
//                if (existing != null) {
//                    throw new RuntimeException("Found a pre-existing feature '" + each.getName() + "' from " + existing.getSource() + ", while trying to add it for " + toString());
//                }
            } else if (type == FeatureExtensionType.EXTEND) {
                FeatureInfo existing = map.get(each.getName());
                FeatureInfo info = new FeatureInfo(each, this, existing);
                map.put(each.getName(), info);
            } else if (type == FeatureExtensionType.REPLACE) {
                FeatureInfo info = new FeatureInfo(each, this);
                map.put(each.getName(), info);
            }
        }
    }


    @NonNull
    public String getSourceString(Resources resources) {
        return resources.getString(getType().getStringResId()) + ": " + getName();
    }

    @Override
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

    @Override
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

    @Override
    public String getAcFormula() {
        return acFormula;
    }

    public void setAcFormula(String acFormula) {
        this.acFormula = acFormula;
    }

    @Override
    public String getHpFormula() {
        return hpFormula;
    }

    public void setHpFormula(String hpFormula) {
        this.hpFormula = hpFormula;
    }


    @Override
    public boolean isBaseArmor() {
        String formula = getAcFormula();
        if (formula == null) return false;
        return formula.startsWith("=");
    }

    @Override
    @Nullable
    public String getBaseAcFormula() {
        if (!isBaseArmor()) return null;
        String formula = getAcFormula();
        if (formula == null) return null;
        return formula.substring(1);
    }

    @Override
    @Nullable
    public String getModifyingAcFormula() {
        if (isBaseArmor()) return null;
        String formula = getAcFormula();
        if (formula == null) return null;

        return formula;
    }

    @Override
    @NonNull
    public List<CharacterSpell> getCantrips() {
        return cantrips;
    }


    public void setSpeedFormula(SpeedType speedType, String valueString) {
        speeds.put(speedType, valueString);
    }

    @Override
    public int getSpeed(Character character, SpeedType type) {
        final String val = speeds.get(type);
        if (val == null || val.trim().length() == 0) return 0;
        return character.evaluateFormula(val, null);
    }

    public List<CharacterSpell> getSpells() {
        return spells;
    }


    // use a wrapper for SimpleXML serialization
    public static class ToolProficiencies {
        @NonNull
        @ElementList(required = false, type = Proficiency.class, inline = true)
        List<Proficiency> proficiencies = new ArrayList<>();
    }

    public String getInitiativeModFormula() {
        return initiativeModFormula;
    }
    @Override
    public int getInitiativeMod(Character character) {
        return character.evaluateFormula(initiativeModFormula, null);
    }

    public void setInitiativeModFormula(String initiativeModFormula) {
        this.initiativeModFormula = initiativeModFormula;
    }
    public String getPassivePerceptionModFormula() {
        return passivePerceptionModFormula;
    }

    @Override
    public int getPassivePerceptionMod(Character character) {
        return character.evaluateFormula(passivePerceptionModFormula, null);
    }

    public void setPassivePerceptionModFormula(String passivePerceptionModFormula) {
        this.passivePerceptionModFormula = passivePerceptionModFormula;
    }
}
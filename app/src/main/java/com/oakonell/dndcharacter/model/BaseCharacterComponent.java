package com.oakonell.dndcharacter.model;

import com.oakonell.dndcharacter.model.components.Feature;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 10/24/2015.
 */
public abstract class BaseCharacterComponent {
    @ElementMap(entry = "statMod", key = "name",value="mod", required = false)
    private Map<StatType, Integer> statModifiers = new HashMap<StatType, Integer>();

    @ElementMap(entry = "skill", key = "name",value="proficiency", required = false)
    private Map<SkillType, Proficient> skillProficiencies = new HashMap<SkillType, Proficient>();
    @ElementMap(entry = "saveMod", key = "name",value="proficiency", required = false)
    private Map<StatType, Proficient> saveProficiencies = new HashMap<StatType, Proficient>();
    @ElementList(required = false)
    private List<Feature> features = new ArrayList<Feature>();

    @Element
    private String name;


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

    public List<FeatureInfo> getFeatures() {
        List<FeatureInfo> result = new ArrayList<FeatureInfo>();
        for (Feature each : features) {
            FeatureInfo info = new FeatureInfo();
            info.feature = each;
            info.source = this;
            result.add(info);
        }
        return result;
    }

    public String getSourceString() {
        return getType().toString() + ": " + getName();
    }

}
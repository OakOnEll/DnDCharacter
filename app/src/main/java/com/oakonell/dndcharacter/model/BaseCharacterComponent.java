package com.oakonell.dndcharacter.model;

import com.oakonell.dndcharacter.model.components.Feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 10/24/2015.
 */
public abstract class BaseCharacterComponent {
    private Map<StatType, Integer> statModifiers = new HashMap<StatType, Integer>();
    private Map<SkillType, Proficient> skillModifiers = new HashMap<SkillType, Proficient>();
    private Map<StatType, Proficient> saveModifiers = new HashMap<StatType, Proficient>();

    private List<Feature> features = new ArrayList<Feature>();

    public int getStatModifier(StatType type) {
        Integer value = statModifiers.get(type);
        if (value == null) return 0;
        return value;
    }

    public void addModifier(StatType type, int modifier) {
        statModifiers.put(type, modifier);
    }

    public void addSkill(SkillType type, Proficient proficient) {
        skillModifiers.put(type, proficient);
    }

    public void addSaveThrow(StatType type, Proficient proficient) {
        saveModifiers.put(type, proficient);
    }

    public Proficient getSkillProficient(SkillType type) {
        Proficient proficient = skillModifiers.get(type);
        if (proficient == null) return Proficient.NONE;
        return proficient;
    }


    public Proficient getSaveProficient(StatType type) {
        Proficient proficient = saveModifiers.get(type);
        if (proficient == null) return Proficient.NONE;
        return proficient;
    }

    public abstract String getName();

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
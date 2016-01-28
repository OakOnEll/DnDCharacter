package com.oakonell.dndcharacter.model.character.spell;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.model.character.ComponentType;
import com.oakonell.dndcharacter.model.character.stats.StatType;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 1/13/2016.
 */
public class CharacterSpell extends BaseCharacterComponent {
    @Element(required = false)
    private String casterClass;
    @Element(required = false)
    private StatType castingStat;
    @Element(required = false)
    private ComponentType source;


    @Element(required = false)
    private int level;
    @Element(required = false)
    private String school;

    @Element(required = false)
    private SpellRange rangeType;
    @Element(required = false)
    private int range;
    @Element(required = false)
    private int numberTargets;


    @Element(required = false)
    private CastingTimeType castingType;
    @Element(required = false)
    private String castingTime;

    @Element(required = false)
    private SpellDurationType durationType;
    @Element(required = false)
    private int durationTime;

    // components
    @Element(required = false)
    private boolean usesVerbal;
    @Element(required = false)
    private boolean usesSemantic;
    @Element(required = false)
    private boolean usesMaterial;
    @Element(required = false)
    private String component;


    @Element(required = false)
    private boolean concentration;
    @Element(required = false)
    private boolean ritual;


    @Element(required = false)
    private SpellAttackType attackType;

    // need this to be per cast level
//    @Element(required = false)
//    private String damageFormula;


    // if the target is self, can apply the effect..
    @NonNull
    @ElementList(required = false)
    private List<CharacterEffect> effects = new ArrayList<>();

    @Element(required = false)
    boolean preparable;
    @Element(required = false)
    boolean prepared;

    public void setPreparable(boolean preparable) {
        this.preparable = preparable;
    }

    public void setPrepared(boolean prepared) {
        this.prepared = prepared;
    }

    public boolean isPreparable() {
        return preparable;
    }

    public boolean isPrepared() {
        return prepared;
    }


    @NonNull
    @Override
    public ComponentType getType() {
        return ComponentType.SPELL;
    }

    public void addEffect(CharacterEffect characterEffect) {
        effects.add(characterEffect);
    }

    @NonNull
    public List<CharacterEffect> getEffects() {
        return effects;
    }


    public String getSchool() {
        return school;
    }

    public SpellRange getRangeType() {
        return rangeType;
    }

    public int getRange() {
        return range;
    }

    public int getNumberTargets() {
        return numberTargets;
    }

    public CastingTimeType getCastingType() {
        return castingType;
    }

    public String getCastingTime() {
        return castingTime;
    }

    public SpellDurationType getDurationType() {
        return durationType;
    }

    public int getDurationTime() {
        return durationTime;
    }

    public boolean isUsesVerbal() {
        return usesVerbal;
    }

    public boolean isUsesSemantic() {
        return usesSemantic;
    }

    public boolean isUsesMaterial() {
        return usesMaterial;
    }

    public String getComponent() {
        return component;
    }

    public boolean isConcentration() {
        return concentration;
    }

    public boolean isRitual() {
        return ritual;
    }

    public SpellAttackType getAttackType() {
        return attackType;
    }

    public int getLevel() {
        return level;
    }


    // setter

    public void setLevel(int level) {
        this.level = level;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setRangeType(SpellRange rangeType) {
        this.rangeType = rangeType;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setNumberTargets(int numberTargets) {
        this.numberTargets = numberTargets;
    }

    public void setCastingType(CastingTimeType castingType) {
        this.castingType = castingType;
    }

    public void setCastingTime(String castingTime) {
        this.castingTime = castingTime;
    }

    public void setDurationType(SpellDurationType durationType) {
        this.durationType = durationType;
    }

    public void setDurationTime(int durationTime) {
        this.durationTime = durationTime;
    }

    public void setUsesVerbal(boolean usesVerbal) {
        this.usesVerbal = usesVerbal;
    }

    public void setUsesSemantic(boolean usesSemantic) {
        this.usesSemantic = usesSemantic;
    }

    public void setUsesMaterial(boolean usesMaterial) {
        this.usesMaterial = usesMaterial;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public void setConcentration(boolean concentration) {
        this.concentration = concentration;
    }

    public void setRitual(boolean ritual) {
        this.ritual = ritual;
    }

    public void setAttackType(SpellAttackType attackType) {
        this.attackType = attackType;
    }

    public String getCasterClass() {
        return casterClass;
    }

    public void setCasterClass(String casterClass) {
        this.casterClass = casterClass;
    }

    public StatType getCastingStat() {
        return castingStat;
    }

    public void setCastingStat(StatType castingStat) {
        this.castingStat = castingStat;
    }

    public void setSource(ComponentType source) {
        this.source = source;
    }

    public ComponentType getSource() {
        return source;
    }

    public String getOriginString() {
        ComponentType type = getSource();
        if (type == ComponentType.CLASS) {
            return getCasterClass();
        }
        if (type == null) {
            return "";
        }
        return type.toString();
    }
}

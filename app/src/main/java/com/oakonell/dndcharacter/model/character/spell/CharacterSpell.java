package com.oakonell.dndcharacter.model.character.spell;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.model.character.ComponentType;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.spell.SpellSchool;
import com.oakonell.dndcharacter.utils.NumberUtils;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 1/13/2016.
 */
@Root(strict = false)
public class CharacterSpell extends BaseCharacterComponent {
    @Element(required = false)
    private String ownerName;
    @Element(required = false)
    private StatType castingStat;
    @Element(required = false)
    private ComponentType source;

    @Element(required = false)
    private int level;
    @Element(required = false)
    private SpellSchool spellSchool;

    @Element(required = false)
    private SpellRange rangeType;
    @Element(required = false)
    private int range;

    @Element(required = false)
    private String higherLevelDescription;


    @Element(required = false)
    private int numberTargets;


    @Element(required = false)
    private CastingTimeType castingType;
    @Element(required = false)
    private int castingTimeValue;

    @Element(required = false)
    private SpellDurationType durationType;
    @Element(required = false)
    private int durationTime;

    // components
    @Element(required = false)
    private boolean usesVerbal;
    @Element(required = false)
    private boolean usesSomatic;
    @Element(required = false)
    private boolean usesMaterial;

    @Element(required = false)
    private String component;


    @Element(required = false)
    private String specialComponent;


    @Element(required = false)
    private boolean concentration;
    @Element(required = false)
    private boolean ritual;

    @Element(required = false)
    private boolean countsAsKnown = true;


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
    @Element(required = false)
    private boolean hasDirectRoll;
    private boolean alwaysPrepared;

    public CharacterSpell(){}

    public void setPreparable(@SuppressWarnings("SameParameterValue") boolean preparable) {
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


    public SpellSchool getSchool() {
        return spellSchool;
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

    public int getCastingTime() {
        return castingTimeValue;
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

    public boolean isUsesSomatic() {
        return usesSomatic;
    }

    public boolean isUsesMaterial() {
        return usesMaterial;
    }

    public String getComponent() {
        return component;
    }

    public String getComponentString() {
        String result = "";
        boolean useComma = false;
        if (usesVerbal) {
            result += "V";
            useComma = true;
        }
        if (usesSomatic) {
            if (useComma) result += ",";
            result += "S";
            useComma = true;
        }
        if (usesMaterial) {
            if (useComma) result += ",";
            result += "M";
            useComma = true;
            result += " (";
            if (component != null) result += component;
            if (specialComponent != null) result += specialComponent;
            result += ")";
        }
        return result;
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

    public void setSchool(SpellSchool school) {
        this.spellSchool = school;
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

    public void setCastingTime(int castingTime) {
        this.castingTimeValue = castingTime;
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

    public void setUsesSomatic(boolean usesSomatic) {
        this.usesSomatic = usesSomatic;
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


    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }


    public StatType getCastingStat() {
        return castingStat;
    }

    public void setCastingStat(StatType castingStat) {
        this.castingStat = castingStat;
    }


    public void countsAsKnown(boolean countsAsKnown) {
        this.countsAsKnown = countsAsKnown;
    }

    public boolean countsAsKnown() {
        return countsAsKnown;
    }

    public void setSource(ComponentType type) {
        this.source = type;
    }

    public ComponentType getSource() {
        return source;
    }

    public String getRangeString(Resources resources) {
        if (rangeType == null) {
            return "??";
        }
        switch (rangeType) {
            case FEET:
                return resources.getQuantityString(R.plurals.range_feet, range, range);
            case MILES:
                return resources.getQuantityString(R.plurals.range_miles, range, range);
            case SELF:
                return resources.getString(R.string.range_self);
            case SIGHT:
                return resources.getString(R.string.range_sight);
            case TOUCH:
                return resources.getString(R.string.range_touch);
            case TARGET:
                return resources.getString(R.string.range_target);
            case SPECIAL:
                return resources.getString(R.string.range_special);
            case UNLIMITED:
                return resources.getString(R.string.range_unlimited);
            case SELF_CONE_FEET:
                return resources.getQuantityString(R.plurals.range_cone_feet, range, range);
            case SELF_CUBE_FEET:
                return resources.getQuantityString(R.plurals.range_cube_feet, range, range);
            case SELF_LINE_FEET:
                return resources.getQuantityString(R.plurals.range_line_feet, range, range);
            case SELF_SPHERE_FEET:
                return resources.getQuantityString(R.plurals.range_sphere_feet, range, range);
            case SELF_SPHERE_MILE:
                return resources.getQuantityString(R.plurals.range_sphere_mile, range, range);
            case SELF_HEMISPHERE_FEET:
                return resources.getQuantityString(R.plurals.range_hemisphere_feet, range, range);
        }
        return "??";
    }

    public String getDurationString(Resources resources) {
        if (getDurationType() == null) {
            return "??";
        }
        String concentrationString = "";
        if (isConcentration()) {
            concentrationString = resources.getString(R.string.concentration_duration_suffix);
        }
        switch (getDurationType()) {
            case INSTANTANEOUS:
                return resources.getString(R.string.instantaneous) + concentrationString;
            case DAY:
                return resources.getQuantityString(R.plurals.duration_day, durationTime, durationTime) + concentrationString;
            case HOUR:
                return resources.getQuantityString(R.plurals.duration_hour, durationTime, durationTime) + concentrationString;
            case MINUTE:
                return resources.getQuantityString(R.plurals.duration_minute, durationTime, durationTime) + concentrationString;
            case ROUND:
                return resources.getQuantityString(R.plurals.duration_round, durationTime, durationTime) + concentrationString;
            case SPECIAL:
                return resources.getString(R.string.duration_special) + concentrationString;
            case UNTIL_DISPELLED:
                return resources.getString(R.string.duration_until_dispelled) + concentrationString;
            case UNTIL_DISPELLED_OR_TRIGGERED:
                return resources.getString(R.string.duration_until_dispelled_or_triggered) + concentrationString;
        }
        return "??";
    }

    public String getCastingTimeString(Resources resources) {
        if (castingType == null) {
            return "??";
        }
        switch (castingType) {
            case ACTION:
                return resources.getQuantityString(R.plurals.cast_time_action, castingTimeValue, castingTimeValue);
            case BONUS_ACTION:
                return resources.getQuantityString(R.plurals.cast_time_bonus_action, castingTimeValue, castingTimeValue);
            case REACTION:
                return resources.getQuantityString(R.plurals.cast_time_reaction, castingTimeValue, castingTimeValue);

            case HOUR:
                return resources.getQuantityString(R.plurals.cast_time_hour, castingTimeValue, castingTimeValue);
            case MINUTE:
                return resources.getQuantityString(R.plurals.cast_time_minute, castingTimeValue, castingTimeValue);
        }
        return "??";
    }

    public String getHigherLevelDescription() {
        return higherLevelDescription;
    }

    public void setHigherLevelDescription(String higherLevelDescription) {
        this.higherLevelDescription = higherLevelDescription;
    }

    public String getSpecialComponent() {
        return specialComponent;
    }

    public void setSpecialComponent(String specialComponent) {
        this.specialComponent = specialComponent;
    }

    public boolean hasDirectRoll() {
        return hasDirectRoll;
    }

    public void setHasDirectRoll(boolean hasDirectRoll) {
        this.hasDirectRoll = hasDirectRoll;
    }

    public void setAlwaysPrepared(boolean alwaysPrepared) {
        this.alwaysPrepared = alwaysPrepared;
    }

    public boolean isAlwaysPrepared() {
        return alwaysPrepared;
    }
}

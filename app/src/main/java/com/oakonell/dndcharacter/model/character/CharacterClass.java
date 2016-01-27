package com.oakonell.dndcharacter.model.character;

import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.expression.context.SimpleVariableContext;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

import java.util.Map;

/**
 * Created by Rob on 10/24/2015.
 */
public class CharacterClass extends BaseCharacterComponent {
    // TODO derivable from order in character...? This is redundant currently
    @Element(required = false)
    private int level;
    @Element(required = false)
    private int hpRoll;
    @Element(required = false)
    private int hitDie;
    @Element(required = false)
    private String subclassName;
    @Element(required = false)
    private SavedChoices subClassChoices;
    @Element(required = false)
    private String preparedSpellsFormula;
    @Element(required = false)
    private String multiclassCasterFactorFormula;
    @Element(required = false)
    private StatType casterStat;
    @Element(required = false)
    private String cantripsKnownFormula;
    @Element(required = false)
    private String spellsKnownFormula;
    @ElementMap(entry = "levelSlot", key = "level", value = "slots", required = false)
    private Map<Integer, String> spellLevelSlotFormulas;


    @Override
    public ComponentType getType() {
        return ComponentType.CLASS;
    }

    @Override
    public String getSourceString() {
        return getType().toString() + ": " + getName() + " " + level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHitDie() {
        return hitDie;
    }

    public void setHitDie(int hitDie) {
        this.hitDie = hitDie;
    }

    public int getHpRoll() {
        return hpRoll;
    }

    public void setHpRoll(int hpRoll) {
        this.hpRoll = hpRoll;
    }

    @Override
    public void addExtraFormulaVariables(SimpleVariableContext variableContext) {
        super.addExtraFormulaVariables(variableContext);
        variableContext.setNumber("classLevel", getLevel());
    }

    public String getSubclassName() {
        return subclassName;
    }

    public void setSubclassName(String subclassName) {
        this.subclassName = subclassName;
    }

    public SavedChoices getSubClassChoices() {
        return subClassChoices;
    }

    public void setSubClassChoices(SavedChoices subClassChoices) {
        this.subClassChoices = subClassChoices;
    }

    public void setPreparedSpellsFormula(String preparedSpellsFormula) {
        this.preparedSpellsFormula = preparedSpellsFormula;
    }

    public String getPreparedSpellsFormula() {
        return preparedSpellsFormula;
    }

    public void setMulticlassCasterFactorFormula(String multiclassCasterFactorFormula) {
        this.multiclassCasterFactorFormula = multiclassCasterFactorFormula;
    }

    public String getMulticlassCasterFactorFormula() {
        return multiclassCasterFactorFormula;
    }

    public void setCasterStat(StatType casterStat) {
        this.casterStat = casterStat;
    }

    public StatType getCasterStat() {
        return casterStat;
    }

    public void setCantripsKnownFormula(String cantripsKnownFormula) {
        this.cantripsKnownFormula = cantripsKnownFormula;
    }

    public String getCantripsKnownFormula() {
        return cantripsKnownFormula;
    }

    public void setSpellsKnownFormula(String spellsKnownFormula) {
        this.spellsKnownFormula = spellsKnownFormula;
    }

    public String getSpellsKnownFormula() {
        return spellsKnownFormula;
    }

    public void setSpellLevelSlotFormulas(Map<Integer, String> spellLevelSlotFormulas) {
        this.spellLevelSlotFormulas = spellLevelSlotFormulas;
    }

    public Map<Integer, String> getSpellLevelSlotFormulas() {
        return spellLevelSlotFormulas;
    }
}

package com.oakonell.dndcharacter.model.character;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.character.item.CharacterArmor;
import com.oakonell.dndcharacter.model.character.item.CharacterItem;
import com.oakonell.dndcharacter.model.character.item.CharacterWeapon;
import com.oakonell.dndcharacter.model.character.rest.AbstractRestRequest;
import com.oakonell.dndcharacter.model.character.rest.LongRestRequest;
import com.oakonell.dndcharacter.model.character.rest.ShortRestRequest;
import com.oakonell.dndcharacter.model.character.stats.BaseStatsType;
import com.oakonell.dndcharacter.model.character.stats.SkillBlock;
import com.oakonell.dndcharacter.model.character.stats.SkillType;
import com.oakonell.dndcharacter.model.character.stats.StatBlock;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.components.Feature;
import com.oakonell.dndcharacter.model.components.IFeatureAction;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;
import com.oakonell.expression.Expression;
import com.oakonell.expression.ExpressionContext;
import com.oakonell.expression.ExpressionType;
import com.oakonell.expression.ExpressionValue;
import com.oakonell.expression.context.SimpleFunctionContext;
import com.oakonell.expression.context.SimpleVariableContext;
import com.oakonell.expression.functions.ExpressionFunction;
import com.oakonell.expression.types.BooleanValue;
import com.oakonell.expression.types.NumberValue;
import com.oakonell.expression.types.StringValue;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/25/2016.
 */

public abstract class AbstractCharacter {
    @NonNull
    @ElementList(required = false)
    protected List<CharacterEffect> effects = new ArrayList<>();

    @NonNull
    @ElementList(required = false)
    private List<CharacterArmor> armor = new ArrayList<>();
    @Element(required = false)
    private long itemIdSequence;
    @NonNull
    @ElementList(required = false)
    private List<CharacterItem> items = new ArrayList<>();
    @NonNull
    @ElementList(required = false)
    private List<CharacterWeapon> weapons = new ArrayList<>();


    // more fluid data
    @Element(required = false)
    private int hp;
    @Element(required = false)
    private int tempHp;
    @NonNull
    @ElementMap(entry = "feature", key = "name", value = "uses", required = false)
    private Map<String, Integer> usedFeatures = new HashMap<>();
    // relatively static data
    @Element(required = false)
    private String name;
    @ElementMap(entry = "stat", key = "name", value = "value", required = false)
    private Map<StatType, Integer> baseStats = new HashMap<>();
    @Element(required = false)
    private BaseStatsType statsType;
    @Element(required = false)
    private String notes = "";
    @NonNull
    @Element(required = false)
    private SpeedType visibleSpeedType = SpeedType.WALK;
    @Element(required = false)
    private int deathSaveFails;
    @Element(required = false)
    private int deathSaveSuccesses;
    @Element(required = false)
    private boolean stable;
    @NonNull
    @ElementMap(entry = "adjustment", key = "key", value = "value", required = false)
    private Map<CustomAdjustmentType, CustomAdjustments> adjustments = new HashMap<>();
    @NonNull
    @ElementMap(entry = "note", key = "key", value = "value", required = false)
    private Map<FeatureContext, ContextNotes> contextNotes = new HashMap<>();

    public static void modifyRootAcs(@NonNull AbstractCharacter character, @NonNull List<ArmorClassWithSource> modifyingAcs, @NonNull List<ArmorClassWithSource> rootAcs) {
        // determine if using a shield
        boolean usingShield = false;
        for (ArmorClassWithSource each : modifyingAcs) {
            if (each.isEquipped() && each.isArmor() && ((CharacterArmor) each.getSource()).isShield()) {
                usingShield = true;
            }

        }

        // clear out the unequippable roots (feature based)
        // determine if wearing armor
        boolean wearingArmor = false;
        // go through items
        for (ArmorClassWithSource each : rootAcs) {
            each.isDisabled = false;
            if (!each.isEquipabble()) {
                each.isEquipped = false;
                continue;
            }

//            if (!each.isArmor()) continue;
            String formula = each.getFormula();
            if (formula != null) {
                if (each.isEquipped()) {
                    wearingArmor = true;
                }
            }
        }


        for (ArmorClassWithSource each : rootAcs) {
            if (each.getSource() == null) continue;
            if (each.isEquipabble()) {
                continue;
            }
            final ComponentSource source = each.getSource();
            String activeFormula = source.getActiveFormula();
            if (activeFormula != null) {
                SimpleVariableContext variableContext = new SimpleVariableContext();
//                source.addExtraFormulaVariables(variableContext, character);
                variableContext.setBoolean("armor", wearingArmor);
                variableContext.setBoolean("shield", usingShield);

                boolean isActive = character.evaluateBooleanFormula(activeFormula, variableContext);
                each.isDisabled = !isActive;
            } else {
                each.isDisabled = false;
            }
        }


        Collections.sort(rootAcs, new Comparator<ArmorClassWithSource>() {
            @Override
            public int compare(@NonNull ArmorClassWithSource lhs, @NonNull ArmorClassWithSource rhs) {
                int lv = lhs.getValue();
                int rv = rhs.getValue();
                return lv < rv ? -1 : (lv == rv ? 0 : 1);
            }
        });
        ArmorClassWithSource noArmorRow = null;
        for (int i = 0; i < rootAcs.size(); i++) {
            ArmorClassWithSource each = rootAcs.get(i);
            if (each.isEquipabble()) continue;
            if (each.isDisabled) continue;
            noArmorRow = each;
            each.isDisabled = true;
        }

        if (!wearingArmor) {
            noArmorRow.setIsEquipped(true);
            noArmorRow.isDisabled = true;
        }

    }

    /**
     * Base info
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * derivable info
     */

    abstract public int getMaxHP();

    abstract public int getProficiency();

    @NonNull
    public StatBlock getStatBlock(StatType type) {
        return new StatBlock(this, type);
    }

    @NonNull
    public SkillBlock getSkillBlock(SkillType type) {
        return new SkillBlock(this, type);
    }

    public FeatureInfo getFeatureNamed(String name) {

        List<FeatureInfo> matchingFeatures = new ArrayList<>();
        for (FeatureInfo each : getFeatureInfos()) {
            if (each.getName().equals(name)) {
                matchingFeatures.add(each);
            }
        }
        if (matchingFeatures.isEmpty()) return null;
        if (matchingFeatures.size() > 1) {
            // report an error, or just return the first?
            //throw new RuntimeException("Multiple effects named '" + name + "'!");
        }
        return matchingFeatures.get(0);
    }

    @Nullable
    public CharacterEffect getEffectNamed(String name) {
        List<CharacterEffect> matchingEffects = new ArrayList<>();
        for (CharacterEffect each : getEffects()) {
            if (each.getName().equals(name)) {
                matchingEffects.add(each);
            }
        }
        if (matchingEffects.isEmpty()) return null;
        if (matchingEffects.size() > 1) {
            // report an error, or just return the first?
            //throw new RuntimeException("Multiple effects named '" + name + "'!");
        }
        return matchingEffects.get(0);
    }

    public void stabilize() {
        stable = true;
        deathSaveFails = 0;
        deathSaveSuccesses = 0;
    }

    public boolean isStable() {
        return stable;
    }

    public boolean hasAdjustments() {
        boolean isEmpty = true;
        for (final CustomAdjustments adjustment : adjustments.values()) {
            if (adjustment.getAdjustments().isEmpty()) {
                continue;
            }
            isEmpty = false;
        }
        return !isEmpty;
    }

    public List<CustomAdjustments> getCustomAdjustments() {
        final ArrayList<CustomAdjustments> result = new ArrayList<>();
        for (CustomAdjustments each : adjustments.values()) {
            if (!each.getAdjustments().isEmpty()) {
                result.add(each);
            }
        }
        return result;
    }

    @NonNull
    public List<ArmorClassWithSource> deriveRootAcs() {
        final List<ArmorClassWithSource> modifyingAcs = deriveModifyingAcs();
        final List<ArmorClassWithSource> rootAcs = deriveRootAcs(modifyingAcs);
        modifyRootAcs(this, modifyingAcs, rootAcs);
        return rootAcs;
    }

    @NonNull
    public List<ArmorClassWithSource> deriveRootAcs(List<ArmorClassWithSource> modifyingAcs) {
        final List<ArmorClassWithSource> result = new ArrayList<>();
        String baseFormula = "10 + dexterityMod";
        int baseValue = evaluateFormula(baseFormula, null);
        ArmorClassWithSource unarmored = new ArmorClassWithSource(baseFormula, baseValue, null, false, false);
        result.add(unarmored);

        RootArmorClassDeriver addRootAc = new RootArmorClassDeriver() {

            @Override
            public void derive(FeatureInfo each) {
                String acFormula = each.getBaseAcFormula();
                if (acFormula != null) {
                    SimpleVariableContext variableContext = new SimpleVariableContext();
                    each.getSource().addExtraFormulaVariables(variableContext, AbstractCharacter.this);
                    int value = evaluateFormula(acFormula, variableContext);
                    ArmorClassWithSource featureAc = new ArmorClassWithSource(acFormula, value, each, false, false);

                    result.add(featureAc);
                }
            }
        };

        addAdditionalACRoots(addRootAc);

        for (CharacterEffect each : getEffects()) {
            if (!each.isBaseArmor()) continue;

            // this is not a "sourcable" component
            String acFormula = each.getBaseAcFormula();
            if (acFormula != null) {
                SimpleVariableContext variableContext = new SimpleVariableContext();
                //each.getSource().addExtraFormulaVariables(variableContext, this);
                int value = evaluateFormula(acFormula, variableContext);
                ArmorClassWithSource featureAc = new ArmorClassWithSource(acFormula, value, each, false, false);
                result.add(featureAc);
            }
        }
        // go through custom adjustments
        final CustomAdjustments customRootACs = getCustomAdjustments(CustomAdjustmentType.ROOT_ACS);
        for (CustomAdjustments.Adjustment each : customRootACs.getAdjustments()) {
            ComponentSource source = new AdjustmentComponentSource(each);
            ArmorClassWithSource customAC = new ArmorClassWithSource(each.stringValue, each.numValue, source, false, true);
            result.add(customAC);
            customAC.setIsEquipped(each.applied);
        }

        // go through items
        for (CharacterArmor each : getArmor()) {
            if (!each.isBaseArmor()) continue;

            String formula = each.getBaseAcFormula();
            if (formula != null) {
                int value = evaluateFormula(formula, null);
                ArmorClassWithSource featureAc = new ArmorClassWithSource(formula, value, each, true, true);
                result.add(featureAc);
            }
        }

        Collections.sort(result, new Comparator<ArmorClassWithSource>() {
            @Override
            public int compare(@NonNull ArmorClassWithSource lhs, @NonNull ArmorClassWithSource rhs) {
                int lv = lhs.getValue();
                int rv = rhs.getValue();
                return lv < rv ? -1 : (lv == rv ? 0 : 1);
            }
        });

        return result;
    }

    protected void addAdditionalACRoots(RootArmorClassDeriver addRootAc) {
    }

    @NonNull
    public List<CharacterArmor> getArmor() {
        return armor;
    }

    public void addArmor(CharacterArmor armor) {
        armor.setId(itemIdSequence++);
        this.armor.add(armor);
    }

    @NonNull
    public List<ArmorClassWithSource> deriveModifyingAcs() {
        final List<ArmorClassWithSource> result = new ArrayList<>();

        final ArmorInfo armorInfo = new ArmorInfo(getArmor());
        // multiple here will really just take the highest ?? at runtime
        ComponentVisitor deriver = new ComponentVisitor() {
            @Override
            public void visitComponent(@NonNull ICharacterComponent component) {
                if (component.isBaseArmor()) return;

                String acFormula = component.getModifyingAcFormula();
                if (acFormula == null) return;

                SimpleVariableContext variableContext = new SimpleVariableContext();
                variableContext.setBoolean("armor", armorInfo.isWearingArmor);
                variableContext.setBoolean("shield", armorInfo.isUsingShield);
                component.addExtraFormulaVariables(variableContext, AbstractCharacter.this);

                int value = evaluateFormula(acFormula, variableContext);
                ArmorClassWithSource featureAc = new ArmorClassWithSource(acFormula, value, component, component instanceof CharacterArmor, true);
                result.add(featureAc);

                String activeFormula = component.getActiveFormula();
                if (activeFormula != null) {
                    boolean isActive = evaluateBooleanFormula(activeFormula, variableContext);
                    featureAc.setIsEquipped(isActive);
                } else {
                    if (component instanceof CharacterArmor) {
                        featureAc.setIsEquipped(((CharacterArmor) component).isEquipped());
                    } else {
                        featureAc.setIsEquipped(true);
                    }
                }
                if (component instanceof Feature) {
                    featureAc.isDisabled = true;
                }
            }
        };

        // go through custom adjustments
        final CustomAdjustments customRootACs = getCustomAdjustments(CustomAdjustmentType.MODIFYING_ACS);
        for (CustomAdjustments.Adjustment each : customRootACs.getAdjustments()) {
            ComponentSource source = new AdjustmentComponentSource(each);
            ArmorClassWithSource customAC = new ArmorClassWithSource(each.stringValue, each.numValue, source, false, true);
            result.add(customAC);
            customAC.setIsEquipped(each.applied);
        }

        getAbilityDeriver(deriver, false).derive(this, "AC modifiers");


        return result;
    }

    public int getArmorClass() {
        List<ArmorClassWithSource> modifiers = deriveModifyingAcs();
        List<ArmorClassWithSource> roots = deriveRootAcs();
        ArmorClassWithSource activeRoot = null;
        for (ArmorClassWithSource each : roots) {
            if (each.isEquipped()) {
                activeRoot = each;
                break;
            }
        }
        if (activeRoot == null) {
            throw new RuntimeException("No active AC root!?");
        }
        // go through active equipment
        // if no equipment affects ac, it is just 10 + dex mod

        int ac = activeRoot.getValue();
        int addition = 0;

        for (ArmorClassWithSource each : modifiers) {
            if (each.isEquipped()) {
                addition += each.getValue();
            }
        }

        return ac + addition;
    }

    @NonNull
    public List<ModifierWithSource> deriveStat(@NonNull final StatType type) {
        final List<ModifierWithSource> result = new ArrayList<>();

        if (baseStats != null) {
            Integer value = baseStats.get(type);
            if (value == null) value = 0;
            ModifierWithSource base = new ModifierWithSource(value, null);
            result.add(base);
        }

        ComponentVisitor deriver = new ComponentVisitor() {
            @Override
            public void visitComponent(@NonNull ICharacterComponent component) {
                int value = component.getStatModifier(type);
                if (value != 0) {
                    ModifierWithSource base = new ModifierWithSource(value, component);
                    result.add(base);
                }
            }
        };
        getAbilityDeriver(deriver, false).derive(this, "stat " + type.name());

        // go through custom adjustments
        final CustomAdjustments customStats = getCustomAdjustments(type.getCustomType());
        for (CustomAdjustments.Adjustment each : customStats.getAdjustments()) {
            AdjustmentComponentSource source = new AdjustmentComponentSource(each);
            ModifierWithSource customStat = new ModifierWithSource(each.numValue, source);
            result.add(customStat);
        }

        return result;
    }

    public int deriveStatValue(@NonNull final StatType type) {
        int result = 0;
        final List<ModifierWithSource> modifiers = deriveStat(type);
        for (ModifierWithSource each : modifiers) {
            if (each.isActive()) {
                result += each.getModifier();
            }
        }

        return result;
    }

    @NonNull
    public List<Character.ProficientWithSource> deriveSkillProciencies(@NonNull final SkillType type) {
        final List<Character.ProficientWithSource> result = new ArrayList<>();

        ComponentVisitor deriver = new ComponentVisitor() {
            @Override
            public void visitComponent(@NonNull ICharacterComponent component) {
                Proficient proficient = component.getSkillProficient(type);
                if (proficient != Proficient.NONE) {
                    Character.ProficientWithSource reason = new Character.ProficientWithSource(proficient, component);
                    result.add(reason);
                }
            }
        };
        getAbilityDeriver(deriver, false).derive(this, "skill profs " + type.name());

        return result;
    }

    @NonNull
    public List<Character.ProficientWithSource> deriveSaveProficiencies(@NonNull final StatType type) {
        final List<Character.ProficientWithSource> result = new ArrayList<>();

        ComponentVisitor deriver = new ComponentVisitor() {
            @Override
            public void visitComponent(@NonNull ICharacterComponent component) {
                Proficient proficient = component.getSaveProficient(type);
                if (proficient != Proficient.NONE) {
                    Character.ProficientWithSource reason = new Character.ProficientWithSource(proficient, component);
                    result.add(reason);
                }
            }
        };
        getAbilityDeriver(deriver, false).derive(this, "save throw prof " + type.name());

        return result;
    }

    public Proficient deriveSkillProciency(@NonNull final SkillType type) {
        final Proficient proficient[] = new Proficient[1];
        proficient[0] = Proficient.NONE;

        ComponentVisitor deriver = new ComponentVisitor() {
            @Override
            public void visitComponent(@NonNull ICharacterComponent component) {
                Proficient compProficient = component.getSkillProficient(type);
                if (compProficient.getMultiplier() > proficient[0].getMultiplier()) {
                    proficient[0] = compProficient;
                }
            }
        };
        getAbilityDeriver(deriver, false).derive(this, "skill prof " + type.name());

        return proficient[0];
    }

    public Proficient deriveSaveProciency(@NonNull final StatType type) {
        final Proficient proficient[] = new Proficient[1];
        proficient[0] = Proficient.NONE;
        final List<Character.LanguageWithSource> languages = new ArrayList<>();
        ComponentVisitor deriver = new ComponentVisitor() {
            @Override
            public void visitComponent(@NonNull ICharacterComponent component) {
                Proficient compProficient = component.getSaveProficient(type);
                if (compProficient.getMultiplier() > proficient[0].getMultiplier()) {
                    proficient[0] = compProficient;
                }
            }
        };
        getAbilityDeriver(deriver, false).derive(this, "save prof " + type.name());
        return proficient[0];
    }

    @NonNull
    public Collection<FeatureInfo> getFeatureInfos() {
        final Map<String, FeatureInfo> map = new HashMap<>();

        // features shouldn't contain features, and any effects are not automatic, but applied on use
        ComponentVisitor deriver = new ComponentVisitor() {
            @Override
            public void visitComponent(@NonNull ICharacterComponent component) {
                // handle extend/replace features
                component.addFeatureInfo(map, AbstractCharacter.this);
            }
        };
        getAbilityDeriver(deriver, true).derive(this, "Feature infos");

        return map.values();
    }

    public boolean evaluateBooleanFormula(@Nullable String formula, @Nullable SimpleVariableContext variableContext) {
        // TODO formula might reference stats and such
        if (formula == null || formula.length() == 0) return false;
        variableContext = getPopulatedVariableContext(variableContext);

        try {
            Expression<Boolean> expression = Expression.parse(formula, ExpressionType.BOOLEAN_TYPE, new ExpressionContext(getFunctionContext(), variableContext));
            return expression.evaluate();
        } catch (Exception e) {
            // should be done at formula save time...
            return false;
        }
    }

    @NonNull
    protected SimpleVariableContext getPopulatedVariableContext(@Nullable SimpleVariableContext variableContext) {
        if (variableContext == null) variableContext = new SimpleVariableContext();

        // enumerate all the stat modifiers and values
        for (StatType each : StatType.values()) {
            StatBlock block = getStatBlock(each);
            int mod = block.getModifier();
            variableContext.setNumber(each.name().toLowerCase() + "Mod", mod);
            variableContext.setNumber(each.name().toLowerCase(), block.getValue());
        }
        return variableContext;
    }

    public int evaluateFormula(@Nullable String formula, @Nullable SimpleVariableContext variableContext) {
        // TODO formula might reference stats and such
        if (formula == null || formula.length() == 0) return 0;
        variableContext = getPopulatedVariableContext(variableContext);

        Expression<Integer> expression = Expression.parse(formula, ExpressionType.NUMBER_TYPE, new ExpressionContext(getFunctionContext(), variableContext));
        return expression.evaluate();
    }

    @NonNull
    protected SimpleFunctionContext getFunctionContext() {
        return new AbstractCharacter.CharacterFunctionContext(this);
    }

    public int getUses(@NonNull String featureName) {
        Integer uses = usedFeatures.get(featureName);
        if (uses == null) return 0;
        return uses;
    }

    public int getUsesRemaining(@NonNull FeatureInfo feature) {
        return feature.evaluateMaxUses(this) - getUses(feature.getName());
    }

    public void setUsesRemaining(@NonNull FeatureInfo feature, int remaining) {
        int max = feature.evaluateMaxUses(this);
        int used = max - remaining;
        usedFeatures.put(feature.getName(), used);
    }

    public void useFeature(@NonNull FeatureInfo feature, int amount) {
        // TODO apply known effects from feature
        int uses = getUses(feature.getName());
        uses = uses + amount;
        usedFeatures.put(feature.getName(), uses);
    }

    public CharacterEffect useFeatureAction(@NonNull FeatureInfo feature, @NonNull IFeatureAction action, Map<String, String> variableValues) {
        useFeature(feature, action.getCost());
        return action.applyToCharacter(this, variableValues);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void longRest(@NonNull LongRestRequest request) {
        hp = Math.min(hp + request.getHealing(), getMaxHP());

        // temp HP disappear, unless a spell/effect?
        tempHp = 0;

        //refresh features
        resetFeatures(request);
    }

    private void resetFeatures(@NonNull AbstractRestRequest request) {
        Collection<FeatureInfo> featureInfos = getFeatureInfos();
        for (FeatureInfo each : featureInfos) {
            Integer resetRequest = request.getFeatureResets().get(each.getName());
            if (resetRequest == null || resetRequest == 0) continue;

            Integer used = usedFeatures.get(each.getName());
            if (used == null) continue;

            used = used - resetRequest;
            if (used <= 0) {
                usedFeatures.remove(each.getName());
            } else {
                usedFeatures.put(each.getName(), used);
            }
        }

    }

    public void shortRest(@NonNull ShortRestRequest request) {
        hp = Math.min(hp + request.getHealing(), getMaxHP());

        resetFeatures(request);
    }

    public int getTempHp() {
        return tempHp;
    }

    public void setTempHP(int tempHP) {
        this.tempHp = tempHP;
    }

    public int getHP() {
        return hp;
    }

    public void setHP(int HP) {
        this.hp = HP;
    }

    public int getTotalHP() {
        return hp + tempHp;
    }

    public void damage(int amount) {
        if (hp == 0) {
            deathSaveFails++;
        }
        if (tempHp > 0) {
            tempHp -= amount;
            if (tempHp < 0) {
                hp += tempHp;
                tempHp = 0;
            }
        } else {
            hp -= amount;
        }
        if (hp <= -getMaxHP()) {
            deathSaveFails = 3;
        }
        if (hp <= 0) {
            stable = false;
            hp = 0;
        }
    }

    public void heal(int amount) {
        this.hp += amount;
        hp = Math.min(hp, getMaxHP());
    }

    public void addTempHp(int amount) {
        // TODO special rules about tempHP?
        tempHp += amount;
    }

    public void addEffect(CharacterEffect characterEffect) {
        effects.add(characterEffect);
    }

    @NonNull
    public List<CharacterEffect> getEffects() {
        return effects;
    }


    public BaseStatsType getStatsType() {
        return statsType;
    }

    public void setStatsType(BaseStatsType statsType) {
        this.statsType = statsType;
    }

    public Map<StatType, Integer> getBaseStats() {
        return baseStats;
    }

    public void setBaseStats(Map<StatType, Integer> baseStats) {
        this.baseStats = baseStats;
        // adjust the HP, in case constitution was changed...
        if (hp > getMaxHP()) {
            hp = getMaxHP();
        }
    }

    @NonNull
    public SpeedType getSpeedType() {
        return visibleSpeedType;
    }

    public void setSpeedType(@NonNull SpeedType type) {
        visibleSpeedType = type;
    }

    @NonNull
    public List<SpeedWithSource> deriveSpeeds(@NonNull final SpeedType type) {
        final List<SpeedWithSource> result = new ArrayList<>();

        final ArmorInfo armorInfo = new ArmorInfo(getArmor());

        final List<SpeedWithSource> baseSpeeds = new ArrayList<>();

        ComponentVisitor deriver = new ComponentVisitor() {
            @Override
            public void visitComponent(@NonNull ICharacterComponent component) {
                int speed = component.getSpeed(AbstractCharacter.this, type);
                Boolean isBaseSpeed = component.isBaseSpeed(type);
                if (speed == 0 && isBaseSpeed == null) return;

                boolean isActive = true;
                String activeFormula = component.getActiveFormula();
                if (activeFormula != null && activeFormula.trim().length() > 0) {
                    SimpleVariableContext variables = new SimpleVariableContext();
                    // add armor class variables
                    variables.setBoolean("armor", armorInfo.isWearingArmor);
                    variables.setBoolean("shield", armorInfo.isUsingShield);
                    isActive = evaluateBooleanFormula(activeFormula, variables);
                }

                SpeedWithSource speedWithSource = new SpeedWithSource(speed, component);
                speedWithSource.setActive(isActive);
                result.add(speedWithSource);
                if (isBaseSpeed && isActive) baseSpeeds.add(speedWithSource);
            }
        };
        getAbilityDeriver(deriver, false).derive(this, "speed " + type.name());

        // pick the highest base speed, unless there are any base zeros, then all are inactive
        boolean hasBaseZero = false;
        SpeedWithSource activeBaseSpeed = null;
        for (SpeedWithSource each : baseSpeeds) {
            each.setActive(false);
        }
        for (SpeedWithSource each : baseSpeeds) {
            if (each.getSpeed() == 0) {
                hasBaseZero = true;
                activeBaseSpeed = each;
                each.setActive(true);
                break;
            }
            if (activeBaseSpeed == null) {
                activeBaseSpeed = each;
                each.setActive(true);
                continue;
            }
            if (each.getSpeed() > activeBaseSpeed.getSpeed()) {
                activeBaseSpeed.setActive(false);
                activeBaseSpeed = each;
                each.setActive(true);
            }
        }


        // apply default crawl, swim, climb, half of walk if missing base speed
        if ((type == SpeedType.CLIMB || type == SpeedType.CRAWL || type == SpeedType.SWIM)
                && (baseSpeeds.isEmpty() && !hasBaseZero)) {
            int walkSpeed = (int) (getSpeed(SpeedType.WALK) / 2.0);
            AbstractCharacter.CheatComponentSource source = new AbstractCharacter.CheatComponentSource(R.string.base_speed);
            SpeedWithSource speedWithSource = new SpeedWithSource(walkSpeed, source);
            speedWithSource.setActive(true);
            result.add(speedWithSource);
        }

        // go through custom adjustments
        final CustomAdjustments customStats = getCustomAdjustments(type.getCustomType());
        for (CustomAdjustments.Adjustment each : customStats.getAdjustments()) {
            AdjustmentComponentSource source = new AdjustmentComponentSource(each);
            SpeedWithSource customStat = new SpeedWithSource(each.numValue, source);
            result.add(customStat);
        }

        if (hasBaseZero) {
            for (SpeedWithSource each : result) {
                if (each == activeBaseSpeed) continue;
                each.setActive(false);
            }
        }


        return result;
    }

    public int getSpeed(@NonNull final SpeedType type) {
        int result = 0;
        for (SpeedWithSource each : deriveSpeeds(type)) {
            if (each.isActive()) {
                result += each.getSpeed();
            }
        }
        return result;
    }

    public int getPassivePerception() {
        int result = 0;
        for (PassivePerceptionWithSource each : derivePassivePerception()) {
            if (each.isActive()) {
                result += each.getPassivePerception();
            }
        }
        return result;
    }

    @NonNull
    public List<PassivePerceptionWithSource> derivePassivePerception() {
        final List<PassivePerceptionWithSource> result = new ArrayList<>();

        PassivePerceptionWithSource baseSource = new PassivePerceptionWithSource(10, new AbstractCharacter.CheatComponentSource(R.string.passive_base));
        result.add(baseSource);
        PassivePerceptionWithSource dexSource = new PassivePerceptionWithSource(getStatBlock(StatType.WISDOM).getModifier(), new AbstractCharacter.CheatComponentSource(R.string.wisdom_mod));
        result.add(dexSource);
        final Proficient proficiency = getSkillBlock(SkillType.PERCEPTION).getProficiency();
        if (proficiency.getMultiplier() > 0) {
            AbstractCharacter.CheatComponentSource source = new AbstractCharacter.CheatComponentSource(R.string.proficiency_bonus);
            PassivePerceptionWithSource proficiencySource = new PassivePerceptionWithSource((int) proficiency.getMultiplier() * getProficiency(), source);
            result.add(proficiencySource);
        }

        ComponentVisitor deriver = new ComponentVisitor() {
            @Override
            public void visitComponent(@NonNull ICharacterComponent component) {
                int perception = component.getPassivePerceptionMod(AbstractCharacter.this);
                if (perception == 0) return;
                PassivePerceptionWithSource passivePerceptionWithSource = new PassivePerceptionWithSource(perception, component);
                result.add(passivePerceptionWithSource);
            }
        };
        getAbilityDeriver(deriver, false).derive(this, "passivePerception");

        // go through custom adjustments
        final CustomAdjustments adjustments = getCustomAdjustments(CustomAdjustmentType.PASSIVE_PERCEPTION);
        for (CustomAdjustments.Adjustment each : adjustments.getAdjustments()) {
            AdjustmentComponentSource source = new AdjustmentComponentSource(each);
            PassivePerceptionWithSource adjustment = new PassivePerceptionWithSource(each.numValue, source);
            result.add(adjustment);
        }

        return result;
    }

    public int getInitiative() {
        int result = 0;
        List<InitiativeWithSource> initList = deriveInitiative();
        for (InitiativeWithSource each : initList) {
            if (each.isActive()) {
                result += each.getInitiative();
            }
        }

        return result;
    }

    @NonNull
    public List<InitiativeWithSource> deriveInitiative() {
        final List<InitiativeWithSource> result = new ArrayList<>();

        InitiativeWithSource baseSoure = new InitiativeWithSource(getStatBlock(StatType.DEXTERITY).getModifier(), null);
        result.add(baseSoure);

        ComponentVisitor deriver = new ComponentVisitor() {
            @Override
            public void visitComponent(@NonNull ICharacterComponent component) {
                int initiative = component.getInitiativeMod(AbstractCharacter.this);
                if (initiative == 0) return;
                InitiativeWithSource initiativeWithSource = new InitiativeWithSource(initiative, component);
                result.add(initiativeWithSource);
            }
        };
        getAbilityDeriver(deriver, false).derive(this, "passivePerception");

        // go through custom adjustments
        final CustomAdjustments customRootACs = getCustomAdjustments(CustomAdjustmentType.INITIATIVE);
        for (CustomAdjustments.Adjustment each : customRootACs.getAdjustments()) {
            AdjustmentComponentSource source = new AdjustmentComponentSource(each);
            InitiativeWithSource customAC = new InitiativeWithSource(each.numValue, source);
            result.add(customAC);
        }

        return result;
    }

    public int getDeathSaveFails() {
        return deathSaveFails;
    }

    public void failDeathSave() {
        deathSaveFails++;
    }

    public int getDeathSaveSuccesses() {
        return deathSaveSuccesses;
    }

    public void passDeathSave() {
        deathSaveSuccesses++;
        if (deathSaveSuccesses >= 3) {
            hp = 0;
            stable = true;
            deathSaveFails = 0;
            deathSaveSuccesses = 0;
        }
    }

    public CustomAdjustments getCustomAdjustments(CustomAdjustmentType type) {
        CustomAdjustments adjustments = this.adjustments.get(type);
        if (adjustments == null) {
            adjustments = new CustomAdjustments(type);
            this.adjustments.put(type, adjustments);
        }
        return adjustments;
    }

    public List<ContextNote> getContextNotes() {
        List<ContextNote> notes = new ArrayList<>();
        for (ContextNotes childNotes : contextNotes.values()) {
            notes.addAll(childNotes.getNotes());
        }
        return notes;
    }

    public List<ContextNote> getContextNotes(FeatureContext context) {
        ContextNotes notes = this.contextNotes.get(context);
        if (notes == null) {
            notes = new ContextNotes(context);
            contextNotes.put(context, notes);
        }
        return notes.getNotes();
    }

    public void addExtraFormulaVariables(SimpleVariableContext variableContext) {
    }

    public abstract boolean anyContextFeats(FeatureContext toHit);

    interface RootArmorClassDeriver {
        void derive(FeatureInfo info);
    }

    public static class ArmorClassWithSource extends WithSource {
        private final String formula;
        private final boolean isArmor;
        private final int value;
        private final boolean isEquipabble;

        boolean isEquipped;
        public boolean isDisabled;

        ArmorClassWithSource(String formula, int value, ComponentSource source, boolean isArmor, boolean isEquipabble) {
            super(source);
            this.value = value;
            this.formula = formula;
            if (source instanceof CharacterArmor) {
                isEquipped = ((CharacterArmor) source).isEquipped();
            }
            this.isEquipabble = isEquipabble;
            this.isArmor = isArmor;
        }

        public void setIsEquipped(boolean value) {
            isEquipped = value;
        }

        public boolean isEquipped() {
            return isEquipped;
        }

        public String getFormula() {
            return formula;
        }

        public int getValue() {
            return value;
        }

        public boolean isEquipabble() {
            return isEquipabble;
        }

        public boolean isArmor() {
            return isArmor;
        }

        public void setEquipped(Resources resources, Character character, boolean equipped) {
            if (getSource() instanceof CharacterArmor) {
                ((CharacterArmor) getSource()).setEquipped(resources, character, equipped);
            } else if (getSource() instanceof AdjustmentComponentSource) {
                ((AdjustmentComponentSource) getSource()).setEquipped(resources, character, equipped);
            }
        }
    }

    private static class ArmorInfo {
        private final boolean isWearingArmor;
        private final boolean isUsingShield;

        ArmorInfo(List<CharacterArmor> armor) {
            boolean usingShield = false;
            boolean wearingArmor = false;
            for (CharacterArmor each : armor) {
                if (each.isBaseArmor()) {
                    if (each.isEquipped()) {
                        wearingArmor = true;
                        break;
                    }
                } else if (each.isShield()) {
                    if (each.isEquipped()) {
                        usingShield = true;
                        break;
                    }
                }
            }
            isWearingArmor = wearingArmor;
            isUsingShield = usingShield;
        }


    }

    public static class ModifierWithSource extends WithSource {
        private final int modifier;

        ModifierWithSource(int modifier, ComponentSource source) {
            super(source);
            this.modifier = modifier;
        }

        public int getModifier() {
            return modifier;
        }

//        @NonNull
//        @Override
//        public String toString() {
//            return modifier + " (" + getSourceString() + ")";
//        }
    }

    public static class SpeedWithSource extends WithSource {
        private final int speed;
        private boolean active;

        SpeedWithSource(int speed, ComponentSource source) {
            super(source);
            this.speed = speed;
        }

        public int getSpeed() {
            return speed;
        }

        @Override
        protected boolean privateIsActive() {
            return active;
        }

        @Override
        protected void privateSetActive(boolean active) {
            this.active = active;
        }
    }

    public static class InitiativeWithSource extends WithSource {
        private final int initiative;

        InitiativeWithSource(int initiative, ComponentSource source) {
            super(source);
            this.initiative = initiative;
        }

        public int getInitiative() {
            return initiative;
        }
    }

    public static class PassivePerceptionWithSource extends WithSource {
        private final int passivePerception;

        PassivePerceptionWithSource(int initiative, ComponentSource source) {
            super(source);
            this.passivePerception = initiative;
        }

        public int getPassivePerception() {
            return passivePerception;
        }
    }

    public abstract static class WithSource {
        private final ComponentSource source;

        WithSource(ComponentSource source) {
            this.source = source;
        }

        public ComponentSource getSource() {
            return source;
        }

        @NonNull
        public String getSourceString(@NonNull Resources resources) {
            if (source == null) return resources.getString(R.string.no_component_source);
            return source.getAsSourceString(resources);
        }

        public String getShortSourceString(Resources resources) {
            if (source == null) return resources.getString(R.string.no_component_source);
            return source.getName();
        }

        public boolean isAdjustment() {
            return getSource() instanceof AdjustmentComponentSource;
        }

        public final boolean isActive() {
            return isAdjustment() ? ((AdjustmentComponentSource) getSource()).adjustment.applied : privateIsActive();
        }

        protected boolean privateIsActive() {
            return true;
        }

        public final void setActive(boolean active) {
            if (isAdjustment()) {
                ((AdjustmentComponentSource) getSource()).adjustment.applied = active;
            } else {
                privateSetActive(active);
            }
        }

        protected void privateSetActive(boolean active) {
            throw new RuntimeException("Shouldn't set the active state of a non-custom");
        }
    }

    protected static class HasArmorFunction implements ExpressionFunction {

        private final AbstractCharacter character;

        public HasArmorFunction(AbstractCharacter character) {
            this.character = character;
        }

        @Override
        public String getName() {
            return "wearingArmor";
        }

        @Override
        public ExpressionValue<?> evaluate(List<ExpressionValue<?>> arguments) {
            if (arguments.size() == 0) {
                final ArmorInfo armorInfo = new ArmorInfo(character.getArmor());
                return BooleanValue.valueOf(armorInfo.isWearingArmor);
            }
            StringValue value = (StringValue) arguments.get(0);
            String categoryName = value.getValue().toUpperCase().trim();
            for (CharacterArmor each : character.getArmor()) {
                if (!each.isBaseArmor()) continue;
                if (!each.isEquipped()) continue;
                if (each.getCategory().toUpperCase().trim().equals(categoryName)) {
                    return BooleanValue.TRUE;
                }
            }
            return BooleanValue.FALSE;
        }

        @Override
        public ExpressionType<?> validate(List<ExpressionType<?>> argumentTypes) {
            final int size = argumentTypes.size();
            if (size > 1) {
                throw new RuntimeException("Function '" + getName() + "' takes 0 or 1 string arguments- name of armor category");
            }
            if (size == 1) {
                if (argumentTypes.get(0) != ExpressionType.STRING_TYPE) {
                    throw new RuntimeException("Function '" + getName() + "' takes a string argument- name of armor category");
                }
            }
            return ExpressionType.BOOLEAN_TYPE;
        }
    }

    protected static class SpeedFunction implements ExpressionFunction {

        private final AbstractCharacter character;

        public SpeedFunction(AbstractCharacter character) {
            this.character = character;
        }

        @Override
        public String getName() {
            return "speed";
        }

        @Override
        public ExpressionValue<?> evaluate(List<ExpressionValue<?>> arguments) {
            StringValue value = (StringValue) arguments.get(0);
            String speedName = value.getValue();
            SpeedType speedType = EnumHelper.stringToEnum(speedName, SpeedType.class);
            return new NumberValue(character.getSpeed(speedType));
        }

        @Override
        public ExpressionType<?> validate(List<ExpressionType<?>> argumentTypes) {
            if (argumentTypes.size() != 1) {
                throw new RuntimeException("Function '" + getName() + "' only takes 1 string argument");
            }
            if (argumentTypes.get(0) != ExpressionType.STRING_TYPE) {
                throw new RuntimeException("Function '" + getName() + "' only takes 1 string argument");
            }
            return ExpressionType.NUMBER_TYPE;
        }
    }

    private class CheatComponentSource implements ComponentSource {
        private final int stringResId;

        CheatComponentSource(int stringResId) {
            this.stringResId = stringResId;
        }

        @NonNull
        public String getName() {
            return "-";
        }

        @NonNull
        @Override
        public String getSourceString(@NonNull Resources resources) {
            return resources.getString(stringResId);
        }

        @Override
        public String getAsSourceString(Resources resources) {
            return getName();
        }

        @Override
        public boolean originatesFrom(ComponentSource currentComponent) {
            return false;
        }

        @Nullable
        @Override
        public ComponentType getType() {
            return null;
        }

        @Nullable
        @Override
        public String getActiveFormula() {
            return null;
        }


    }

    private static class CharacterFunctionContext extends SimpleFunctionContext {
        CharacterFunctionContext(AbstractCharacter character) {
            super();
            add(new AbstractCharacter.HasEffectFunction(character));
            add(new HasArmorFunction(character));
            add(new SpeedFunction(character));
        }
    }

    private static class HasEffectFunction implements ExpressionFunction {

        private final AbstractCharacter character;

        public HasEffectFunction(AbstractCharacter character) {
            this.character = character;
        }

        @Override
        public String getName() {
            return "effect";
        }

        @Override
        public ExpressionValue<?> evaluate(List<ExpressionValue<?>> arguments) {
            StringValue value = (StringValue) arguments.get(0);
            return BooleanValue.valueOf(character.getEffectNamed(value.getValue()) != null);
        }

        @Override
        public ExpressionType<?> validate(List<ExpressionType<?>> argumentTypes) {
            if (argumentTypes.size() != 1) {
                throw new RuntimeException("Function '" + getName() + "' only takes 1 string argument");
            }
            if (argumentTypes.get(0) != ExpressionType.STRING_TYPE) {
                throw new RuntimeException("Function '" + getName() + "' only takes 1 string argument");
            }
            return ExpressionType.BOOLEAN_TYPE;
        }
    }

    public void addItem(CharacterItem item) {
        item.setId(itemIdSequence++);
        items.add(item);
    }

    @NonNull
    public List<CharacterItem> getItems() {
        return items;
    }

    @NonNull
    public List<CharacterItem> getItemsNamed(String name) {
        List<CharacterItem> result = new ArrayList<>();
        String upperName = name.toUpperCase();
        for (CharacterItem each : items) {
            if (each.getName().toUpperCase().equals(upperName)) result.add(each);
        }
        return result;
    }

    public CharacterItem getItemById(long id) {
        for (CharacterItem each : items) {
            if (each.getId() == id) return each;
        }
        return null;
    }

    public CharacterWeapon getWeaponById(long id) {
        for (CharacterWeapon each : weapons) {
            if (each.getId() == id) return each;
        }
        return null;
    }

    public CharacterArmor getArmorById(long id) {
        for (CharacterArmor each : armor) {
            if (each.getId() == id) return each;
        }
        return null;
    }

    public void addWeapon(CharacterWeapon weapon) {
        weapon.setId(itemIdSequence++);
        weapons.add(weapon);
    }

    @NonNull
    public List<CharacterWeapon> getWeapons() {
        return weapons;
    }

    public AbstractCharacterAbilityDeriver getAbilityDeriver(ComponentVisitor visitor, boolean skipFeatures) {
        return new AbstractCharacterAbilityDeriver(visitor, skipFeatures);
    }

}

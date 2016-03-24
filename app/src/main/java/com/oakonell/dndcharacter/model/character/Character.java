package com.oakonell.dndcharacter.model.character;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.character.item.CharacterArmor;
import com.oakonell.dndcharacter.model.character.item.CharacterItem;
import com.oakonell.dndcharacter.model.character.item.CharacterWeapon;
import com.oakonell.dndcharacter.model.character.rest.AbstractRestRequest;
import com.oakonell.dndcharacter.model.character.rest.LongRestRequest;
import com.oakonell.dndcharacter.model.character.rest.ShortRestRequest;
import com.oakonell.dndcharacter.model.character.spell.CharacterSpell;
import com.oakonell.dndcharacter.model.character.stats.BaseStatsType;
import com.oakonell.dndcharacter.model.character.stats.SkillBlock;
import com.oakonell.dndcharacter.model.character.stats.SkillType;
import com.oakonell.dndcharacter.model.character.stats.StatBlock;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.classes.AClass;
import com.oakonell.dndcharacter.model.components.Feature;
import com.oakonell.dndcharacter.model.components.IFeatureAction;
import com.oakonell.dndcharacter.model.components.Proficiency;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.model.components.RefreshType;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;
import com.oakonell.expression.Expression;
import com.oakonell.expression.ExpressionContext;
import com.oakonell.expression.ExpressionType;
import com.oakonell.expression.context.SimpleFunctionContext;
import com.oakonell.expression.context.SimpleVariableContext;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/**
 * Created by Rob on 10/21/2015.
 */
public class Character {
    // more fluid data
    @Element(required = false)
    private int hp;

    @Element(required = false)
    private int xp;

    @Element(required = false)
    @Deprecated
    // TODO delete- will need to dump DB
            int tempHpMax;
    @Element(required = false)
    private int tempHp;
    @NonNull
    @ElementMap(entry = "feature", key = "name", value = "uses", required = false)
    private Map<String, Integer> usedFeatures = new HashMap<>();
    // relatively static data
    @Element(required = false)
    private String name;
    @Element(required = false)
    private CharacterBackground background;
    @Element(required = false)
    private CharacterRace race;
    @NonNull
    @ElementList(required = false)
    private List<CharacterClass> classes = new ArrayList<>();
    @ElementMap(entry = "stat", key = "name", value = "value", required = false)
    private Map<StatType, Integer> baseStats = new HashMap<>();

    @Element(required = false)
    private BaseStatsType statsType;
    @Element(required = false)
    private String notes;
    @NonNull
    @ElementMap(entry = "hitDie", key = "die", value = "uses", required = false)
    private Map<Integer, Integer> hitDieUses = new HashMap<>();

    @Element(required = false)
    private String backstory;

    @NonNull
    @ElementList(required = false)
    private List<CharacterItem> items = new ArrayList<>();

    @NonNull
    @ElementList(required = false)
    private List<CharacterArmor> armor = new ArrayList<>();

    @NonNull
    @ElementList(required = false)
    private List<CharacterWeapon> weapons = new ArrayList<>();

    @NonNull
    @ElementList(required = false)
    private List<CharacterSpell> cantrips = new ArrayList<>();

    @NonNull
    @ElementMap(entry = "spellLevel", key = "level", value = "spells", required = false)
    private Map<Integer, SpellListWrapper> spellsForLevel = new HashMap<>();


    // Money
    @Element(required = false)
    private int gold;
    @Element(required = false)
    private int copper;
    @Element(required = false)
    private int silver;
    @Element(required = false)
    private int electrum;
    @Element(required = false)
    private int platinum;
    @NonNull
    @ElementList(required = false)
    private List<CharacterEffect> effects = new ArrayList<>();

    @NonNull
    @ElementMap(entry = "spellSlotsUsed", key = "level", value = "used", required = false)
    private Map<Integer, Integer> spellSlotsUsed = new HashMap<>();

    @Element(required = false)
    private String age;
    @Element(required = false)
    private String height;
    @Element(required = false)
    private String weight;

    @Element(required = false)
    private String skin;
    @Element(required = false)
    private String hair;
    @Element(required = false)
    private String eyes;

    @NonNull
    @Element(required = false)
    private SpeedType visibleSpeedType = SpeedType.WALK;

    @Element(required = false)
    private int deathSaveFails;
    @Element(required = false)
    private int deathSaveSuccesses;
    @Element(required = false)
    private boolean stable;
    @Element(required = false)
    private Alignment alignment;

    @Element(required = false)
    private long itemIdSequence;


    @NonNull
    @ElementMap(entry = "adjustment", key = "key", value = "value", required = false)
    private Map<CustomAdjustmentType, CustomAdjustments> adjustments = new HashMap<>();

    public Character() {
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

    public int getMaxHP() {
        int hp = 0;
        StatBlock conBlock = getStatBlock(StatType.CONSTITUTION);
        int conMod = conBlock.getModifier();

        for (CharacterClass each : classes) {
            hp += each.getHpRoll();
            hp += conMod;
        }
        // TODO what about race, like dwarf, that effects hp
        final int value[] = new int[]{hp};
        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull ICharacterComponent component) {
                value[0] += evaluateFormula(component.getHpFormula(), null);
            }
        };
        deriver.derive(this, "hp mods");

        return value[0];
    }

    @NonNull
    public StatBlock getStatBlock(StatType type) {
        return new StatBlock(this, type);
    }

    @NonNull
    public SkillBlock getSkillBlock(SkillType type) {
        return new SkillBlock(this, type);
    }

    @Nullable
    public String getBackgroundName() {
        if (background == null) return null;
        return background.getName();
    }

    @Nullable
    public String getRaceName() {
        if (race == null) return null;
        return race.getName();
    }

    @Nullable
    public String getSubRaceName() {
        if (race == null) return null;
        return race.getSubraceName();
    }

    public int getCharacterLevel() {
        return classes.size();
    }

    @NonNull
    public String getClassesString() {
        Map<String, Integer> classLevels = getClassLevels();
        if (classLevels.isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Integer> entry : classLevels.entrySet()) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            Integer level = entry.getValue();
            String name = entry.getKey();

            String subclass = getSubclassFor(name);
            if (subclass != null) {
                name += "[" + subclass + "]";
            }

            builder.append(name + " " + level);
        }
        return builder.toString();
    }

    @NonNull
    public Map<String, Integer> getClassLevels() {
        Map<String, Integer> classLevels = new LinkedHashMap<>();
        for (CharacterClass each : classes) {
            Integer level = classLevels.get(each.getName());
            if (level == null) level = 0;
            classLevels.put(each.getName(), level + 1);
        }
        return classLevels;
    }

    @Nullable
    public String getSubclassFor(String className) {
        for (CharacterClass each : classes) {
            if (!each.getName().equals(className)) continue;
            String subclass = each.getSubclassName();
            if (subclass != null) return subclass;
        }
        return null;
    }

    @NonNull
    public String getLanguagesString() {
        Set<String> languages = getLanguages();
        StringBuilder builder = new StringBuilder();
        for (Iterator<String> iter = languages.iterator(); iter.hasNext(); ) {
            String language = iter.next();
            builder.append(language);
            if (iter.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }


    @NonNull
    public List<CharacterClass> getClasses() {
        return classes;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getCopper() {
        return copper;
    }

    public void setCopper(int copper) {
        this.copper = copper;
    }

    public int getSilver() {
        return silver;
    }

    public void setSilver(int silver) {
        this.silver = silver;
    }

    public int getElectrum() {
        return electrum;
    }

    public void setElectrum(int electrum) {
        this.electrum = electrum;
    }

    public int getPlatinum() {
        return platinum;
    }

    public void setPlatinum(int platinum) {
        this.platinum = platinum;
    }

    @NonNull
    public Set<String> getLanguages() {
        Set<String> result = new HashSet<>();
        for (LanguageWithSource each : deriveLanguages()) {
            result.add(each.language);
        }
        return result;
    }

    @NonNull
    public List<LanguageWithSource> deriveLanguages() {
        final List<LanguageWithSource> languages = new ArrayList<>();
        CharacterAbilityDeriver languagesDeriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull ICharacterComponent component) {
                for (String each : component.getLanguages()) {
                    LanguageWithSource row = new LanguageWithSource(each, component);
                    languages.add(row);
                }
            }
        };
        languagesDeriver.derive(this, "languages");
        return languages;
    }

    public boolean isProficientWith(@NonNull CharacterArmor armor) {
        return isProficientWithItem(ProficiencyType.ARMOR, armor);
    }

    public boolean isProficientWith(@NonNull CharacterWeapon weapon) {
        return isProficientWithItem(ProficiencyType.WEAPON, weapon);
    }

    protected boolean isProficientWithItem(@NonNull ProficiencyType type, @NonNull CharacterItem item) {
        String name = item.getName().toUpperCase();
        String category = item.getCategory();
        if (category == null) {
            category = "";
        } else {
            category = category.toUpperCase();
        }
        return isProficientWithItem(type, name, category);
    }

    public boolean isProficientWithItem(@NonNull ProficiencyType type, @NonNull String name, @NonNull String category) {
        List<ToolProficiencyWithSource> list = deriveToolProficiencies(type);
        for (ToolProficiencyWithSource each : list) {
            final Proficiency proficiency = each.getProficiency();
            if (proficiency.getProficient().getMultiplier() < 1) continue;
            String eachName = proficiency.getName();
            if (eachName == null) {
                eachName = "";
            } else {
                eachName = eachName.toUpperCase();
            }
            String eachCategory = proficiency.getCategory();
            if (eachCategory == null) {
                eachCategory = "";
            } else {
                eachCategory = eachCategory.toUpperCase();
            }

            if (name.equalsIgnoreCase(eachName) || category.equalsIgnoreCase(eachCategory)) {
                return true;
            }
        }
        return false;
    }

    public CharacterRace getRace() {
        return race;
    }

    public CharacterBackground getBackground() {
        return background;
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

    public void removeEffect(CharacterEffect effect) {
        effects.remove(effect);
    }

    public boolean anyContextFeats(FeatureContext context) {
        FeatureContextArgument withArg = new FeatureContextArgument(context);
        for (CharacterEffect each : getEffects()) {
            if (each.isInContext(withArg)) return true;
        }
        for (FeatureInfo each : getFeatureInfos()) {
            if (each.isInContext(withArg)) return true;
        }
        return false;
    }

    public int getCantripsKnownForClass(@NonNull final String className) {
        final int[] count = new int[]{0};
        for (CharacterSpell each : cantrips) {
            if (each.getSource() == ComponentType.CLASS && className.equals(each.getOwnerName())) {
                if (!each.countsAsKnown()) continue;
                count[0]++;
            }
        }
        for (CharacterClass eachClass : classes) {
            if (!eachClass.getName().equals(className)) continue;
            for (CharacterSpell each : eachClass.getCantrips()) {
                if (each.getSource() == ComponentType.CLASS && className.equals(each.getOwnerName())) {
                    if (!each.countsAsKnown()) continue;
                    count[0]++;
                }
            }

        }
        return count[0];
    }

    public int getSpellsKnownForClass(@NonNull String className) {
        final int[] known = new int[]{0};

        for (Map.Entry<Integer, SpellListWrapper> entry : spellsForLevel.entrySet()) {
            List<CharacterSpell> spells = entry.getValue().spells;
            for (CharacterSpell each : spells) {
                if (!each.countsAsKnown()) continue;
                if (each.getSource() == ComponentType.CLASS && className.equals(each.getOwnerName())) {
                    known[0]++;
                }
            }
        }

        for (CharacterClass eachClass : classes) {
            if (!eachClass.getName().equals(className)) continue;
            List<CharacterSpell> spells = eachClass.getSpells();
            for (CharacterSpell each : spells) {
                if (!each.countsAsKnown()) continue;
                if (each.getSource() == ComponentType.CLASS && className.equals(each.getOwnerName())) {
                    known[0]++;
                }
            }

        }

        return known[0];
    }

    public int getSpellsPreparedForClass(@NonNull String className) {
        int prepared = 0;
        for (Map.Entry<Integer, SpellListWrapper> entry : spellsForLevel.entrySet()) {
            List<CharacterSpell> spells = entry.getValue().spells;
            for (CharacterSpell each : spells) {
                if (each.getSource() == ComponentType.CLASS && className.equals(each.getOwnerName())) {
                    if (each.isPrepared()) prepared++;
                }
            }
        }
        return prepared;
    }

    public void useSpellSlot(int level) {
        Integer used = spellSlotsUsed.get(level);
        if (used == null) used = 0;
        spellSlotsUsed.put(level, used + 1);
    }

    public void setSpellSlots(int level, int slotsUsed) {
        spellSlotsUsed.put(level, slotsUsed);
    }

    public boolean canChooseAbilityScoreImprovement(AClass model, int classLevel) {
        // all classes have standard ASI levels!
        switch (classLevel) {
            case 4:
            case 8:
            case 12:
            case 16:
            case 19:
                return true;
            default:
                return false;
        }
    }

    public void stabilize() {
        stable = true;
        deathSaveFails = 0;
        deathSaveSuccesses = 0;
    }

    public boolean isStable() {
        return stable;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
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

    @NonNull
    public List<ArmorClassWithSource> deriveRootAcs() {
        final List<ArmorClassWithSource> modifyingAcs = deriveModifyingAcs();
        final List<ArmorClassWithSource> rootAcs = deriveRootAcs(modifyingAcs);
        modifyRootAcs(this, modifyingAcs, rootAcs);
        return rootAcs;
    }


    @NonNull
    public List<ArmorClassWithSource> deriveRootAcs(List<ArmorClassWithSource> modifyingAcs) {
        List<ArmorClassWithSource> result = new ArrayList<>();
        String baseFormula = "10 + dexterityMod";
        int baseValue = evaluateFormula(baseFormula, null);
        ArmorClassWithSource unarmored = new ArmorClassWithSource(baseFormula, baseValue, null, false, false);
        result.add(unarmored);

        // multiple here will really just take the highest ?? at runtime
        for (CharacterClass eachClass : classes) {
            for (FeatureInfo each : eachClass.getFeatures(this)) {
                if (!each.isBaseArmor()) continue;

                String acFormula = each.getBaseAcFormula();
                if (acFormula != null) {
                    SimpleVariableContext variableContext = new SimpleVariableContext();
                    each.getSource().addExtraFormulaVariables(variableContext, this);
                    int value = evaluateFormula(acFormula, variableContext);
                    ArmorClassWithSource featureAc = new ArmorClassWithSource(acFormula, value, each, false, false);

                    result.add(featureAc);
                }
            }
        }
        for (CharacterEffect each : getEffects()) {
            if (!each.isBaseArmor()) continue;

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


    public static void modifyRootAcs(@NonNull Character character, @NonNull List<ArmorClassWithSource> modifyingAcs, @NonNull List<ArmorClassWithSource> rootAcs) {
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

    @NonNull
    public List<ArmorClassWithSource> deriveModifyingAcs() {
        final List<ArmorClassWithSource> result = new ArrayList<>();

        boolean usingShield = false;
        boolean wearingArmor = false;
        for (CharacterArmor each : getArmor()) {
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
        final boolean isWearingArmor = wearingArmor;
        final boolean isUsingShield = usingShield;
        // multiple here will really just take the highest ?? at runtime
        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            @Override
            void visitComponent(@NonNull ICharacterComponent component) {
                if (component.isBaseArmor()) return;

                String acFormula = component.getModifyingAcFormula();
                if (acFormula == null) return;

                SimpleVariableContext variableContext = new SimpleVariableContext();
                variableContext.setBoolean("armor", isWearingArmor);
                variableContext.setBoolean("shield", isUsingShield);
                component.addExtraFormulaVariables(variableContext, Character.this);

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

        deriver.derive(this, "AC modifiers");


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

    public SavedChoices getBackgroundChoices() {
        if (background == null) return new SavedChoices();
        return background.getSavedChoices();
    }

    public void setBackground(CharacterBackground background) {
        this.background = background;
    }

    public SavedChoices getRaceChoices() {
        if (race == null) return new SavedChoices();
        return race.getSavedChoices();
    }

    public void setRace(CharacterRace race) {
        this.race = race;
    }

    public SavedChoices getSubRaceChoices() {
        if (race == null) return new SavedChoices();
        return race.getSubRaceChoices();
    }

    @Nullable
    public String getDisplayRaceName() {
        if (race == null) return null;

        String displayName;
        String mainRaceName = race.getName();
        displayName = mainRaceName;
        String subRacename = race.getSubraceName();
        if (subRacename != null) {
            if (!subRacename.contains(mainRaceName)) {
                displayName = subRacename + "(" + mainRaceName + ")";
            } else {
                displayName = subRacename;
            }
        }
        return displayName;
    }

    public String getSpecialtyTitle() {
        return background.getSpecialtyTitle();
    }

    @Nullable
    public String getSpecialty() {
        if (background == null) return null;
        return background.getSpecialty();
    }

    @NonNull
    public String getArmorProficiencyString() {
        final ProficiencyType type = ProficiencyType.ARMOR;
        return getToolProficiencyString(type);
    }

    @NonNull
    private String getToolProficiencyString(@NonNull ProficiencyType type) {
        List<ToolProficiencyWithSource> list = deriveToolProficiencies(type);

        // first categories, so can pull out specifics if category already covered
        Map<String, Proficient> categories = new TreeMap<>();
        for (ToolProficiencyWithSource each : list) {
            Proficiency proficiency = each.proficient;
            if (proficiency.getCategory() == null) continue;
            String text = "(" + proficiency.getCategory() + ")";
            final Proficient proficient = each.getProficiency().getProficient();
            final Proficient current = categories.get(text);
            if (current == null || current.getMultiplier() < proficient.getMultiplier()) {
                categories.put(text, proficient);
            }
        }

        Set<String> upperCaseCategories = new HashSet<>();
        for (String each : categories.keySet()) {
            upperCaseCategories.add(each.toUpperCase());
        }

        // next specific items
        // TODO ugh.. specific could be a higher proficiency..
        Map<String, Proficient> specifics = new TreeMap<>();
        for (ToolProficiencyWithSource each : list) {
            Proficiency proficiency = each.proficient;
            if (proficiency.getCategory() != null) continue;
            String text = proficiency.getName();

            ItemRow item = new Select()
                    .from(ItemRow.class).where("UPPER(name) = ?", text.toUpperCase()).executeSingle();
            if (item != null) {
                if (item.getCategory() != null) {
                    if (upperCaseCategories.contains(item.getCategory().toUpperCase())) continue;
                }
            }

            final Proficient proficient = each.getProficiency().getProficient();
            Proficient current = specifics.get(text);
            if (current == null || current.getMultiplier() < proficient.getMultiplier()) {
                specifics.put(text, proficient);
            }
        }

        StringBuilder builder = new StringBuilder();
        String comma = appendToolProficiencies(builder, "", categories.entrySet());
        appendToolProficiencies(builder, comma, specifics.entrySet());
        return builder.toString();
    }

    private String appendToolProficiencies(@NonNull StringBuilder builder, String comma, @NonNull Set<Map.Entry<String, Proficient>> entries) {
        for (Map.Entry<String, Proficient> each : entries) {
            builder.append(comma);
            comma = ", ";
            builder.append(each.getKey());
            final Proficient proficient = each.getValue();
            if (proficient.getMultiplier() != 1) {
                builder.append("(" + proficient + ")");
            }
        }
        return comma;
    }

    @NonNull
    public String getWeaponsProficiencyString() {
        final ProficiencyType type = ProficiencyType.WEAPON;
        return getToolProficiencyString(type);
    }

    @NonNull
    public String getToolsProficiencyString() {
        final ProficiencyType type = ProficiencyType.TOOL;
        return getToolProficiencyString(type);
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

        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull ICharacterComponent component) {
                int value = component.getStatModifier(type);
                if (value > 0) {
                    ModifierWithSource base = new ModifierWithSource(value, component);
                    result.add(base);
                }
            }
        };
        deriver.derive(this, "stat " + type.name());

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
    public List<ProficientWithSource> deriveSkillProciencies(@NonNull final SkillType type) {
        final List<ProficientWithSource> result = new ArrayList<>();

        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull ICharacterComponent component) {
                Proficient proficient = component.getSkillProficient(type);
                if (proficient != Proficient.NONE) {
                    ProficientWithSource reason = new ProficientWithSource(proficient, component);
                    result.add(reason);
                }
            }
        };
        deriver.derive(this, "skill profs " + type.name());

        return result;
    }

    @NonNull
    public List<ProficientWithSource> deriveSaveProficiencies(@NonNull final StatType type) {
        final List<ProficientWithSource> result = new ArrayList<>();

        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull ICharacterComponent component) {
                Proficient proficient = component.getSaveProficient(type);
                if (proficient != Proficient.NONE) {
                    ProficientWithSource reason = new ProficientWithSource(proficient, component);
                    result.add(reason);
                }
            }
        };
        deriver.derive(this, "save throw prof " + type.name());

        return result;
    }

    public Proficient deriveSkillProciency(@NonNull final SkillType type) {
        final Proficient proficient[] = new Proficient[1];
        proficient[0] = Proficient.NONE;

        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull ICharacterComponent component) {
                Proficient compProficient = component.getSkillProficient(type);
                if (compProficient.getMultiplier() > proficient[0].getMultiplier()) {
                    proficient[0] = compProficient;
                }
            }
        };
        deriver.derive(this, "skill prof " + type.name());

        return proficient[0];
    }

    public int getProficiency() {
        if (classes == null) {
            return 2;
        }
        int charLevel = getCharacterLevel();
        if (charLevel < 5) return 2;
        if (charLevel < 9) return 3;
        if (charLevel < 13) return 4;
        if (charLevel < 17) return 5;
        return 6;
    }

    public Proficient deriveSaveProciency(@NonNull final StatType type) {
        final Proficient proficient[] = new Proficient[1];
        proficient[0] = Proficient.NONE;
        final List<LanguageWithSource> languages = new ArrayList<>();
        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull ICharacterComponent component) {
                Proficient compProficient = component.getSaveProficient(type);
                if (compProficient.getMultiplier() > proficient[0].getMultiplier()) {
                    proficient[0] = compProficient;
                }
            }
        };
        deriver.derive(this, "save prof " + type.name());
        return proficient[0];
    }

    @NonNull
    public Collection<FeatureInfo> getFeatureInfos() {
        final Map<String, FeatureInfo> map = new HashMap<>();

        // features shouldn't contain features, and any effects are not automatic, but applied on use
        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver(true) {
            protected void visitComponent(@NonNull ICharacterComponent component) {
                // handle extend/replace features
                component.addFeatureInfo(map, Character.this);
            }
        };
        deriver.derive(this, "Feature infos");

        return map.values();
    }

    public boolean evaluateBooleanFormula(@Nullable String formula, @Nullable SimpleVariableContext variableContext) {
        // TODO formula might reference stats and such
        if (formula == null || formula.length() == 0) return false;
        variableContext = getPopulatedVariableContext(variableContext);

        try {
            Expression<Boolean> expression = Expression.parse(formula, ExpressionType.BOOLEAN_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext));
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
        variableContext.setNumber("level", getClasses().size());
        return variableContext;
    }

    public int evaluateFormula(@Nullable String formula, @Nullable SimpleVariableContext variableContext) {
        // TODO formula might reference stats and such
        if (formula == null || formula.length() == 0) return 0;
        variableContext = getPopulatedVariableContext(variableContext);

        Expression<Integer> expression = Expression.parse(formula, ExpressionType.NUMBER_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext));
        return expression.evaluate();
    }

    public int getUses(@NonNull String featureName) {
        Integer uses = usedFeatures.get(featureName);
        if (uses == null) return 0;
        return uses;
    }

    public int getUsesRemaining(@NonNull FeatureInfo feature) {
        return feature.evaluateMaxUses(this) - getUses(feature.getName());
    }

    public void useFeature(@NonNull FeatureInfo feature, int amount) {
        // TODO apply known effects from feature
        int uses = getUses(feature.getName());
        uses = uses + amount;
        usedFeatures.put(feature.getName(), uses);
    }

    public void useFeatureAction(@NonNull FeatureInfo feature, @NonNull IFeatureAction action, Map<Feature.FeatureEffectVariable, String> variableValues) {
        useFeature(feature, action.getCost());
        action.applyToCharacter(this, variableValues);
    }

    public String getNotes() {
        return notes;
    }

    public void longRest(@NonNull LongRestRequest request) {
        hp = Math.min(hp + request.getHealing(), getMaxHP());

        // restore hit die / 2
        for (Map.Entry<Integer, Integer> entry : request.getHitDiceToRestore().entrySet()) {
            int die = entry.getKey();
            int requestNumToRestore = entry.getValue();

            Integer uses = hitDieUses.get(die);
            if (uses == null) uses = 0;
            uses -= requestNumToRestore;
            if (uses <= 0) {
                hitDieUses.remove(die);
            } else {
                hitDieUses.put(die, uses);
            }
        }

        // temp HP disappear, unless a spell/effect?
        tempHp = 0;


        //refresh features
        resetFeatures(request);
        resetSpellSlots(request);
    }

    private void resetSpellSlots(@NonNull AbstractRestRequest request) {
        final Map<Integer, Integer> spellSlotResets = request.getSpellSlotResets();
        for (Map.Entry<Integer, Integer> entry : spellSlotResets.entrySet()) {
            Integer level = entry.getKey();
            Integer toRestore = entry.getValue();
            Integer used = spellSlotsUsed.get(level);
            if (used == null) used = 0;
            used = Math.max(0, used - toRestore);
            if (used == 0) {
                spellSlotsUsed.remove(level);
            } else {
                spellSlotsUsed.put(level, used);
            }
        }
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

        for (Map.Entry<Integer, Integer> entry : request.getHitDieUses().entrySet()) {
            int die = entry.getKey();
            int requestUses = entry.getValue();

            Integer uses = hitDieUses.get(die);
            if (uses == null) uses = 0;
            uses += requestUses;
            hitDieUses.put(die, uses);
        }

        resetFeatures(request);
        resetSpellSlots(request);
    }

    @NonNull
    public String getHitDiceString() {
        if (classes == null) return "";

        List<HitDieRow> dice = getHitDiceCounts();
        StringBuilder builder = new StringBuilder();
        Iterator<HitDieRow> iter = dice.iterator();
        while (iter.hasNext()) {
            HitDieRow entry = iter.next();
            builder.append("(" + entry.numDiceRemaining + "/" + entry.totalDice + ")" + "d" + entry.dieSides);
            if (iter.hasNext()) builder.append(", ");
        }
        return builder.toString();
    }

    @NonNull
    public List<HitDieRow> getHitDiceCounts() {
        Map<Integer, Integer> dice = new LinkedHashMap<>();
        if (classes == null) return Collections.emptyList();

        for (CharacterClass each : classes) {
            Integer dieCount = dice.get(each.getHitDie());
            if (dieCount == null) dieCount = 0;
            dice.put(each.getHitDie(), dieCount + 1);
        }
        List<HitDieRow> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : dice.entrySet()) {
            HitDieRow row = new HitDieRow();
            row.dieSides = entry.getKey();
            row.totalDice = entry.getValue();
            // TODO
            Integer uses = hitDieUses.get(row.dieSides);
            if (uses != null) {
                row.numDiceRemaining = row.totalDice - uses;
            } else {
                row.numDiceRemaining = row.totalDice;
            }
            result.add(row);
        }
        return result;
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

    public String getBackstory() {
        return backstory;
    }

    public void setBackstory(String backstory) {
        this.backstory = backstory;
    }

    public String getPersonalityTrait() {
        if (background == null) return "";
        return background.getPersonalityTrait();
    }

    public void setPersonalityTrait(String personalityTrait) {
        background.setPersonalityTrait(personalityTrait);
    }

    public String getIdeals() {
        if (background == null) return "";
        return background.getIdeal();
    }

    public void setIdeals(String ideals) {
        background.setIdeal(ideals);
    }

    public String getBonds() {
        if (background == null) return "";
        return background.getBonds();
    }

    public void setBonds(String bonds) {
        background.setBond(bonds);
    }

    public String getFlaws() {
        if (background == null) return "";
        return background.getFlaw();
    }

    public void setFlaws(String flaws) {
        background.setFlaw(flaws);
    }

    public void setTraitSavedChoiceToCustom(String trait) {
        background.setTraitSavedChoiceToCustom(trait);
    }

    @NonNull
    public List<ToolProficiencyWithSource> deriveToolProficiencies(@NonNull final ProficiencyType type) {
        final List<ToolProficiencyWithSource> result = new ArrayList<>();

        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull ICharacterComponent component) {
                List<Proficiency> profs = component.getToolProficiencies(type);
                for (Proficiency each : profs) {
                    ToolProficiencyWithSource newRow = new ToolProficiencyWithSource(each, component);
                    result.add(newRow);
                }
            }
        };
        deriver.derive(this, "tools profs " + type.name());

        return result;
    }

    public void addEffect(CharacterEffect characterEffect) {
        effects.add(characterEffect);
    }

    @NonNull
    public List<CharacterEffect> getEffects() {
        return effects;
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
    public List<CharacterArmor> getArmor() {
        return armor;
    }

    public void addArmor(CharacterArmor armor) {
        armor.setId(itemIdSequence++);
        this.armor.add(armor);
    }

    @NonNull
    public List<CharacterWeapon> getWeapons() {
        return weapons;
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

    public static class LanguageWithSource extends WithSource {
        private final String language;

        LanguageWithSource(String language, ComponentSource source) {
            super(source);
            this.language = language;
        }

        public String getLanguage() {
            return language;
        }

//        @NonNull
//        @Override
//        public String toString() {
//            return language + " (" + getSourceString() + ")";
//        }


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

    public static class ProficientWithSource extends WithSource {
        private final Proficient proficient;

        ProficientWithSource(Proficient proficient, ComponentSource source) {
            super(source);
            this.proficient = proficient;
        }

        public Proficient getProficient() {
            return proficient;
        }

//        @NonNull
//        @Override
//        public String toString() {
//            return proficient + " (" + getSourceString() + ")";
//        }

    }

    public static class CharacterSpellWithSource extends WithSource {
        private final CharacterSpell spell;

        CharacterSpellWithSource(CharacterSpell spell, ComponentSource source) {
            super(source);
            this.spell = spell;
        }

        public CharacterSpell getSpell() {
            return spell;
        }

//        @NonNull
//        @Override
//        public String toString() {
//            return proficient + " (" + getSourceString() + ")";
//        }

    }

    public static class HitDieRow {
        public int dieSides;
        public int numDiceRemaining;
        public int totalDice;

        @NonNull
        public String toString() {
            return "(" + numDiceRemaining + "/" + totalDice + ")" + "d" + dieSides;
        }
    }

    public static class ToolProficiencyWithSource extends WithSource {
        private final Proficiency proficient;

        ToolProficiencyWithSource(Proficiency proficient, ComponentSource source) {
            super(source);
            this.proficient = proficient;
        }

        public Proficiency getProficiency() {
            return proficient;
        }

//        @NonNull
//        @Override
//        public String toString() {
//            if (proficient.getCategory() != null) {
//                return "[" + proficient.getCategory() + "] (" + getSourceString() + ")";
//            }
//            return proficient.getName() + " (" + getSourceString() + ")";
//        }

    }

    public static class SpeedWithSource extends WithSource {
        private final int speed;

        SpeedWithSource(int speed, ComponentSource source) {
            super(source);
            this.speed = speed;
        }

        public int getSpeed() {
            return speed;
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
            return source.getSourceString(resources);
        }

        public boolean isAdjustment() {
            return getSource() instanceof AdjustmentComponentSource;
        }

        public boolean isActive() {
            return isAdjustment() ? ((AdjustmentComponentSource) getSource()).adjustment.applied : true;
        }

        public void setActive(boolean active) {
            if (isAdjustment()) {
                ((AdjustmentComponentSource) getSource()).adjustment.applied = active;
            } else {
                throw new RuntimeException("Shouldn't set the active state of a non-custom");
            }
        }
    }

    public void addExperience(int xp) {
        this.xp += xp;
    }

    public int getXp() {
        return xp;
    }

    @NonNull
    public List<CharacterSpell> getSpells(int level) {
        if (level == 0) {
            return cantrips;
        }
        if (spellsForLevel == null) {
            spellsForLevel = new HashMap<>();
        }
        SpellListWrapper spells = spellsForLevel.get(level);
        if (spells == null) {
            spells = new SpellListWrapper();
            spellsForLevel.put(level, spells);
        }
        return spells.spells;
    }

    @NonNull
    public List<SpellLevelInfo> getSpellInfos() {
        // cantrips can come from random components- background, race
        final List<SpellLevelInfo> spellsLevels = new ArrayList<>();
        final SpellLevelInfo cantrips = new SpellLevelInfo();

        CharacterAbilityDeriver cantripDeriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull ICharacterComponent component) {
                for (CharacterSpell each : component.getCantrips()) {
                    cantrips.getSpellInfos().add(new CharacterSpellWithSource(each, component));
                }
            }
        };
        cantripDeriver.derive(this, "cantrip infos");


        for (CharacterSpell each : this.cantrips) {
            cantrips.spellInfos.add(new CharacterSpellWithSource(each, null));
        }

        final List<SpellLevelInfo> result = new ArrayList<>();
        result.add(cantrips);

        // go through class levels for usable spell levels?

        final Collection<CastingClassInfo> casterClassInfo = getCasterClassInfo();
        if (casterClassInfo.size() == 0) {
            return result;
        }
        int maxSlotLevel = 0;
        final Map<String, Integer> classLevels = getClassLevels();
        if (classLevels.size() > 1) {
            // multiclass
            Map<String, Integer> multiclassCasterSlots = new HashMap<>();
            List<CastingClassInfo> specialSlotClasses = new ArrayList<>();
            for (CastingClassInfo each : casterClassInfo) {
                String factor = each.getMulticlassCastorFactor();
                if (factor.equals("-1")) {
                    specialSlotClasses.add(each);
                    continue;
                }
                Integer factorLevel = multiclassCasterSlots.get(factor);
                if (factorLevel == null) factorLevel = 0;
                factorLevel += classLevels.get(each.getOwningClassName());
                multiclassCasterSlots.put(factor, factorLevel);
            }
            int effectiveCasterLevel = 0;
            for (Map.Entry<String, Integer> each : multiclassCasterSlots.entrySet()) {
                final String factorFormula = each.getKey();

                final Integer value = each.getValue();
                int roundedLevel = evaluateFormula(factorFormula + " * " + value, null);
                effectiveCasterLevel += roundedLevel;
            }
            addMulticlassSpellSlotLevels(result, effectiveCasterLevel, specialSlotClasses);
            maxSlotLevel = result.size() - 1;
        } else {
            final CastingClassInfo casterInfo = casterClassInfo.iterator().next();
            maxSlotLevel = casterInfo.getMaxSpellLevel();
            for (int i = 1; i <= maxSlotLevel; i++) {
                SpellLevelInfo level = new SpellLevelInfo();
                level.level = i;
                final String slots = casterInfo.getSlotMap().get(i);
                if (slots != null) {
                    SimpleVariableContext variableContext = new SimpleVariableContext();
                    variableContext.setNumber("classLevel", casterInfo.classLevel);
                    level.maxSlots = evaluateFormula(slots, variableContext);
                } else {
                    level.maxSlots = 0;
                }

                result.add(level);
            }
        }


        for (int i = 1; i <= maxSlotLevel; i++) {
            Integer used = spellSlotsUsed.get(i);
            if (used == null) used = 0;
            SpellLevelInfo level = result.get(i);
            level.slotsAvailable = level.maxSlots - used;

            final List<CharacterSpellWithSource> spells = level.getSpellInfos();
            for (CharacterSpell each : getSpells(i)) {
                spells.add(new CharacterSpellWithSource(each, null));
            }
        }

        CharacterAbilityDeriver spellDeriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull ICharacterComponent component) {
                for (CharacterSpell each : component.getSpells()) {
                    int level = each.getLevel();
                    if (level == 0) {
                        // the level was unknown at creation time... assume 1st level?
                        level = 1;
                    }
                    SpellLevelInfo levelInfo = result.get(level);
                    levelInfo.getSpellInfos().add(new CharacterSpellWithSource(each, component));
                }
            }
        };
        spellDeriver.derive(this, "spell infos");

//
//        // spell levels / slots
//        // warlock spell slots count by themselves
//        // others multiclass
//        for (int i = 1; i <= 2; i++) {
//            SpellLevelInfo level = new SpellLevelInfo();
//            level.level = i;
//            if (i > 0) {
//                // TODO
//                level.maxSlots = 4 / i;
//                level.slotsAvailable = level.maxSlots;
//            }
//            if (i == 1) {
////                SpellInfo info = new SpellInfo();
////                info.preparable = true;
////                info.prepared = true;
////                info.spell = new CharacterSpell();
////                info.spell.setName("Charm");
////                level.spellInfos.add(info);
//            }
//            if (i == 2) {
////                SpellInfo info = new SpellInfo();
////                info.preparable = false;
////                info.prepared = true;
////                info.spell = new CharacterSpell();
////                info.spell.setName("Level 2 spell...");
////                level.spellInfos.add(info);
//            }
//            result.add(level);
//        }
        return result;
    }

    private void addMulticlassSpellSlotLevels(@NonNull List<SpellLevelInfo> result, int effectiveCasterLevel, @NonNull List<CastingClassInfo> specialSlotClasses) {
        if (effectiveCasterLevel == 1) {
            SpellLevelInfo level = new SpellLevelInfo();
            level.level = 1;
            level.maxSlots = 2;
            result.add(level);
        } else if (effectiveCasterLevel == 2) {
            SpellLevelInfo level = new SpellLevelInfo();
            level.level = 1;
            level.maxSlots = 3;
            result.add(level);
        } else if (effectiveCasterLevel == 3) {
            SpellLevelInfo level = new SpellLevelInfo();
            level.level = 1;
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo();
            level.level = 2;
            level.maxSlots = 2;
            result.add(level);
        } else if (effectiveCasterLevel == 4) {
            SpellLevelInfo level = new SpellLevelInfo();
            level.level = 1;
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo();
            level.level = 2;
            level.maxSlots = 3;
            result.add(level);
        } else if (effectiveCasterLevel == 5) {
            SpellLevelInfo level = new SpellLevelInfo();
            level.level = 1;
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo();
            level.level = 2;
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo();
            level.level = 3;
            level.maxSlots = 2;
            result.add(level);
        }// TODO ...

        for (CastingClassInfo specialCastingClass : specialSlotClasses) {
            for (Map.Entry<Integer, String> each : specialCastingClass.getSlotMap().entrySet()) {
                int level = each.getKey();
                String slots = each.getValue();
                while (level >= result.size()) {
                    SpellLevelInfo levelInfo = new SpellLevelInfo();
                    levelInfo.level = result.size();
                    result.add(levelInfo);
                }
                final SpellLevelInfo levelInfo = result.get(level);
                levelInfo.maxSlots += evaluateFormula(slots, null);
            }
        }

    }

    public static class SpellLevelInfo {
        int level;
        int slotsAvailable;
        int maxSlots;

        @NonNull
        private List<CharacterSpellWithSource> spellInfos = new ArrayList<>();

        @NonNull
        public List<CharacterSpellWithSource> getSpellInfos() {
            return spellInfos;
        }

        public int getLevel() {
            return level;
        }

        public int getSlotsAvailable() {
            return slotsAvailable;
        }

        public int getMaxSlots() {
            return maxSlots;
        }
    }

    public static class CastingClassInfo {
        // TODO might be multiple, eg EldritchKnight/ArcaneTrickter combo!
        String owningClassName;
        String casterClassName;

        StatType castingStat;
        String multiclassCastorFactor = "1";

        String knownCantrips;

        String preparedSpells;
        String knownSpells;
        int maxSpellLevel;
        public Map<Integer, String> slotMap;
        private int classLevel;

        public RefreshType spellSlotRefresh;

        public String getOwningClassName() {
            return owningClassName;
        }

        public String getCasterClassName() {
            if (casterClassName == null) return owningClassName;
            return casterClassName;
        }

        public StatType getCastingStat() {
            return castingStat;
        }

        public String getMulticlassCastorFactor() {
            return multiclassCastorFactor;
        }

        public String getKnownCantrips() {
            return knownCantrips;
        }

        public String getPreparedSpells() {
            return preparedSpells;
        }

        public String getKnownSpells() {
            return knownSpells;
        }

        public int getMaxSpellLevel() {
            return maxSpellLevel;
        }

        public Map<Integer, String> getSlotMap() {
            return slotMap;
        }

        public int getClassLevel() {
            return classLevel;
        }

        public boolean usesPreparedSpells() {
            return preparedSpells != null && preparedSpells.length() > 0;
        }

        public RefreshType getSpellSlotRefresh() {
            return spellSlotRefresh;
        }

        public void setSpellSlotRefresh(RefreshType spellSlotRefresh) {
            this.spellSlotRefresh = spellSlotRefresh;
        }
    }

    public CastingClassInfo getCasterClassInfoFor(String owningClassName) {
        // TODO optimize this
        Map<String, CastingClassInfo> classInfoMap = getCastingClassInfoMap();
        return classInfoMap.get(owningClassName);
    }

    @NonNull
    public Collection<CastingClassInfo> getCasterClassInfo() {
        Map<String, CastingClassInfo> classInfoMap = getCastingClassInfoMap();
        return classInfoMap.values();
    }

    @NonNull
    protected Map<String, CastingClassInfo> getCastingClassInfoMap() {
        Map<String, CastingClassInfo> classInfoMap = new HashMap<>();

        for (CharacterClass each : classes) {
            StatType castingStat = each.getCasterStat();
            CastingClassInfo info = classInfoMap.get(each.getName());

            if (castingStat != null) {
                String prepared = each.getPreparedSpellsFormula();
                String castorFactor = each.getMulticlassCasterFactorFormula();

                if (info == null) {
                    info = new CastingClassInfo();
                    info.owningClassName = each.getName();
                    info.casterClassName = each.getSpellClassFilter();
                    classInfoMap.put(info.owningClassName, info);
                }
                info.castingStat = castingStat;
                if (prepared != null) info.preparedSpells = prepared;
                if (castorFactor != null) info.multiclassCastorFactor = castorFactor;
            }

            // look for cantrips/spells
            String cantripsKnown = each.getCantripsKnownFormula();
            if (cantripsKnown != null && cantripsKnown.trim().length() > 0) {
                if (info == null) {
                    info = new CastingClassInfo();
                    info.owningClassName = each.getName();
                    classInfoMap.put(info.owningClassName, info);
                }
                info.knownCantrips = cantripsKnown;
            }

            String spellsKnown = each.getSpellsKnownFormula();
            if (spellsKnown != null && spellsKnown.trim().length() > 0) {
                if (info == null) {
                    info = new CastingClassInfo();
                    info.owningClassName = each.getName();
                    classInfoMap.put(info.owningClassName, info);
                }
                info.knownSpells = spellsKnown;
            }
            int maxLevel = 0;
            Map<Integer, String> slotMap = each.getSpellLevelSlotFormulas();
            if (slotMap != null) {
                for (Integer level : slotMap.keySet()) {
                    if (level > maxLevel) maxLevel = level;
                }
                info.maxSpellLevel = maxLevel;
                info.slotMap = slotMap;
            }
            // allow this to be overwritten with each class level processed, in order
            if (info != null) {
                info.classLevel = each.getLevel();
                info.spellSlotRefresh = each.getSpellSlotRefresh();
            }
        }
        return classInfoMap;
    }


    private static class SpellListWrapper {
        @NonNull
        @ElementList(required = false)
        List<CharacterSpell> spells = new ArrayList<>();
    }

    public void deleteSpell(@NonNull CharacterSpell info) {
        int level = info.getLevel();
        if (level == 0) {
            boolean wasRemoved = cantrips.remove(info);
            if (wasRemoved) return;
//            return;
            // fall through for current bad data...
        }
        final SpellListWrapper listWrapper = spellsForLevel.get(level);
        if (listWrapper.spells.remove(info)) return;

        // fall through for current bad data...
        for (SpellListWrapper each : spellsForLevel.values()) {
            if (each.spells.remove(info)) return;
        }
    }


    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getHair() {
        return hair;
    }

    public void setHair(String hair) {
        this.hair = hair;
    }

    public String getEyes() {
        return eyes;
    }

    public void setEyes(String eyes) {
        this.eyes = eyes;
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

        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull ICharacterComponent component) {
                int speed = component.getSpeed(Character.this, type);
                if (speed == 0) return;
                SpeedWithSource speedWithSource = new SpeedWithSource(speed, component);
                result.add(speedWithSource);
            }
        };
        deriver.derive(this, "speed " + type.name());

        return result;
    }

    public int getSpeed(@NonNull final SpeedType type) {
        final int result[] = new int[]{0};
        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull ICharacterComponent component) {
                result[0] += component.getSpeed(Character.this, type);
            }
        };
        deriver.derive(this, "speed " + type.name());
        return result[0];
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

        PassivePerceptionWithSource baseSource = new PassivePerceptionWithSource(10, new CheatComponentSource(R.string.passive_base));
        result.add(baseSource);
        PassivePerceptionWithSource dexSource = new PassivePerceptionWithSource(getStatBlock(StatType.WISDOM).getModifier(), new CheatComponentSource(R.string.wisdom_mod));
        result.add(dexSource);
        final Proficient proficiency = getSkillBlock(SkillType.PERCEPTION).getProficiency();
        if (proficiency.getMultiplier() > 0) {
            CheatComponentSource source = new CheatComponentSource(R.string.proficiency_bonus);
            PassivePerceptionWithSource proficiencySource = new PassivePerceptionWithSource((int) proficiency.getMultiplier() * getProficiency(), source);
            result.add(proficiencySource);
        }

        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull ICharacterComponent component) {
                int initiative = component.getPassivePerceptionMod(Character.this);
                if (initiative == 0) return;
                PassivePerceptionWithSource passivePerceptionWithSource = new PassivePerceptionWithSource(initiative, component);
                result.add(passivePerceptionWithSource);
            }
        };
        deriver.derive(this, "passivePerception");

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

        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull ICharacterComponent component) {
                int initiative = component.getInitiativeMod(Character.this);
                if (initiative == 0) return;
                InitiativeWithSource initiativeWithSource = new InitiativeWithSource(initiative, component);
                result.add(initiativeWithSource);
            }
        };
        deriver.derive(this, "passivePerception");

        // go through custom adjustments
        final CustomAdjustments customRootACs = getCustomAdjustments(CustomAdjustmentType.INITIATIVE);
        for (CustomAdjustments.Adjustment each : customRootACs.getAdjustments()) {
            AdjustmentComponentSource source = new AdjustmentComponentSource(each);
            InitiativeWithSource customAC = new InitiativeWithSource(each.numValue, source);
            result.add(customAC);
        }

        return result;
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

}
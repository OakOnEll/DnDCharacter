package com.oakonell.dndcharacter.model.character;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.R;
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
    private int age;
    @Element(required = false)
    private String height;
    @Element(required = false)
    private int weight;

    @Element(required = false)
    private String skin;
    @Element(required = false)
    private String hair;
    @Element(required = false)
    private String eyes;


    public Character(boolean defaults) {
        name = "Feng";

        baseStats.put(StatType.STRENGTH, 12);
        baseStats.put(StatType.DEXTERITY, 13);
        baseStats.put(StatType.CONSTITUTION, 14);
        baseStats.put(StatType.INTELLIGENCE, 8);
        baseStats.put(StatType.WISDOM, 10);
        baseStats.put(StatType.CHARISMA, 15);
/*
        race = new CharacterRace();
        race.setName("Half-Orc");
        race.addModifier(StatType.STRENGTH, 2);
        race.addModifier(StatType.CONSTITUTION, 1);
        race.addSkill(SkillType.INTIMIDATION, Proficient.PROFICIENT);

        Feature darkVision = new Feature();
        darkVision.setName("Dark Vision");
        darkVision.setDescription("Within 60' can see in dim light as bright, in darkness as dim(greyscale).");
        race.addFeature(darkVision);

        Feature savageAttacks = new Feature();
        savageAttacks.setName("Savage Attacks");
        savageAttacks.setDescription("On a critical melee attack, additional damage dice.");
        race.addFeature(savageAttacks);

        Feature relentlessEndurance = new Feature();
        relentlessEndurance.setName("Relentless Endurance");
        relentlessEndurance.setDescription("Once per long rest, when hit points reach 0, go to 1 hp.");
        relentlessEndurance.setFormula("1");
        relentlessEndurance.setRefreshesOn(RefreshType.LONG_REST);
        race.addFeature(relentlessEndurance);


        background = new CharacterBackground();
        background.setName("Urchin");
        background.addSkill(SkillType.SLEIGHT_OF_HAND, Proficient.PROFICIENT);
        background.addSkill(SkillType.STEALTH, Proficient.PROFICIENT);
        Feature fastTravel = new Feature();
        fastTravel.setName("City Secrets");
        fastTravel.setDescription("Can travel quickly in cities");
        background.addFeature(fastTravel);

        CharacterClass sorcerer = new CharacterClass();
        sorcerer.setName("Sorcerer");
        sorcerer.setLevel(1);
        sorcerer.addSaveThrow(StatType.CHARISMA, Proficient.PROFICIENT);
        sorcerer.addSaveThrow(StatType.CONSTITUTION, Proficient.PROFICIENT);
        sorcerer.addSkill(SkillType.DECEPTION, Proficient.PROFICIENT);
        sorcerer.addSkill(SkillType.PERSUASION, Proficient.PROFICIENT);

        Feature tides = new Feature();
        tides.setDescription("Can gain advantage on a roll");
        tides.setName("Tides of Chaos");
        tides.setFormula("1");
        tides.setRefreshesOn(RefreshType.LONG_REST);
        sorcerer.addFeature(tides);
        sorcerer.setHitDie(6);
        sorcerer.setHpRoll(6);

        classes.add(sorcerer);*/
    }

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
            protected void visitComponent(@NonNull BaseCharacterComponent component) {
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

    public String getBackgroundName() {
        if (background == null) return "(no background)";
        return background.getName();
    }

    public String getRaceName() {
        if (race == null) return "(no race)";
        return race.getName();
    }

    public String getSubRaceName() {
        if (race == null) return "(no race)";
        return race.getSubraceName();
    }

    public int getCharacterLevel() {
        return classes.size();
    }

    @NonNull
    public String getClassesString() {
        Map<String, Integer> classLevels = getClassLevels();
        if (classLevels.isEmpty()) {
            return "[no class]";
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
            protected void visitComponent(@NonNull BaseCharacterComponent component) {
                for (String each : component.getLanguages()) {
                    LanguageWithSource row = new LanguageWithSource(each, component);
                    languages.add(row);
                }
            }
        };
        languagesDeriver.derive(this, "languages");
        return languages;
    }

    public boolean isProficientWith(@NonNull CharacterWeapon weapon) {
        List<ToolProficiencyWithSource> list = deriveToolProficiencies(ProficiencyType.WEAPON);
        String name = weapon.getName().toUpperCase();
        String category = weapon.getCategory();
        if (category == null) {
            category = "";
        } else {
            category = category.toUpperCase();
        }
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

            if (name.equals(eachName) || category.equals(eachCategory)) {
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
        for (CharacterEffect each : getEffects()) {
            if (each.isInContext(context)) return true;
        }
        for (FeatureInfo each : getFeatureInfos()) {
            if (each.isInContext(context)) return true;
        }
        return false;
    }

    public int getCantripsForClass(@NonNull String className) {
        int count = 0;
        for (CharacterSpell each : cantrips) {
            if (className.equals(each.getCasterClass())) {
                count++;
            }
        }
        return count;
    }

    public int getSpellsKnownForClass(@NonNull String className) {
        int known = 0;
        for (Map.Entry<Integer, SpellListWrapper> entry : spellsForLevel.entrySet()) {
            List<CharacterSpell> spells = entry.getValue().spells;
            for (CharacterSpell each : spells) {
                if (className.equals(each.getCasterClass())) {
                    known++;
                }
            }
        }
        return known;
    }

    public int getSpellsPreparedForClass(@NonNull String className) {
        int prepared = 0;
        for (Map.Entry<Integer, SpellListWrapper> entry : spellsForLevel.entrySet()) {
            List<CharacterSpell> spells = entry.getValue().spells;
            for (CharacterSpell each : spells) {
                if (className.equals(each.getCasterClass())) {
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


    public static class ArmorClassWithSource extends WithSource {
        private final String formula;
        boolean isEquipped;
        private final int value;
        public boolean isDisabled;

        ArmorClassWithSource(String formula, int value, BaseCharacterComponent source) {
            super(source);
            this.value = value;
            this.formula = formula;
            if (source instanceof CharacterArmor) {
                isEquipped = ((CharacterArmor) source).isEquipped();
            }
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

        public boolean isArmor() {
            return getSource() instanceof CharacterArmor;
        }
    }

    @NonNull
    public List<ArmorClassWithSource> deriveRootAcs() {
        List<ArmorClassWithSource> result = new ArrayList<>();
        String baseFormula = "10 + dexterityMod";
        int baseValue = evaluateFormula(baseFormula, null);
        ArmorClassWithSource unarmored = new ArmorClassWithSource(baseFormula, baseValue, null);
        result.add(unarmored);

        // multiple here will really just take the highest ?? at runtime
        for (CharacterClass eachClass : classes) {
            for (FeatureInfo each : eachClass.getFeatures()) {
                if (!each.getFeature().isBaseArmor()) continue;

                String acFormula = each.getFeature().getBaseAcFormula();
                if (acFormula != null) {
                    SimpleVariableContext variableContext = new SimpleVariableContext();
                    each.getSource().addExtraFormulaVariables(variableContext);
                    int value = evaluateFormula(acFormula, variableContext);
                    ArmorClassWithSource featureAc = new ArmorClassWithSource(acFormula, value, each.getFeature());
                    result.add(featureAc);
                }
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
        int max = result.size() - 1;
        for (int i = 0; i + 1 < result.size(); i++) {
            ArmorClassWithSource each = result.get(i);
            each.isDisabled = true;
        }
        ArmorClassWithSource noArmorRow = result.get(max);


        // go through items
        boolean hasAny = false;
        for (CharacterArmor each : getArmor()) {
            if (!each.isBaseArmor()) continue;

            String formula = each.getBaseAcFormula();
            if (formula != null) {
                int value = evaluateFormula(formula, null);

                ArmorClassWithSource featureAc = new ArmorClassWithSource(formula, value, each);
                if (featureAc.isEquipped()) {
                    hasAny = true;
                }
                result.add(featureAc);
            }
        }
        if (!hasAny) {
            noArmorRow.setIsEquipped(true);
            noArmorRow.isDisabled = true;
        }


        return result;
    }

    @NonNull
    public List<ArmorClassWithSource> deriveModifyingAcs() {
        List<ArmorClassWithSource> result = new ArrayList<>();

        boolean wearingArmor = false;
        for (CharacterArmor each : getArmor()) {
            if (each.isBaseArmor()) {
                if (each.isEquipped()) {
                    wearingArmor = true;
                    break;
                }
            }
        }

        // multiple here will really just take the highest ?? at runtime
        for (CharacterClass eachClass : classes) {
            for (FeatureInfo each : eachClass.getFeatures()) {
                if (each.getFeature().isBaseArmor()) continue;


                String acFormula = each.getFeature().getModifyingAcFormula();
                if (acFormula == null) continue;


                SimpleVariableContext variableContext = new SimpleVariableContext();
                variableContext.setBoolean("armor", wearingArmor);
                each.getSource().addExtraFormulaVariables(variableContext);

                int value = evaluateFormula(acFormula, variableContext);
                ArmorClassWithSource featureAc = new ArmorClassWithSource(acFormula, value, each.getFeature());
                result.add(featureAc);

                String activeFormula = each.getFeature().getActiveFormula();
                if (activeFormula != null) {
                    boolean isActive = evaluateBooleanFormula(activeFormula, variableContext);
                    featureAc.setIsEquipped(isActive);
                }
                featureAc.isDisabled = true;
            }
        }
        // go through items
        for (CharacterArmor each : getArmor()) {
            if (each.isBaseArmor()) continue;

            String formula = each.getModifyingAcFormula();
            if (formula != null) {
                int value = evaluateFormula(formula, null);
                ArmorClassWithSource acMod = new ArmorClassWithSource(formula, value, each);
                result.add(acMod);
            }
        }

        return result;
    }

    public int getArmorClass() {
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

        List<ArmorClassWithSource> modifiers = deriveModifyingAcs();
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

    public String getDisplayRaceName() {
        if (race == null) return "[None]";

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
    private String getToolProficiencyString(ProficiencyType type) {
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
    public List<ModifierWithSource> deriveStat(final StatType type) {
        final List<ModifierWithSource> result = new ArrayList<>();

        if (baseStats != null) {
            int value = baseStats.get(type);
            if (value > 0) {
                ModifierWithSource base = new ModifierWithSource(value, null);
                result.add(base);
            }
        }

        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull BaseCharacterComponent component) {
                int value = component.getStatModifier(type);
                if (value > 0) {
                    ModifierWithSource base = new ModifierWithSource(value, component);
                    result.add(base);
                }
            }
        };
        deriver.derive(this, "stat " + type.name());

        return result;
    }

    public int deriveStatValue(final StatType type) {
        // start with base stats
        final int value[] = new int[]{0};
        if (baseStats != null) {
            value[0] = baseStats.get(type);
        }

        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull BaseCharacterComponent component) {
                value[0] += component.getStatModifier(type);
            }
        };
        deriver.derive(this, "stat value " + type.name());

        return value[0];
    }

    @NonNull
    public List<ProficientWithSource> deriveSkillProciencies(final SkillType type) {
        final List<ProficientWithSource> result = new ArrayList<>();

        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull BaseCharacterComponent component) {
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
    public List<ProficientWithSource> deriveSaveProficiencies(final StatType type) {
        final List<ProficientWithSource> result = new ArrayList<>();

        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull BaseCharacterComponent component) {
                Proficient proficient = component.getSaveProficient(type);
                if (proficient != Proficient.NONE) {
                    ProficientWithSource reason = new ProficientWithSource(proficient, background);
                    result.add(reason);
                }
            }
        };
        deriver.derive(this, "save throw prof " + type.name());

        return result;
    }

    public Proficient deriveSkillProciency(final SkillType type) {
        final Proficient proficient[] = new Proficient[1];
        proficient[0] = Proficient.NONE;

        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull BaseCharacterComponent component) {
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

    public Proficient deriveSaveProciency(final StatType type) {
        final Proficient proficient[] = new Proficient[1];
        proficient[0] = Proficient.NONE;
        final List<LanguageWithSource> languages = new ArrayList<>();
        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull BaseCharacterComponent component) {
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
    public List<FeatureInfo> getFeatureInfos() {
        final List<FeatureInfo> result = new ArrayList<>();

        // features shouldn't contain features, and any effects are not automatic, but applied on use
        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver(true) {
            protected void visitComponent(@NonNull BaseCharacterComponent component) {
                result.addAll(component.getFeatures());
            }
        };
        deriver.derive(this, "Feature infos");

        return result;
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
            variableContext.setNumber(each.name().toLowerCase() + "Value", block.getValue());
        }
        variableContext.setNumber("level", getClasses().size());
        return variableContext;
    }

    public int evaluateFormula(@Nullable String formula, @Nullable SimpleVariableContext variableContext) {
        // TODO formula might reference stats and such
        if (formula == null || formula.length() == 0) return 0;
        variableContext = getPopulatedVariableContext(variableContext);

        try {
            Expression<Integer> expression = Expression.parse(formula, ExpressionType.NUMBER_TYPE, new ExpressionContext(new SimpleFunctionContext(), variableContext));
            return expression.evaluate();
        } catch (Exception e) {
            // should be done at formula save time...
            return 10;
        }
    }

    public int getUses(@NonNull Feature feature) {
        Integer uses = usedFeatures.get(feature.getName());
        if (uses == null) return 0;
        return uses;
    }

    public int getUsesRemaining(@NonNull FeatureInfo feature) {
        return feature.evaluateMaxUses(this) - getUses(feature.getFeature());
    }

    public void useFeature(@NonNull Feature feature, int amount) {
        // TODO apply known effects from feature
        int uses = getUses(feature);
        uses = uses + amount;
        usedFeatures.put(feature.getName(), uses);
    }

    public void useFeatureAction(@NonNull Feature feature, @NonNull IFeatureAction action) {
        useFeature(feature, action.getCost());
        action.applyToCharacter(this);
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
        List<FeatureInfo> featureInfos = getFeatureInfos();
        for (FeatureInfo each : featureInfos) {
            Integer resetRequest = request.getFeatureResets().get(each.getName());
            if (resetRequest == null || resetRequest == 0) continue;

            Integer used = usedFeatures.get(each.getName());
            if (used == null) continue;

            used = used - resetRequest;
            if (used <= 0) {
                usedFeatures.remove(each.getName());
            } else {
                usedFeatures.put(each.getFeature().getName(), used);
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
        if (tempHp > 0) {
            tempHp -= amount;
            if (tempHp < 0) {
                hp += tempHp;
                tempHp = 0;
            }
        } else {
            hp -= amount;
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
    public List<ToolProficiencyWithSource> deriveToolProficiencies(final ProficiencyType type) {
        final List<ToolProficiencyWithSource> result = new ArrayList<>();

        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull BaseCharacterComponent component) {
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
        items.add(item);
    }

    @NonNull
    public List<CharacterItem> getItems() {
        return items;
    }

    @NonNull
    public List<CharacterItem> getItemsNamed(String name) {
        List<CharacterItem> result = new ArrayList<>();
        for (CharacterItem each : items) {
            if (each.getName().equals(name)) result.add(each);
        }
        return result;
    }

    public void addWeapon(CharacterWeapon weapon) {
        weapons.add(weapon);
    }

    @NonNull
    public List<CharacterArmor> getArmor() {
        return armor;
    }

    public void addArmor(CharacterArmor armor) {
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
    }

    public static class LanguageWithSource extends WithSource {
        private final String language;

        LanguageWithSource(String language, BaseCharacterComponent source) {
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

        ModifierWithSource(int modifier, BaseCharacterComponent source) {
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

        ProficientWithSource(Proficient proficient, BaseCharacterComponent source) {
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

        ToolProficiencyWithSource(Proficiency proficient, BaseCharacterComponent source) {
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

    public abstract static class WithSource {
        private final BaseCharacterComponent source;

        WithSource(BaseCharacterComponent source) {
            this.source = source;
        }

        public BaseCharacterComponent getSource() {
            return source;
        }

        public String getSourceString(Resources resources) {
            if (source == null) return resources.getString(R.string.no_component_source);
            return source.getSourceString(resources);
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

        CharacterAbilityDeriver spellDeriver = new CharacterAbilityDeriver() {
            protected void visitComponent(@NonNull BaseCharacterComponent component) {
                for (CharacterSpell each : component.getCantrips()) {
                    cantrips.getSpellInfos().add(each);
                }
            }
        };
        spellDeriver.derive(this, "spell infos");


        for (CharacterSpell each : this.cantrips) {
            cantrips.spellInfos.add(each);
        }

        List<SpellLevelInfo> result = new ArrayList<>();
        result.add(cantrips);

        // go through class levels for usable spell levels?

        final Map<String, CastingClassInfo> casterClassInfo = getCasterClassInfo();
        if (casterClassInfo.size() == 0) {
            return result;
        }
        int maxSlotLevel = 0;
        final Map<String, Integer> classLevels = getClassLevels();
        if (classLevels.size() > 1) {
            // multiclass
            Map<String, Integer> multiclassCasterSlots = new HashMap<>();
            List<CastingClassInfo> specialSlotClasses = new ArrayList<>();
            for (CastingClassInfo each : casterClassInfo.values()) {
                String factor = each.getMulticlassCastorFactor();
                if (factor.equals("-1")) {
                    specialSlotClasses.add(each);
                    continue;
                }
                Integer factorLevel = multiclassCasterSlots.get(factor);
                if (factorLevel == null) factorLevel = 0;
                factorLevel += classLevels.get(each.getClassName());
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
            final CastingClassInfo casterInfo = casterClassInfo.values().iterator().next();
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

            final List<CharacterSpell> spells = level.getSpellInfos();
            for (CharacterSpell each : getSpells(i)) {
                spells.add(each);
            }
        }

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
        private List<CharacterSpell> spellInfos = new ArrayList<>();

        @NonNull
        public List<CharacterSpell> getSpellInfos() {
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
        String className;

        StatType castingStat;
        String multiclassCastorFactor = "1";

        String knownCantrips;

        String preparedSpells;
        String knownSpells;
        int maxSpellLevel;
        public Map<Integer, String> slotMap;
        private int classLevel;

        public RefreshType spellSlotRefresh;

        public String getClassName() {
            return className;
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

    @NonNull
    public Map<String, CastingClassInfo> getCasterClassInfo() {
        Map<String, CastingClassInfo> classInfoMap = new HashMap<>();

        for (CharacterClass each : classes) {
            StatType castingStat = each.getCasterStat();
            CastingClassInfo info = classInfoMap.get(each.getName());

            if (castingStat != null) {
                String prepared = each.getPreparedSpellsFormula();
                String castorFactor = each.getMulticlassCasterFactorFormula();

                if (info == null) {
                    info = new CastingClassInfo();
                    info.className = each.getName();
                    classInfoMap.put(info.className, info);
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
                    info.className = each.getName();
                    classInfoMap.put(info.className, info);
                }
                info.knownCantrips = cantripsKnown;
            }

            String spellsKnown = each.getSpellsKnownFormula();
            if (spellsKnown != null && spellsKnown.trim().length() > 0) {
                if (info == null) {
                    info = new CastingClassInfo();
                    info.className = each.getName();
                    classInfoMap.put(info.className, info);
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


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
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
}
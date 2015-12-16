package com.oakonell.dndcharacter.model;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.components.Feature;
import com.oakonell.dndcharacter.model.components.Proficiency;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.model.components.RefreshType;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by Rob on 10/21/2015.
 */
public class Character {
    // more fluid data
    @Element(required = false)
    int hp;
    @Element(required = false)
    @Deprecated
    // TODO delete- will need to dump DB
            int tempHpMax;
    @Element(required = false)
    int tempHp;
    @ElementMap(entry = "feature", key = "name", value = "uses", required = false)
    Map<String, Integer> usedFeatures = new HashMap<String, Integer>();
    // relatively static data
    @Element(required = false)
    private String name;
    @Element(required = false)
    private CharacterBackground background;
    @Element(required = false)
    private CharacterRace race;
    @ElementList(required = false)
    private List<CharacterClass> classes = new ArrayList<CharacterClass>();
    @ElementMap(entry = "stat", key = "name", value = "value", required = false)
    private Map<StatType, Integer> baseStats = new HashMap<StatType, Integer>();

    @Element(required = false)
    private BaseStatsType statsType;
    @Element(required = false)
    private String notes;
    @ElementMap(entry = "hitDie", key = "die", value = "uses", required = false)
    private Map<Integer, Integer> hitDieUses = new HashMap<>();

    @Element(required = false)
    private String backstory;

    @ElementList(required = false)
    private List<CharacterItem> items = new ArrayList<CharacterItem>();

    @ElementList(required = false)
    private List<CharacterArmor> armor = new ArrayList<CharacterArmor>();

    @ElementList(required = false)
    private List<CharacterWeapon> weapons = new ArrayList<CharacterWeapon>();


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
        return hp;
    }

    public StatBlock getStatBlock(StatType type) {
        return new StatBlock(this, type);
    }

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

    public String getLanguagesString() {
        List<String> languages = getLanguages();
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

    public List<String> getLanguages() {
        List<String> result = new ArrayList<>();
        for (LanguageWithSource each : deriveLanguages()) {
            result.add(each.language);
        }
        return result;
    }

    public List<LanguageWithSource> deriveLanguages() {
        List<LanguageWithSource> languages = new ArrayList<>();
        if (background != null) {
            for (String each : background.getLanguages()) {
                LanguageWithSource row = new LanguageWithSource(each, background);
                languages.add(row);
            }
        }
        if (race != null) {
            for (String each : race.getLanguages()) {
                LanguageWithSource row = new LanguageWithSource(each, race);
                languages.add(row);
            }
        }
        if (classes != null) {
            for (CharacterClass eachClass : classes) {
                for (String each : eachClass.getLanguages()) {
                    LanguageWithSource row = new LanguageWithSource(each, eachClass);
                    languages.add(row);
                }
            }
        }
        return languages;
    }

    public int getArmorClass() {
        // go through active equipment
        // if no equipment affects ac, it is just 10 + dex mod
        return 10 + getStatBlock(StatType.DEXTERITY).getModifier();
    }

    public SavedChoices getBackgroundChoices() {
        if (background == null) return new SavedChoices();
        return background.savedChoices;
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

    public String getSpecialty() {
        if (background == null) return null;
        return background.getSpecialty();
    }

    public String getArmorProficiencyString() {
        final ProficiencyType type = ProficiencyType.ARMOR;
        return getToolProficiencyString(type);
    }

    @NonNull
    private String getToolProficiencyString(ProficiencyType type) {
        List<ToolProficiencyWithSource> list = deriveToolProficiencies(type);
        // TODO collapse duplicates AND subsume individual items with categories
        // TODO apply an order? category first, alphabetical
        Set<String> set = new HashSet<>();
        for (ToolProficiencyWithSource each : list) {
            Proficiency proficiency = each.proficient;

            if (proficiency.getCategory() != null) {
                set.add("(" + proficiency.getCategory() + ")");
            } else {
                set.add(proficiency.getName());
            }
        }
        StringBuilder builder = new StringBuilder();
        String comma = "";
        for (String each : set) {
            builder.append(comma);
            comma = ", ";
            builder.append(each);
        }
        return builder.toString();
    }

    public String getWeaponsProficiencyString() {
        final ProficiencyType type = ProficiencyType.WEAPON;
        return getToolProficiencyString(type);
    }

    public String getToolsProficiencyString() {
        final ProficiencyType type = ProficiencyType.TOOL;
        return getToolProficiencyString(type);
    }

    public List<ModifierWithSource> deriveStat(StatType type) {
        List<ModifierWithSource> result = new ArrayList<>();
        if (baseStats != null) {
            int value = baseStats.get(type);
            if (value > 0) {
                ModifierWithSource base = new ModifierWithSource(value, null);
                result.add(base);
            }
        }
        // look to background
        if (background != null) {
            int value = background.getStatModifier(type);
            if (value > 0) {
                ModifierWithSource base = new ModifierWithSource(value, background);
                result.add(base);
            }
        }
        // look to race
        if (race != null) {
            int value = race.getStatModifier(type);
            if (value > 0) {
                ModifierWithSource base = new ModifierWithSource(value, race);
                result.add(base);
            }
        }
        // look to classes
        if (classes != null) {
            for (CharacterClass each : classes) {
                int value = each.getStatModifier(type);
                if (value > 0) {
                    ModifierWithSource base = new ModifierWithSource(value, each);
                    result.add(base);
                }
            }
        }
        // go through equipment
        // go through effects..

        return result;
    }

    public int deriveStatValue(StatType type) {
        // start with base stats
        int value = 0;
        if (baseStats != null) {
            value = baseStats.get(type);
        }
        // look to background
        if (background != null) {
            value += background.getStatModifier(type);
        }
        // look to race
        if (race != null) {
            value += race.getStatModifier(type);
        }
        // look to classes
        if (classes != null) {
            for (CharacterClass each : classes) {
                value += each.getStatModifier(type);
            }
        }
        // go through equipment
        // go through effects..
        return value;
    }

    public List<ProficientWithSource> deriveSkillProciencies(SkillType type) {
        List<ProficientWithSource> result = new ArrayList<>();
        if (background != null) {
            Proficient proficient = background.getSkillProficient(type);
            if (proficient != Proficient.NONE) {
                ProficientWithSource reason = new ProficientWithSource(proficient, background);
                result.add(reason);
            }
        }
        if (race != null) {
            Proficient raceProficient = race.getSkillProficient(type);
            if (raceProficient != Proficient.NONE) {
                ProficientWithSource reason = new ProficientWithSource(raceProficient, race);
                result.add(reason);
            }
        }
        if (classes != null) {
            for (CharacterClass each : classes) {
                Proficient classProficient = each.getSkillProficient(type);
                if (classProficient != null) {
                    if (classProficient != Proficient.NONE) {
                        ProficientWithSource reason = new ProficientWithSource(classProficient, each);
                        result.add(reason);
                    }
                }
            }
        }
        return result;
    }

    public List<ProficientWithSource> deriveSaveProficiencies(StatType type) {
        List<ProficientWithSource> result = new ArrayList<>();
        if (background != null) {
            Proficient proficient = background.getSaveProficient(type);
            if (proficient != Proficient.NONE) {
                ProficientWithSource reason = new ProficientWithSource(proficient, background);
                result.add(reason);
            }
        }
        if (race != null) {
            Proficient raceProficient = race.getSaveProficient(type);
            if (raceProficient != Proficient.NONE) {
                ProficientWithSource reason = new ProficientWithSource(raceProficient, race);
                result.add(reason);
            }
        }
        if (classes != null) {
            for (CharacterClass each : classes) {
                Proficient classProficient = each.getSaveProficient(type);
                if (classProficient != null) {
                    if (classProficient != Proficient.NONE) {
                        ProficientWithSource reason = new ProficientWithSource(classProficient, each);
                        result.add(reason);
                    }
                }
            }
        }
        return result;

    }

    public Proficient deriveSkillProciency(SkillType type) {
        Proficient proficient = Proficient.NONE;
        if (background != null) {
            proficient = background.getSkillProficient(type);
        }
        if (race != null) {
            Proficient raceProficient = race.getSkillProficient(type);
            if (raceProficient.getMultiplier() > proficient.getMultiplier()) {
                proficient = raceProficient;
            }
        }
        if (classes != null) {
            for (CharacterClass each : classes) {
                Proficient classProficient = each.getSkillProficient(type);
                if (classProficient != null) {
                    if (classProficient.getMultiplier() > proficient.getMultiplier()) {
                        proficient = classProficient;
                    }
                }
            }
        }
        return proficient;
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

    public Proficient deriveSaveProciency(StatType type) {
        Proficient proficient = Proficient.NONE;
        if (background != null) {
            proficient = background.getSaveProficient(type);
        }
        if (classes != null) {
            for (CharacterClass each : classes) {
                Proficient classProficient = each.getSaveProficient(type);
                if (classProficient != null) {
                    if (classProficient.getMultiplier() > proficient.getMultiplier()) {
                        proficient = classProficient;
                    }
                }
            }
        }
        //race.getSkillProficient(type);
        return proficient;
    }

    public List<FeatureInfo> getFeatureInfos() {
        List<FeatureInfo> result = new ArrayList<FeatureInfo>();
        if (background != null) result.addAll(background.getFeatures());
        if (race != null) result.addAll(race.getFeatures());
        for (CharacterClass each : classes) {
            result.addAll(each.getFeatures());
        }
        return result;
    }

    public int evaluateMaxUses(Feature feature) {
        String formula = feature.getUsesFormula();
        // formula might reference stats and such
        if (formula == null || formula.length() == 0) return 0;
        return Integer.parseInt(formula);
    }

    public int getUses(Feature feature) {
        Integer uses = usedFeatures.get(feature.getName());
        if (uses == null) return 0;
        return uses;
    }

    public int getUsesRemaining(Feature feature) {
        return evaluateMaxUses(feature) - getUses(feature);
    }

    public void useFeature(Feature feature) {
        // TODO apply known effects from feature
        int uses = getUses(feature);
        uses = uses + 1;
        usedFeatures.put(feature.getName(), uses);
    }

    public String getNotes() {
        return notes;
    }

    public void longRest(LongRestRequest request) {
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
    }

    private void resetFeatures(AbstractRestRequest request) {
        List<FeatureInfo> featureInfos = getFeatureInfos();
        for (FeatureInfo each : featureInfos) {
            Integer resetRequest = request.getFeatureResets().get(each.getName());
            if (resetRequest == null || resetRequest == 0) continue;

            if (each.getFeature().getRefreshesOn() == RefreshType.LONG_REST) {
                int used = usedFeatures.get(each.getName());
                used = used - resetRequest;
                if (used <= 0) {
                    usedFeatures.remove(each.getName());
                } else {
                    usedFeatures.put(each.getFeature().getName(), used);
                }
            }
        }

    }

    public void shortRest(ShortRestRequest request) {
        hp = Math.min(hp + request.getHealing(), getMaxHP());

        for (Map.Entry<Integer, Integer> entry : request.getHitDieUses().entrySet()) {
            int die = entry.getKey();
            int requestUses = entry.getValue();

            Integer uses = hitDieUses.get(die);
            if (uses == null) uses = 0;
            uses += requestUses;
            hitDieUses.put(die, uses);
        }

        // need to take an input context ?
        //     which features to refresh, how many hit die to apply, etc
        List<FeatureInfo> featureInfos = getFeatureInfos();
        for (FeatureInfo each : featureInfos) {
            if (each.getFeature().getRefreshesOn() == RefreshType.SHORT_REST) {
                usedFeatures.put(each.getFeature().getName(), 0);
            }
        }
        resetFeatures(request);
    }

    public String getHitDiceString() {
        if (classes == null) return "";

        List<HitDieRow> dice = getHitDiceCounts();
        StringBuilder builder = new StringBuilder();
        Iterator<HitDieRow> iter = dice.iterator();
        while (iter.hasNext()) {
            HitDieRow entry = iter.next();
            builder.append("(" + entry.numDiceRemaining + "/" + entry.totalDice + ")" + "d" + entry.dieSides);
        }
        return builder.toString();
    }

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

    public List<ToolProficiencyWithSource> deriveToolProficiencies(ProficiencyType type) {
        List<ToolProficiencyWithSource> result = new ArrayList<>();
        // look to background
        if (background != null) {
            List<Proficiency> profs = background.getToolProficiencies(type);
            for (Proficiency each : profs) {
                ToolProficiencyWithSource newRow = new ToolProficiencyWithSource(each, background);
                result.add(newRow);
            }
        }
        // look to race
        if (race != null) {
            List<Proficiency> profs = race.getToolProficiencies(type);
            for (Proficiency each : profs) {
                ToolProficiencyWithSource newRow = new ToolProficiencyWithSource(each, race);
                result.add(newRow);
            }
        }
        // look to classes
        if (classes != null) {
            for (CharacterClass each : classes) {
                List<Proficiency> profs = each.getToolProficiencies(type);
                for (Proficiency eachProf : profs) {
                    ToolProficiencyWithSource newRow = new ToolProficiencyWithSource(eachProf, each);
                    result.add(newRow);
                }
            }
        }
        // go through equipment
        // go through effects..

        return result;
    }

    public void addItem(CharacterItem item) {
        items.add(item);
    }

    public List<CharacterItem> getItems() {
        return items;
    }

    public void addWeapon(CharacterWeapon weapon) {
        weapons.add(weapon);
    }

    public List<CharacterArmor> getArmor() {
        return armor;
    }

    public void addArmor(CharacterArmor armor) {
        this.armor.add(armor);
    }

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

        @Override
        public String toString() {
            return language + " (" + getSourceString() + ")";
        }


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

        @Override
        public String toString() {
            return modifier + " (" + getSourceString() + ")";
        }
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

        @Override
        public String toString() {
            return proficient + " (" + getSourceString() + ")";
        }

    }

    public static class HitDieRow {
        public int dieSides;
        public int numDiceRemaining;
        public int totalDice;

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

        @Override
        public String toString() {
            if (proficient.getCategory() != null) {
                return "[" + proficient.getCategory() + "] (" + getSourceString() + ")";
            }
            return proficient.getName() + " (" + getSourceString() + ")";
        }

    }

    public abstract static class WithSource {
        private final BaseCharacterComponent source;

        WithSource(BaseCharacterComponent source) {
            this.source = source;
        }

        public BaseCharacterComponent getSource() {
            return source;
        }

        public String getSourceString() {
            if (source == null) return "No Source";
            return source.getSourceString();
        }

    }
}
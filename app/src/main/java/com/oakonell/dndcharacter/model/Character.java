package com.oakonell.dndcharacter.model;

import com.oakonell.dndcharacter.model.components.Feature;
import com.oakonell.dndcharacter.model.components.RefreshType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Rob on 10/21/2015.
 */
public class Character {
    // relatively static data
    private String name;
    private CharacterBackground background;
    private CharacterRace race;
    private List<CharacterClass> classes;
    private Map<StatType, Integer> baseStats = new HashMap<StatType, Integer>();


    // more fluid data
    int hp;
    int tempHpMax;
    int tempHp;
    Map<String, Integer> usedFeatures = new HashMap<String, Integer>();
    private String notes;

    public Character() {
        name = "Feng";

        baseStats.put(StatType.STRENGTH, 12);
        baseStats.put(StatType.DEXTERITY, 13);
        baseStats.put(StatType.CONSTITUTION, 14);
        baseStats.put(StatType.INTELLIGENCE, 8);
        baseStats.put(StatType.WISDOM, 10);
        baseStats.put(StatType.CHARISMA, 15);

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

        classes = new ArrayList<CharacterClass>();
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

        classes.add(sorcerer);

    }

    /**
     * Base info
     */

    public String getName() {
        return name;
    }

    /**
     * derivable info
     */

    public int getMaxHP() {
        int hp = 0;
        StatBlock conBlock = getStatBlock(StatType.CONSTITUTION);
        int conMod = conBlock.getModifier();

        for (CharacterClass each : classes) {
            hp += each.getHPRoll();
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
        if (race == null) return "(no background)";
        return background.getName();
    }

    public String getRaceName() {
        if (race == null) return "(no race)";
        return race.getName();
    }

    public String getClassesString() {
        StringBuilder builder = new StringBuilder();
        // TODO only grab the highest level of each class...
        for (CharacterClass each : classes) {
            builder.append(each.getName() + " " + each.getLevel());
        }
        return builder.toString();
    }

    public int getArmorClass() {
        // go through active equipment
        // if no equipment affects ac, it is just 10 + dex mod
        return 10 + getStatBlock(StatType.DEXTERITY).getModifier();
    }

    public int deriveStatValue(StatType type) {
        // start with base stats
        int value = 0;
        if (baseStats != null) {
            value = baseStats.get(type);
        }
        // look to background (proficiencies)
        if (background != null) {
            value += background.getStatModifier(type);
        }
        // look to race (proficiencies)
        if (race != null) {
            value += race.getStatModifier(type);
        }
        // look to classes (proficiencies, half, expert)
        if (classes != null) {
            for (CharacterClass each : classes) {
                value += each.getStatModifier(type);
            }
        }
        // go through equipment
        // go through effects..
        return value;
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
        // TODO
        // base off classes.size()
        return 2;
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
        result.addAll(background.getFeatures());
        result.addAll(race.getFeatures());
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

    public Document toXML() {
      /*  DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element root = document.createElement("character");
        document.appendChild(root);

        Element nameElem = document.createElement("name");
        nameElem.setTextContent(name);
        root.appendChild(nameElem);

        Element nameElem = document.createElement("name");
        nameElem.setTextContent(name);
        root.appendChild(nameElem);



        return document;*/
        return null;
    }

    public void longRest() {
        // need to take an input context ?
        //     which features to refresh, to apply full HP, etc

        // temp HP disappear, unless a spell/effect

        List<FeatureInfo> featureInfos = getFeatureInfos();
        for (FeatureInfo each : featureInfos) {
            if (each.getFeature().getRefreshesOn() == RefreshType.LONG_REST) {
                usedFeatures.put(each.getFeature().getName(), 0);
            }
        }
        hp = getMaxHP();
        // restore hit die / 2
        //refresh features
    }

    public void shortRest() {
        // need to take an input context ?
        //     which features to refresh, how many hit die to apply, etc

        List<FeatureInfo> featureInfos = getFeatureInfos();
        for (FeatureInfo each : featureInfos) {
            if (each.getFeature().getRefreshesOn() == RefreshType.SHORT_REST) {
                usedFeatures.put(each.getFeature().getName(), 0);
            }
        }
        hp = getMaxHP();
    }


}
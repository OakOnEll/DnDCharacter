package com.oakonell.dndcharacter.model.character;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.model.character.companion.AbstractCompanionType;
import com.oakonell.dndcharacter.model.character.companion.CharacterCompanion;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.character.item.CharacterArmor;
import com.oakonell.dndcharacter.model.character.item.CharacterItem;
import com.oakonell.dndcharacter.model.character.item.CharacterWeapon;
import com.oakonell.dndcharacter.model.character.rest.AbstractRestRequest;
import com.oakonell.dndcharacter.model.character.rest.LongRestRequest;
import com.oakonell.dndcharacter.model.character.rest.ShortRestRequest;
import com.oakonell.dndcharacter.model.character.spell.CharacterSpell;
import com.oakonell.dndcharacter.model.character.stats.StatBlock;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.classes.AClass;
import com.oakonell.dndcharacter.model.components.Proficiency;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.model.components.RefreshType;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;
import com.oakonell.expression.ExpressionType;
import com.oakonell.expression.ExpressionValue;
import com.oakonell.expression.context.SimpleFunctionContext;
import com.oakonell.expression.context.SimpleVariableContext;
import com.oakonell.expression.functions.ExpressionFunction;
import com.oakonell.expression.types.BooleanValue;
import com.oakonell.expression.types.StringValue;

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
public class Character extends AbstractCharacter {

    @Element(required = false)
    private int xp;

    @Element(required = false)
    @Deprecated
    // TODO delete- will need to dump DB
            int tempHpMax;
    @Element(required = false)
    private CharacterBackground background;
    @Element(required = false)
    private CharacterRace race;
    @NonNull
    @ElementList(required = false)
    private List<CharacterClass> classes = new ArrayList<>();

    @NonNull
    @ElementMap(entry = "hitDie", key = "die", value = "uses", required = false)
    private Map<Integer, Integer> hitDieUses = new HashMap<>();

    @Element(required = false)
    private String backstory;


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
    private List<Gem> gems = new ArrayList<>();


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

    @Element(required = false)
    private Alignment alignment;


    @ElementList(required = false)
    private List<CharacterCompanion> companions = new ArrayList<>();
    @Element(required = false)
    private int displayedCompanionIndex = -1;


    @ElementMap(entry = "displayOrder", key = "level", value = "value", required = false)
    private Map<Integer, SpellSort> displayOrderSpellsByLevel = new HashMap<>();
    @ElementMap(entry = "preparedOnly", key = "level", value = "value", required = false)
    private Map<Integer, Boolean> showOnlyPreparedSpellsByLevel = new HashMap<>();


    public Character() {
    }

    public int getMaxHP() {
        int hp = 0;
        StatBlock conBlock = getStatBlock(StatType.CONSTITUTION);
        int conMod = conBlock.getModifier();

        for (CharacterClass each : classes) {
            // You always gain at least 1 HP per level
            int gain = each.getHpRoll() + conMod;
            hp += Math.max(gain, 1);
        }
        // TODO what about race, like dwarf, that effects hp
        final int value[] = new int[]{hp};
        ComponentVisitor deriver = new ComponentVisitor() {
            @Override
            public void visitComponent(@NonNull ICharacterComponent component) {
                SimpleVariableContext variableContext = new SimpleVariableContext();
                component.addExtraFormulaVariables(variableContext, Character.this);
                value[0] += evaluateFormula(component.getHpFormula(), variableContext);
            }
        };
        getAbilityDeriver(deriver, false).derive(this, "hp mods");

        return value[0];
    }

    public Collection<? extends AbstractCompanionType> getCompanionTypes() {
        final List<AbstractCompanionType> types = new ArrayList<>();

        ComponentVisitor visitor = new ComponentVisitor() {
            @Override
            public void visitComponent(ICharacterComponent component) {
                types.addAll(component.getCompanionTypes());
            }
        };

        CharacterAbilityDeriver deriver = getAbilityDeriver(visitor, false);
        deriver.derive(this, "Deriving companion types");

        final List<SpellLevelInfo> spellInfos = getSpellInfos();
        for (SpellLevelInfo eachInfo : spellInfos) {
            final List<CharacterSpellWithSource> spells = eachInfo.getSpellInfos();
            for (CharacterSpellWithSource spell : spells) {
                types.addAll(spell.getSpell().getCompanionTypes());
            }
        }

        return types;
    }

//    static class CompanionTypeDeriver extends CharacterAbilityDeriver {
//        public CompanionTypeDeriver(ComponentVisitor visitor) {
//            super(visitor);
//        }
//    }

    @NonNull
    protected SimpleVariableContext getPopulatedVariableContext(@Nullable SimpleVariableContext variableContext) {
        variableContext = super.getPopulatedVariableContext(variableContext);
        variableContext.setNumber("level", getCharacterLevel());
        return variableContext;
    }


    public void longRest(@NonNull LongRestRequest request) {
        super.longRest(request);

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

        resetSpellSlots(request);
    }

    public void shortRest(@NonNull ShortRestRequest request) {
        super.shortRest(request);

        for (Map.Entry<Integer, Integer> entry : request.getHitDieUses().entrySet()) {
            int die = entry.getKey();
            int requestUses = entry.getValue();

            Integer uses = hitDieUses.get(die);
            if (uses == null) uses = 0;
            uses += requestUses;
            hitDieUses.put(die, uses);
        }

        resetSpellSlots(request);
    }


    protected void addAdditionalACRoots(RootArmorClassDeriver addRootAc) {
        if (getRace() != null) {
            for (FeatureInfo each : getRace().getFeatures(this)) {
                if (!each.isBaseArmor()) continue;

                addRootAc.derive(each);
            }
        }

        // multiple here will really just take the highest ?? at runtime
        for (CharacterClass eachClass : classes) {
            for (FeatureInfo each : eachClass.getFeatures(this)) {
                if (!each.isBaseArmor()) continue;

                addRootAc.derive(each);
            }
        }
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

            builder.append(name).append(" ").append(level);
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

    public void addGem(Gem gem) {
        gems.add(gem);
    }

    public List<Gem> getGems() {
        return gems;
    }

    public MoneyValue getGemsValue() {
        MoneyValue value = new MoneyValue();
        for (Gem each : gems) {
            value = value.add(each.value);
        }
        return value.simplified();
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
        ComponentVisitor languagesDeriver = new ComponentVisitor() {
            @Override
            public void visitComponent(@NonNull ICharacterComponent component) {
                for (String each : component.getLanguages()) {
                    LanguageWithSource row = new LanguageWithSource(each, component);
                    languages.add(row);
                }
            }
        };
        getAbilityDeriver(languagesDeriver, false).derive(this, "languages");
        return languages;
    }

    public boolean isProficientWith(@NonNull CharacterArmor armor) {
        // If armor includes rings and such, this works. Otherwise, we'll need an indicator whether
        //    proficiency is applicable or not?
        if (armor.getCategory() == null && !armor.isShield()) return true;
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

    public void removeEffect(CharacterEffect effect) {
        effects.remove(effect);
    }

    @Override
    public boolean anyContextFeats(@SuppressWarnings("SameParameterValue") FeatureContext context) {
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
        final List<SpellLevelInfo> spellInfos = getSpellInfos();
        for (SpellLevelInfo each : spellInfos) {
            for (CharacterSpellWithSource spell : each.getSpellInfos()) {
                if (spell.getSpell().getSource() == ComponentType.CLASS && className.equals(spell.getSpell().getOwnerName())) {
                    if (spell.getSpell().isPrepared()) prepared++;
                }
            }
        }
//        for (Map.Entry<Integer, SpellListWrapper> entry : spellsForLevel.entrySet()) {
//            List<CharacterSpell> spells = entry.getValue().spells;
//            for (CharacterSpell each : spells) {
//                if (each.getSource() == ComponentType.CLASS && className.equals(each.getOwnerName())) {
//                    if (each.isPrepared()) prepared++;
//                }
//            }
//        }
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

    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
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
                builder.append("(").append(proficient).append(")");
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

    @NonNull
    public String getHitDiceString() {
        if (classes == null) return "";

        List<HitDieRow> dice = getHitDiceCounts();
        StringBuilder builder = new StringBuilder();
        Iterator<HitDieRow> iter = dice.iterator();
        while (iter.hasNext()) {
            HitDieRow entry = iter.next();
            builder.append("(").append(entry.numDiceRemaining).append("/").append(entry.totalDice).append(")").append("d").append(entry.dieSides);
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

        @NonNull
        public String getSourceString(@NonNull Resources resources) {
            if (getSource() == null) return getSpell().getOwnerName();
            return super.getSourceString(resources);
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
        final SpellLevelInfo cantrips = new SpellLevelInfo(this, 0);

        ComponentVisitor cantripDeriver = new ComponentVisitor() {
            @Override
            public void visitComponent(@NonNull ICharacterComponent component) {
                for (CharacterSpell each : component.getCantrips()) {
                    cantrips.getSpellInfos().add(new CharacterSpellWithSource(each, component));
                }
            }
        };
        getAbilityDeriver(cantripDeriver, false).derive(this, "cantrip infos");


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
                SpellLevelInfo level = new SpellLevelInfo(this, i);
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

        final Map<String, List<CharacterClass.ReplacedSpell>> replacedSpells = new HashMap<>();
        final Map<CharacterClass.ReplacedSpell, CharacterClass> replacedSpellComponent = new HashMap<>();
        for (CharacterClass each : getClasses()) {
            CharacterClass.ReplacedSpell spell = each.getReplacedSpell();
            if (spell != null) {
                List<CharacterClass.ReplacedSpell> spells = replacedSpells.get(each.getName());
                if (spells == null) {
                    spells = new ArrayList<>();
                    replacedSpells.put(each.getName(), spells);
                }
                spells.add(spell);
                replacedSpellComponent.put(spell, each);
            }
        }

        ComponentVisitor spellDeriver = new ComponentVisitor() {
            @Override
            public void visitComponent(@NonNull ICharacterComponent component) {
                List<CharacterClass.ReplacedSpell> classReplacedSpells = replacedSpells.get(component.getName());
                int classLevel = 0;
                if (component instanceof CharacterClass) {
                    classLevel = ((CharacterClass) component).getLevel();
                }
                int index = 0;
                for (CharacterSpell each : component.getSpells()) {
                    int level = each.getLevel();
                    if (level == 0) {
                        // the level was unknown at creation time... assume 1st level?
                        level = 1;
                    }
                    SpellLevelInfo levelInfo = result.get(level);
                    boolean replaced = false;
                    if (classReplacedSpells != null) {
                        for (CharacterClass.ReplacedSpell replacedSpell : classReplacedSpells) {
                            if (replacedSpell.knownLevel != classLevel) continue;
                            if (replacedSpell.index != index) continue;
                            replaced = true;
                            final CharacterClass characterClass = replacedSpellComponent.get(replacedSpell);
                            levelInfo.getSpellInfos().add(new CharacterSpellWithSource(replacedSpell.spell, characterClass));
                        }
                    }
                    if (!replaced) {
                        levelInfo.getSpellInfos().add(new CharacterSpellWithSource(each, component));
                    }
                    index++;
                }
            }
        };
        getAbilityDeriver(spellDeriver, false).derive(this, "spell infos");

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
            SpellLevelInfo level = new SpellLevelInfo(this, 1);
            level.maxSlots = 2;
            result.add(level);
        } else if (effectiveCasterLevel == 2) {
            SpellLevelInfo level = new SpellLevelInfo(this, 1);
            level.maxSlots = 3;
            result.add(level);
        } else if (effectiveCasterLevel == 3) {
            SpellLevelInfo level = new SpellLevelInfo(this, 1);
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo(this, 2);
            level.maxSlots = 2;
            result.add(level);
        } else if (effectiveCasterLevel == 4) {
            SpellLevelInfo level = new SpellLevelInfo(this, 1);
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo(this, 2);
            level.maxSlots = 3;
            result.add(level);
        } else if (effectiveCasterLevel == 5) {
            SpellLevelInfo level = new SpellLevelInfo(this, 1);
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo(this, 2);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 3);
            level.maxSlots = 2;
            result.add(level);
        } else if (effectiveCasterLevel == 6) {
            SpellLevelInfo level = new SpellLevelInfo(this, 1);
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo(this, 2);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 3);
            level.maxSlots = 3;
            result.add(level);
        } else if (effectiveCasterLevel == 7) {
            SpellLevelInfo level = new SpellLevelInfo(this, 1);
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo(this, 2);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 3);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 4);
            level.maxSlots = 1;
            result.add(level);
        } else if (effectiveCasterLevel == 8) {
            SpellLevelInfo level = new SpellLevelInfo(this, 1);
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo(this, 2);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 3);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 4);
            level.maxSlots = 2;
            result.add(level);
        } else if (effectiveCasterLevel == 9) {
            SpellLevelInfo level = new SpellLevelInfo(this, 1);
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo(this, 2);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 3);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 4);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 5);
            level.maxSlots = 1;
            result.add(level);
        } else if (effectiveCasterLevel == 10) {
            SpellLevelInfo level = new SpellLevelInfo(this, 1);
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo(this, 2);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 3);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 4);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 5);
            level.maxSlots = 2;
            result.add(level);
        } else if (effectiveCasterLevel == 11 || effectiveCasterLevel == 12) {
            SpellLevelInfo level = new SpellLevelInfo(this, 1);
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo(this, 2);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 3);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 4);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 5);
            level.maxSlots = 2;
            result.add(level);

            level = new SpellLevelInfo(this, 6);
            level.maxSlots = 1;
            result.add(level);
        } else if (effectiveCasterLevel == 13 || effectiveCasterLevel == 14) {
            SpellLevelInfo level = new SpellLevelInfo(this, 1);
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo(this, 2);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 3);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 4);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 5);
            level.maxSlots = 2;
            result.add(level);

            level = new SpellLevelInfo(this, 6);
            level.maxSlots = 1;
            result.add(level);

            level = new SpellLevelInfo(this, 7);
            level.maxSlots = 1;
            result.add(level);
        } else if (effectiveCasterLevel == 15 || effectiveCasterLevel == 16) {
            SpellLevelInfo level = new SpellLevelInfo(this, 1);
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo(this, 2);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 3);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 4);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 5);
            level.maxSlots = 2;
            result.add(level);

            level = new SpellLevelInfo(this, 6);
            level.maxSlots = 1;
            result.add(level);

            level = new SpellLevelInfo(this, 7);
            level.maxSlots = 1;
            result.add(level);

            level = new SpellLevelInfo(this, 8);
            level.maxSlots = 1;
            result.add(level);
        } else if (effectiveCasterLevel == 17) {
            SpellLevelInfo level = new SpellLevelInfo(this, 1);
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo(this, 2);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 3);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 4);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 5);
            level.maxSlots = 2;
            result.add(level);

            level = new SpellLevelInfo(this, 6);
            level.maxSlots = 1;
            result.add(level);

            level = new SpellLevelInfo(this, 7);
            level.maxSlots = 1;
            result.add(level);

            level = new SpellLevelInfo(this, 8);
            level.maxSlots = 1;
            result.add(level);

            level = new SpellLevelInfo(this, 9);
            level.maxSlots = 1;
            result.add(level);
        } else if (effectiveCasterLevel == 18) {
            SpellLevelInfo level = new SpellLevelInfo(this, 1);
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo(this, 2);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 3);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 4);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 5);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 6);
            level.maxSlots = 1;
            result.add(level);

            level = new SpellLevelInfo(this, 7);
            level.maxSlots = 1;
            result.add(level);

            level = new SpellLevelInfo(this, 8);
            level.maxSlots = 1;
            result.add(level);

            level = new SpellLevelInfo(this, 9);
            level.maxSlots = 1;
            result.add(level);
        } else if (effectiveCasterLevel == 19) {
            SpellLevelInfo level = new SpellLevelInfo(this, 1);
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo(this, 2);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 3);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 4);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 5);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 6);
            level.maxSlots = 2;
            result.add(level);

            level = new SpellLevelInfo(this, 7);
            level.maxSlots = 1;
            result.add(level);

            level = new SpellLevelInfo(this, 8);
            level.maxSlots = 1;
            result.add(level);

            level = new SpellLevelInfo(this, 9);
            level.maxSlots = 1;
            result.add(level);
        } else if (effectiveCasterLevel == 20) {
            SpellLevelInfo level = new SpellLevelInfo(this, 1);
            level.maxSlots = 4;
            result.add(level);

            level = new SpellLevelInfo(this, 2);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 3);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 4);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 5);
            level.maxSlots = 3;
            result.add(level);

            level = new SpellLevelInfo(this, 6);
            level.maxSlots = 2;
            result.add(level);

            level = new SpellLevelInfo(this, 7);
            level.maxSlots = 2;
            result.add(level);

            level = new SpellLevelInfo(this, 8);
            level.maxSlots = 1;
            result.add(level);

            level = new SpellLevelInfo(this, 9);
            level.maxSlots = 1;
            result.add(level);
        }

        for (CastingClassInfo specialCastingClass : specialSlotClasses) {
            for (Map.Entry<Integer, String> each : specialCastingClass.getSlotMap().entrySet()) {
                int level = each.getKey();
                String slots = each.getValue();
                while (level >= result.size()) {
                    SpellLevelInfo levelInfo = new SpellLevelInfo(this, result.size());
                    result.add(levelInfo);
                }
                final SpellLevelInfo levelInfo = result.get(level);
                levelInfo.maxSlots += evaluateFormula(slots, null);
            }
        }

    }

    public enum SpellSort {
        NONE, UP, DOWN;

        public SpellSort next() {
            if (this == NONE) return UP;
            if (this == UP) return DOWN;
            return NONE;
        }
    }

    public static class SpellLevelInfo {
        final Character character;
        final int level;
        int slotsAvailable;
        int maxSlots;

        @NonNull
        private List<CharacterSpellWithSource> spellInfos = new ArrayList<>();

        private boolean showPreparedOnly;
        private SpellSort displaySortOrder;

        SpellLevelInfo(Character character, int level) {
            this.character = character;
            this.level = level;

            Boolean charPreparedOnly = character.showOnlyPreparedSpellsByLevel.get(level);
            if (charPreparedOnly == null) charPreparedOnly = Boolean.FALSE;
            showPreparedOnly = charPreparedOnly;

            SpellSort charSort = character.displayOrderSpellsByLevel.get(level);
            if (charSort == null) charSort = SpellSort.NONE;
            displaySortOrder = charSort;
        }

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

        public void setShowPreparedOnly(boolean showPreparedOnly) {
            this.showPreparedOnly = showPreparedOnly;
            character.setSpellShowPreparedOnly(level, showPreparedOnly);
        }

        public boolean isShowPreparedOnly() {
            return showPreparedOnly;
        }

        public SpellSort getDisplaySortOrder() {
            return displaySortOrder;
        }

        public void setDisplaySortOrder(SpellSort displaySortOrder) {
            this.displaySortOrder = displaySortOrder;
            character.setSpellShowSortOrder(level, displaySortOrder);
        }

        public List<CharacterSpellWithSource> getDisplaySpellInfos() {
            List<CharacterSpellWithSource> result = new ArrayList<>();
            if (showPreparedOnly) {
                for (CharacterSpellWithSource each : spellInfos) {
                    if (each.getSpell().isAlwaysPrepared() || !each.getSpell().isPreparable() || each.getSpell().isPrepared() || each.getSpell().isRitual()) {
                        result.add(each);
                    }
                }
            } else {
                result.addAll(spellInfos);
            }

            Comparator<CharacterSpellWithSource> spellSorter = null;
            if (displaySortOrder == SpellSort.UP) {
                spellSorter = new Comparator<CharacterSpellWithSource>() {
                    @Override
                    public int compare(CharacterSpellWithSource lhs, CharacterSpellWithSource rhs) {
                        return lhs.getSpell().getName().compareToIgnoreCase(rhs.getSpell().getName());
                    }
                };
            } else if (displaySortOrder == SpellSort.DOWN) {
                spellSorter = new Comparator<CharacterSpellWithSource>() {
                    @Override
                    public int compare(CharacterSpellWithSource lhs, CharacterSpellWithSource rhs) {
                        return -lhs.getSpell().getName().compareToIgnoreCase(rhs.getSpell().getName());
                    }
                };
            }
            if (spellSorter != null) {
                Collections.sort(result, spellSorter);
            }

            return result;
        }
    }

    private void setSpellShowSortOrder(int level, SpellSort displaySortOrder) {
        displayOrderSpellsByLevel.put(level, displaySortOrder);
    }


    private void setSpellShowPreparedOnly(int level, boolean showPreparedOnly) {
        showOnlyPreparedSpellsByLevel.put(level, showPreparedOnly);
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


        public CastingClassInfo() {
            // for finding usages
        }

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
        return getCasterClassInfoFor(owningClassName, null);
    }

    public CastingClassInfo getCasterClassInfoFor(String owningClassName, Integer characterLevelInt) {
        // TODO optimize this
        Map<String, CastingClassInfo> classInfoMap = getCastingClassInfoMap(characterLevelInt);
        return classInfoMap.get(owningClassName);
    }

    @NonNull
    public Collection<CastingClassInfo> getCasterClassInfo() {
        Map<String, CastingClassInfo> classInfoMap = getCastingClassInfoMap(null);
        return classInfoMap.values();
    }

    @NonNull
    protected Map<String, CastingClassInfo> getCastingClassInfoMap(Integer characterLevelInt) {
        Map<String, CastingClassInfo> classInfoMap = new HashMap<>();

        int maxLevel = characterLevelInt == null ? 9999 : characterLevelInt;
        int charLevel = 0;
        for (CharacterClass each : classes) {
            charLevel++;
            if (charLevel > maxLevel) break;
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
            int maxSpellLevel = 0;
            Map<Integer, String> slotMap = each.getSpellLevelSlotFormulas();
            if (slotMap != null) {
                for (Integer level : slotMap.keySet()) {
                    if (level > maxSpellLevel) maxSpellLevel = level;
                }
                info.maxSpellLevel = maxSpellLevel;
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


    public static class Gem implements Parcelable {
        // Method to recreate a HpRow from a Parcel
        @NonNull
        public static Parcelable.Creator<Gem> CREATOR = new Parcelable.Creator<Gem>() {

            @NonNull
            @Override
            public Gem createFromParcel(@NonNull Parcel source) {
                return new Gem(source);
            }

            @NonNull
            @Override
            public Gem[] newArray(int size) {
                return new Gem[size];
            }

        };
        @Element(required = false)
        String name;

        @Element(required = false)
        private MoneyValue value;

        // For simple persistence FW
        public Gem() {

        }

        public Gem(String name, MoneyValue value) {
            this.name = name;
            this.value = value;
        }

        public Gem(Parcel source) {
            name = source.readString();
            value = source.readParcelable(ClassLoader.getSystemClassLoader());
        }

        public String getName() {
            return name;
        }

        public MoneyValue getValue() {
            return value;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeParcelable(value, flags);
        }
    }

    @NonNull
    public List<CharacterCompanion> getCompanions() {
        if (companions == null) {
            companions = new ArrayList<>();
        }
        return companions;
    }

    public CharacterCompanion getDisplayedCompanion() {
        if (displayedCompanionIndex < 0) return null;
        if (companions.size() == 0) return null;
        if (displayedCompanionIndex >= companions.size()) {
            displayedCompanionIndex = companions.size() - 1;
        }
        return companions.get(displayedCompanionIndex);
    }

    public int getDisplayedCompanionIndex() {
        return displayedCompanionIndex;
    }

    public void setDisplayedCompanion(int index) {
        displayedCompanionIndex = index;
    }

    public void addExtraFormulaVariables(SimpleVariableContext variableContext) {
        variableContext.setNumber("level", getClasses().size());
    }


    @Override
    public CharacterAbilityDeriver getAbilityDeriver(ComponentVisitor visitor, boolean skipFeatures) {
        return new CharacterAbilityDeriver(visitor, skipFeatures);
    }

    @NonNull
    protected String getBaseACString() {
        return "10 + dexterityMod";
    }
}
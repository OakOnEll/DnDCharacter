package com.oakonell.dndcharacter.model.character.companion;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.model.character.FeatureInfo;
import com.oakonell.dndcharacter.model.character.SpeedType;
import com.oakonell.dndcharacter.model.character.rest.AbstractRestRequest;
import com.oakonell.dndcharacter.model.character.rest.LongRestRequest;
import com.oakonell.dndcharacter.model.character.rest.ShortRestRequest;
import com.oakonell.dndcharacter.model.character.stats.SkillBlock;
import com.oakonell.dndcharacter.model.character.stats.SkillType;
import com.oakonell.dndcharacter.model.character.stats.StatBlock;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.components.IFeatureAction;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 10/31/2016.
 */

public class Companion extends AbstractCharacter {
    @Element(required = false)
    private String name;

    @Element(required = false)
    private String race;

    @Element(required = false)
    private int hp;

    @Element(required = false)
    private int tempHp;


    @Element(required = false)
    private String hpMaxFormula;

    @Element(required = false)
    private int deathSaveFails;
    @Element(required = false)
    private int deathSaveSuccesses;
    @Element(required = false)
    private boolean stable;

    @Element(required = false)
    private String notes = "";

    @ElementMap(entry = "stat", key = "name", value = "value", required = false)
    private Map<StatType, Integer> baseStats = new HashMap<>();

    @Element(required = false)
    private boolean deleting;


    public int getArmorClass() {
        return 10;
//        List<Character.ArmorClassWithSource> modifiers = deriveModifyingAcs();
//        List<Character.ArmorClassWithSource> roots = deriveRootAcs();
//        Character.ArmorClassWithSource activeRoot = null;
//        for (Character.ArmorClassWithSource each : roots) {
//            if (each.isEquipped()) {
//                activeRoot = each;
//                break;
//            }
//        }
//        if (activeRoot == null) {
//            throw new RuntimeException("No active AC root!?");
//        }
//        // go through active equipment
//        // if no equipment affects ac, it is just 10 + dex mod
//
//        int ac = activeRoot.getValue();
//        int addition = 0;
//
//        for (Character.ArmorClassWithSource each : modifiers) {
//            if (each.isEquipped()) {
//                addition += each.getValue();
//            }
//        }
//
//        return ac + addition;
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


    public int getMaxHP() {
        int hp = 0;
        StatBlock conBlock = getStatBlock(StatType.CONSTITUTION);
        //int conMod = conBlock.getModifier();

//        for (CharacterClass each : classes) {
//            // You always gain at least 1 HP per level
//            int gain = each.getHpRoll() + conMod;
//            hp += Math.max(gain, 1);
//        }
//        // TODO what about race, like dwarf, that effects hp
//        final int value[] = new int[]{hp};
//        CharacterAbilityDeriver deriver = new CharacterAbilityDeriver() {
//            protected void visitComponent(@NonNull ICharacterComponent component) {
//                SimpleVariableContext variableContext = new SimpleVariableContext();
//                component.addExtraFormulaVariables(variableContext, Character.this);
//                value[0] += evaluateFormula(component.getHpFormula(), variableContext);
//            }
//        };
//        deriver.derive(this, "hp mods");

//        return value[0];

        return 10;
    }

    @NonNull
    public StatBlock getStatBlock(StatType type) {
        //return new StatBlock(this, type);
        return null;
    }

    @NonNull
    public SkillBlock getSkillBlock(SkillType type) {

        //return new SkillBlock(this, type);
        return null;

    }


    public CharacterEffect useFeatureAction(@NonNull FeatureInfo feature, @NonNull IFeatureAction action, Map<String, String> variableValues) {
        useFeature(feature, action.getCost());
        //return action.applyToCharacter(this, variableValues);
        return null;
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
//        resetSpellSlots(request);
    }

    public void shortRest(@NonNull ShortRestRequest request) {
        hp = Math.min(hp + request.getHealing(), getMaxHP());


        resetFeatures(request);
    }

    public int getInitiative() {
        int result = 0;
//        List<Character.InitiativeWithSource> initList = deriveInitiative();
//        for (Character.InitiativeWithSource each : initList) {
//            if (each.isActive()) {
//                result += each.getInitiative();
//            }
//        }

        return result;
    }

    public int getPassivePerception() {
        int result = 0;
//        for (Character.PassivePerceptionWithSource each : derivePassivePerception()) {
//            if (each.isActive()) {
//                result += each.getPassivePerception();
//            }
//        }
        return result;
    }

    private void resetFeatures(@NonNull AbstractRestRequest request) {
//        Collection<FeatureInfo> featureInfos = getFeatureInfos();
//        for (FeatureInfo each : featureInfos) {
//            Integer resetRequest = request.getFeatureResets().get(each.getName());
//            if (resetRequest == null || resetRequest == 0) continue;
//
//            Integer used = usedFeatures.get(each.getName());
//            if (used == null) continue;
//
//            used = used - resetRequest;
//            if (used <= 0) {
//                usedFeatures.remove(each.getName());
//            } else {
//                usedFeatures.put(each.getName(), used);
//            }
    }


    public void useFeature(@NonNull FeatureInfo feature, int amount) {
        // TODO apply known effects from feature
//        int uses = getUses(feature.getName());
//        uses = uses + amount;
//        usedFeatures.put(feature.getName(), uses);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public boolean isDeleting() {
        return deleting;
    }

    public void setDeleting(boolean deleting) {
        this.deleting = deleting;
    }

    public SpeedType getSpeedType() {
        return SpeedType.WALK;
    }

    public long getSpeed(SpeedType speedType) {
        return 30;
    }

    public int getTempHp() {
        return tempHp;
    }

    public boolean isStable() {
        return stable;
    }

    public void stabilize() {
        stable = true;
    }
}

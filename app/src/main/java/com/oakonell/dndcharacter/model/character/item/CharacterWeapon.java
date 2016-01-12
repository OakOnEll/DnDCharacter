package com.oakonell.dndcharacter.model.character.item;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.*;
import com.oakonell.dndcharacter.model.character.stats.StatBlock;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.item.ItemType;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 12/8/2015.
 */
public class CharacterWeapon extends CharacterItem {
    @Element(required = false)
    private boolean isRanged;

    @Element(required = false)
    private String range;
    @ElementList(required = false)
    private Set<String> properties;

    @ElementList(required = false)
    private List<DamageFormula> damageFormulas = new ArrayList<>();

    @ElementList(required = false)
    private List<DamageFormula> versatileDamageFormulas = new ArrayList<>();
    @Element(required = false)
    private String ammunition;


    public ItemType getItemType() {
        return ItemType.WEAPON;
    }


    public void addDamage(String formula, DamageType type) {
        DamageFormula damage = new DamageFormula(formula, type);
        damageFormulas.add(damage);
    }

    public void addVersatileDamage(String formula, DamageType type) {
        DamageFormula damage = new DamageFormula(formula, type);
        versatileDamageFormulas.add(damage);
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public void setProperties(String[] properties) {
        this.properties = new HashSet<>();
        for (String each : properties) {
            this.properties.add(each.toUpperCase().trim());
        }
    }

    public String getDamageString() {
        final List<DamageFormula> damageFormulas = this.damageFormulas;
        return getDamagesString(damageFormulas);
    }

    public String getVersatileDamageString() {
        final List<DamageFormula> damageFormulas = this.versatileDamageFormulas;
        return getDamagesString(damageFormulas);
    }

    @NonNull
    private String getDamagesString(List<DamageFormula> damageFormulas) {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for (DamageFormula each : damageFormulas) {
            if (!isFirst) {
                builder.append(", ");
            }
            builder.append(each.damageFormula);
            builder.append(" ");
            builder.append(each.type.toString());
            isFirst = false;
        }
        return builder.toString();
    }

    public List<DamageFormula> getDamages() {
        return damageFormulas;
    }

    public List<DamageFormula> getVersatileDamages() {
        return versatileDamageFormulas;
    }


    public void setIsRanged(boolean ranged) {
        this.isRanged = ranged;
    }

    public boolean isRanged() {
        return isRanged;
    }

    public boolean isVersatile() {
        return properties != null && properties.contains("VERSATILE");
    }

    public boolean isTwoHanded() {
        return properties != null && properties.contains("TWO-HANDED");
    }

    public boolean isFinesse() {
        return properties != null && properties.contains("FINESSE");
    }

    public String getDescriptionString() {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        if (properties != null) {
            for (String each : properties) {
                if (!isFirst) {
                    builder.append(", ");
                }
                String string = each.substring(0, 1) + each.substring(1).toLowerCase();
                builder.append(string);
                if (string.equals("Thrown")) {
                    builder.append("(");
                    builder.append(range);
                    builder.append(")");
                }
                isFirst = false;
            }
        }
        if (isRanged) {
            if (!isFirst) {
                builder.append(", ");
            }
            builder.append("Ranged(");
            builder.append(range);
            builder.append(")");
            isFirst = false;
        }
        return builder.toString();
    }

    public AttackModifiers getAttackModifiers(com.oakonell.dndcharacter.model.character.Character character, boolean useFinesse) {
        StatBlock statBlock;
        if (useFinesse || isRanged()) {
            statBlock = character.getStatBlock(StatType.DEXTERITY);
        } else {
            statBlock = character.getStatBlock(StatType.STRENGTH);
        }

        int damageModifier = statBlock.getModifier();

        boolean isProficient = character.isProficientWith(this);
        int profBonus = isProficient ? character.getProficiency() : 0;

        return new AttackModifiers(damageModifier + profBonus, damageModifier);
    }

    public void setAmmunition(String ammunition) {
        this.ammunition = ammunition;
    }

    public String getAmmunition() {
        return ammunition;
    }

    public static class DamageFormula {
        @Element(required = false)
        String damageFormula;
        @Element(required = false)
        DamageType type;

        public DamageFormula() {
            // for SimpleXML persistence FW
        }

        public DamageFormula(String formula, DamageType type) {
            this.damageFormula = formula;
            this.type = type;
        }

        public String getDamageFormula() {
            return damageFormula;
        }

        public DamageType getType() {
            return type;
        }
    }

    public static class AttackModifiers {
        int attackBonus;
        int damageModifier;

        public AttackModifiers(int attackBonus, int damageModifier) {
            this.attackBonus = attackBonus;
            this.damageModifier = damageModifier;
        }

        public int getAttackBonus() {
            return attackBonus;
        }

        public int getDamageModifier() {
            return damageModifier;
        }
    }
}

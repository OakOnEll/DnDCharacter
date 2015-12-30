package com.oakonell.dndcharacter.model;

import com.oakonell.dndcharacter.model.item.ItemType;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 12/8/2015.
 */
public class CharacterWeapon extends CharacterItem {

    private String range;
    private String[] properties;

    public ItemType getItemType() {
        return ItemType.WEAPON;
    }

    @ElementList(required = false)
    private List<DamageFormula> damageFormulas = new ArrayList<>();

    public void addDamage(String formula, DamageType type) {
        DamageFormula damage = new DamageFormula(formula, type);
        damageFormulas.add(damage);
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getRange() {
        return range;
    }

    public void setProperties(String[] properties) {
        this.properties = properties;
    }

    public String[] getProperties() {
        return properties;
    }

    public String getDamageString() {
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

    public AttackModifiers getAttackModifiers(Character character) {
        // TODO finesse may use dexterity ??
        // TODO Ranged DOES use dexterity.
        final StatBlock statBlock = character.getStatBlock(StatType.STRENGTH);

        int damageModifier = statBlock.getModifier();

        boolean isProficient = character.isProficientWith(this);
        int profBonus = isProficient ? character.getProficiency() : 0;

        return new AttackModifiers(damageModifier + profBonus, damageModifier);
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

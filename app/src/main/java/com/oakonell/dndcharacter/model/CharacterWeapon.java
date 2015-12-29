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
    }
}

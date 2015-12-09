package com.oakonell.dndcharacter.model;

import com.oakonell.dndcharacter.model.item.ItemType;

import java.util.List;

/**
 * Created by Rob on 12/8/2015.
 */
public class CharacterWeapon extends CharacterItem {
    public static class DamageDice {
        int sides;
        int num;
    }

    public static class DamageMD {
        DamageType type;
        List<DamageDice> dice;
        int bonus;
    }

    public List<DamageMD> getDamageMDs() {
        return null;
    }

    public ItemType getItemType() {
        return ItemType.WEAPON;
    }

}

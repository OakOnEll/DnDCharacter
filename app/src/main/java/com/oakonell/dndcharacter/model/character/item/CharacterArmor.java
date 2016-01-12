package com.oakonell.dndcharacter.model.character.item;

import com.oakonell.dndcharacter.model.item.ItemType;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 12/8/2015.
 */
public class CharacterArmor extends CharacterItem {

    @Element(required = false)
    private boolean equipped;

    public ItemType getItemType() {
        return ItemType.ARMOR;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }


}

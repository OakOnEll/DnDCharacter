package com.oakonell.dndcharacter.model;

import com.oakonell.dndcharacter.model.item.ItemType;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 12/8/2015.
 */
public class CharacterArmor extends CharacterItem {
    @Element(required = false)
    String acFormula;

    @Element(required = false)
    private boolean equipped;

    public ItemType getItemType() {
        return ItemType.ARMOR;
    }

    public void setAcFormula(String acFormula) {
        this.acFormula = acFormula;
    }

    public String getAcFormula() {
        return acFormula;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }
}

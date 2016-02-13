package com.oakonell.dndcharacter.model.item;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 11/22/2015.
 */
public enum ItemType {
    WEAPON(R.string.weapon), ARMOR(R.string.armor), EQUIPMENT(R.string.equipment);

    private final int stringResId;

    ItemType(int stringResId) {
        this.stringResId = stringResId;
    }

    public int getStringResId() {
        return stringResId;
    }

    @Override
    public String toString() {
        return super.toString();
    }
    }

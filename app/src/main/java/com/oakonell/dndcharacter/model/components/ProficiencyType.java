package com.oakonell.dndcharacter.model.components;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 10/24/2015.
 */
public enum ProficiencyType {
    WEAPON(R.string.weapon), ARMOR(R.string.armor), TOOL(R.string.tool);

    private final int stringResId;

    ProficiencyType(int stringResId) {
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

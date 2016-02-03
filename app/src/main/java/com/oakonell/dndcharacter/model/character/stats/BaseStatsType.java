package com.oakonell.dndcharacter.model.character.stats;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 12/15/2015.
 */
public enum BaseStatsType {
    CUSTOM(R.string.custom), SIMPLE(R.string.simple), POINT_BUY(R.string.point_buy), ROLL(R.string.roll);

    private final int stringResId;

    BaseStatsType(int stringResId) {
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

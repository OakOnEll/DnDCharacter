package com.oakonell.dndcharacter.model.character;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 2/5/2016.
 */
public enum SpeedType {
    WALK(R.string.walk, CustomAdjustmentType.SPEED_WALK),  SWIM(R.string.swim, CustomAdjustmentType.SPEED_SWIM),
    CLIMB(R.string.climb, CustomAdjustmentType.SPEED_CLIMB), CRAWL(R.string.crawl, CustomAdjustmentType.SPEED_CRAWL),
    FLY(R.string.fly, CustomAdjustmentType.SPEED_FLY),BURROW(R.string.burrow, CustomAdjustmentType.SPEED_BURROW),;

    private final int stringResId;
    private final CustomAdjustmentType customType;

    SpeedType(int stringResId, CustomAdjustmentType customType) {
        this.stringResId = stringResId;
        this.customType = customType;
    }

    public int getStringResId() {
        return stringResId;
    }

    public CustomAdjustmentType getCustomType() {
        return customType;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

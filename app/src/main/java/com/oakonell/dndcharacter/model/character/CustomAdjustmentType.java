package com.oakonell.dndcharacter.model.character;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 3/21/2016.
 */
public enum CustomAdjustmentType {
    ROOT_ACS(R.string.root_ac), MODIFYING_ACS(R.string.modifying_acs),

    STAT_STRENGTH(R.string.strength), STAT_DEXTERITY(R.string.dexterity), STAT_CONSTITUTION(R.string.constitution),
    STAT_INTELLIGENCE(R.string.intelligence), STAT_WISDOM(R.string.wisdom), STAT_CHARISMA(R.string.charisma),

    INITIATIVE(R.string.initiative_title), PASSIVE_PERCEPTION(R.string.passive_perception_title),


    SPEED_WALK(R.string.walk), SPEED_FLY(R.string.fly), SPEED_SWIM(R.string.swim), SPEED_BURROW(R.string.burrow),
    SPEED_CLIMB(R.string.climb), SPEED_CRAWL(R.string.crawl);


    private final int stringResId;

    CustomAdjustmentType(int stringResId) {
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

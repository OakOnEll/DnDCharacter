package com.oakonell.dndcharacter.model.character;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 2/5/2016.
 */
public enum SpeedType {
    WALK(R.string.walk), FLY(R.string.fly), SWIM(R.string.swim), BURROW(R.string.burrow), CLIMB(R.string.climb), CRAWL(R.string.crawl);

    private final int stringResId;

    SpeedType(int stringResId) {
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

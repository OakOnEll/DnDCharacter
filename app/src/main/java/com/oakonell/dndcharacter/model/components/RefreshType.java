package com.oakonell.dndcharacter.model.components;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 10/26/2015.
 */
public enum RefreshType {
    LONG_REST(R.string.long_rest), SHORT_REST(R.string.short_rest);

    private final int stringResId;

    RefreshType(int stringResId) {
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

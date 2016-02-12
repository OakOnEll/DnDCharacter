package com.oakonell.dndcharacter.model.character;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 2/11/2016.
 */
public enum FeatureExtensionType {
    EXTEND(R.string.extend), REPLACE(R.string.replace);

    private final int stringResId;

    FeatureExtensionType(int stringResId) {
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

package com.oakonell.dndcharacter.model.character;

import android.content.res.Resources;

/**
 * Created by Rob on 2/11/2016.
 */
public interface ComponentSource {
    String getSourceString(Resources resources);

    ComponentType getType();

    String getActiveFormula();
}

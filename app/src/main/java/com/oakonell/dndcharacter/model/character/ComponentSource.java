package com.oakonell.dndcharacter.model.character;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Rob on 2/11/2016.
 */
public interface ComponentSource {
    String getName();

    @NonNull
    String getSourceString(Resources resources);

    @Nullable
    ComponentType getType();

    @Nullable
    String getActiveFormula();


    String getAsSourceString(Resources resources);

    boolean originatesFrom(ComponentSource currentComponent);
}

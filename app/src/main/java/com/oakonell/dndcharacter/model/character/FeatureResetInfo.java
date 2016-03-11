package com.oakonell.dndcharacter.model.character;

import android.support.annotation.Nullable;

import com.oakonell.dndcharacter.model.components.RefreshType;

/**
 * Created by Rob on 11/9/2015.
 */
public class FeatureResetInfo {
    public String name;
    @Nullable
    public String description;
    public boolean reset;
    public RefreshType refreshOn;
    public String uses;
    public int numToRestore;
    public boolean needsResfesh;
    public int maxToRestore;
}

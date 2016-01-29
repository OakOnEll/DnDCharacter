package com.oakonell.dndcharacter.model.character.rest;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class AbstractRestRequest {
    final Map<String, Integer> featureResets = new HashMap<>();
    final Map<Integer, Integer> spellSlotResets = new HashMap<>();

    public void addFeatureReset(String name, int numToRestore) {
        Integer resets = featureResets.get(name);
        if (resets == null) resets = 0;
        featureResets.put(name, numToRestore + resets);
    }

    @NonNull
    public Map<String, Integer> getFeatureResets() {
        return featureResets;
    }

    public void addSpellSlotReset(int level, int restoreSlots) {
        Integer resets = spellSlotResets.get(level);
        if (resets == null) resets = 0;
        spellSlotResets.put(level, restoreSlots + resets);
    }

    public Map<Integer, Integer> getSpellSlotResets() {
        return spellSlotResets;
    }
}

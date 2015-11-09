package com.oakonell.dndcharacter.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Rob on 11/9/2015.
 */
public class AbstractRestRequest {
    Map<String, Integer> featureResets = new HashMap<>();

    public void addFeatureReset(String name, int numToRestore) {
        Integer resets = featureResets.get(name);
        if (resets == null) resets = 0;
        featureResets.put(name, numToRestore + resets);
    }

    public Map<String, Integer> getFeatureResets() {
        return featureResets;
    }
}

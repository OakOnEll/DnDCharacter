package com.oakonell.dndcharacter.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rob on 11/9/2015.
 */
public class AbstractRestRequest {
    Set<String> featureResets = new HashSet<>();

    public void addFeatureReset(String name) {
        featureResets.add(name);
    }

    public Set<String> getFeatureResets() {
        return featureResets;
    }
}

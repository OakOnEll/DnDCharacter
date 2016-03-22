package com.oakonell.dndcharacter.model.character;

import android.support.annotation.NonNull;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 3/21/2016.
 */
public class CustomAdjustments {
    @Element(required = false)
    private CustomAdjustmentType type;
    @NonNull
    @ElementList(required = false)
    private List<Adjustment> adjustments = new ArrayList<>();

    protected CustomAdjustments() {
        // for simple xml persistence
    }

    public CustomAdjustments(CustomAdjustmentType type) {
        this.type = type;
    }

    public List<Adjustment> getAdjustments() {
        return adjustments;
    }

    public void addAdjustment(String name, int value) {
        Adjustment adj = new Adjustment();
        adj.comment = name;
        adj.numValue = value;
        adjustments.add(adj);

    }

    public void addAdjustment(String name, String value) {
        Adjustment adj = new Adjustment();
        adj.comment = name;
        adj.stringValue = value;
        adjustments.add(adj);
    }

    public void addAdjustment(String name, String stringVal, int numVal) {
        Adjustment adj = new Adjustment();
        adj.comment = name;
        adj.stringValue = stringVal;
        adj.numValue = numVal;
        adjustments.add(adj);
    }

    public void delete(Adjustment source) {
        adjustments.remove(source);
    }

    public static class Adjustment {
        @Element(required = false)
        public int numValue;

        @Element(required = false)
        public String stringValue;


        @Element(required = false)
        public String comment;

        @Element(required = false)
        public boolean applied;
    }
}
package com.oakonell.dndcharacter.model.character;

import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 5/12/2016.
 */
public class ContextNotes {
    @Element(required = false)
    private FeatureContext context;
    @ElementList(required = false)

    private List<ContextNote> notes = new ArrayList<>();

    public ContextNotes(FeatureContext context) {
        this.context = context;
    }

    protected ContextNotes() {
        // for simple xml persistence
    }

    public FeatureContext getContext() {
        return context;
    }

    public List<ContextNote> getNotes() {
        return notes;
    }

}

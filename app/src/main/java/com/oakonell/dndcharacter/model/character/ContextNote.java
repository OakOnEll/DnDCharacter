package com.oakonell.dndcharacter.model.character;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.views.character.IContextualComponent;

import org.simpleframework.xml.Element;

import java.util.Set;

/**
 * Created by Rob on 5/12/2016.
 */
public class ContextNote implements IContextualComponent {
    @NonNull
    @Element(required = false)
    private FeatureContextArgument context;

    @NonNull
    @Element(required = false)
    private String text;

    ContextNote() {
        // for simple XML persistence
    }

    public ContextNote(FeatureContextArgument featureContextArgument, String text) {
        context = featureContextArgument;
        this.text = text;
    }

    @Override
    public boolean isInContext(@NonNull FeatureContextArgument otherContext) {
        if (context.getContext() != otherContext.getContext()) return false;
        if (context.getArgument() == null) return true;
        return context.getArgument().equals(otherContext.getArgument());
    }

    @Override
    public boolean isInContext(@NonNull Set<FeatureContextArgument> filter) {
        for (FeatureContextArgument eachFilter : filter) {
            if (isInContext(eachFilter)) return true;
        }
        return false;
    }

    public String getText() {
        return text;
    }


    @NonNull
    public FeatureContextArgument getContext() {
        return context;
    }
}

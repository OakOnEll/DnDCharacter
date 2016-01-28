package com.oakonell.dndcharacter.model.character;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.views.character.IContextualComponent;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import org.simpleframework.xml.ElementList;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rob on 1/4/2016.
 */
public abstract class AbstractContextualComponent extends BaseCharacterComponent implements IContextualComponent{
    @NonNull
    @ElementList(required = false)
    private Set<FeatureContext> contexts = new HashSet<>();

    public void addContext(FeatureContext context) {
        contexts.add(context);
    }

    @Override
    public boolean isInContext(FeatureContext context) {
        return contexts.contains(context);
    }

    @Override
    public boolean isInContext(@NonNull Set<FeatureContext> filter) {
        Set<FeatureContext> intersection = new HashSet<>(filter);
        intersection.retainAll(contexts);
        return !intersection.isEmpty();
    }
}

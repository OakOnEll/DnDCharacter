package com.oakonell.dndcharacter;

import com.oakonell.dndcharacter.views.FeatureContext;

import java.util.Set;

/**
 * Created by Rob on 1/4/2016.
 */
public interface IContextualComponent {
    boolean isInContext(FeatureContext context);

    boolean isInContext(Set<FeatureContext> filter);
}

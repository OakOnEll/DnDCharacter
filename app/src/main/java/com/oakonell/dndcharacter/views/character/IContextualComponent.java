package com.oakonell.dndcharacter.views.character;

import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import java.util.Set;

/**
 * Created by Rob on 1/4/2016.
 */
public interface IContextualComponent {
    boolean isInContext(FeatureContextArgument context);

    boolean isInContext(Set<FeatureContextArgument> filter);
}

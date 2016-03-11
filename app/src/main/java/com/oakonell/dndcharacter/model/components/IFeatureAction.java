package com.oakonell.dndcharacter.model.components;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.views.character.IContextualComponent;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Rob on 1/4/2016.
 */
public interface IFeatureAction {
    String getAction();

    int getCost();

    boolean hasActionContext();

    boolean isActionInContext(FeatureContextArgument context);

    boolean isActionInContext(Set<FeatureContextArgument> filter);

    boolean hasVariables();

    void applyToCharacter(Character character, Map<Feature.FeatureEffectVariable, String> promptValues);

    String getActionDescription();

    boolean replacesPrevious();

    String getName();

    @NonNull
    List<Feature.FeatureEffectVariable> getVariables();
}

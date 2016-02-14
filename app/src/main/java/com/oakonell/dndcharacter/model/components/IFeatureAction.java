package com.oakonell.dndcharacter.model.components;

import com.oakonell.dndcharacter.model.character.Character;

import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 1/4/2016.
 */
public interface IFeatureAction {
    String getAction();

    int getCost();

    boolean hasVariables();

    void applyToCharacter(Character character,  Map<Feature.FeatureEffectVariable, String> promptValues);

    String getActionDescription();

    boolean replacesPrevious();

    String getName();

    List<Feature.FeatureEffectVariable> getVariables();
}

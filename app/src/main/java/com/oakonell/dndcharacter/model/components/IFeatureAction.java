package com.oakonell.dndcharacter.model.components;

import com.oakonell.dndcharacter.model.character.Character;

/**
 * Created by Rob on 1/4/2016.
 */
public interface IFeatureAction {
    String getAction();

    int getCost();

    void applyToCharacter(Character character);
}

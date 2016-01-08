package com.oakonell.dndcharacter.model.components;

import com.oakonell.dndcharacter.model.*;
import com.oakonell.dndcharacter.model.Character;

/**
 * Created by Rob on 1/4/2016.
 */
public interface IFeatureAction {
    String getAction();

    int getCost();

    void applyToCharacter(Character character);
}

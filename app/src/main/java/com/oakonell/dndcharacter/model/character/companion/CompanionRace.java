package com.oakonell.dndcharacter.model.character.companion;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.character.ComponentType;

/**
 * Created by Rob on 12/4/2016.
 */

public class CompanionRace extends BaseCharacterComponent {
    @NonNull
    @Override
    public ComponentType getType() {
        return ComponentType.COMPANION_RACE;
    }
}

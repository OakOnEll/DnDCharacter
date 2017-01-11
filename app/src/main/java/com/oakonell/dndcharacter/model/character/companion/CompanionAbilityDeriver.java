package com.oakonell.dndcharacter.model.character.companion;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.AbstractCharacterAbilityDeriver;
import com.oakonell.dndcharacter.model.character.ComponentVisitor;

/**
 * Created by Rob on 1/3/2016.
 */
public class CompanionAbilityDeriver extends AbstractCharacterAbilityDeriver<CharacterCompanion> {
    boolean skipFeatures;

    public CompanionAbilityDeriver(ComponentVisitor visitor) {
        super(visitor);
    }

    public CompanionAbilityDeriver(ComponentVisitor visitor, @SuppressWarnings("SameParameterValue") boolean skipFeatures) {
        super(visitor, skipFeatures);
    }


    //@DebugLog
    @Override
    protected void derive(@NonNull CharacterCompanion character, String comment) {
        CompanionRace race = character.getRace();
        if (race != null) {
            getVisitor().visitComponent(race);
        }
        super.derive(character, comment);
    }

}

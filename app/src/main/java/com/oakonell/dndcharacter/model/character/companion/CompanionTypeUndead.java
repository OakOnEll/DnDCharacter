package com.oakonell.dndcharacter.model.character.companion;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;

/**
 * Created by Rob on 11/27/2016.
 */

public class CompanionTypeUndead extends AbstractCompanionType {
    @Override
    public String getType() {
        return "undead";
    }

    @Override
    public int getStringResId() {
        return R.string.companion_undead;
    }

    @Override
    public int getDescriptionResId() {
        return R.string.undead_description;
    }

    @Override
    public int getShortDescriptionResId() {
        return R.string.undead_short_description;
    }


    public boolean usesLimitedRaces() {
        return true;
    }

}

package com.oakonell.dndcharacter.model.character.companion;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 11/27/2016.
 */

public class CompanionTypePet extends AbstractHardCompanionType {
    @Override
    public String getType() {
        return "pet";
    }

    @Override
    public int getStringResId() {
        return R.string.companion_pet;
    }

    @Override
    public int getDescriptionResId() {
        return R.string.pet_description;
    }

    @Override
    public int getShortDescriptionResId() {
        return R.string.pet_short_description;
    }



}

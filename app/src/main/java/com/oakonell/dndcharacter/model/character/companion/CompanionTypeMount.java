package com.oakonell.dndcharacter.model.character.companion;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 11/27/2016.
 */

public class CompanionTypeMount extends AbstractHardCompanionType {
    @Override
    public String getType() {
        return "mount";
    }

    @Override
    public int getStringResId() {
        return R.string.companion_mount;
    }

    @Override
    public int getDescriptionResId() {
        return R.string.mount_description;
    }

    @Override
    public int getShortDescriptionResId() {
        return R.string.mount_short_description;
    }



}

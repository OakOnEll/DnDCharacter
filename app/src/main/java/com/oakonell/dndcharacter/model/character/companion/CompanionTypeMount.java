package com.oakonell.dndcharacter.model.character.companion;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;

/**
 * Created by Rob on 11/27/2016.
 */

public class CompanionTypeMount extends AbstractCompanionType {
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

    @Override
    public String getCrLimit(Character character) {
        return null;
    }
}

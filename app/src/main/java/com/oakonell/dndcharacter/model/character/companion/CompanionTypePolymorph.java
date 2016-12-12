package com.oakonell.dndcharacter.model.character.companion;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;

/**
 * Created by Rob on 11/27/2016.
 */

public class CompanionTypePolymorph extends AbstractHardCompanionType {
    @Override
    public String getType() {
        return "polymorph";
    }

    public boolean effectsSelf() {
        return false;
    }
    @Override
    public int getStringResId() {
        return R.string.companion_polymorph;
    }

    @Override
    public int getDescriptionResId() {
        return R.string.polymorph_description;
    }

    @Override
    public int getShortDescriptionResId() {
        return R.string.polymorph_short_description;
    }

    @Override
    public String getCrLimit(Character character) {
        return character.getCharacterLevel() + "";
    }
}

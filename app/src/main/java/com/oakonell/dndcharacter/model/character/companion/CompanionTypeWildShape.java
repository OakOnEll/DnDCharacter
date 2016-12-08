package com.oakonell.dndcharacter.model.character.companion;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;

/**
 * Created by Rob on 11/27/2016.
 */

/*
   Wild shape- p66  for cr. Lvl 2 cr 1/4, lvl 4 cr 1/2, lvl 8 cr 1.
          Hp, hit dice of creature
          Int, wis, charisma remain. retain save throw plus creature

 */
public class CompanionTypeWildShape extends AbstractCompanionType {
    @Override
    public String getType() {
        return "wildShape";
    }

    public boolean effectsSelf() {
        return false;
    }

    @Override
    public int getStringResId() {
        return R.string.companion_wildshape;
    }

    @Override
    public int getDescriptionResId() {
        return R.string.wildshape_description;
    }

    @Override
    public String getCrLimit(Character character) {
        return character.getCharacterLevel() + "";
    }
}

package com.oakonell.dndcharacter.model.character.companion;

import com.oakonell.dndcharacter.R;

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
    String getType() {
        return "wildShape";
    }

    public boolean effectsSelf() {
        return false;
    }

    @Override
    public int getStringResId() {
        return R.string.companion_wildshape;
    }


}

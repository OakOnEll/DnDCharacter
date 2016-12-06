package com.oakonell.dndcharacter.model.character.companion;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 11/27/2016.
 */

/*
   Ranger, cr < 1/4.  Ac, attack, damage rolls, Sav throw, skills add char prof
          Hp is higher of normal max or 4×char level

 */
public class CompanionTypeRangerCompanion extends AbstractCompanionType {
    @Override
    String getType() {
        return "rangerCompanion";
    }

    public boolean onlyOneActiveAllowed() {
        return true;
    }

    @Override
    public int getStringResId() {
        return R.string.companion_ranger_companion;
    }


}

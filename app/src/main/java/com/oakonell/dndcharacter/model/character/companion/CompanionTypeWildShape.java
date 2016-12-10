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
    public int getShortDescriptionResId() {
        return R.string.wildshape_short_description;
    }

    @Override
    public String getCrLimit(Character character) {
        // TODO don't like hard coding this druid class name...
        //  TODO nor the criteria for levels either... could make new companion types from class meta-data?
        Integer druidLevel = character.getClassLevels().get("Druid");
        if (druidLevel == null) return "0";
        // TODO also... what about flying, swimming.. restrictions
        if (druidLevel >= 2) return "1/4";
        if (druidLevel >= 4) return "1/2";
        if (druidLevel >= 8) return "1";
        return "0";
    }

    public boolean applies(Character character) {
        return true;
        //return character.getClassLevels().get("Druid") != null;
    }
}

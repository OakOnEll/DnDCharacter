package com.oakonell.dndcharacter.model.character.companion;


import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;

/**
 * Created by Rob on 11/27/2016.
 */

/*
   Polymorph, new form cr < target cr/level.  Stats unchanged.  Hp new form
 */
public class CompanionTypeFamiliar extends AbstractCompanionType {
    @Override
    public String getType() {
        return "familiar";
    }

    public boolean onlyOneActiveAllowed() {
        return true;
    }

    @Override
    public int getStringResId() {
        return R.string.companion_familiar;
    }

    @Override
    public int getDescriptionResId() {
        return R.string.familiar_description;
    }

    @Override
    public String getCrLimit(Character character) {
        return null;
    }

}

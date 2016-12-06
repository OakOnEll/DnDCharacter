package com.oakonell.dndcharacter.model.character.companion;


import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 11/27/2016.
 */

/*
   Polymorph, new form cr < target cr/level.  Stats unchanged.  Hp new form
 */
public class CompanionTypeFamiliar extends AbstractCompanionType {
    @Override
    String getType() {
        return "familiar";
    }

    public boolean onlyOneActiveAllowed() {
        return true;
    }

    @Override
    public int getStringResId() {
        return R.string.companion_familiar;
    }

}

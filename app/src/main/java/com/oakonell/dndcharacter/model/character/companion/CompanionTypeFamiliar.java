package com.oakonell.dndcharacter.model.character.companion;

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

}

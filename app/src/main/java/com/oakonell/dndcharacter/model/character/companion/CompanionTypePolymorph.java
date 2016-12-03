package com.oakonell.dndcharacter.model.character.companion;

/**
 * Created by Rob on 11/27/2016.
 */

public class CompanionTypePolymorph extends AbstractCompanionType {
    @Override
    String getType() {
        return "polymorph";
    }

    public boolean effectsSelf() {
        return false;
    }

}

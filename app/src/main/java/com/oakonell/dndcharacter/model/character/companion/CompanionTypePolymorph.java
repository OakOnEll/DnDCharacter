package com.oakonell.dndcharacter.model.character.companion;

import com.oakonell.dndcharacter.R;

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
    @Override
    public int getStringResId() {
        return R.string.companion_polymorph;
    }


}

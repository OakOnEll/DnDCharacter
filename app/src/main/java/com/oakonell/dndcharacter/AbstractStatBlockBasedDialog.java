package com.oakonell.dndcharacter;

import android.os.Bundle;

import com.oakonell.dndcharacter.model.StatBlock;
import com.oakonell.dndcharacter.model.StatType;

/**
 * Created by Rob on 12/24/2015.
 */
public abstract class AbstractStatBlockBasedDialog extends RollableDialogFragment {
    private StatBlock statBlock;

    protected void setStatTypeArg(StatBlock block) {
        int typeIndex = block.getType().ordinal();
        Bundle args = new Bundle();
        args.putInt("type", typeIndex);
        setArguments(args);
    }

    protected StatBlock setStatBlock(com.oakonell.dndcharacter.model.Character character) {
        int typeIndex = getArguments().getInt("type");
        StatType type = StatType.values()[typeIndex];
        statBlock = character.getStatBlock(type);
        return statBlock;
    }

    public StatBlock getStatBlock() {
        return statBlock;
    }
}

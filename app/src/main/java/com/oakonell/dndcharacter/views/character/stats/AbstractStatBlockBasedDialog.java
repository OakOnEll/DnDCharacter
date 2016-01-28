package com.oakonell.dndcharacter.views.character.stats;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.stats.StatBlock;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.views.character.RollableDialogFragment;

/**
 * Created by Rob on 12/24/2015.
 */
public abstract class AbstractStatBlockBasedDialog extends RollableDialogFragment {
    public static final String TYPE = "type";
    private StatBlock statBlock;

    protected void setStatTypeArg(@NonNull StatBlock block) {
        int typeIndex = block.getType().ordinal();
        Bundle args = new Bundle();
        args.putInt(TYPE, typeIndex);
        setArguments(args);
    }

    protected StatBlock setStatBlock(@NonNull com.oakonell.dndcharacter.model.character.Character character) {
        int typeIndex = getArguments().getInt(TYPE);
        StatType type = StatType.values()[typeIndex];
        statBlock = character.getStatBlock(type);
        return statBlock;
    }

    public StatBlock getStatBlock() {
        return statBlock;
    }
}

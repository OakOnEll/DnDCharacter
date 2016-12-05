package com.oakonell.dndcharacter.views.character.stats;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.stats.StatBlock;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.views.character.RollableDialogFragment;

/**
 * Created by Rob on 12/24/2015.
 */
public abstract class AbstractStatBlockBasedDialog extends RollableDialogFragment {
    public static final String TYPE = "type";
    private StatBlock statBlock;
    private StatType type;

    private boolean isForCompanion;

    protected void setStatTypeArg(@NonNull StatBlock block) {
        int typeIndex = block.getType().ordinal();
        Bundle args = new Bundle();
        args.putInt(TYPE, typeIndex);
        setArguments(args);
    }

    public void setIsForCompanion(boolean isForCompanion) {
        this.isForCompanion = isForCompanion;
    }

    public boolean isForCompanion() {
        return isForCompanion;
    }

    public void setForCompanion(boolean isForCompanion) {
        this.isForCompanion = isForCompanion;
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int typeIndex = getArguments().getInt(TYPE);
        type = StatType.values()[typeIndex];
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected StatBlock setStatBlock(@NonNull com.oakonell.dndcharacter.model.character.Character character) {
        AbstractCharacter source = character;
        if (isForCompanion) {
            source = character.getDisplayedCompanion();
        }
        statBlock = source.getStatBlock(type);
        return statBlock;
    }

    public StatType getType() {
        return type;
    }

    public StatBlock getStatBlock() {
        return statBlock;
    }
}

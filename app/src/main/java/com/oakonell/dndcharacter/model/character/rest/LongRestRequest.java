package com.oakonell.dndcharacter.model.character.rest;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.companion.CharacterCompanion;
import com.oakonell.dndcharacter.model.components.RefreshType;
import com.oakonell.dndcharacter.views.character.rest.longRest.HitDieRestoreRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/8/2015.
 */
public class LongRestRequest extends AbstractRestRequest<CompanionLongRest> {
    @NonNull
    public static Creator<LongRestRequest> CREATOR = new Creator<LongRestRequest>() {

        @NonNull
        @Override
        public LongRestRequest createFromParcel(@NonNull Parcel source) {
            return new LongRestRequest(source);
        }

        @NonNull
        @Override
        public LongRestRequest[] newArray(int size) {
            return new LongRestRequest[size];
        }

    };

    public LongRestRequest(Parcel source) {
        super(source);
        fullHealing = source.readByte() == 1;
        hitDieRestores = source.readArrayList(ClassLoader.getSystemClassLoader());
    }

    public LongRestRequest() {
        hitDieRestores = new ArrayList<>();
    }

    private boolean fullHealing;
    private final List<HitDieRestoreRow> hitDieRestores;


    public boolean isFullHealing() {
        return fullHealing;
    }

    public void setFullHealing(boolean isFullHealing) {
        fullHealing = isFullHealing;
    }

    @NonNull
    public List<HitDieRestoreRow> getHitDiceToRestore() {
        return hitDieRestores;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (fullHealing ? 1 : 0));
        dest.writeList(hitDieRestores);
    }

    @Override
    protected boolean shouldReset(RefreshType refreshesOn) {
        return true;
    }

    @Override
    protected int getSlotsToRestore(Character character, @NonNull Character.SpellLevelInfo each) {
        return each.getMaxSlots() - each.getSlotsAvailable();
    }

    @Override
    protected CompanionLongRest createCompanionRequest(CharacterCompanion each, Context context) {
        return (CompanionLongRest) each.createLongRestRequest(context);
    }

    @Override
    public void apply(AbstractCharacter character) {
        character.longRest(this);
    }

    @Override
    public int getTotalHealing() {
        if (fullHealing) {
            return getMaxHP() - getStartHP();
        }
        return getExtraHealing();
    }
}

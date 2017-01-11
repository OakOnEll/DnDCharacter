package com.oakonell.dndcharacter.model.character.rest;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.companion.CharacterCompanion;
import com.oakonell.dndcharacter.model.components.RefreshType;
import com.oakonell.dndcharacter.views.character.rest.shortRest.HitDieUseRow;
import com.oakonell.expression.context.SimpleVariableContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 11/8/2015.
 */
public class ShortRestRequest extends AbstractRestRequest<CompanionShortRest> {
    @NonNull
    public static Creator<ShortRestRequest> CREATOR = new Creator<ShortRestRequest>() {

        @NonNull
        @Override
        public ShortRestRequest createFromParcel(@NonNull Parcel source) {
            return new ShortRestRequest(source);
        }

        @NonNull
        @Override
        public ShortRestRequest[] newArray(int size) {
            return new ShortRestRequest[size];
        }

    };
    private int healing;
    private final List<HitDieUseRow> hitDieUses;

    public ShortRestRequest(Parcel source) {
        super(source);
        healing = source.readInt();
        hitDieUses = source.readArrayList(ClassLoader.getSystemClassLoader());
    }

    public ShortRestRequest() {
        hitDieUses = new ArrayList<>();
    }


    @Override
    public int getExtraHealing() {
        return healing;
    }

    @Override
    public void setExtraHealing(int hp) {
        healing = hp;
    }

    @NonNull
    public List<HitDieUseRow> getHitDieUses() {
        return hitDieUses;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(healing);
        dest.writeList(hitDieUses);
    }


    @Override
    protected int getSlotsToRestore(Character character, @NonNull Character.SpellLevelInfo info) {
        int value = 0;
        for (Character.CastingClassInfo each : character.getCasterClassInfo()) {
            final RefreshType refreshType = each.getSpellSlotRefresh();
            if (refreshType != RefreshType.SHORT_REST) continue;
            final String slotFormula = each.getSlotMap().get(info.getLevel());
            if (slotFormula == null || slotFormula.length() == 0) continue;

            SimpleVariableContext variableContext = new SimpleVariableContext();
            variableContext.setNumber("classLevel", each.getClassLevel());
            value += character.evaluateFormula(slotFormula, variableContext);
        }
        return Math.min(info.getMaxSlots() - info.getSlotsAvailable(), value);
    }

    @Override
    protected CompanionShortRest createCompanionRequest(CharacterCompanion each, Context context) {
        return (CompanionShortRest) each.createShortRestRequest(context);
    }

    @Override
    protected boolean shouldReset(RefreshType refreshesOn) {
        return refreshesOn == RefreshType.SHORT_REST;
    }


    public int getTotalHealing() {
        int healing = 0;
        for (HitDieUseRow each : getHitDieUses()) {
            for (Integer eachRoll : each.rolls) {
                healing += eachRoll;
            }
        }

        healing += getExtraHealing();
        return healing;
    }

    @Override
    public void apply(AbstractCharacter character) {
        character.shortRest(this);
    }

}

package com.oakonell.dndcharacter.views.character.rest.shortRest;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 11/8/2015.
 */
public class HitDieUseRow implements Parcelable {
    @NonNull
    public static Creator<HitDieUseRow> CREATOR = new Creator<HitDieUseRow>() {

        @NonNull
        @Override
        public HitDieUseRow createFromParcel(@NonNull Parcel source) {
            return new HitDieUseRow(source);
        }

        @NonNull
        @Override
        public HitDieUseRow[] newArray(int size) {
            return new HitDieUseRow[size];
        }

    };
    public int dieSides;
    public int numDiceRemaining;
    public int totalDice;
    public final List<Integer> rolls = new ArrayList<>();

    public HitDieUseRow() {
    }

    public HitDieUseRow(@NonNull Parcel source) {
        dieSides = source.readInt();
        numDiceRemaining = source.readInt();
        totalDice = source.readInt();
        int rollsSize = source.readInt();
        int[] rollsArray = new int[rollsSize];
        source.readIntArray(rollsArray);
        for (int each : rollsArray) {
            rolls.add(each);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(dieSides);
        dest.writeInt(numDiceRemaining);
        dest.writeInt(totalDice);
        final int size = rolls.size();
        dest.writeInt(size);
        int[] rollsArray = new int[size];
        for (int i = 0; i < size; i++) {
            rollsArray[i] = rolls.get(i);
        }
        dest.writeIntArray(rollsArray);
    }

}

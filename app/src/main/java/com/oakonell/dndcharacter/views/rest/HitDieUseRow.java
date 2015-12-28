package com.oakonell.dndcharacter.views.rest;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Rob on 11/8/2015.
 */
public class HitDieUseRow implements Parcelable {
    public int dieSides;

    public int numDiceRemaining;
    public int totalDice;

    public List<Integer> rolls = new ArrayList<>();

    public HitDieUseRow() {
    }
    public HitDieUseRow(Parcel source) {
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dieSides);
        dest.writeInt(numDiceRemaining);
        dest.writeInt(totalDice);
        final int size = rolls.size();
        dest.writeInt(size);
        int[] rollsArray = new int[size];
        for (int i=0; i< size;i++) {
            rollsArray[i] = rolls.get(i);
        }
        dest.writeIntArray(rollsArray);
    }

    public static Creator<HitDieUseRow> CREATOR = new Creator<HitDieUseRow>() {

        @Override
        public HitDieUseRow createFromParcel(Parcel source) {
            return new HitDieUseRow(source);
        }

        @Override
        public HitDieUseRow[] newArray(int size) {
            return new HitDieUseRow[size];
        }

    };

}

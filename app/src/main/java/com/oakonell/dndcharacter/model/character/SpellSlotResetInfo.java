package com.oakonell.dndcharacter.model.character;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Rob on 1/27/2016.
 */
public class SpellSlotResetInfo implements Parcelable {

    @NonNull
    public static Creator<SpellSlotResetInfo> CREATOR = new Creator<SpellSlotResetInfo>() {

        @NonNull
        @Override
        public SpellSlotResetInfo createFromParcel(@NonNull Parcel source) {
            return new SpellSlotResetInfo(source);
        }

        @NonNull
        @Override
        public SpellSlotResetInfo[] newArray(int size) {
            return new SpellSlotResetInfo[size];
        }

    };

    public SpellSlotResetInfo(Parcel source) {
        reset = source.readByte() == 1;
        level = source.readInt();
        availableSlots = source.readInt();
        maxSlots = source.readInt();
        restoreSlots = source.readInt();
    }

    public SpellSlotResetInfo() {
    }

    public boolean reset;
    public int level;
    public int availableSlots;
    public int maxSlots;
    public int restoreSlots;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeByte((byte) (reset ? 1 : 0));
        dest.writeInt(level);
        dest.writeInt(availableSlots);
        dest.writeInt(maxSlots);
        dest.writeInt(restoreSlots);
    }
}

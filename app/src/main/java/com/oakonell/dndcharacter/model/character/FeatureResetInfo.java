package com.oakonell.dndcharacter.model.character;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.components.RefreshType;

/**
 * Created by Rob on 11/9/2015.
 */
public class FeatureResetInfo implements Parcelable {
    @NonNull
    public static Parcelable.Creator<FeatureResetInfo> CREATOR = new Parcelable.Creator<FeatureResetInfo>() {

        @NonNull
        @Override
        public FeatureResetInfo createFromParcel(@NonNull Parcel source) {
            return new FeatureResetInfo(source);
        }

        @NonNull
        @Override
        public FeatureResetInfo[] newArray(int size) {
            return new FeatureResetInfo[size];
        }

    };

    public FeatureResetInfo(Parcel source) {
        reset = source.readByte() == 1;
        name = source.readString();
        description = source.readString();
        refreshOn = EnumHelper.stringToEnum(source.readString(), RefreshType.class);
        uses = source.readString();
        numToRestore = source.readInt();
        needsResfesh = source.readByte() == 1;
        maxToRestore = source.readInt();
    }

    public FeatureResetInfo() {
    }

    public boolean reset;
    public String name;
    @Nullable
    public String description;
    public RefreshType refreshOn;
    public String uses;
    public int numToRestore;
    public boolean needsResfesh;
    public int maxToRestore;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeByte((byte) (reset ? 1 : 0));
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(refreshOn.name());
        dest.writeString(uses);
        dest.writeInt(numToRestore);
        dest.writeByte((byte) (needsResfesh ? 1 : 0));
        dest.writeInt(maxToRestore);
    }

}

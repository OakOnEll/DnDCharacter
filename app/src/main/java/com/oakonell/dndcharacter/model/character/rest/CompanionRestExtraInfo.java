package com.oakonell.dndcharacter.model.character.rest;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Rob on 1/11/2017.
 */

public class CompanionRestExtraInfo implements Parcelable {
    @NonNull
    public static Parcelable.Creator<CompanionRestExtraInfo> CREATOR = new Parcelable.Creator<CompanionRestExtraInfo>() {

        @NonNull
        @Override
        public CompanionRestExtraInfo createFromParcel(@NonNull Parcel source) {
            return new CompanionRestExtraInfo(source);
        }

        @NonNull
        @Override
        public CompanionRestExtraInfo[] newArray(int size) {
            return new CompanionRestExtraInfo[size];
        }

    };

    private int index;
    private String name;
    private String description;
    private boolean reset;

    public CompanionRestExtraInfo(Parcel source) {
        index = source.readInt();
        name = source.readString();
        description = source.readString();
        reset = source.readByte() == 1;
    }

    public CompanionRestExtraInfo() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeByte((byte) (reset ? 1 : 0));
    }

    public String getName() {
        return name;
    }

    public boolean shouldReset() {
        return reset;
    }

    public String getDescription() {
        return description;
    }

    public void shouldReset(boolean shouldReset) {
        reset = shouldReset;
    }

    public void setCompanionIndex(int index) {
        this.index = index;
    }

    public int getCompanionIndex() {
        return index;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

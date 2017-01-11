package com.oakonell.dndcharacter.model.character.rest;

import android.os.Parcel;
import android.support.annotation.NonNull;

/**
 * Created by Rob on 12/26/2016.
 */

public class CompanionLongRest extends LongRestRequest implements CompanionRest {
    @NonNull
    public static Creator<CompanionLongRest> CREATOR = new Creator<CompanionLongRest>() {

        @NonNull
        @Override
        public CompanionLongRest createFromParcel(@NonNull Parcel source) {
            return new CompanionLongRest(source);
        }

        @NonNull
        @Override
        public CompanionLongRest[] newArray(int size) {
            return new CompanionLongRest[size];
        }

    };

    public CompanionLongRest(Parcel source) {
        super(source);
        extraInfo = source.readParcelable(ClassLoader.getSystemClassLoader());
    }

    public CompanionLongRest() {
        super();
        extraInfo = new CompanionRestExtraInfo();
    }

    private CompanionRestExtraInfo extraInfo;


    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(extraInfo, 0);
    }

    @Override
    public String getName() {
        return extraInfo.getName();
    }

    @Override
    public boolean shouldReset() {
        return extraInfo.shouldReset();
    }

    @Override
    public String getDescription() {
        return extraInfo.getDescription();
    }

    @Override
    public void shouldReset(boolean shouldReset) {
        extraInfo.shouldReset(shouldReset);
    }

    @Override
    public void setCompanionIndex(int index) {
        extraInfo.setCompanionIndex(index);
    }

    @Override
    public int getCompanionIndex() {
        return extraInfo.getCompanionIndex();
    }


    @Override
    public void setName(String name) {
        extraInfo.setName(name);
    }

    @Override
    public void setDescription(String description) {
        extraInfo.setDescription(description);
    }
}

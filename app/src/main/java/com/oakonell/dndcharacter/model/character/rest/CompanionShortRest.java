package com.oakonell.dndcharacter.model.character.rest;

import android.os.Parcel;
import android.support.annotation.NonNull;

/**
 * Created by Rob on 12/26/2016.
 */

public class CompanionShortRest extends ShortRestRequest implements CompanionRest {
    @NonNull
    public static Creator<CompanionShortRest> CREATOR = new Creator<CompanionShortRest>() {

        @NonNull
        @Override
        public CompanionShortRest createFromParcel(@NonNull Parcel source) {
            return new CompanionShortRest(source);
        }

        @NonNull
        @Override
        public CompanionShortRest[] newArray(int size) {
            return new CompanionShortRest[size];
        }

    };

    public CompanionShortRest(Parcel source) {
        super(source);
        extraInfo = source.readParcelable(ClassLoader.getSystemClassLoader());
    }

    private CompanionRestExtraInfo extraInfo;


    public CompanionShortRest() {
        super();
        extraInfo = new CompanionRestExtraInfo();

    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(extraInfo,0);
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

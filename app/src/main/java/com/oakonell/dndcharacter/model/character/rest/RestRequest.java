package com.oakonell.dndcharacter.model.character.rest;

import android.os.Parcelable;

import com.oakonell.dndcharacter.model.character.FeatureResetInfo;

import java.util.List;

/**
 * Created by Rob on 1/10/2017.
 */

public interface RestRequest extends Parcelable {
    int getStartHP();

    int getMaxHP();

    int getExtraHealing();

    void setExtraHealing(int hp);

    int getTotalHealing();

    List<FeatureResetInfo> getFeatureResets();
}












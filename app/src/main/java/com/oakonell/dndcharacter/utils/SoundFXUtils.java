package com.oakonell.dndcharacter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.views.settings.SettingsActivity;

/**
 * Created by Rob on 4/19/2016.
 */
public class SoundFXUtils {

    public static void playDiceRoll(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enableSoundFX = sharedPref.getBoolean(SettingsActivity.KEY_PREF_SOUND_FX, true);
        if (!enableSoundFX) return;

        int randRollSound = RandomUtils.random(1, 3);
        MediaPlayer mp;
        switch (randRollSound) {
            case 1:
                mp = MediaPlayer.create(context, R.raw.dice_roll);
                break;
            case 2:
                mp = MediaPlayer.create(context, R.raw.dice_roll2);
                break;
            case 3:
                mp = MediaPlayer.create(context, R.raw.dice_roll3);
                break;
            default:
                mp = MediaPlayer.create(context, R.raw.dice_roll);
                break;
        }
        mp.start();
    }

    public static void playCriticalFail(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enableSoundFX = sharedPref.getBoolean(SettingsActivity.KEY_PREF_SOUND_FX, true);
        if (!enableSoundFX) return;

        MediaPlayer mp = MediaPlayer.create(context, R.raw.critical_failure);
        mp.start();
    }

    public static void playCriticalSuccess(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enableSoundFX = sharedPref.getBoolean(SettingsActivity.KEY_PREF_SOUND_FX, true);
        if (!enableSoundFX) return;

        MediaPlayer mp = MediaPlayer.create(context, R.raw.critical_success);
        mp.start();
    }
}

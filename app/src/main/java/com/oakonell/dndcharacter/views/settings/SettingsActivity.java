package com.oakonell.dndcharacter.views.settings;

import android.preference.PreferenceActivity;

import com.oakonell.dndcharacter.R;

import java.util.List;

/**
 * Created by Rob on 4/19/2016.
 */
public class SettingsActivity extends PreferenceActivity {
    public static final String KEY_PREF_SOUND_FX = "sound_fx";

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.headers_preference, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return SettingsFragment.class.getName().equals(fragmentName);
    }
}
package com.oakonell.dndcharacter.views.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 4/19/2016.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);
    }
}

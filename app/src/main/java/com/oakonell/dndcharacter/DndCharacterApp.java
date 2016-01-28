package com.oakonell.dndcharacter;


import android.content.Context;
import android.support.annotation.NonNull;

import com.activeandroid.app.Application;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by Rob on 11/2/2015.
 */
public class DndCharacterApp extends Application {
    public static RefWatcher getRefWatcher(@NonNull Context context) {
        DndCharacterApp application = (DndCharacterApp) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;

    @Override public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
    }
}

package com.oakonell.dndcharacter.utils;

import android.content.Context;

import java.io.File;

/**
 * Created by Rob on 4/1/2016.
 */
public class FileUtils {
    public static File getCharactersDirectory(Context context) {
        // this should match up with the file_paths.xml configuration
        return new File(context.getCacheDir(), "characters");
    }

    public static String getFileProviderName() {
        return "com.oakonell.dndcharacter.fileprovider";
    }
}

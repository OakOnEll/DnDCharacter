package com.oakonell.dndcharacter.utils;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

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

    public static Uri uriFor(Context context, File newFile) {
        return FileProvider.getUriForFile(context, FileUtils.getFileProviderName(), newFile);
    }
}

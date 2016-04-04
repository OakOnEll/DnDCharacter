package com.oakonell.dndcharacter.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.oakonell.dndcharacter.views.exports.ExportActivity;

import java.io.File;

/**
 * Created by Rob on 4/1/2016.
 */
public class FileUtils {
    public static File getCharactersDirectory(Context context) {
        final File dnd_characters = new File(Environment.getExternalStorageDirectory(), "dnd_characters");
        Log.i("FileUtils", "Referencing file '" + dnd_characters.getAbsolutePath() + "'");
        return dnd_characters;
    }


    public static Uri uriFor(Context context, File newFile) {
        return Uri.fromFile(newFile);
    }
}

package com.oakonell.dndcharacter.model;


import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.activeandroid.Cache;
import com.activeandroid.content.ContentProvider;

/**
 * Created by Rob on 2/29/2016.
 */
public class DnDContentProvider extends ContentProvider {
    public static final String SPELL_AND_CLASSES = "spellAndClasses";
    public static final String COMPANION_AND_TYPES = "companionAndTypes";
    public static String sAuthority;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int JOINED_SPELL_CLASSES = 10001;
    private static final int JOINED_COMPANION_TYPES = 10002;

    @Override
    public boolean onCreate() {
        super.onCreate();

        sAuthority = getAuthority();
        URI_MATCHER.addURI(sAuthority, SPELL_AND_CLASSES, JOINED_SPELL_CLASSES);
        URI_MATCHER.addURI(sAuthority, COMPANION_AND_TYPES, JOINED_COMPANION_TYPES);

        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = URI_MATCHER.match(uri);
        if (match < 0) return super.getType(uri);


        StringBuilder mimeType = new StringBuilder();
        if (match == JOINED_SPELL_CLASSES) {
            mimeType.append("vnd");
            mimeType.append(".");
            mimeType.append(sAuthority);
            mimeType.append(".");
            mimeType.append("dir");
            mimeType.append("/");
            mimeType.append("vnd");
            mimeType.append(".");
            mimeType.append(sAuthority);
            mimeType.append(".");
            mimeType.append(SPELL_AND_CLASSES);
        } else if (match == JOINED_COMPANION_TYPES) {
            mimeType.append("vnd");
            mimeType.append(".");
            mimeType.append(sAuthority);
            mimeType.append(".");
            mimeType.append("dir");
            mimeType.append("/");
            mimeType.append("vnd");
            mimeType.append(".");
            mimeType.append(sAuthority);
            mimeType.append(".");
            mimeType.append(COMPANION_AND_TYPES);
        }

        return mimeType.toString();
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int match = URI_MATCHER.match(uri);
        if (match == JOINED_SPELL_CLASSES) {
            final Cursor cursor = Cache.openDatabase().query(
                    "spell JOIN spell_class ON spell._id = spell_class.spell",
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);

            return cursor;
        } else if (match == JOINED_COMPANION_TYPES) {
            final Cursor cursor = Cache.openDatabase().query(
                    "companion JOIN companion_type ON companion._id = companion_type.companion",
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);

            return cursor;
        }
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }
}

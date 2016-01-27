package com.oakonell.dndcharacter.views;

import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 12/7/2015.
 */
public class CursorIndexesByName {
    private final Map<String, Integer> cursorIndexesByName = new HashMap<>();

    public int getIndex(Cursor cursor, String name) {
        Integer result = cursorIndexesByName.get(name);
        if (result != null) return result;
        result = cursor.getColumnIndex(name);
        cursorIndexesByName.put(name, result);
        return result;
    }
}

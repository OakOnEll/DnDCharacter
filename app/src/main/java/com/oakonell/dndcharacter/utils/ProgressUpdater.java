package com.oakonell.dndcharacter.utils;

/**
 * Created by Rob on 4/1/2016.
 */
public interface ProgressUpdater {
    void progress(ProgressData data);

    boolean isCancelled();
}

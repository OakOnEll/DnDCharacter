package com.oakonell.dndcharacter.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Rob on 4/1/2016.
 */
public class ProgressReportingInputStream extends InputStream {
    private static final long UPDATE_INTERVAL = 1024;
    private final InputStream source;
    private final ProgressUpdater progress;
    private final ProgressData result;
    private long lastUpdated = 0;

    public ProgressReportingInputStream(InputStream source, ProgressUpdater progress) throws IOException {
        this.source = source;
        this.progress = progress;
        result = new ProgressData();
        result.message = "Reading xml";
        result.total = source.available();
        result.progress = 0;
        if (progress != null) {
            progress.progress(result);
        }
    }

    @Override
    public int available() throws IOException {
        return source.available();
    }

    @Override
    public void close() throws IOException {
        source.close();
    }

    private void updateProcessed(long amount) {
        result.progress += amount;
        if (progress != null) {
            if (progress.isCancelled()) {
                throw new InputStreamCanceled();
            }
            if (result.progress > lastUpdated + UPDATE_INTERVAL) {
                progress.progress(result);
                lastUpdated = result.progress;
            }
        }
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        long startRead = result.progress;
        int numRead = source.read(buffer, byteOffset, byteCount);
        if (startRead == result.progress) {
            updateProcessed(numRead);
        }
        return numRead;
    }

    @Override
    public int read() throws IOException {
        int aByte = source.read();
        if (aByte != -1) {
            updateProcessed(1);
        }
        return aByte;
    }

    @Override
    public long skip(long byteCount) throws IOException {
        long startRead = result.progress;
        long skipped = source.skip(byteCount);
        if (startRead == result.progress) {
            updateProcessed(skipped);
        }
        return skipped;
    }

    public static class InputStreamCanceled extends RuntimeException {
    }
}

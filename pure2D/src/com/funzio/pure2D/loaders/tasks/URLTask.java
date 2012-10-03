/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Intent;
import android.util.Log;

/**
 * @author long
 */
public abstract class URLTask implements IntentTask {
    public static boolean LOG_ENABLED = true;

    private static final String TAG = URLTask.class.getSimpleName();
    private static final String CLASS_NAME = URLTask.class.getName();

    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";
    public static String EXTRA_URL = "url";

    protected static final int BUFFER = 1024;

    protected final String mURL;
    protected int mTotalBytesLoaded;

    public URLTask(final String url) {
        mURL = url;
    }

    public String getURL() {
        return mURL;
    }

    @Override
    public boolean run() {
        // Log.v(TAG, "run(), " + mURL);

        final URLConnection conn;
        try {
            final URL address = new URL(mURL);
            conn = address.openConnection();
        } catch (Exception e) {
            if (LOG_ENABLED) {
                Log.e(TAG, "CONNECTION ERROR!", e);
            }
            return false;
        }

        int count = 0;
        mTotalBytesLoaded = 0;
        try {
            final BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream());
            final byte[] data = new byte[BUFFER];
            while ((count = inputStream.read(data)) != -1) {
                mTotalBytesLoaded += count;
                onProgress(data, count);
            }
            inputStream.close();
        } catch (Exception e) {
            if (LOG_ENABLED) {
                Log.e(TAG, "READ ERROR!", e);
            }
            return false;
        }

        return true;
    }

    abstract protected void onProgress(final byte[] data, final int count) throws Exception;

    @Override
    public Intent getCompleteIntent() {
        final Intent intent = new Intent(INTENT_COMPLETE);
        intent.putExtra(EXTRA_URL, mURL);
        return intent;
    }
}

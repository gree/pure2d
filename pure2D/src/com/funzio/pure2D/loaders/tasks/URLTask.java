/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.util.Log;

/**
 * @author long
 */
public abstract class URLTask implements IntentTask {
    public static boolean LOG_ENABLED = true;
    protected static final int DEFAULT_BUFFER = 1024;
    protected static final int DEFAULT_TIMEOUT = 20 * 1000;

    private static final String TAG = URLTask.class.getSimpleName();
    private static final String CLASS_NAME = URLTask.class.getName();

    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";
    public static String EXTRA_URL = "url";

    protected int mBufferSize = DEFAULT_BUFFER;
    protected final String mURL;
    protected int mContentLength = -1;
    protected int mTotalBytesLoaded = 0;

    public URLTask(final String url) {
        mURL = url;
    }

    public URLTask(final String url, final int bufferSize) {
        mURL = url;
        mBufferSize = bufferSize;
    }

    public String getURL() {
        return mURL;
    }

    protected boolean openURL() {
        return openURL(null);
    }

    protected boolean openURL(final Map<String, String> properties) {
        // Log.v(TAG, "run(), " + mURL);

        final URLConnection conn;
        try {
            final URL address = new URL(mURL);

            conn = address.openConnection();
            conn.setConnectTimeout(DEFAULT_TIMEOUT);

            // add properties to the post http request
            if (properties != null) {
                final Set<String> keys = properties.keySet();
                for (String key : keys) {
                    String value = properties.get(key);
                    if (value != null) {
                        conn.setRequestProperty(key, value);
                    }
                }
            }

            mContentLength = conn.getContentLength();

        } catch (Exception e) {
            if (LOG_ENABLED) {
                Log.v(TAG, "CONNECTION ERROR!", e);
            }
            return false;
        }

        int count = 0;
        mTotalBytesLoaded = 0;
        try {
            final BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream());
            final byte[] data = new byte[mBufferSize];
            while ((count = inputStream.read(data)) != -1) {
                mTotalBytesLoaded += count;
                onProgress(data, count);
            }
            inputStream.close();
        } catch (Exception e) {
            if (LOG_ENABLED) {
                Log.v(TAG, "READ ERROR!", e);
            }
            return false;
        }

        // verify the size if it's specified. NOTE: this only works with gzip disabled!
        return mContentLength < 0 || (mContentLength == mTotalBytesLoaded);
        // NOTE: content length can be smaller if it's gzip compressed!
        // return mContentLength <= mTotalBytesLoaded;
    }

    protected boolean postURL(final String data, final Map<String, String> properties) {
        final HttpURLConnection conn;

        try {
            URL address = new URL(mURL);
            conn = (HttpURLConnection) address.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            // add properties to the post http request
            if (properties != null) {
                final Set<String> keys = properties.keySet();
                for (String key : keys) {
                    String value = properties.get(key);
                    if (value != null) {
                        conn.setRequestProperty(key, value);
                    }
                }
            }

            conn.setFixedLengthStreamingMode(data.getBytes().length);

        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }

        // now that connection is open send data
        try {
            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
            os.write(data);
            os.close();
            conn.getResponseCode();

            // TODO: Add support to save or return http response

        } catch (IOException e) {

            if (LOG_ENABLED) {
                Log.v(TAG, "WRITE ERROR!", e);
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

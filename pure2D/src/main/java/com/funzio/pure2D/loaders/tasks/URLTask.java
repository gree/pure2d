/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    protected byte[] mBuffer;

    protected final String mURL;
    protected int mContentLength = -1;
    protected int mTotalBytesLoaded = 0;
    protected boolean mGzipEnabled = true; // enabled by default

    public URLTask(final String url) {
        mURL = url;
    }

    public URLTask(final String url, final int bufferSize) {
        mURL = url;
        mBufferSize = bufferSize;
    }

    public boolean isGzipEnabled() {
        return mGzipEnabled;
    }

    public void setGzipEnabled(final boolean gzipEnabled) {
        mGzipEnabled = gzipEnabled;
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
            conn.setReadTimeout(DEFAULT_TIMEOUT);

            if (!mGzipEnabled) {
                // disable gzip
                conn.setRequestProperty("Accept-Encoding", "identity");
            }

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

        try {
            mTotalBytesLoaded = readStream(conn.getInputStream());
        } catch (Exception e) {
            if (LOG_ENABLED) {
                Log.v(TAG, "READ ERROR!", e);
            }
            return false;
        }

        // verify the size if it's specified. NOTE: this only works with gzip disabled!
        return mContentLength < 0 || (mContentLength == mTotalBytesLoaded) || (mGzipEnabled && mTotalBytesLoaded > 0);
    }

    protected int readStream(final InputStream stream) throws Exception {
        int count = 0;
        int totalBytesLoaded = 0;
        final BufferedInputStream inputStream = new BufferedInputStream(stream);
        // only create buffer once
        if (mBuffer == null) {
            mBuffer = new byte[mBufferSize];
        }
        while ((count = inputStream.read(mBuffer)) != -1) {
            totalBytesLoaded += count;
            onProgress(mBuffer, count);
        }
        inputStream.close();

        return totalBytesLoaded;
    }

    @Deprecated
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
            if (LOG_ENABLED) {
                Log.v(TAG, "READ ERROR!", e);
            }
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

    /**
     * Internal callback when progressing
     * 
     * @param data
     * @param count
     * @throws Exception
     */
    abstract protected void onProgress(final byte[] data, final int count) throws Exception;

    @Override
    public Intent getCompleteIntent() {
        final Intent intent = new Intent(INTENT_COMPLETE);
        intent.putExtra(EXTRA_URL, mURL);
        return intent;
    }
}

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

import android.util.Log;

/**
 * @author sajjadtabib
 *
 */
public abstract class NetworkTask implements Task {

    public static boolean LOG_ENABLED = true;

    protected static final int DEFAULT_CONNECT_TIMEOUT = 20000;
    protected static final int DEFAULT_READ_TIMEOUT = 20000;
    protected static final int DEFAULT_RETRY_COUNT = 0;

    protected String mUrl;
    protected boolean mStatus;
    protected int mMaxRetries;
    protected int mRetries;
    protected int mConnectTimeout;
    protected int mReadTimeout;
    protected long mBackoffMillis;

    /**
     * 
     */
    public NetworkTask() {
        mRetries = 0; //the number of attempted retries
        mMaxRetries = DEFAULT_RETRY_COUNT;
        mConnectTimeout = DEFAULT_CONNECT_TIMEOUT;
        mReadTimeout = DEFAULT_READ_TIMEOUT;
    }

    public NetworkTask setUrl(final String url) {
        mUrl = url;
        return this;
    }

    /**
     * Set the socket connection timeout
     * @param timeout connection timeout in milliseconds. 0 means no timeout
     */
    public NetworkTask setConnectTimeout(final int timeout) {
        if (timeout >= 0) {
            mConnectTimeout = timeout;
        }
        return this;
    }

    /**
     * Set's the tcp socket read timeout. A value of 0 indicates no timeout
     * @param timeout
     */
    public NetworkTask setReadTimeout(final int timeout) {
        if (timeout >= 0) {
            mReadTimeout = timeout;
        }
        return this;
    }

    public NetworkTask setRetryCount(final int retry) {
        if (retry >= 0) {
            mMaxRetries = retry;
        }
        return this;
    }

    public NetworkTask setBackoffMillis(final long backoff) {
        mBackoffMillis = backoff;
        return this;
    }

    /* (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.Task#reset()
     */
    @Override
    public void reset() {
        mStatus = false;
        mRetries = 0;

    }

    /* (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.Task#run()
     */
    @Override
    public final boolean run() {
        if (LOG_ENABLED) {
            Log.d(getLogTag(), "connecting to: " + mUrl);
        }
        mStatus = doNetworkTask();

        while (!mStatus && mRetries < mMaxRetries) {

            if (mBackoffMillis > 0) {

                if (LOG_ENABLED) {
                    Log.d(getLogTag(), "backing off for " + mBackoffMillis + " millis");
                }

                try {
                    Thread.sleep(mBackoffMillis);
                } catch (InterruptedException e) {

                    if (LOG_ENABLED) {
                        Log.v(getLogTag(), "Interrupted: ", e);
                    }
                }
            }

            if (LOG_ENABLED) {
                Log.d(getLogTag(), "Retry #: " + (mRetries + 1) + " Max Retries: " + mMaxRetries);
            }

            //ping the url and get the status
            mStatus = doNetworkTask();
            ++mRetries;

        }

        return mStatus;
    }

    /* (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.Task#isSucceeded()
     */
    @Override
    public boolean isSucceeded() {
        return mStatus;
    }

    protected abstract boolean doNetworkTask();

    protected abstract String getLogTag();

}

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

    protected boolean LOG_ENABLED = true;
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
        Log.d(getLogTag(), "connecting to: " + mUrl);
        mStatus = doNetworkTask();

        while (!mStatus && mRetries < mMaxRetries) {

            if (mBackoffMillis > 0) {
                Log.d(getLogTag(), "backing off for " + mBackoffMillis + " millis");
                try {
                    Thread.sleep(mBackoffMillis);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }

            //ping the url and get the status
            Log.d(getLogTag(), "Retry #: " + (mRetries + 1) + " Max Retries: " + mMaxRetries);
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

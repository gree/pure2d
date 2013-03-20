/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

/**
 * A task that postbacks/pings an HTTP Url.
 *   
 * @author sajjadtabib
 *
 */
public class PingTask implements Task, Retriable {
    private static final String LOG_TAG = PingTask.class.getSimpleName();
    private static final int DEFAULT_CONNECT_TIMEOUT = 20000;
    private static final int DEFAULT_READ_TIMEOUT = 20000;
    private static final int DEFAULT_RETRY_COUNT = 0;

    protected String mUrl;
    protected boolean mStatus;
    protected int mMaxRetries;
    protected int mRetries;
    protected int mConnectTimeout;
    protected int mReadTimeout;
    protected long mBackoffMillis;

    public PingTask() {
        mRetries = 0; //the number of attempted retries
        mMaxRetries = DEFAULT_RETRY_COUNT;
        mConnectTimeout = DEFAULT_CONNECT_TIMEOUT;
        mReadTimeout = DEFAULT_READ_TIMEOUT;

    }

    public PingTask setHttpUrl(final String httpUrl) {
        mUrl = httpUrl;

        return this;
    }

    /**
     * Set the socket connection timeout
     * @param timeout connection timeout in milliseconds. 0 means no timeout
     */
    public PingTask setConnectTimeout(final int timeout) {
        if (timeout >= 0) {
            mConnectTimeout = timeout;
        }
        return this;
    }

    /**
     * Set's the tcp socket read timeout. A value of 0 indicates no timeout
     * @param timeout
     */
    public PingTask setReadTimeout(final int timeout) {
        if (timeout >= 0) {
            mReadTimeout = timeout;
        }
        return this;
    }

    public PingTask setRetryCount(final int retry) {
        if (retry >= 0) {
        }
        return this;
    }

    public PingTask setBackoffMillis(final long backoff) {
        mBackoffMillis = backoff;
        return this;
    }

    /* (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.Task#reset()
     */
    @Override
    public void reset() {
        mStatus = false;

    }

    /* (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.Task#run()
     */
    @Override
    public boolean run() {

        do {
            Log.d(LOG_TAG, "pinging: " + mUrl);
            //ping the url and get the status
            mStatus = ping();

            if (!mStatus && mRetries < mMaxRetries) {
                if (mBackoffMillis > 0) {
                    Log.d(LOG_TAG, "backing off for " + mBackoffMillis + " millis");
                    try {
                        Thread.sleep(mBackoffMillis);
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                }
                Log.d(LOG_TAG, "Retries: " + mRetries + " MaxRetries: " + mMaxRetries);
            }

        } while (!mStatus && mRetries++ < mMaxRetries);

        return mStatus;
    }

    protected boolean ping() {
        boolean status = false;
        try {

            //set up the http client instance
            URL url = new URL(mUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(mConnectTimeout);
            httpConn.setReadTimeout(mReadTimeout);

            //connect to the url
            httpConn.connect();

            //check the status code
            int responseCode = httpConn.getResponseCode();
            Log.d(LOG_TAG, "pinged " + mUrl + " with status code: " + responseCode);
            if ((200 <= responseCode) && (responseCode < 300)) {

                status = true;
            }

            //disconnect
            httpConn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return status;
    }

    /* (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.Task#isSucceeded()
     */
    @Override
    public boolean isSucceeded() {
        // TODO Auto-generated method stub
        return mStatus;
    }

}

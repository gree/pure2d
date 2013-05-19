/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import android.content.Intent;
import android.util.Log;

/**
 * @author long
 */
public abstract class URLRetriableTask extends URLTask implements Retriable {
    public static boolean LOG_ENABLED = true;
    public static final String TAG = URLRetriableTask.class.getSimpleName();

    public static final String CLASS_NAME = URLRetriableTask.class.getName();
    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";

    protected boolean mSucceeded; // whether the execution was successful or not.

    private int mRetriedAlready = 0; // number of times already retried
    private int mRetryMax = 0; // max number of retries
    private int mRetryDelay = 0; // delay between retries

    public URLRetriableTask(final String srcURL) {
        super(srcURL);
    }

    public URLRetriableTask(final String srcURL, final int retryMax) {
        super(srcURL);

        mRetryMax = retryMax;
    }

    public URLRetriableTask(final String srcURL, final int retryMax, final int retryDelay) {
        super(srcURL);

        mRetryMax = retryMax;
        mRetryDelay = retryDelay;
    }

    public void reset() {
        mSucceeded = false;
        mRetriedAlready = 0;
    }

    @Override
    public boolean run() {
        if (LOG_ENABLED) {
            Log.v(TAG, "run(), " + mURL);
        }

        mSucceeded = openURL();
        if (!mSucceeded) {
            mSucceeded = retry();
        }

        return mSucceeded;
    }

    protected boolean retry() {
        if (mRetriedAlready < mRetryMax || mRetryMax == RETRY_UNLIMITED) {
            if (mRetryDelay > 0) {
                try {
                    Thread.sleep(mRetryDelay);
                } catch (InterruptedException e) {
                    // TODO nothing
                }
            }
            mRetriedAlready++;

            // try again
            if (openURL()) {
                return true;
            } else {
                // recursively retry
                return retry();
            }
        }

        return false;
    }

    @Override
    public Intent getCompleteIntent() {
        final Intent intent = super.getCompleteIntent();
        intent.setAction(INTENT_COMPLETE);
        return intent;
    }

    public boolean isSucceeded() {
        return mSucceeded;
    }

}

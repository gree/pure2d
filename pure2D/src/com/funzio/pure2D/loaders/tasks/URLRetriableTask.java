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

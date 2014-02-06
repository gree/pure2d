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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Intent;
import android.util.Log;

/**
 * @author long
 */
public class DownloadTask extends URLTask implements Retriable, Optional {
    public static boolean LOG_ENABLED = true;
    public static final String TAG = DownloadTask.class.getSimpleName();

    public static final String CLASS_NAME = DownloadTask.class.getName();
    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";
    public static final String EXTRA_FILE_PATH = "filePath";

    protected final String mFilePath;
    protected boolean mOverriding = false;
    private OutputStream mOutputStream;

    protected boolean mSucceeded; // whether the execution was successful or not.
    protected boolean mIsOptional; // whether we should care about whether this task failed or not

    private int mRetriedAlready = 0; // number of times already retried
    private int mRetryMax = 0; // max number of retries
    private int mRetryDelay = 0; // delay between retries

    public DownloadTask(final String srcURL, final String dstFilePath) {
        super(srcURL);

        mFilePath = dstFilePath;
    }

    public DownloadTask(final String srcURL, final String dstFilePath, final boolean overriding) {
        super(srcURL);

        mFilePath = dstFilePath;
        mOverriding = overriding;
    }

    public DownloadTask(final String srcURL, final String dstFilePath, final boolean overriding, final int retryMax) {
        super(srcURL);

        mFilePath = dstFilePath;
        mOverriding = overriding;
        mRetryMax = retryMax;
    }

    public DownloadTask(final String srcURL, final String dstFilePath, final boolean overriding, final int retryMax, final int retryDelay) {
        super(srcURL);

        mFilePath = dstFilePath;
        mOverriding = overriding;
        mRetryMax = retryMax;
        mRetryDelay = retryDelay;
    }

    public void reset() {
        mSucceeded = false;
        mRetriedAlready = 0;
    }

    public void setIsOptional(final boolean isOptional) {
        mIsOptional = isOptional;
    }

    @Override
    public boolean run() {
        if (LOG_ENABLED) {
            Log.v(TAG, "run(), " + mURL + ", " + mFilePath);
        }

        mSucceeded = download();
        if (!mSucceeded) {
            mSucceeded = retry();
        }

        return mSucceeded;
    }

    protected boolean download() {

        final File file = new File(mFilePath);
        try {
            if (file.exists() && file.length() > 0 && !mOverriding) {
                if (LOG_ENABLED) {
                    Log.v(TAG, mFilePath + " already exists. Skip!");
                }
                return true; // early success
            }

            // create the dirs if not existing
            final File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }

            mOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            if (LOG_ENABLED) {
                Log.e(TAG, "OPEN ERROR!", e);
            }
            return false;
        }

        // run now
        boolean success = openURL();
        try {
            // finalize
            mOutputStream.flush();
            mOutputStream.close();
        } catch (IOException e) {
            if (LOG_ENABLED) {
                Log.e(TAG, "CLOSE ERROR!", e);
            }

            // uh oh!
            success = false;
        }

        if (!success) {
            // remove the file
            file.delete();
        }

        return success;
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
            if (download()) {
                return true;
            } else {
                // recursively retry
                return retry();
            }
        }

        return false;
    }

    @Override
    protected void onProgress(final byte[] data, final int count) throws Exception {
        mOutputStream.write(data, 0, count);
    }

    @Override
    public Intent getCompleteIntent() {
        final Intent intent = super.getCompleteIntent();
        intent.setAction(INTENT_COMPLETE);
        intent.putExtra(EXTRA_FILE_PATH, mFilePath);
        return intent;
    }

    public boolean isSucceeded() {
        return mSucceeded;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public boolean isOverriding() {
        return mOverriding;
    }

    public void setOverriding(final boolean overriding) {
        mOverriding = overriding;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[DownloadTask " + mURL + ", " + mFilePath + " ]";
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.Optional#isOptional()
     */
    @Override
    public boolean isOptional() {
        return mIsOptional;
    }
}

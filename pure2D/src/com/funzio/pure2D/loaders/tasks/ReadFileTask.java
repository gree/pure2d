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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * @author long
 */
public abstract class ReadFileTask implements IntentTask {
    public static final String TAG = ReadFileTask.class.getSimpleName();
    public static final String CLASS_NAME = ReadFileTask.class.getName();
    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";

    public static String EXTRA_FILE_PATH = "filePath";

    protected final AssetManager mAssets;
    protected final String mFilePath;

    protected boolean mSucceeded; // whether the execution was successful or not.

    private InputStream mInputStream;

    public ReadFileTask(final String filePath) {
        mAssets = null;
        mFilePath = filePath;
    }

    public ReadFileTask(final AssetManager assets, final String filePath) {
        mAssets = assets;
        mFilePath = filePath;
    }

    public void reset() {
        mSucceeded = false;
    }

    @Override
    public boolean run() {
        mSucceeded = doWork();

        Log.v(TAG, "run(), " + mFilePath + ", success: " + mSucceeded);
        return mSucceeded;
    }

    private boolean doWork() {

        if (mAssets == null) {
            // read from file system
            final File file = new File(mFilePath);
            if (!file.exists()) {
                Log.e(TAG, mFilePath + " does not exist!");
                return false; // early success
            }

            try {
                mInputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "OPEN ERROR!", e);
                return false;
            }
        } else {
            // read from Assets
            try {
                mInputStream = mAssets.open(mFilePath);
            } catch (IOException e) {
                Log.e(TAG, "OPEN ERROR!", e);
                return false;
            }
        }

        boolean success = true;
        try {
            // read text now
            readContent(mInputStream);

            // finalize
            mInputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "READ ERROR!", e);
            success = false;
        }

        return success;

    }

    abstract protected void readContent(InputStream in) throws IOException;

    @Override
    public Intent getCompleteIntent() {
        final Intent intent = new Intent(INTENT_COMPLETE);
        intent.putExtra(EXTRA_FILE_PATH, mFilePath);
        return intent;
    }

    public boolean isSucceeded() {
        return mSucceeded;
    }

    public String getFilePath() {
        return mFilePath;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[ReadFileTask " + mFilePath + " ]";
    }
}

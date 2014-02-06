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
public abstract class WriteFileTask implements IntentTask {
    public static final String TAG = WriteFileTask.class.getSimpleName();
    public static final String CLASS_NAME = WriteFileTask.class.getName();
    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";

    public static String EXTRA_FILE_PATH = "filePath";

    protected final String mFilePath;
    protected final boolean mOverriding;

    protected boolean mSucceeded; // whether the execution was successful or not.

    private OutputStream mOutputStream;

    public WriteFileTask(final String dstFilePath, final boolean overriding) {
        mFilePath = dstFilePath;
        mOverriding = overriding;
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

        final File file = new File(mFilePath);
        try {
            if (file.exists() && !mOverriding) {
                Log.v(TAG, mFilePath + " already exists. Skip!");
                return true; // early success
            }

            // create the dirs if not existing
            final File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }

            mOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "OPEN ERROR!", e);
            return false;
        }

        boolean success = true;
        try {
            // write text now
            writeContent(mOutputStream);

            // finalize
            mOutputStream.flush();
            mOutputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "WRITE ERROR!", e);
            success = false;
        }

        if (!success) {
            // remove the file
            file.delete();
        }

        return success;

    }

    abstract protected void writeContent(OutputStream out) throws IOException;

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
        return "[WriteFileTask " + mFilePath + " ]";
    }
}

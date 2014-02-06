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
public class RunnableTask implements IntentTask {
    public static boolean LOG_ENABLED = true;

    public static final String TAG = RunnableTask.class.getSimpleName();
    public static final String CLASS_NAME = RunnableTask.class.getName();
    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";

    protected boolean mSucceeded; // whether the execution was successful or not.
    protected Runnable mRunnable;

    public RunnableTask(final Runnable runnable) {
        mRunnable = runnable;
    }

    public void reset() {
        mSucceeded = false;
    }

    @Override
    public boolean run() {
        try {
            mRunnable.run();
            // always assume successful unless there are exceptions
            mSucceeded = true;
        } catch (Exception e) {
            mSucceeded = false;

            if (LOG_ENABLED) {
                Log.e(TAG, "RUN ERROR!", e);
            }
        }

        return mSucceeded;
    }

    @Override
    public Intent getCompleteIntent() {
        final Intent intent = new Intent(INTENT_COMPLETE);
        return intent;
    }

    public boolean isSucceeded() {
        return mSucceeded;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[RunnableTask " + mRunnable + " ]";
    }
}

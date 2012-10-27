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
                Log.v(TAG, "RUN ERROR!", e);
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

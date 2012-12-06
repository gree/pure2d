/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.util.Log;

/**
 * @author long
 */
public abstract class ReadStreamTask implements IntentTask {
    public static final String TAG = ReadStreamTask.class.getSimpleName();
    public static final String CLASS_NAME = ReadStreamTask.class.getName();
    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";

    protected boolean mSucceeded; // whether the execution was successful or not.

    private InputStream mInputStream;

    public ReadStreamTask(final InputStream stream) {
        mInputStream = stream;
    }

    public void reset() {
        mSucceeded = false;
    }

    @Override
    public boolean run() {
        mSucceeded = doWork();

        Log.v(TAG, "run(),  success: " + mSucceeded);
        return mSucceeded;
    }

    private boolean doWork() {

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
        return "[ReadStreamTask " + mInputStream + " ]";
    }
}

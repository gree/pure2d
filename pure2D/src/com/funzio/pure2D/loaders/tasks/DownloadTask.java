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
public class DownloadTask extends URLTask {
    public static boolean LOG_ENABLED = true;

    public static final String TAG = DownloadTask.class.getSimpleName();
    public static final String CLASS_NAME = DownloadTask.class.getName();
    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";

    public static String EXTRA_FILE_PATH = "filePath";

    protected final String mFilePath;
    protected final boolean mOverriding;

    protected boolean mIsFinished;
    protected boolean mIsSuccessfull; // whether the execution was successful or not.

    private OutputStream mOutputStream;

    public DownloadTask(final String srcURL, final String dstFilePath, final boolean overriding) {
        super(srcURL);

        mFilePath = dstFilePath;
        mOverriding = overriding;
        mIsFinished = false;
    }

    @Override
    public boolean run() {
        if (LOG_ENABLED) {
            Log.v(TAG, "run(), " + mURL + ", " + mFilePath);
        }

        mIsSuccessfull = doWork();
        mIsFinished = true;

        return mIsSuccessfull;
    }

    private boolean doWork() {

        final File file = new File(mFilePath);
        try {
            if (file.exists() && !mOverriding) {
                if (LOG_ENABLED) {
                    Log.v(TAG, mFilePath + " already exists. Skip!");
                }
                return false;
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
        final boolean success = super.run();

        try {
            // finalize
            mOutputStream.flush();
            mOutputStream.close();
        } catch (IOException e) {
            if (LOG_ENABLED) {
                Log.e(TAG, "CLOSE ERROR!", e);
            }
        }

        if (!success) {
            // remove the file
            file.delete();
            return false;
        }

        return true;

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

    public boolean isSuccessful() {
        return mIsSuccessfull;
    }

    public boolean isFinished() {
        return mIsFinished;
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
        return "[DownloadTask " + mURL + ", " + mFilePath + " ]";
    }
}

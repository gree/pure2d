/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.content.Intent;
import android.util.Log;

/**
 * @author long
 */
public class CreateTextFileTask implements IntentTask {
    public static final String TAG = CreateTextFileTask.class.getSimpleName();
    public static final String CLASS_NAME = CreateTextFileTask.class.getName();
    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";

    public static String EXTRA_FILE_PATH = "filePath";

    protected final String mContent;
    protected final String mFilePath;
    protected final boolean mOverriding;

    protected boolean mIsFinished;
    protected boolean mIsSuccessfull; // whether the execution was successful or not.

    private OutputStream mOutputStream;

    public CreateTextFileTask(final String content, final String dstFilePath, final boolean overriding) {
        mContent = content;
        mFilePath = dstFilePath;
        mOverriding = overriding;
        mIsFinished = false;
    }

    @Override
    public boolean run() {
        Log.v(TAG, "run(), " + mFilePath);
        mIsSuccessfull = doWork();
        mIsFinished = true;

        return mIsSuccessfull;
    }

    private boolean doWork() {

        final File file = new File(mFilePath);
        try {
            if (file.exists() && !mOverriding) {
                Log.v(TAG, mFilePath + " already exists. Skip!");
                return false;
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
            if (mContent != null && !mContent.equalsIgnoreCase("")) {
                final OutputStreamWriter writer = new OutputStreamWriter(mOutputStream);
                writer.write(mContent);
                writer.flush();
            }

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
            return false;
        }

        return true;

    }

    @Override
    public Intent getCompleteIntent() {
        final Intent intent = new Intent(INTENT_COMPLETE);
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

    public String getContent() {
        return mContent;
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

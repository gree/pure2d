/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.content.Intent;

/**
 * @author long
 */
public class WriteTextFileTask extends WriteFileTask {
    public static final String TAG = WriteTextFileTask.class.getSimpleName();
    public static final String CLASS_NAME = WriteTextFileTask.class.getName();
    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";

    protected final String mContent;

    public WriteTextFileTask(final String content, final String dstFilePath, final boolean overriding) {
        super(dstFilePath, overriding);
        mContent = content;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.WriteFileTask#writeContent(java.io.OutputStream)
     */
    @Override
    protected void writeContent(final OutputStream out) throws IOException {
        // write text now
        if (mContent != null && !mContent.equalsIgnoreCase("")) {
            final OutputStreamWriter writer = new OutputStreamWriter(out);
            writer.write(mContent);
            writer.flush();
        }
    }

    public String getContent() {
        return mContent;
    }

    @Override
    public Intent getCompleteIntent() {
        final Intent intent = super.getCompleteIntent();
        intent.setAction(INTENT_COMPLETE);
        return intent;
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

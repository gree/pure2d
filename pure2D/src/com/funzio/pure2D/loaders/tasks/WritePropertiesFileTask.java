/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import android.content.Intent;

/**
 * @author long
 */
public class WritePropertiesFileTask extends WriteFileTask {
    public static final String TAG = WritePropertiesFileTask.class.getSimpleName();
    public static final String CLASS_NAME = WritePropertiesFileTask.class.getName();
    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";

    protected final Properties mContent;

    public WritePropertiesFileTask(final Properties properties, final String dstFilePath, final boolean overriding) {
        super(dstFilePath, overriding);
        mContent = properties;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.WriteFileTask#writeContent(java.io.OutputStream)
     */
    @Override
    protected void writeContent(final OutputStream out) throws IOException {
        // write text now
        if (mContent != null) {
            mContent.store(out, null);
        }
    }

    public Properties getContent() {
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
        return "[WritePropertiesFileTask " + mFilePath + " ]";
    }
}

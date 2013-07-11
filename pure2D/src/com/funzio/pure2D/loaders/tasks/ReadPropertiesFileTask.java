/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Intent;
import android.content.res.AssetManager;

/**
 * @author long
 */
public class ReadPropertiesFileTask extends ReadFileTask {
    public static final String TAG = ReadPropertiesFileTask.class.getSimpleName();
    public static final String CLASS_NAME = ReadPropertiesFileTask.class.getName();
    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";

    protected Properties mContent;

    public ReadPropertiesFileTask(final String filePath) {
        super(filePath);
    }

    public ReadPropertiesFileTask(final AssetManager assets, final String filePath) {
        super(assets, filePath);
    }

    @Override
    protected void readContent(final InputStream in) throws IOException {
        mContent = new Properties();
        mContent.load(in);
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
        return "[ReadPropertiesFileTask " + mFilePath + " ]";
    }
}

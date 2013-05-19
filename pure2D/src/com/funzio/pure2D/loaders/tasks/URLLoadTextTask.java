/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import android.content.Intent;

/**
 * @author long
 */
public class URLLoadTextTask extends URLRetriableTask {

    public static final String CLASS_NAME = URLLoadTextTask.class.getName();
    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";

    private StringBuilder mStringBuilder = new StringBuilder();

    /**
     * @param srcURL
     */
    public URLLoadTextTask(final String srcURL) {
        super(srcURL);
    }

    /**
     * @param srcURL
     * @param retryMax
     */
    public URLLoadTextTask(final String srcURL, final int retryMax) {
        super(srcURL, retryMax);
    }

    /**
     * @param srcURL
     * @param retryMax
     * @param retryDelay
     */
    public URLLoadTextTask(final String srcURL, final int retryMax, final int retryDelay) {
        super(srcURL, retryMax, retryDelay);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.URLTask#onProgress(byte[], int)
     */
    @Override
    protected void onProgress(final byte[] data, final int count) throws Exception {
        // append to the string
        mStringBuilder.append(new String(data, 0, count));
    }

    public StringBuilder getStringBuilder() {
        return mStringBuilder;
    }

    @Override
    public Intent getCompleteIntent() {
        final Intent intent = super.getCompleteIntent();
        intent.setAction(INTENT_COMPLETE);
        return intent;
    }
}

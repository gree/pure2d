/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author long
 */
public class URLLoadPropertiesTask extends URLRetriableTask {

    protected Properties mContent;

    /**
     * @param srcURL
     */
    public URLLoadPropertiesTask(final String srcURL) {
        super(srcURL);
    }

    /**
     * @param srcURL
     * @param retryMax
     */
    public URLLoadPropertiesTask(final String srcURL, final int retryMax) {
        super(srcURL, retryMax);
    }

    /**
     * @param srcURL
     * @param retryMax
     * @param retryDelay
     */
    public URLLoadPropertiesTask(final String srcURL, final int retryMax, final int retryDelay) {
        super(srcURL, retryMax, retryDelay);
    }

    @Override
    protected int readStream(final InputStream stream) throws Exception {
        if (mContent == null) {
            mContent = new Properties();
        }
        mContent.load(stream);
        stream.close();

        // always true
        return mContentLength;
    }

    @Override
    protected void onProgress(final byte[] data, final int count) throws Exception {
        // nothing
    }

    public Properties getContent() {
        return mContent;
    }
}

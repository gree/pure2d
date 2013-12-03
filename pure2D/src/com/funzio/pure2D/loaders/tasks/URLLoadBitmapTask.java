/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author long
 */
public class URLLoadBitmapTask extends URLRetriableTask {

    protected Bitmap mContent;
    protected BitmapFactory.Options mOptions;

    /**
     * @param srcURL
     */
    public URLLoadBitmapTask(final String srcURL, final BitmapFactory.Options options) {
        super(srcURL);

        mOptions = options;
    }

    /**
     * @param srcURL
     * @param retryMax
     */
    public URLLoadBitmapTask(final String srcURL, final BitmapFactory.Options options, final int retryMax) {
        super(srcURL, retryMax);

        mOptions = options;
    }

    /**
     * @param srcURL
     * @param retryMax
     * @param retryDelay
     */
    public URLLoadBitmapTask(final String srcURL, final BitmapFactory.Options options, final int retryMax, final int retryDelay) {
        super(srcURL, retryMax, retryDelay);

        mOptions = options;
    }

    @Override
    protected int readStream(final InputStream stream) throws Exception {
        mContent = BitmapFactory.decodeStream(stream, null, mOptions);
        stream.close();

        // always true
        return mContentLength;
    }

    @Override
    protected void onProgress(final byte[] data, final int count) throws Exception {
        // nothing
    }

    public Bitmap getContent() {
        return mContent;
    }
}

/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.funzio.pure2D.gl.gl10.textures.TextureOptions;

/**
 * @author long
 */
public class URLLoadBitmapTask extends URLRetriableTask {

    protected Bitmap mContent;
    protected TextureOptions mOptions;

    /**
     * @param srcURL
     */
    public URLLoadBitmapTask(final String srcURL, final TextureOptions options) {
        super(srcURL);

        mOptions = options;
    }

    /**
     * @param srcURL
     * @param retryMax
     */
    public URLLoadBitmapTask(final String srcURL, final TextureOptions options, final int retryMax) {
        super(srcURL, retryMax);

        mOptions = options;
    }

    /**
     * @param srcURL
     * @param retryMax
     * @param retryDelay
     */
    public URLLoadBitmapTask(final String srcURL, final TextureOptions options, final int retryMax, final int retryDelay) {
        super(srcURL, retryMax, retryDelay);

        mOptions = options;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.URLTask#readStream(java.io.InputStream)
     */
    @Override
    protected int readStream(final InputStream stream) throws Exception {
        mContent = BitmapFactory.decodeStream(stream, null, mOptions);
        stream.close();

        // always true
        return mContentLength;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.URLTask#onProgress(byte[], int)
     */
    @Override
    protected void onProgress(final byte[] data, final int count) throws Exception {
        // nothing
    }

    public Bitmap getContent() {
        return mContent;
    }
}

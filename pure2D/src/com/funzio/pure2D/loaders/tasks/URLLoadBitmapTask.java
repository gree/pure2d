/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
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

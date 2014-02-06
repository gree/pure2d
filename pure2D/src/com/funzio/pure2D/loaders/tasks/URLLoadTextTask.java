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

import android.content.Intent;

/**
 * @author long
 */
public class URLLoadTextTask extends URLRetriableTask {

    public static final String CLASS_NAME = URLLoadTextTask.class.getName();
    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";

    protected StringBuilder mStringBuilder = new StringBuilder();

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

    @Override
    public void reset() {
        super.reset();

        // reset string
        mStringBuilder.setLength(0);
    }

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

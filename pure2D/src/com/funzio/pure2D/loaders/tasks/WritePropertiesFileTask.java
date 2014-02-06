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

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Intent;
import android.content.res.AssetManager;

/**
 * @author long
 */
public class ReadTextFileTask extends ReadFileTask {
    public static final String TAG = ReadTextFileTask.class.getSimpleName();
    public static final String CLASS_NAME = ReadTextFileTask.class.getName();
    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";

    protected String mContent;

    public ReadTextFileTask(final String filePath) {
        super(filePath);
    }

    public ReadTextFileTask(final AssetManager assets, final String filePath) {
        super(assets, filePath);
    }

    @Override
    protected void readContent(final InputStream in) throws IOException {

        final StringBuffer sb = new StringBuffer();
        final InputStreamReader ir = new InputStreamReader(in);
        final BufferedReader br = new BufferedReader(ir);

        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        ir.close();

        // get the string
        mContent = sb.toString();
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
        return "[ReadTextFileTask]";
    }
}

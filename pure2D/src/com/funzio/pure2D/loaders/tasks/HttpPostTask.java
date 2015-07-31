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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.util.Log;

/**
 * A task that performs a simple HTTP post
 * @author sajjadtabib
 * 
 */
public class HttpPostTask extends NetworkTask {
    public static boolean LOG_ENABLED = true;
    public final String LOG_TAG = HttpPostTask.class.getSimpleName();

    protected String mData;
    protected Map<String, String> mHttpProperties;

    private static final String POST_METHOD = "POST";

    /**
     * 
     */
    public HttpPostTask(final String data, final Map<String, String> httpProperties) {
        super();
        mData = data;
        mHttpProperties = httpProperties;
    }

    protected boolean postUrl() {
        final HttpURLConnection httpConn;

        try {
            URL address = new URL(mUrl);
            httpConn = (HttpURLConnection) address.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setRequestMethod(POST_METHOD);

            // add properties to the http post request
            for (String key : mHttpProperties.keySet()) {
                String value = mHttpProperties.get(key);
                if (value != null) {
                    httpConn.setRequestProperty(key, value);
                }
            }

            httpConn.setFixedLengthStreamingMode(mData.getBytes().length);

        } catch (IOException e) {
            if (LOG_ENABLED) {
                Log.d(LOG_TAG, "error setting http params: ", e);
            }
            return false;
        }

        // now that connection is open send data
        try {
            OutputStreamWriter os = new OutputStreamWriter(httpConn.getOutputStream());
            os.write(mData);
            os.close();
            int responseCode = httpConn.getResponseCode();

            if (LOG_ENABLED) {
                Log.d(LOG_TAG, "posted url with status code: " + responseCode);
            }
            if ((200 <= responseCode) && (responseCode < 300)) {

                return true;
            }

            //TODO: add ability to read the response later

            httpConn.disconnect();

        } catch (IOException e) {

            if (LOG_ENABLED) {
                Log.v(LOG_TAG, "write error!", e);
            }
        }

        return false;
    }

    /* (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.NetworkTask#doNetworkTask()
     */
    @Override
    protected boolean doNetworkTask() {
        return postUrl();
    }

    /* (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.NetworkTask#getLogTag()
     */
    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }

    /* (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.Task#reset()
     */
    @Override
    public void reset() {
        super.reset();

    }

}

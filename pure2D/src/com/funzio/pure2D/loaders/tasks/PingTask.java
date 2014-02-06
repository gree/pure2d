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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

/**
 * A task that postbacks/pings an HTTP Url.
 *   
 * @author sajjadtabib
 *
 */
public class PingTask extends NetworkTask implements Retriable {

    public static boolean LOG_ENABLED = true;
    private static final String LOG_TAG = PingTask.class.getSimpleName();

    public PingTask() {
        super();
    }

    /* (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.Task#reset()
     */
    @Override
    public void reset() {
        super.reset();

    }

    protected boolean ping() {
        boolean status = false;
        try {

            //set up the http client instance
            URL url = new URL(mUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(mConnectTimeout);
            httpConn.setReadTimeout(mReadTimeout);

            //connect to the url
            httpConn.connect();

            //check the status code
            int responseCode = httpConn.getResponseCode();

            if (LOG_ENABLED) {
                Log.d(LOG_TAG, "pinged " + mUrl + " with status code: " + responseCode);
            }

            if ((200 <= responseCode) && (responseCode < 300)) {

                status = true;
            }

            //disconnect
            httpConn.disconnect();

        } catch (MalformedURLException e) {
            if (LOG_ENABLED) {
                Log.e(LOG_TAG, "ping error: ", e);
            }
        } catch (IOException e) {
            if (LOG_ENABLED) {
                Log.e(LOG_TAG, "ping error: ", e);
            }
        }

        return status;
    }

    /* (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.NetworkTask#doNetworkTask()
     */
    @Override
    protected boolean doNetworkTask() {

        return ping();
    }

    /* (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.NetworkTask#getLogTag()
     */
    @Override
    protected String getLogTag() {

        return LOG_TAG;
    }

}

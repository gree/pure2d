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
                Log.e(LOG_TAG, "" + e);
            }
        } catch (IOException e) {
            if (LOG_ENABLED) {
                Log.e(LOG_TAG, "" + e);
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

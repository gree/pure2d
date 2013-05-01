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

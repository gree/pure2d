/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author long
 */
public class URLLoadJsonTask extends URLLoadTextTask {

    protected JSONObject mContent;

    /**
     * @param srcURL
     */
    public URLLoadJsonTask(final String srcURL) {
        super(srcURL);
    }

    /**
     * @param srcURL
     * @param retryMax
     */
    public URLLoadJsonTask(final String srcURL, final int retryMax) {
        super(srcURL, retryMax);
    }

    /**
     * @param srcURL
     * @param retryMax
     * @param retryDelay
     */
    public URLLoadJsonTask(final String srcURL, final int retryMax, final int retryDelay) {
        super(srcURL, retryMax, retryDelay);
    }

    @Override
    public boolean run() {
        if (super.run()) {
            try {
                mContent = new JSONObject(mStringBuilder.toString());
            } catch (JSONException e) {
                Log.e(TAG, "Json parsing error!", e);
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public JSONObject getContent() {
        return mContent;
    }
}

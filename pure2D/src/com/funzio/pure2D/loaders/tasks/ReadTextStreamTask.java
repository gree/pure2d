/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Intent;

/**
 * @author long
 */
public class ReadTextStreamTask extends ReadStreamTask {
    public static final String TAG = ReadTextStreamTask.class.getSimpleName();
    public static final String CLASS_NAME = ReadTextStreamTask.class.getName();
    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";

    protected String mContent;

    public ReadTextStreamTask(final InputStream stream) {
        super(stream);
    }

    @Override
    protected void readContent(final InputStream in) throws IOException {

        final StringBuffer storedString = new StringBuffer();

        final InputStreamReader ir = new InputStreamReader(in);
        final BufferedReader br = new BufferedReader(ir);
        String line = "";
        while ((line = br.readLine()) != null) {
            storedString.append(line);
        }
        br.close();
        ir.close();

        // get the string
        mContent = storedString.toString();
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

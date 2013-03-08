/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author long
 */
public class ReadJsonFileTask extends ReadFileTask {
    public static final String TAG = ReadJsonFileTask.class.getSimpleName();
    public static final String CLASS_NAME = ReadJsonFileTask.class.getName();
    public static final String INTENT_COMPLETE = CLASS_NAME + ".INTENT_COMPLETE";

    protected ObjectMapper mObjectMapper;
    protected Class<?> mObjectClass;
    protected Object mContent;

    public ReadJsonFileTask(final String filePath, final ObjectMapper mapper, final Class<?> objectClass) {
        super(filePath);

        mObjectMapper = mapper;
        mObjectClass = objectClass;
    }

    @Override
    protected void readContent(final InputStream in) throws IOException {
        mContent = mObjectMapper.readValue(in, mObjectClass);
    }

    public Object getContent() {
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
        return "[ReadJsonFileTask " + mFilePath + " ]";
    }
}

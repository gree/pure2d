/**
 * 
 */
package com.funzio.pure2D.sounds;

import java.io.File;

import android.net.Uri;

/**
 * @author sajjadtabib
 * 
 */
public class MediaUri extends AbstractMedia {

    protected Uri mUri;

    /**
     * @param key
     * @param filePath
     */
    public MediaUri(final int key, final String filePath) {
        super(key);

        mUri = Uri.fromFile(new File(filePath));

    }

    public MediaUri(final int key, final Uri uri) {
        super(key);
        mUri = uri;

    }

    public Uri getMediaUri() {
        return mUri;
    }

}

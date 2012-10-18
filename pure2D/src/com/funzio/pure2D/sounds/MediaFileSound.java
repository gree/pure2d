/**
 * 
 */
package com.funzio.pure2D.sounds;

import android.net.Uri;

/**
 * @author sajjadtabib
 * Acts similar to the FileSound, but uses the media player instead of the soundpool
 */
public class MediaFileSound extends FileSound {

    protected Uri mUri;

    /**
     * @param key
     * @param filePath
     */
    public MediaFileSound(final int key, final Uri uri) {
        super(key, null);

        mUri = uri;
    }

    public Uri getMediaUri() {
        return mUri;
    }

}

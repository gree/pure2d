/**
 * 
 */
package com.funzio.pure2D.sounds;

import java.io.File;

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
    public MediaFileSound(final int key, final String filePath) {
        super(key, filePath);

        mUri = Uri.fromFile(new File(filePath));

    }

    public Uri getMediaUri() {
        return mUri;
    }

}

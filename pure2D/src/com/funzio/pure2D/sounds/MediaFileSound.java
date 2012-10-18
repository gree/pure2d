/**
 * 
 */
package com.funzio.pure2D.sounds;

import java.io.FileDescriptor;

/**
 * @author sajjadtabib
 * Acts similar to the FileSound, but uses the media player instead of the soundpool
 */
public class MediaFileSound extends FileSound {

    //    protected Uri mUri;
    protected FileDescriptor mFd;

    /**
     * @param key
     * @param filePath
     */
    public MediaFileSound(final int key, final FileDescriptor fd) {
        super(key, null);

    }

    public FileDescriptor getMediaFileDescriptor() {
        return mFd;
    }

}

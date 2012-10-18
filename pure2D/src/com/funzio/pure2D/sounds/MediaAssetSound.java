/**
 * 
 */
package com.funzio.pure2D.sounds;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

/**
 * @author sajjadtabib
 *
 */
public class MediaAssetSound extends AssetSound {

    private AssetFileDescriptor mAssetFd;

    /**
     * @param key
     * @param assets
     * @param filePath
     */
    public MediaAssetSound(final int key, final AssetManager assets, final String filePath) {
        super(key, assets, filePath);

        try {
            mAssetFd = assets.openFd(filePath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public long getStartOffset() {
        return mAssetFd.getStartOffset();
    }

    public long getLength() {
        return mAssetFd.getLength();
    }

    public AssetFileDescriptor getAssetFileDescriptor() {
        return mAssetFd;
    }

}

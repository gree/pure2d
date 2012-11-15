/**
 * 
 */
package com.funzio.pure2D.sounds;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * @author sajjadtabib
 */
public class AssetMedia extends AbstractMedia {
    protected static final String TAG = AssetMedia.class.getSimpleName();

    private AssetFileDescriptor mAssetFd;

    /**
     * @param key
     * @param assets
     * @param filePath
     */
    public AssetMedia(final int key, final AssetManager assets, final String filePath) {
        super(key);

        try {
            mAssetFd = assets.openFd(filePath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public AssetFileDescriptor getAssetFileDescriptor() {
        return mAssetFd;
    }

    public int load(final MediaPlayer player, final Context context) {
        try {
            player.setDataSource(mAssetFd.getFileDescriptor(), mAssetFd.getStartOffset(), mAssetFd.getLength());
        } catch (Exception e) {
            Log.e(TAG, "" + e.getMessage());
            return 0;
        }

        return 1;
    }
}

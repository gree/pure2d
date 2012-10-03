/**
 * 
 */
package com.funzio.pure2D.sounds;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.SoundPool;
import android.util.Log;

/**
 * @author long
 */
public class AssetSound extends AbstractSound {
    protected static final String TAG = AssetSound.class.getSimpleName();

    private final AssetManager mAssets;
    private final String mFilePath;

    public AssetSound(final int key, final AssetManager assets, final String filePath) {
        super(key);

        mAssets = assets;
        mFilePath = filePath;
    }

    public int load(final SoundPool soundPool) {
        Log.v(TAG, "load(" + mFilePath + ")");

        AssetFileDescriptor desc;
        try {
            desc = mAssets.openFd(mFilePath);
        } catch (IOException e) {
            Log.e(TAG, "LOAD ERROR!\n" + e.getMessage());
            return -1;
        }

        return mSoundID = soundPool.load(desc, mPriority);
    }
}

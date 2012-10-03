/**
 * 
 */
package com.funzio.pure2D.sounds;

import android.media.SoundPool;
import android.util.Log;

/**
 * @author long
 */
public class FileSound extends AbstractSound {
    protected static final String TAG = FileSound.class.getSimpleName();

    private final String mFilePath;

    public FileSound(final int key, final String filePath) {
        super(key);

        mFilePath = filePath;
    }

    public int load(final SoundPool soundPool) {
        Log.v(TAG, "load(" + mFilePath + ")");

        return mSoundID = soundPool.load(mFilePath, mPriority);
    }
}

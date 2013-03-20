/**
 * 
 */
package com.funzio.pure2D.sounds;

import java.io.File;

import android.media.SoundPool;
import android.util.Log;

/**
 * @author long
 */
public class FileSound extends AbstractSound {
    protected static final String TAG = FileSound.class.getSimpleName();

    public static final long BITRATE = 16000;

    private final String mFilePath;

    public FileSound(final int key, final String filePath) {
        super(key);

        mFilePath = filePath;
    }

    public int load(final SoundPool soundPool) {
        Log.v(TAG, "load(" + mFilePath + ")");

        File file = new File(mFilePath);
        long fileSize = file.length();
        mLength = (long) Math.floor(fileSize * 1000 / BITRATE);

        return mSoundID = soundPool.load(mFilePath, mPriority);
    }
}

/**
 * 
 */
package com.funzio.pure2D.sounds;

import android.content.Context;
import android.media.SoundPool;
import android.util.Log;

/**
 * @author long
 */
public class ResourceSound extends AbstractSound {
    protected static final String TAG = ResourceSound.class.getSimpleName();

    private final Context mContext;
    private final int mResID;

    public ResourceSound(final int key, final Context context, final int resID) {
        super(key);

        mContext = context;
        mResID = resID;
    }

    public int load(final SoundPool soundPool) {
        Log.v(TAG, "load(" + mResID + ")");

        return mSoundID = soundPool.load(mContext, mResID, mPriority);
    }
}

package com.funzio.pure2D.sounds;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;

public class SoundManager {
    protected static final String TAG = SoundManager.class.getSimpleName();

    protected volatile SparseArray<Soundable> mSoundMap;

    protected SoundPool mSoundPool;
    protected volatile boolean mSoundEnabled = true;

    protected Context mContext;
    protected AudioManager mAudioManager;

    protected SoundManager(final Context context) {
        mContext = context;
        mSoundMap = new SparseArray<Soundable>();
        mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public boolean isSoundEnabled() {
        return mSoundEnabled;
    }

    public void setSoundEnabled(final boolean enabled) {
        mSoundEnabled = enabled;
    }

    @SuppressLint("NewApi")
    public void load(final Soundable... sounds) {
        final AsyncLoader loader = new AsyncLoader();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            loader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sounds);
        } else {
            loader.execute(sounds);
        }
    }

    private class AsyncLoader extends AsyncTask<Soundable, Void, Void> {
        @Override
        protected Void doInBackground(final Soundable... params) {
            for (final Soundable sound : params) {
                final int soundID = sound.load(mSoundPool);

                // check and add to the map
                if (soundID > 0) {
                    mSoundMap.put(sound.getKey(), sound);
                }
            }

            return null;
        }
    }

    public boolean play(final int key) {
        // Log.v(TAG, "play(" + key + ")");

        return play(mSoundMap.get(key));
    }

    public boolean play(final Soundable sound) {
        Log.v(TAG, "play(" + sound + ")");

        if (mSoundEnabled && sound != null && sound.getSoundID() > 0) {
            final float volume = (float) mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / (float) mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            if (volume > 0) {
                mSoundPool.play(sound.getSoundID(), volume, volume, 1, sound.getLoop(), 1f);
            }

            return true;
        }

        return false;
    }

    protected boolean playByID(final int soundID) {
        Log.v(TAG, "playByID(" + soundID + ")");

        if (mSoundEnabled && soundID > 0) {
            final float volume = (float) mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / (float) mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            if (volume > 0) {
                mSoundPool.play(soundID, volume, volume, 1, 0, 1f);
            }

            return true;
        }

        return false;
    }

    public Context getContext() {
        return mContext;
    }

    public void destroyPool() {
        mSoundPool.release();
        mSoundPool = null;
    }
}

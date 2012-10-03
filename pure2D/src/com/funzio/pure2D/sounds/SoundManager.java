package com.funzio.pure2D.sounds;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;

public class SoundManager implements SoundPool.OnLoadCompleteListener {
    protected static final String TAG = SoundManager.class.getSimpleName();

    protected volatile SparseArray<Soundable> mSoundMap;

    protected SoundPool mSoundPool;
    protected volatile boolean mSoundEnabled = true;

    protected Context mContext;
    protected AudioManager mAudioManager;

    protected SoundManager(final Context context, final int maxStream) {
        mContext = context;
        mSoundMap = new SparseArray<Soundable>();

        mSoundPool = new SoundPool(maxStream, AudioManager.STREAM_MUSIC, 0);
        mSoundPool.setOnLoadCompleteListener(this);

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

    /**
     * @param key
     * @return a non-zero as the Stream ID if success
     */
    public int play(final int key) {
        // Log.v(TAG, "play(" + key + ")");

        return play(mSoundMap.get(key));
    }

    /**
     * @param sound
     * @return a non-zero as the Stream ID if success
     */
    public int play(final Soundable sound) {
        // Log.v(TAG, "play(" + sound + ")");

        if (mSoundEnabled && sound != null && sound.getSoundID() > 0) {
            final float volume = (float) mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / (float) mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            return mSoundPool.play(sound.getSoundID(), volume, volume, 1, sound.getLoop(), 1f);
        }

        return 0;
    }

    protected int playByID(final int soundID) {
        // Log.v(TAG, "playByID(" + soundID + ")");

        if (mSoundEnabled && soundID > 0) {
            final float volume = (float) mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / (float) mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            return mSoundPool.play(soundID, volume, volume, 1, 0, 1f);
        }

        return 0;
    }

    public void stop(final int streamID) {
        mSoundPool.stop(streamID);
    }

    public Context getContext() {
        return mContext;
    }

    public void dispose() {
        mSoundMap.clear();

        mSoundPool.release();
        mSoundPool = null;
    }

    public void onLoadComplete(final SoundPool soundPool, final int sampleId, final int status) {
        Log.v(TAG, "onLoadComplete(" + sampleId + ", " + status + ")");
    }

}

/**
 * 
 */
package com.funzio.pure2D.sounds;

import android.media.SoundPool;

/**
 * @author long
 */
public interface Soundable {
    public int getKey();

    public int getSoundID();

    public int getLoop();

    public int load(SoundPool soundPool);
}

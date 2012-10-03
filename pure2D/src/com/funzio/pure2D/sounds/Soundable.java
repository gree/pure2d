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

    public int load(SoundPool soundPool);

    public int getPriority();

    public void setPriority(final int priority);

    public int getLoop();

    public void setLoop(final int loop);
}

/**
 * 
 */
package com.funzio.pure2D.sounds;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * @author sajjadtabib A representation of an object to be played by the media player
 */
public interface Media {

    public int getKey();

    public boolean isLooping();

    public void setLooping(boolean loop);

    public int load(MediaPlayer player, Context context);

}

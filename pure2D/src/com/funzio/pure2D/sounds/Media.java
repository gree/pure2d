/**
 * 
 */
package com.funzio.pure2D.sounds;


/**
 * @author sajjadtabib
 * A representation of an object to be played by the media player
 */
public interface Media {

    public int getKey();

    public boolean isLooping();

    public void setLooping(boolean loop);

}

/**
 * 
 */
package com.funzio.pure2D;

import android.graphics.RectF;

/**
 * @author long
 */
public interface Playable extends DisplayObject {
    public static final int LOOP_NONE = 0;
    public static final int LOOP_REPEAT = 1;
    public static final int LOOP_REVERSE = 2;

    public void play();

    public void playAt(final int frame);

    public void stop();

    public void stopAt(final int frame);

    public int getLoop();

    public void setLoop(final int type);

    public int getCurrentFrame();

    public int getNumFrames();

    public boolean isPlaying();

    public RectF getFrameRect(final int frame);
}

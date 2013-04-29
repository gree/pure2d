/**
 * 
 */
package com.funzio.pure2D.shapes;

import android.graphics.PointF;
import android.graphics.RectF;

import com.funzio.pure2D.Playable;
import com.funzio.pure2D.atlas.AtlasFrameSet;

/**
 * @author long
 */
public class Clip extends Sprite implements Playable {
    private int mLoop = LOOP_REPEAT;
    private boolean mPlaying = true;
    private int mCurrentFrame = 0;
    private int mPreviousFrame = -1;
    private int mNumFrames = 0;
    private AtlasFrameSet mFrameSet;
    private int mPendingTime = 0;
    private int mAccumimatedFrames = 0;

    public Clip() {
        super();

        setSizeToTexture(false);
    }

    public Clip(final AtlasFrameSet frameSet) {
        super();

        setSizeToTexture(false);
        setAtlasFrameSet(frameSet);
    }

    public void setAtlasFrameSet(final AtlasFrameSet frameSet) {
        mFrameSet = frameSet;

        if (frameSet != null) {
            mNumFrames = frameSet.getNumFrames();
            setFps(frameSet.getFps());

            // start from first frame
            mCurrentFrame = 0;

            setAtlasFrame(mNumFrames > 0 ? frameSet.getFrame(mCurrentFrame) : null);
        } else {
            mNumFrames = 0;
        }
    }

    public AtlasFrameSet getAtlasFrameSet() {
        return mFrameSet;
    }

    @Override
    public RectF getFrameRect(final int frame) {
        if (mFrameSet == null) {
            return null;
        } else {
            return new RectF(mFrameSet.getFrame(frame).getRect());
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#setOriginAtCenter()
     */
    @Override
    public void setOriginAtCenter() {
        if (mFrameSet != null) {
            final PointF maxSize = mFrameSet.getFrameMaxSize();
            super.setOrigin(maxSize.x / 2f, maxSize.y / 2f);
        } else {
            super.setOriginAtCenter();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.shapes.Shape#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        final boolean returned = super.update(deltaTime);

        // get next frame
        if (mNumFrames > 1 && mPlaying) {
            int frames = 1;
            // if there is specific fps
            if (getFps() > 0) {
                mPendingTime += deltaTime;
                frames = mPendingTime / (int) mFrameDuration;
                if (frames > 0) {
                    mPendingTime %= (int) mFrameDuration;
                }
            }

            if (frames > 0) {
                mAccumimatedFrames += frames;
                mCurrentFrame += frames;
                if (mLoop == LOOP_REPEAT) {
                    if (mCurrentFrame >= mNumFrames) {
                        mCurrentFrame %= mNumFrames;
                    }
                } else if (mLoop == LOOP_REVERSE) {
                    final int trips = (mAccumimatedFrames / mNumFrames);
                    if (trips % 2 == 0) {
                        // play forward
                        if (mCurrentFrame >= mNumFrames) {
                            mCurrentFrame %= mNumFrames;
                        }
                    } else {
                        // play backward
                        mCurrentFrame = mNumFrames - 1 - mAccumimatedFrames % mNumFrames;
                    }
                } else {
                    if (mCurrentFrame >= mNumFrames) {
                        // done, stop at last frame
                        mCurrentFrame = mNumFrames - 1;
                        stop();

                        // callback
                        onClipEnd(mFrameSet);
                    }
                }
            }
        }

        // change frame
        if (mCurrentFrame != mPreviousFrame && mFrameSet != null) {
            mPreviousFrame = mCurrentFrame;
            setAtlasFrame(mFrameSet.getFrame(mCurrentFrame));
        }

        return returned;
    }

    public void play() {
        mPlaying = true;
    }

    public void playAt(final int frame) {
        mCurrentFrame = frame;
        mPendingTime = 0;
        play();
    }

    public void stop() {
        mPlaying = false;
        mPendingTime = 0;
    }

    public void stopAt(final int frame) {
        mCurrentFrame = frame;
        stop();
    }

    /**
     * @return the Looping
     */
    public int getLoop() {
        return mLoop;
    }

    /**
     * @param Looping can be NONE, REPEAT, CIRCLE
     */
    public void setLoop(final int type) {
        mLoop = type;
    }

    /**
     * @return the currentFrame
     */
    public int getCurrentFrame() {
        return mCurrentFrame;
    }

    /**
     * @return the total number of frames
     */
    public int getNumFrames() {
        return mNumFrames;
    }

    public boolean isPlaying() {
        return mPlaying;
    }

    protected void onClipEnd(final AtlasFrameSet frameSet) {
        // TODO
    }
}

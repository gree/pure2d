package com.funzio.pure2D.animation;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.Playable;

/**
 * @author long
 */
public abstract class PlayableObject extends BaseDisplayObject implements Playable {
    protected int mLoop = LOOP_REPEAT;
    protected boolean mPlaying = true;
    protected int mCurrentFrame = 0;
    protected int mPreviousFrame = -1;
    protected int mNumFrames = 0;
    protected int mPendingTime = 0;
    protected int mAccumulatedFrames = 0;

    abstract protected void updateFrame(final int frame);

    @Override
    public boolean update(final int deltaTime) {
        super.update(deltaTime);

        // update current frame
        if (mCurrentFrame != mPreviousFrame) {
            updateFrame(mCurrentFrame);
            mPreviousFrame = mCurrentFrame;
            invalidate(FRAME);
        }

        // get next frame
        if (mNumFrames > 0 && mPlaying) {
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
                mAccumulatedFrames += frames;
                mCurrentFrame += frames;
                if (mLoop == LOOP_REPEAT) {
                    if (mCurrentFrame >= mNumFrames) {
                        mCurrentFrame %= mNumFrames;
                    }
                } else if (mLoop == LOOP_REVERSE) {
                    final int trips = (mAccumulatedFrames / mNumFrames);
                    if (trips % 2 == 0) {
                        // play forward
                        if (mCurrentFrame >= mNumFrames) {
                            mCurrentFrame %= mNumFrames;
                        }
                    } else {
                        // play backward
                        mCurrentFrame = mNumFrames - 1 - mAccumulatedFrames % mNumFrames;
                    }
                } else {
                    if (mCurrentFrame >= mNumFrames) {
                        // done, stop at last frame
                        mCurrentFrame = mNumFrames - 1;
                        stop();
                    }
                }
            }
        }

        return mNumFrames > 0;
    }

    public void play() {
        mPlaying = true;
    }

    public void playAt(final int frame) {
        mCurrentFrame = frame;
        play();
    }

    public void stop() {
        mPlaying = false;
        mPendingTime = 0;
    }

    public void stopAt(final int frame) {
        if (mCurrentFrame != frame || mCurrentFrame == 0) {
            mCurrentFrame = frame;

            // update the frame
            updateFrame(mCurrentFrame);
            invalidate(FRAME);
        }

        stop();
    }

    /**
     * @return the Loop
     */
    public int getLoop() {
        return mLoop;
    }

    /**
     * @param type can be NONE, REPEAT, CIRCLE
     * @see Playable
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

}

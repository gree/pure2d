/**
 * 
 */
package com.funzio.pure2D.animators;

import android.view.animation.Interpolator;

import com.funzio.pure2D.Playable;

/**
 * @author long
 */
public class TweenAnimator extends BaseAnimator {
    protected int mDuration;
    protected Interpolator mInterpolator;

    protected float mCurrentValue = 0;
    protected int mLoopMode = Playable.LOOP_NONE;
    protected int mLoopCount = -1;

    public TweenAnimator(final Interpolator interpolator) {
        super();

        setInterpolator(interpolator);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.BaseAnimator#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (super.update(deltaTime)) {
            final int trips = (mDuration == 0) ? 0 : mElapsedTime / mDuration;
            if ((mLoopCount >= 0 && trips > mLoopCount) || (mLoopMode == Playable.LOOP_NONE && mElapsedTime >= mDuration)) {
                // end it
                end();
            } else {

                float timeLine;
                if (mLoopMode == Playable.LOOP_REPEAT) {
                    timeLine = ((float) mElapsedTime % (float) mDuration) / mDuration;
                } else if (mLoopMode == Playable.LOOP_REVERSE) {
                    timeLine = ((float) mElapsedTime % (float) mDuration) / mDuration;
                    if (trips % 2 == 1) {
                        // reverse
                        timeLine = 1 - timeLine;
                    }
                } else {
                    timeLine = (float) mElapsedTime / (float) mDuration;
                }

                mCurrentValue = (mInterpolator == null) ? timeLine : mInterpolator.getInterpolation(timeLine);
                onUpdate(mCurrentValue);
            }

            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.BaseAnimator#end()
     */
    @Override
    public void end() {
        // force end
        if (mLoopMode == Playable.LOOP_REVERSE && mLoopCount > 0) {
            mCurrentValue = mLoopCount % 2 == 0 ? 1 : 0;
        } else {
            mCurrentValue = 1;
        }
        onUpdate(mCurrentValue);

        super.end();
    }

    /**
     * @return the duration
     */
    public int getDuration() {
        return mDuration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(final int duration) {
        mDuration = duration;
    }

    /**
     * @return the interpolator
     */
    public Interpolator getInterpolator() {
        return mInterpolator;
    }

    /**
     * @param interpolator the interpolator to set
     */
    public void setInterpolator(final Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    /**
     * @return the looping
     */
    public int getLoop() {
        return mLoopMode;
    }

    /**
     * @param loop the looping to set
     */
    public void setLoop(final int loop) {
        mLoopMode = loop;
    }

    public int getLoopCount() {
        return mLoopCount;
    }

    public void setLoopCount(final int value) {
        mLoopCount = value;
    }

    /**
     * @return the currentValue
     */
    public float getCurrentValue() {
        return mCurrentValue;
    }

    protected void onUpdate(final float value) {
        if (mListener != null) {
            mListener.onAnimationUpdate(this, value);
        }
    }
}

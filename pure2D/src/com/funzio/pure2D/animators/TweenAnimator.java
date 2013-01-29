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
    protected int mLoop = Playable.LOOP_NONE;
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
            final int trips = mElapsedTime / mDuration;
            float elapsed = mElapsedTime;
            boolean endLoop = false;
            if ((mLoopCount >= 0 && trips > mLoopCount) || (mLoop == Playable.LOOP_NONE && mElapsedTime >= mDuration)) {
                // cap it
                elapsed = trips * mDuration;
                endLoop = true;
            }

            float timeLine;
            if (mLoop == Playable.LOOP_REPEAT) {
                timeLine = endLoop ? 1 : (elapsed % mDuration) / mDuration;
            } else if (mLoop == Playable.LOOP_REVERSE) {
                timeLine = (elapsed % mDuration) / mDuration;
                if (trips % 2 == 1) {
                    // reverse
                    timeLine = 1 - timeLine;
                }
            } else {
                timeLine = elapsed / mDuration;
            }

            mCurrentValue = (mInterpolator == null) ? timeLine : mInterpolator.getInterpolation(timeLine);
            onUpdate(mCurrentValue);

            if (endLoop) {
                super.end();
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
        mCurrentValue = 1;
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
        return mLoop;
    }

    /**
     * @param loop the looping to set
     */
    public void setLoop(final int loop) {
        mLoop = loop;
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

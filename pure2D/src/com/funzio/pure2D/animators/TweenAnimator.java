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
            if (mLoop == Playable.LOOP_NONE && mElapsedTime >= mDuration) {
                // force end
                end();
            } else {
                float timeLine;
                if (mLoop == Playable.LOOP_REPEAT) {
                    timeLine = ((float) mElapsedTime % (float) mDuration) / mDuration;
                } else if (mLoop == Playable.LOOP_REVERSE) {
                    timeLine = ((float) mElapsedTime % (float) mDuration) / mDuration;
                    final int trips = (mElapsedTime / mDuration);
                    if (trips % 2 == 1) {
                        timeLine = 1 - timeLine;
                    }
                } else {
                    timeLine = (float) mElapsedTime / (float) mDuration;
                }

                mCurrentValue = mInterpolator == null ? timeLine : mInterpolator.getInterpolation(timeLine);
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

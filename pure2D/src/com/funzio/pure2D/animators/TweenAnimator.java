/**
 * 
 */
package com.funzio.pure2D.animators;

import android.view.animation.Interpolator;

import com.funzio.pure2D.LoopModes;

/**
 * @author long
 */
public class TweenAnimator extends BaseAnimator {
    protected int mDuration;
    protected Interpolator mInterpolator;

    protected float mLastValue = 0;
    protected float mCurrentValue = 0;
    protected float mCurrentUninterpolatedValue = 0;
    protected boolean mReversed = false;

    protected int mLoopMode = LoopModes.LOOP_NONE;
    protected int mLoopCount = -1; // forever
    protected int mTripCount = 0;

    public TweenAnimator() {
        super();
    }

    public TweenAnimator(final Interpolator interpolator) {
        super();

        mInterpolator = interpolator;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.BaseAnimator#startElapse(int)
     */
    @Override
    public void startElapse(final int elapsedTime) {
        super.startElapse(elapsedTime);

        // clear the values
        mLastValue = mCurrentValue = mCurrentUninterpolatedValue = (mReversed ? 1 : 0);
        mTripCount = 0;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.BaseAnimator#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (mDuration > 0 && super.update(deltaTime)) {
            float timeline = (float) mElapsedTime / (float) mDuration;
            final int trips = (int) Math.floor(timeline);
            if ((mLoopCount >= 0 && trips > mLoopCount) || (mLoopMode == LoopModes.LOOP_NONE && mElapsedTime >= mDuration)) {
                // end it
                end();
            } else {

                // detect loop
                if (trips > mTripCount) {
                    mTripCount = trips;
                    if (mLoopMode != LoopModes.LOOP_NONE) {
                        // callback
                        onLoop();
                    }
                }

                if (mLoopMode == LoopModes.LOOP_REPEAT) {
                    timeline = ((float) mElapsedTime % (float) mDuration) / mDuration;
                } else if (mLoopMode == LoopModes.LOOP_REVERSE) {
                    timeline = ((float) mElapsedTime % (float) mDuration) / mDuration;
                    if (trips % 2 == 1) {
                        // reverse
                        timeline = 1 - timeline;
                    }
                }

                // interpolate the value
                mLastValue = mCurrentValue;
                mCurrentUninterpolatedValue = timeline;
                mCurrentValue = (mInterpolator == null) ? timeline : mInterpolator.getInterpolation(timeline);
                // explicit reversed
                if (mReversed) {
                    mCurrentUninterpolatedValue = 1 - mCurrentUninterpolatedValue;
                    mCurrentValue = 1 - mCurrentValue;
                }
                // and update
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
        if (mLoopMode == LoopModes.LOOP_REVERSE && mLoopCount > 0) {
            mCurrentValue = mCurrentUninterpolatedValue = mLoopCount % 2 == 0 ? 1 : 0;
        } else {
            mCurrentValue = mCurrentUninterpolatedValue = 1;
        }

        // explicit reversed
        if (mReversed) {
            mCurrentUninterpolatedValue = 1 - mCurrentUninterpolatedValue;
            mCurrentValue = 1 - mCurrentValue;
        }

        // update
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

    public boolean isReversed() {
        return mReversed;
    }

    /**
     * To explicitly play in reversed direction
     * 
     * @param reversed
     */
    public void setReversed(final boolean reversed) {
        mReversed = reversed;
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
     * @return the looping mode
     */
    public int getLoop() {
        return mLoopMode;
    }

    /**
     * @param loop the looping mode to set
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

    protected void onLoop() {
        // TOOD
    }
}

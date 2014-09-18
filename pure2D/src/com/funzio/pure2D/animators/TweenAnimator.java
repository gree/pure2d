/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
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

    @Override
    public void startElapse(final int elapsedTime) {
        super.startElapse(elapsedTime);

        // clear the values
        mLastValue = mCurrentValue = mCurrentUninterpolatedValue = (mReversed ? 1 : 0);
        mTripCount = 0;
    }

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
                    if (trips % 2 != 0) {
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

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

import com.funzio.pure2D.Manipulatable;

/**
 * @author long
 */
public class BaseAnimator implements Animator {

    // input values
    protected Manipulatable mTarget;
    protected AnimatorListener mListener;
    protected Object mData; // extra data
    // no accumulating by default
    protected boolean mAccumulating = false;

    // time values
    protected int mStartDelay = 0;
    protected int mLifespan = 0; // <=0 ~ unlimited

    // state values
    protected int mElapsedTime = 0;
    protected int mLastDeltaTime = 0;
    protected boolean mRunning = false;
    protected boolean mLifeEnded = false;
    private boolean mStartDelayPassed = false;

    @Override
    public void reset(final Object... params) {
        stop();

        mTarget = null;
        mListener = null;
    }

    @Override
    public void setTarget(final Manipulatable target) {
        mTarget = target;
    }

    @Override
    public Manipulatable getTarget() {
        return mTarget;
    }

    public int getStartDelay() {
        return mStartDelay;
    }

    public void setStartDelay(final int startDelay) {
        mStartDelay = startDelay;
    }

    @Override
    public boolean update(final int deltaTime) {
        if (mRunning) {

            // life check
            if (mLifeEnded) {
                end();
                return false;
            }

            mLastDeltaTime = deltaTime;
            mElapsedTime += deltaTime;

            // check start delay
            if (!mStartDelayPassed) {
                if (mElapsedTime < mStartDelay) {
                    return false;
                } else {
                    // officially start now
                    mStartDelayPassed = true;
                    // reset elapsed time
                    mElapsedTime -= mStartDelay;
                    mLastDeltaTime = mElapsedTime;
                }
            }

            // has lifespan? check it
            if (mLifespan > 0 && mElapsedTime >= mLifespan) {
                // flag
                mLifeEnded = true;
            }

            return true;
        }

        return false;
    }

    @Override
    final public void start() {
        startElapse(0);
    }

    /**
     * Start with a specific elapsed time
     * 
     * @param elapsedTime
     */
    public void startElapse(final int elapsedTime) {
        if (mRunning) {
            // force end
            end();
        }

        mElapsedTime = elapsedTime;
        mLastDeltaTime = 0;
        mRunning = true;
        mStartDelayPassed = false;
        mLifeEnded = false;
    }

    @Override
    public void stop() {
        mRunning = false;
    }

    /**
     * Jump to some elapsed time
     * 
     * @param elapsedTimeDelta
     */
    public void elapse(final int elapsedTimeDelta) {
        mElapsedTime += elapsedTimeDelta;
    }

    /**
     * Jump to some elapsed time
     * 
     * @param elapsedTimeAt
     */
    public void setElapsedTime(final int elapsedTimeAt) {
        mElapsedTime = elapsedTimeAt;
    }

    public int getElapsedTime() {
        return mElapsedTime;
    }

    @Override
    public void end() {
        mRunning = false;

        if (mListener != null) {
            mListener.onAnimationEnd(this);
        }
    }

    public boolean isRunning() {
        return mRunning;
    }

    public int getLifespan() {
        return mLifespan;
    }

    public void setLifespan(final int lifespan) {
        mLifespan = lifespan;
    }

    public boolean isAccumulating() {
        return mAccumulating;
    }

    public void setAccumulating(final boolean accumulating) {
        mAccumulating = accumulating;
    }

    @Override
    public void setListener(final AnimatorListener listener) {
        mListener = listener;
    }

    @Override
    public AnimatorListener getListener() {
        return mListener;
    }

    @Override
    public void setData(final Object data) {
        mData = data;
    }

    @Override
    public Object getData() {
        return mData;
    }

}

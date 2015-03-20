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
package com.funzio.pure2D.animators;

/**
 * @author long
 */
public class VelocityAnimator extends BaseAnimator {

    protected float mAcceleration = 0;
    protected float mVelocity = 0;
    protected int mPendingElapse = 0;

    public VelocityAnimator() {
        super();
    }

    public VelocityAnimator(final float veloc, final float acceleration, final int duration) {
        this();

        mVelocity = veloc;
        mAcceleration = acceleration;
        mLifespan = duration;
        mPendingElapse = 0;
    }

    public void start(final float veloc, final float acceleration, final int duration) {
        mVelocity = veloc;
        mAcceleration = acceleration;
        mLifespan = duration;
        mPendingElapse = 0;

        super.start();
    }

    @Override
    public void stop() {
        mVelocity = 0;

        super.stop();
    }

    /**
     * @return the velocity
     */
    public float getVelocity() {
        return mVelocity;
    }

    public void setVelocity(final float velocity) {
        mVelocity = velocity;
    }

    /**
     * @return the Acceleration
     */
    public float getAcceleration() {
        return mAcceleration;
    }

    public void setAcceleration(final float acceleration) {
        mAcceleration = acceleration;
    }

    @Override
    public void elapse(final int elapsedTimeDelta) {
        mPendingElapse += elapsedTimeDelta;
    }

    /**
     * @return the Duration in ms
     */
    public int getDuration() {
        return mLifespan;
    }

    public int getRemainingTime() {
        return mLifespan - mElapsedTime;
    }

    @Override
    public boolean update(final int deltaTime) {
        if (mRunning) {

            if (mVelocity != 0 && ((mLifespan > 0 && mElapsedTime < mLifespan) || mLifespan <= 0)) {

                int myDeltaTime = deltaTime + mPendingElapse;
                mPendingElapse = 0;
                boolean isTimeUp = false;
                // if there is a time limit
                if (mLifespan > 0 && mElapsedTime + myDeltaTime > mLifespan) {
                    // uh oh, time's up!
                    myDeltaTime = mLifespan - mElapsedTime;
                    isTimeUp = true;
                }
                mElapsedTime += myDeltaTime;

                float deltaVeloc = mAcceleration * myDeltaTime;
                float newVeloc = mVelocity + deltaVeloc;

                // scroll now
                float delta = mVelocity * myDeltaTime + 0.5f * deltaVeloc * myDeltaTime; // Real physics, Newton's
                onUpdate(delta);

                // direction changed or time's up?
                if ((mLifespan <= 0 && newVeloc * mVelocity <= 0) || isTimeUp) {
                    mVelocity = 0;
                    // done! do callback
                    end();
                } else {
                    // update veloc
                    mVelocity = newVeloc;
                }
            } else if (mVelocity != 0) {
                mVelocity = 0;
                // done! do callback
                end();
            }

            return true;
        }

        return false;
    }

    protected void onUpdate(final float value) {
        if (mListener != null) {
            mListener.onAnimationUpdate(this, value);
        }
    }
}

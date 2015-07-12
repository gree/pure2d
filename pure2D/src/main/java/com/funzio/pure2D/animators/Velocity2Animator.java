/**
 * ****************************************************************************
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
 * ****************************************************************************
 */
package com.funzio.pure2D.animators;

import android.graphics.PointF;

/**
 * 2D Velocity Animator
 *
 * @author long
 */
public class Velocity2Animator extends BaseAnimator {

    protected PointF mAcceleration = new PointF();
    protected PointF mVelocity = new PointF();
    protected PointF mLastDelta = new PointF();
    protected int mPendingElapse = 0;
    protected float mMinSpeed = Float.NEGATIVE_INFINITY;
    protected float mMaxSpeed = Float.POSITIVE_INFINITY;

    protected boolean mAutoStop = true; // auto stop when velocity changes direction or zero

    public Velocity2Animator() {
        super();
    }

    public Velocity2Animator(final float velocX, final float velocY, final float accelerationX, final float accelerationY, final int duration) {
        this();

        mVelocity.x = velocX;
        mVelocity.y = velocY;
        mAcceleration.x = accelerationX;
        mAcceleration.y = accelerationY;

        mLifespan = duration;
        mPendingElapse = 0;
    }

    public void start(final float velocX, final float velocY, final float accelerationX, final float accelerationY, final int duration) {
        mVelocity.x = velocX;
        mVelocity.y = velocY;
        mAcceleration.x = accelerationX;
        mAcceleration.y = accelerationY;

        mLifespan = duration;
        mPendingElapse = 0;

        super.start();
    }

    @Override
    public void stop() {
        mVelocity.x = mVelocity.y = 0;

        super.stop();
    }

    /**
     * @return the velocity
     */
    public PointF getVelocity() {
        return mVelocity;
    }

    public void setVelocity(final PointF velocity) {
        mVelocity.x = velocity.x;
        mVelocity.y = velocity.y;
    }

    public void setVelocity(final float vx, final float vy) {
        mVelocity.x = vx;
        mVelocity.y = vy;
    }

    /**
     * @return the Acceleration
     */
    public PointF getAcceleration() {
        return mAcceleration;
    }

    public void setAcceleration(final PointF acceleration) {
        mAcceleration.x = acceleration.x;
        mAcceleration.y = acceleration.y;
    }

    public PointF getLastDelta() {
        return mLastDelta;
    }

    public void setAcceleration(final float ax, final float ay) {
        mAcceleration.x = ax;
        mAcceleration.y = ay;
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

    /**
     * Set the speed limit range
     *
     * @param min
     * @param max
     */
    public void setSpeedRange(final float min, final float max) {
        mMinSpeed = min;
        mMaxSpeed = max;
    }

    @Override
    public boolean update(final int deltaTime) {
        if (mRunning) {

            if ((!mAutoStop || mVelocity.x != 0 || mVelocity.y != 0) && ((mLifespan > 0 && mElapsedTime < mLifespan) || mLifespan <= 0)) {

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

                float deltaVelocX = mAcceleration.x * myDeltaTime;
                float newVelocX = mVelocity.x + deltaVelocX;
                float deltaVelocY = mAcceleration.y * myDeltaTime;
                float newVelocY = mVelocity.y + deltaVelocY;
                final float newSpeedSquared = newVelocX * newVelocX + newVelocY * newVelocY;
                // speed range check
                if (newSpeedSquared < mMinSpeed * mMinSpeed) {
                    /*final float angle = (float) Math.atan2(newVelocY, newVelocX);
                    newVelocX = mMinSpeed * (float) Math.cos(angle);
                    newVelocY = mMinSpeed * (float) Math.sin(angle);*/
                    final float f = mMinSpeed / (float) Math.sqrt(newSpeedSquared);
                    newVelocX *= f;
                    newVelocY *= f;
                } else if (newSpeedSquared > mMaxSpeed * mMaxSpeed) {
                    //final float angle = (float) Math.atan2(newVelocY, newVelocX);
                    /*newVelocX = mMaxSpeed * (float) Math.cos(angle);
                    newVelocY = mMaxSpeed * (float) Math.sin(angle);*/
                    final float f = mMaxSpeed / (float) Math.sqrt(newSpeedSquared);
                    newVelocX *= f;
                    newVelocY *= f;
                }

                // update veloc
                mVelocity.x = newVelocX;
                mVelocity.y = newVelocY;

                // Real physics, Newton's
                mLastDelta.x = mVelocity.x * myDeltaTime + 0.5f * deltaVelocX * myDeltaTime;
                mLastDelta.y = mVelocity.y * myDeltaTime + 0.5f * deltaVelocY * myDeltaTime;
                onUpdate(deltaTime);

                // direction changed or time's up?
                if ((mAutoStop && mLifespan <= 0 && (newVelocX * mVelocity.x <= 0 && newVelocY * mVelocity.y <= 0)) || isTimeUp) {
                    mVelocity.x = mVelocity.y = 0;
                    // done! do callback
                    end();
                }
            } else if (mVelocity.x != 0 || mVelocity.y != 0) {
                mVelocity.x = mVelocity.y = 0;
                // done! do callback
                end();
            }

            return true;
        }

        return false;
    }

    public boolean isAutoStop() {
        return mAutoStop;
    }

    public Velocity2Animator setAutoStop(final boolean autoStop) {
        mAutoStop = autoStop;
        return this;
    }

    protected void onUpdate(final float deltaTime) {
        // move target
        if (mTarget != null && (mLastDelta.x != 0 || mLastDelta.y != 0)) {
            mTarget.move(mLastDelta.x, mLastDelta.y);
        }

        if (mListener != null) {
            mListener.onAnimationUpdate(this, deltaTime);
        }
    }
}

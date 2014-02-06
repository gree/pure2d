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
 * All formulas in this are based on: http://en.wikipedia.org/wiki/Trajectory_of_a_projectile
 */
package com.funzio.pure2D.animators;

import android.graphics.PointF;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class TrajectoryAnimator extends BaseAnimator {
    public static float TIME_FACTOR = 50;
    public static float DEFAULT_GRAVITY = 10;

    protected float mGravity = DEFAULT_GRAVITY;
    protected float mSrcX = 0;
    protected float mSrcY = 0;

    protected float mGround = 0;
    protected float mAngle = 0;
    protected float mSin = 0;
    protected float mCos = 1;
    protected float mVelocity;
    protected float mDistance;
    protected float mDuration;
    protected PointF mCurrentVelocity = new PointF();

    // rotation
    protected boolean mTargetAngleFixed = true;
    protected float mTargetAngleOffset = 0;

    // axis system
    protected int mAxisSystem = Scene.AXIS_BOTTOM_LEFT;

    public TrajectoryAnimator() {
        super();
    }

    public TrajectoryAnimator(final float ground) {
        super();

        mGround = ground;
    }

    public boolean isTargetAngleFixed() {
        return mTargetAngleFixed;
    }

    public void setTargetAngleFixed(final boolean fixed) {
        mTargetAngleFixed = fixed;
    }

    public float getTargetAngleOffset() {
        return mTargetAngleOffset;
    }

    public void setTargetAngleOffset(final float offsetDegree) {
        mTargetAngleOffset = offsetDegree;
    }

    public void setValues(final float srcX, final float srcY, final float velocity, final float angle) {
        mSrcX = srcX;
        mSrcY = srcY;

        mVelocity = velocity;
        if (mAngle != angle) {
            mAngle = angle;
            mSin = (float) Math.sin(mAngle);
            mCos = (float) Math.cos(mAngle);
        }

        // pre-cals
        final float absGravity = Math.abs(mGravity);

        if (mAxisSystem == Scene.AXIS_BOTTOM_LEFT) {
            final float vcos = mVelocity * mCos;
            final float vsin = mVelocity * mSin;

            mDistance = (vcos / absGravity) * (vsin + (float) Math.sqrt(vsin * vsin + 2 * absGravity * (mSrcY - mGround)));
            mDuration = TIME_FACTOR * mDistance / (vcos == 0 ? 1 : vcos);
        } else {
            // Calculate distance and duration by flipping coordinate system.
            final float vcos = mVelocity * mCos;
            final float vsin = mVelocity * -mSin;

            mDistance = (vcos / absGravity) * (vsin + (float) Math.sqrt(vsin * vsin + 2 * absGravity * (mGround - mSrcY)));
            mDuration = TIME_FACTOR * mDistance / (vcos == 0 ? 1 : vcos);
        }
    }

    public void setValues(final float velocity, final float angle) {
        setValues(0, 0, velocity, angle);
    }

    public void start(final float srcX, final float srcY, final float velocity, final float angle) {
        setValues(srcX, srcY, velocity, angle);

        start();
    }

    public void start(final float velocity, final float angle) {
        if (mTarget != null) {
            final PointF position = mTarget.getPosition();
            setValues(position.x, position.y, velocity, angle);
        } else {
            setValues(0, 0, velocity, angle);
        }

        start();
    }

    public void start(final float srcX, final float srcY, final float velocity, final float dstX, final float dstY) {
        mSrcX = srcX;
        mSrcY = srcY;
        mVelocity = velocity;

        final float deltaX = dstX - srcX;
        final float deltaY = dstY - srcY;

        final float v2 = mVelocity * mVelocity;
        // find the angle to hit the destination
        mAngle = (float) Math.atan((v2 + (float) Math.sqrt(v2 * v2 - mGravity * (mGravity * deltaX * deltaX + 2 * deltaY * v2))) / (mGravity * deltaX));
        mSin = (float) Math.sin(mAngle);
        mCos = (float) Math.cos(mAngle);

        final float vcos = mVelocity * mCos;
        // final float vsin = mVelocity * mSin;
        mDistance = deltaX;// (vcos / GRAVITY) * (vsin + (float)Math.sqrt(vsin * vsin + 2 * GRAVITY * mSrcY));
        mDuration = TIME_FACTOR * mDistance / (vcos == 0 ? 1 : vcos);

        start();
    }

    public void start(final float velocity, final float dstX, final float dstY) {
        if (mTarget != null) {
            final PointF position = mTarget.getPosition();
            start(position.x, position.y, velocity, dstX, dstY);
        }
    }

    @Override
    public boolean update(final int deltaTime) {
        if (super.update(deltaTime)) {

            final float t = Math.min(mElapsedTime, mDuration) / TIME_FACTOR;
            final float x = mSrcX + mVelocity * t * mCos;
            final float y = mSrcY + mVelocity * t * mSin - 0.5f * mGravity * t * t;

            if (mTarget != null) {

                final PointF currentPos = mTarget.getPosition();
                final float deltaX = x - currentPos.x;
                final float deltaY = y - currentPos.y;

                mCurrentVelocity.x = deltaX / deltaTime;
                mCurrentVelocity.y = deltaY / deltaTime;

                // rotation
                if (!mTargetAngleFixed) {
                    mTarget.setRotation(mTargetAngleOffset + (float) (Math.atan2(mCurrentVelocity.y, mCurrentVelocity.x) * Pure2DUtils.RADIAN_TO_DEGREE));
                }

                // position
                if (mAccumulating) {
                    mTarget.move(deltaX, deltaY);
                } else {
                    mTarget.setPosition(x, y);
                }
            }

            // callback
            if (mListener != null) {
                mListener.onAnimationUpdate(this, t);
            }

            // time's up?
            if (mElapsedTime >= mDuration) {
                // force end
                end();
            }

            return true;
        }

        return false;
    }

    public float getGravity() {
        return mGravity;
    }

    public void setGravity(final float gravity) {
        mGravity = gravity;
    }

    public float getDuration() {
        return mDuration;
    }

    public float getGround() {
        return mGround;
    }

    public void setGround(final float ground) {
        mGround = ground;
    }

    public float getVelocity() {
        return mVelocity;
    }

    public PointF getCurrentVelocity() {
        return mCurrentVelocity;
    }

    public void setCurrentVelocity(final PointF currentVelocity) {
        mCurrentVelocity = currentVelocity;
    }

    public void setAxisSystem(final int axisSystem) {
        mAxisSystem = axisSystem;
    }
}

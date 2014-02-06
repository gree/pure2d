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

import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class RotateAnimator extends TweenAnimator {
    public static final int PIVOT_CLEAR = -9999;

    protected float mSrc = 0;
    protected float mDst = 0;
    protected float mDelta = 0;

    protected float mPivotX = PIVOT_CLEAR;
    protected float mPivotY = PIVOT_CLEAR;
    protected float mLastX = 0;
    protected float mLastY = 0;
    protected float mRadius = 0;

    public RotateAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.BaseAnimator#reset(java.lang.Object[])
     */
    @Override
    public void reset(final Object... params) {
        super.reset(params);

        clearPivot();
    }

    public void setValues(final float src, final float dst) {
        mSrc = src;
        mDst = dst;
        mDelta = mDst - mSrc;
    }

    public void setDelta(final float delta) {
        mDelta = delta;
    }

    /**
     * The center of the rotation.
     * 
     * @param x
     * @param y
     * @see #clearPivot()
     */
    public void setPivot(final float x, final float y, final float radius) {
        mPivotX = x;
        mPivotY = y;
        mRadius = radius;

        // // find the radius
        // if (hasPivot() && mTarget != null) {
        // updateRadiusToTarget();
        // } else {
        // mRadius = 0;
        // mLastX = mLastY = 0;
        // }
    }

    /**
     * Unset the pivot.
     */
    public void clearPivot() {
        mPivotX = mPivotY = PIVOT_CLEAR;
        mRadius = 0;
    }

    public boolean hasPivot() {
        return mPivotX != PIVOT_CLEAR && mPivotY != PIVOT_CLEAR;
    }

    // /*
    // * (non-Javadoc)
    // * @see com.funzio.pure2D.animators.BaseAnimator#setTarget(com.funzio.pure2D.Manipulatable)
    // */
    // @Override
    // public void setTarget(final Manipulatable target) {
    // super.setTarget(target);
    //
    // // find the radius
    // if (hasPivot() && mTarget != null) {
    // updateRadiusToTarget();
    // }
    // }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.TweenAnimator#startElapse(int)
     */
    @Override
    public void startElapse(final int elapsedTime) {
        super.startElapse(elapsedTime);

        // if (mTarget != null) {
        // final PointF pt = mTarget.getPosition();
        // mLastX = pt.x;
        // mLastY = pt.y;
        // }
        mLastX = mLastY = 0;
    }

    public void start(final float src, final float dst) {
        mSrc = src;
        mDst = dst;
        mDelta = mDst - mSrc;

        start();
    }

    public void start(final float dest) {
        if (mTarget != null) {
            start(mTarget.getRotation(), dest);

            // // find the radius
            // if (hasPivot()) {
            // updateRadiusToTarget();
            // }
        }
    }

    // private void updateRadiusToTarget() {
    // final PointF pt = mTarget.getPosition();
    // mRadius = (float) Math.sqrt((pt.x - mPivotX) * (pt.x - mPivotX) + (pt.y - mPivotY) + (pt.y - mPivotY));
    // mLastX = pt.x;
    // mLastY = pt.y;
    // }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {
            if (mAccumulating) {
                mTarget.rotate((value - mLastValue) * mDelta);
            } else {
                mTarget.setRotation(mSrc + value * mDelta);
            }

            // also move the target when pivot is set
            if (mRadius != 0) {
                final float radian = (mSrc + value * mDelta) * Pure2DUtils.DEGREE_TO_RADIAN;
                final float newX = mPivotX + mRadius * (float) Math.cos(radian);
                final float newY = mPivotY + mRadius * (float) Math.sin(radian);

                if (mAccumulating) {
                    mTarget.move(newX - mLastX, newY - mLastY);
                    // keep the values
                    mLastX = newX;
                    mLastY = newY;
                } else {
                    mTarget.setPosition(newX, newY);
                }
            }
        }

        super.onUpdate(value);
    }

    public float getDelta() {
        return mDelta;
    }
}

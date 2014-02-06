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

import android.graphics.PointF;
import android.view.animation.Interpolator;

import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class WhirlAnimator extends TweenAnimator {
    public static final float DEFAULT_ANGLE = (float) Math.PI * 2;
    // default values
    public static final int DEFAULT_RADIUS = 100;
    public static final float DEFAULT_CIRCLE_RATIO = 1;

    protected float mSrcX = 0;
    protected float mSrcY = 0;

    private float mRadius1;
    private float mRadius2;
    private float mRadianAngle1;
    private float mRadianAngle2;
    private float mCircleRatio;
    private Interpolator mCircleInterpolator;

    private float mRadiusLength;
    private float mRadianLength;
    private float mLastX;
    private float mLastY;
    private float mCircleMultiplier;

    public WhirlAnimator(final Interpolator interpolator) {
        super(interpolator);

        reset();
    }

    @Override
    public void reset(final Object... params) {
        super.reset(params);

        // just set some default params
        mRadius1 = 0;
        mRadius2 = DEFAULT_RADIUS;
        mRadianAngle1 = 0;
        mRadianAngle2 = DEFAULT_ANGLE;
        mCircleRatio = DEFAULT_CIRCLE_RATIO;
        mCircleMultiplier = 1;

        // find implicit radian length
        mRadiusLength = mRadius2 - mRadius1;
        mRadianLength = (mRadianAngle2 - mRadianAngle1) * mCircleMultiplier;

        mLastX = mLastY = 0;
    }

    public Interpolator getCircleInterpolator() {
        return mCircleInterpolator;
    }

    public void setCircleInterpolator(final Interpolator circleInterpolator) {
        mCircleInterpolator = circleInterpolator;
    }

    public float getCircleRatio() {
        return mCircleRatio;
    }

    public void setCircleRatio(final float circleRatio) {
        mCircleRatio = circleRatio;
    }

    public float getCircleMultiplier() {
        return mCircleMultiplier;
    }

    public void setCircleMultiplier(final float circleMultiplier) {
        mCircleMultiplier = circleMultiplier;

        // find implicit radian length
        mRadianLength = (mRadianAngle2 - mRadianAngle1) * mCircleMultiplier;
    }

    public void setValues(final float srcX, final float srcY, final float radius1, final float radius2, final float radianAngle1, final float radianAngle2) {
        mSrcX = srcX;
        mSrcY = srcY;

        mRadius1 = radius1;
        mRadius2 = radius2;
        mRadianAngle1 = radianAngle1;
        mRadianAngle2 = radianAngle2;

        // find implicit radian length
        mRadiusLength = mRadius2 - mRadius1;
        mRadianLength = mRadianAngle2 - mRadianAngle1;
    }

    public void setValues(final float srcX, final float srcY, final float radius1, final float radius2, final int degreeAngle1, final int degreeAngle2) {
        setValues(srcX, srcY, radius1, radius2, degreeAngle1 * Pure2DUtils.DEGREE_TO_RADIAN, degreeAngle2 * Pure2DUtils.DEGREE_TO_RADIAN);
    }

    public void setValues(final float radius1, final float radius2, final float radianAngle1, final float radianAngle2) {
        final PointF position = mTarget != null ? mTarget.getPosition() : null;
        setValues(position != null ? position.x : 0, position != null ? position.y : 0, radius1, radius2, radianAngle1, radianAngle2);
    }

    public void setValues(final float radius1, final float radius2, final int degreeAngle1, final int degreeAngle2) {
        final PointF position = mTarget != null ? mTarget.getPosition() : null;
        setValues(position != null ? position.x : 0, position != null ? position.y : 0, radius1, radius2, degreeAngle1, degreeAngle2);
    }

    public void start(final float srcX, final float srcY, final float radius1, final float radius2, final float radianAngle1, final float radianAngle2) {
        setValues(srcX, srcY, radius1, radius2, radianAngle1, radianAngle2);

        start();
    }

    public void start(final float srcX, final float srcY, final float radius1, final float radius2, final int degreeAngle1, final int degreeAngle2) {
        setValues(srcX, srcY, radius1, radius2, degreeAngle1, degreeAngle2);

        start();
    }

    public void start(final float radius1, final float radius2, final float radianAngle1, final float radianAngle2) {
        setValues(radius1, radius2, radianAngle1, radianAngle2);

        start();
    }

    public void start(final float radius1, final float radius2, final int degreeAngle1, final int degreeAngle2) {
        setValues(radius1, radius2, degreeAngle1, degreeAngle2);

        start();
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {

            final float angle = mRadianAngle1 + mRadianLength * (mCircleInterpolator == null ? value : mCircleInterpolator.getInterpolation(mCurrentUninterpolatedValue));
            final float radius = mRadius1 + mRadiusLength * value;

            final float dx = radius * mCircleRatio * (float) Math.cos(angle);
            final float dy = radius * (float) Math.sin(angle);

            if (mAccumulating) {
                mTarget.move(dx - mLastX, dy - mLastY);
            } else {
                mTarget.setPosition(mSrcX + dx, mSrcY + dy);
            }
            mLastX = dx;
            mLastY = dy;
        }

        super.onUpdate(value);
    }
}

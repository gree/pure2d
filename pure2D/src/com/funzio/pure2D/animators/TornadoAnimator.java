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

import com.funzio.pure2D.DisplayObject;

/**
 * @author long
 */
public class TornadoAnimator extends TweenAnimator {
    // default values
    public static final int DEFAULT_CIRLCLE_NUM = 10;
    public static final float DEFAULT_CIRCLE_RATIO = 0.25f;
    public static final int DEFAULT_CIRCLE_RADIUS = 100;

    protected float mSrcX = 0;
    protected float mSrcY = 0;
    protected PointF mDelta = new PointF();

    private float mCircleRadius;
    private float mCircleRatio;
    private float mCircleNum;
    private float mCircleMultiplier;
    private Interpolator mCircleInterpolator;

    private float mRadianLength;
    private float mLastX;
    private float mLastY;
    private float mLastZ;
    private float mLengthX;
    private float mLengthY;
    private float mSinAngle;
    private float mCosAngle;

    private boolean mZEnabled = false;

    public TornadoAnimator(final Interpolator interpolator) {
        super(interpolator);

        reset();
    }

    @Override
    public void reset(final Object... params) {
        super.reset(params);

        // just set some default params
        mCircleRadius = DEFAULT_CIRCLE_RADIUS;
        mCircleRatio = DEFAULT_CIRCLE_RATIO;
        mCircleNum = DEFAULT_CIRLCLE_NUM;
        mCircleMultiplier = 1;

        // find implicit radian length
        mRadianLength = ((float) Math.PI * (mCircleNum * 2)) * mCircleMultiplier;

        mLastX = mLastY = mLastZ = 0;
    }

    public Interpolator getCircleInterpolator() {
        return mCircleInterpolator;
    }

    public void setCircleInterpolator(final Interpolator circleInterpolator) {
        mCircleInterpolator = circleInterpolator;
    }

    public float getCircleMultiplier() {
        return mCircleMultiplier;
    }

    public void setCircleMultiplier(final float circleMultiplier) {
        mCircleMultiplier = circleMultiplier;

        // find implicit radian length
        mRadianLength = ((float) Math.PI * (mCircleNum * 2)) * mCircleMultiplier;
    }

    public void setValues(final float srcX, final float srcY, final float dstX, final float dstY) {
        mSrcX = srcX;
        mSrcY = srcY;

        // apply the delta
        setDelta(dstX - srcX, dstY - srcY);
    }

    public void setCircles(final float circleRadius, final float circleNum, final float circleRatio, final Interpolator circleInterpolator) {
        mCircleRadius = circleRadius != 0 ? circleRadius : DEFAULT_CIRCLE_RADIUS;
        mCircleNum = circleNum != 0 ? circleNum : DEFAULT_CIRLCLE_NUM;
        mCircleRatio = circleRatio != 0 ? circleRatio : DEFAULT_CIRCLE_RATIO;
        mCircleInterpolator = circleInterpolator;

        // find implicit radian length
        mRadianLength = ((float) Math.PI * (mCircleNum * 2)) * mCircleMultiplier;

        // travel length for the center
        mLengthX = mDelta.x - mCircleRadius * mCircleRatio * mCosAngle;
        mLengthY = mDelta.y - mCircleRadius * mCircleRatio * mSinAngle;
    }

    public void setDelta(final float dx, final float dy) {
        mDelta.x = dx;
        mDelta.y = dy;

        // pre-calculate
        final float angle = (float) Math.atan2(mDelta.y, mDelta.x);
        mSinAngle = (float) Math.sin(angle);
        mCosAngle = (float) Math.cos(angle);
        // travel length for the center
        mLengthX = mDelta.x - mCircleRadius * mCircleRatio * mCosAngle;
        mLengthY = mDelta.y - mCircleRadius * mCircleRatio * mSinAngle;
    }

    public void start(final float srcX, final float srcY, final float dstX, final float dstY) {
        setValues(srcX, srcY, dstX, dstY);

        start();
    }

    public void start(final float destX, final float destY) {
        if (mTarget != null) {
            final PointF position = mTarget.getPosition();
            start(position.x, position.y, destX, destY);
        }
    }

    public float getCircleRadius() {
        return mCircleRadius;
    }

    public float getCircleNum() {
        return mCircleNum;
    }

    public float getCircleRatio() {
        return mCircleRatio;
    }

    public PointF getDelta() {
        return mDelta;
    }

    public void setZEnabled(final boolean value) {
        mZEnabled = value;
    }

    public boolean getZEnabled() {
        return mZEnabled;
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {

            final float centerX = value * mLengthX;
            final float centerY = value * mLengthY;
            final float angle = value * mRadianLength;
            final float radius = mCircleRadius * (mCircleInterpolator == null ? value : mCircleInterpolator.getInterpolation(mCurrentUninterpolatedValue));

            final float cos = (float) Math.cos(angle);
            final float dx = radius * mCircleRatio * cos;
            final float dy = radius * (float) Math.sin(angle);
            // rotate to the main direction
            final float newX = centerX + dx * mCosAngle - dy * mSinAngle;
            final float newY = centerY + dx * mSinAngle + dy * mCosAngle;

            if (mAccumulating) {
                mTarget.move(newX - mLastX, newY - mLastY);
            } else {
                mTarget.setPosition(mSrcX + newX, mSrcY + newY);
            }

            mLastX = newX;
            mLastY = newY;

            // z-enabled?
            if (mZEnabled && mTarget instanceof DisplayObject) {
                final float newZ = -radius * cos;

                if (mAccumulating) {
                    ((DisplayObject) mTarget).setZ(((DisplayObject) mTarget).getZ() + newZ - mLastZ);
                } else {
                    ((DisplayObject) mTarget).setZ(newZ);
                }

                mLastZ = newZ;
            }
        }

        super.onUpdate(value);
    }
}

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
public class SinWaveAnimator extends TweenAnimator {
    public static final int DEFAULT_RADIUS = 10;
    public static final int DEFAULT_WAVE_NUM = 2;

    protected float mSrcX = 0;
    protected float mSrcY = 0;
    protected PointF mDelta = new PointF();
    protected float mDeltaLength = 0;

    protected int mWaveNum = DEFAULT_WAVE_NUM;
    protected float mWaveRadius1 = DEFAULT_RADIUS;
    protected float mWaveRadius2 = DEFAULT_RADIUS;
    protected Interpolator mRadiusInterpolator;
    protected float mAngle;
    protected float mSinAngle;
    protected float mCosAngle;

    private float mLastX;
    private float mLastY;
    private float mRadiusLength;

    public SinWaveAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final float srcX, final float srcY, final float dstX, final float dstY) {
        mSrcX = srcX;
        mSrcY = srcY;

        mDelta.x = dstX - srcX;
        mDelta.y = dstY - srcY;
        mDeltaLength = (float) Math.sqrt(mDelta.x * mDelta.x + mDelta.y * mDelta.y);

        // pre-cals
        mAngle = (float) Math.atan2(mDelta.y, mDelta.x);
        mSinAngle = (float) Math.sin(mAngle);
        mCosAngle = (float) Math.cos(mAngle);
    }

    public void setDelta(final float dx, final float dy) {
        mDelta.x = dx;
        mDelta.y = dy;
        mDeltaLength = (float) Math.sqrt(mDelta.x * mDelta.x + mDelta.y * mDelta.y);

        // pre-cals
        mAngle = (float) Math.atan2(mDelta.y, mDelta.x);
        mSinAngle = (float) Math.sin(mAngle);
        mCosAngle = (float) Math.cos(mAngle);
    }

    public void setDistance(final float distance, final float radianAngle) {
        setDelta(distance * (float) Math.cos(radianAngle), distance * (float) Math.sin(radianAngle));
    }

    public void setDistance(final float distance, final int degreeAngle) {
        final float radianAngle = degreeAngle * Pure2DUtils.DEGREE_TO_RADIAN;
        setDelta(distance * (float) Math.cos(radianAngle), distance * (float) Math.sin(radianAngle));
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

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.TweenAnimator#startElapse(int)
     */
    @Override
    public void startElapse(final int elapsedTime) {
        super.startElapse(elapsedTime);

        mLastX = mLastY = 0;
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {
            final float currentAngle = value * mWaveNum * (float) Math.PI;
            final float radius = mWaveRadius1 + mRadiusLength * (mRadiusInterpolator != null ? mRadiusInterpolator.getInterpolation(mCurrentUninterpolatedValue) : value);
            final float dx = value * mDeltaLength;
            final float dy = (float) Math.sin(currentAngle) * radius;
            final float newX = dx * mCosAngle - dy * mSinAngle;
            final float newY = dx * mSinAngle + dy * mCosAngle;

            if (mAccumulating) {
                mTarget.move(newX - mLastX, newY - mLastY);
            } else {
                mTarget.setPosition(mSrcX + newX, mSrcY + newY);
            }

            mLastX = newX;
            mLastY = newY;
        }

        super.onUpdate(value);
    }

    public PointF getDelta() {
        return mDelta;
    }

    public int getWaveNum() {
        return mWaveNum;
    }

    public void setWaveNum(final int waveNum) {
        mWaveNum = waveNum;
    }

    public float getWaveRadius1() {
        return mWaveRadius1;
    }

    public float getWaveRadius2() {
        return mWaveRadius2;
    }

    public void setWaveRadius(final float waveRadius) {
        mWaveRadius1 = mWaveRadius2 = waveRadius;
        mRadiusLength = 0;
    }

    public void setWaveRadius(final float waveRadius1, final float waveRadius2) {
        mWaveRadius1 = waveRadius1;
        mWaveRadius2 = waveRadius2;
        mRadiusLength = waveRadius2 - waveRadius1;
    }

    public Interpolator getRadiusInterpolator() {
        return mRadiusInterpolator;
    }

    public void setRadiusInterpolator(final Interpolator radiusInterpolator) {
        mRadiusInterpolator = radiusInterpolator;
    }
}

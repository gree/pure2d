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

import java.util.Random;

import android.graphics.PointF;
import android.view.animation.Interpolator;

/**
 * Usage:
 *      Camera cam = ...; 
 *      ShakeAnimator anim = new ShakeAnimator(null);
 *      cam.addManipulator(anim);
 *      anim.start(cam.getPosition(), 100, 3600, 750);
 */
public class ShakeAnimator extends TweenAnimator {

    private final Random mRandom = new Random();
    private float mAngleIncrement;
    private float mInitialAngle = 0f;
    private float mRadius;
    private float mSrcX;
    private float mSrcY;

    public ShakeAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {
            final float currentAngle = mInitialAngle + value * mAngleIncrement;
            mTarget.moveTo(mSrcX + mRadius * (1 - value) * (float) Math.cos(currentAngle), mSrcY + mRadius
                    * (1 - value) * (float) Math.sin(currentAngle));
        }
        super.onUpdate(value);
    }

    private void setValues(final float radius, final float degree, final int duration) {
        if (mTarget != null) {
            mSrcX = mTarget.getPosition().x;
            mSrcY = mTarget.getPosition().y;
        } else {
            mSrcX = mSrcY = 0;
        }
        mRadius = radius;
        mInitialAngle = mRandom.nextFloat() * 360f;
        mAngleIncrement = degree / 180f * (float) Math.PI;
        setDuration(duration);
    }

    public void start(final float radius, final float degree, final int duration) {
        setValues(radius, degree, duration);
        start();
    }

}

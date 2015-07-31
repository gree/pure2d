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
public class UnstableMoveAnimator extends TweenAnimator {
    protected float mSrcX = 0;
    protected float mSrcY = 0;
    protected PointF mDelta = new PointF();

    protected float mWindX1;
    protected float mWindX2;
    protected float mWindY1;
    protected float mWindY2;
    protected int mSegmentDuration;
    protected float mModX = 0;
    protected float mModY = 0;
    protected float mModStartX = 0;
    protected float mModStartY = 0;
    protected float mSegmentElapsedTime = 0;
    protected int mCurrentSegment = -1;
    protected float mCurrentWindX = 0;
    protected float mCurrentWindY = 0;

    public UnstableMoveAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final float srcX, final float srcY, final float dstX, final float dstY) {
        mSrcX = srcX;
        mSrcY = srcY;

        mDelta.x = dstX - srcX;
        mDelta.y = dstY - srcY;
    }

    public void setDelta(final float dx, final float dy) {
        mDelta.x = dx;
        mDelta.y = dy;
    }

    public void setDistance(final float distance, final float radianAngle) {
        setDelta(distance * (float) Math.cos(radianAngle), distance * (float) Math.sin(radianAngle));
    }

    public void setDistance(final float distance, final int degreeAngle) {
        final float radianAngle = degreeAngle * Pure2DUtils.DEGREE_TO_RADIAN;
        setDelta(distance * (float) Math.cos(radianAngle), distance * (float) Math.sin(radianAngle));
    }

    public void setWindRange(final float x1, final float x2, final float y1, final float y2) {
        mWindX1 = x1;
        mWindX2 = x2;
        mWindY1 = y1;
        mWindY2 = y2;
    }

    public void setSegmentDuration(final int segmentDuration) {
        mSegmentDuration = segmentDuration;
    }

    public void start(final float srcX, final float srcY, final float dstX, final float dstY) {
        mSrcX = srcX;
        mSrcY = srcY;

        mDelta.x = dstX - srcX;
        mDelta.y = dstY - srcY;

        start();
    }

    public void start(final float destX, final float destY) {
        if (mTarget != null) {
            final PointF position = mTarget.getPosition();
            start(position.x, position.y, destX, destY);
        }
    }

    @Override
    public void startElapse(final int elapsedTime) {
        super.startElapse(elapsedTime);

        mCurrentSegment = -1;
        mModStartX = mModStartY = 0;
        mModX = mModY = 0;
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {

            final float lastModX = mModX;
            final float lastModY = mModY;
            if (mSegmentDuration > 0 && mSegmentDuration < mDuration) {
                int index = (int) Math.floor(mElapsedTime / mSegmentDuration);
                if (index != mCurrentSegment) {
                    mCurrentSegment = index;
                    if (index % 2 != 0) {
                        mModStartX = mModX;
                        mModStartY = mModY;
                        // go backward
                        mCurrentWindX = -mCurrentWindX;
                        mCurrentWindY = -mCurrentWindY;
                    } else {
                        // restart
                        mModStartX = mModStartY = 0;
                        mCurrentWindX = mWindX1 + (float) Math.random() * (mWindX2 - mWindX1);
                        mCurrentWindY = mWindY1 + (float) Math.random() * (mWindY2 - mWindY1);
                    }
                    // start time
                    mSegmentElapsedTime = 0;
                } else {
                    mSegmentElapsedTime += mLastDeltaTime;
                }
                float f = Math.min(1, mSegmentElapsedTime / mSegmentDuration);
                if (mInterpolator != null) {
                    f = mInterpolator.getInterpolation(f);
                }
                mModX = mModStartX + mCurrentWindX * f;
                mModY = mModStartY + mCurrentWindY * f;

                // float newVelocX = value * mDelta.x + mModX;
                // float newVelocY = value * mDelta.y + mModY;
                // mVelocX += (newVelocX - mVelocX) / 2;
                // mVelocY += (newVelocY - mVelocY) / 2;
            }

            if (mAccumulating) {
                mTarget.move((value - mLastValue) * mDelta.x - lastModX + mModX, (value - mLastValue) * mDelta.y - lastModY + mModY);
            } else {
                mTarget.setPosition(mSrcX + value * mDelta.x + mModX, mSrcY + value * mDelta.y + mModY);
            }
        }

        super.onUpdate(value);
    }

    public PointF getDelta() {
        return mDelta;
    }
}

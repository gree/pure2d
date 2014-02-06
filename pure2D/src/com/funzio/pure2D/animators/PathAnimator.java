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

/**
 * @author long
 */
public class PathAnimator extends TweenAnimator {
    protected PointF[] mPoints;
    protected float[] mDx, mDy;
    protected float[] mSegments;
    protected float mTotalLength;

    // current velocity
    protected final PointF mVelocity = new PointF();
    protected int mCurrentSegment = 0;
    protected boolean mSnapEnabled = false;
    protected int mNumSegments = 0;

    public PathAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final PointF... points) {
        setValues(points.length, points);
    }

    public void setValues(final int limit, final PointF... points) {
        mPoints = points;

        mNumSegments = Math.min(limit, points.length) - 1;
        // safety check
        if (mNumSegments < 1) {
            return;
        }

        // reuse arrays when possible
        if (mSegments == null || mNumSegments > mSegments.length) {
            mDx = new float[mNumSegments];
            mDy = new float[mNumSegments];
            mSegments = new float[mNumSegments];
        }

        float dx, dy;
        mTotalLength = 0;
        for (int i = 0; i < mNumSegments; i++) {
            dx = points[i + 1].x - points[i].x;
            dy = points[i + 1].y - points[i].y;
            mDx[i] = dx;
            mDy[i] = dy;
            mSegments[i] = (float) Math.sqrt(dx * dx + dy * dy);
            mTotalLength += mSegments[i];
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.TweenAnimator#startElapse(int)
     */
    @Override
    public void startElapse(final int elapsedTime) {
        super.startElapse(elapsedTime);

        mCurrentSegment = 0;
    }

    public void start(final PointF... points) {
        setValues(points);

        start();
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null && mSegments != null) {

            if (mSnapEnabled) {
                final int newSegment = getSegment(value);
                // segment change?
                if (newSegment != mCurrentSegment) {
                    if (newSegment > mCurrentSegment) {
                        mTarget.setPosition(mPoints[newSegment]);
                    } else {
                        mTarget.setPosition(mPoints[mCurrentSegment]);
                    }

                    // set
                    mCurrentSegment = newSegment;

                    super.onUpdate(value);

                    return;
                }
            }

            final float valueLen = value * mTotalLength;
            float len = 0;

            // find the right segment
            float segment;
            for (int i = 0; i < mNumSegments; i++) {
                segment = mSegments[i];

                // bingo?
                if (len + segment >= valueLen) {
                    mCurrentSegment = i;
                    final float deltaScale = (valueLen - len) / segment;
                    PointF currentPos = mTarget.getPosition();
                    float lastX = currentPos.x;
                    float lastY = currentPos.y;

                    // new position
                    mTarget.setPosition(mPoints[i].x + deltaScale * mDx[i], mPoints[i].y + deltaScale * mDy[i]);

                    // find the velocity
                    currentPos = mTarget.getPosition();
                    mVelocity.x = (currentPos.x - lastX) / mLastDeltaTime;
                    mVelocity.y = (currentPos.y - lastY) / mLastDeltaTime;
                    break;
                }

                // add up
                len += segment;
            }
        }

        super.onUpdate(value);
    }

    protected int getSegment(final float value) {
        final float valueLen = value * mTotalLength;
        float len = 0;

        // find the right segment
        for (int i = 0; i < mNumSegments; i++) {
            len += mSegments[i];

            // bingo?
            if (len >= valueLen) {
                return i;
            }
        }

        return 0;
    }

    public PointF[] getPoints() {
        return mPoints;
    }

    /**
     * @return the current velocity
     */
    public PointF getVelocity() {
        return mVelocity;
    }

    public int getCurrentSegment() {
        return mCurrentSegment;
    }

    public boolean isSnapEnabled() {
        return mSnapEnabled;
    }

    public void setSnapEnabled(final boolean snapEnabled) {
        mSnapEnabled = snapEnabled;
    }

    public float getTotalLength() {
        return mTotalLength;
    }

    @Override
    protected void onLoop() {
        super.onLoop();

        mCurrentSegment = 0;
    }

    public void dispose() {
        mPoints = null;
        mDx = mDy = null;
        mSegments = null;
    }

}

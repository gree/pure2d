/**
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
 */
package com.funzio.pure2D.effects.trails;

import android.graphics.PointF;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.shapes.Polyline;

/**
 * @author long
 *
 *         This is similar to MotionTrailShape; however the points are not easing, for tracing motion precisely.
 */
public class MotionTraceShape extends Polyline implements MotionTrail {
    public static final int DEFAULT_NUM_POINTS = 10;

    protected int mNumPoints = DEFAULT_NUM_POINTS;
    protected float mMinLength = 1;
    protected float mSegmentLength = 0;

    protected Manipulatable mTarget;
    protected PointF mTargetOffset = new PointF(0, 0);
    protected Object mData;

    public MotionTraceShape() {
        this(null);
    }

    public MotionTraceShape(final Manipulatable target) {
        super();

        // set default num points
        setNumPoints(mNumPoints);

        if (target != null) {
            setTarget(target);
        }
    }

    @Override
    public void reset(final Object... params) {
        setPointsAt(0, 0);
    }

    @Override
    public Object getData() {
        return mData;
    }

    @Override
    public void setData(final Object data) {
        mData = data;
    }

    @Override
    public void setPosition(final float x, final float y) {
        if (mNumPoints > 0) {
            if (!mPoints[0].equals(x, y)) {
                if (mNumPoints > 1) {
                    final PointF p1 = mPoints[1];
                    final float dx = x - p1.x;
                    final float dy = y - p1.y;
                    // long enough to shift?
                    if (mMinLength <= 1 || (dx * dx + dy * dy) > (mSegmentLength * mSegmentLength)) {
                        // shift them
                        shiftPoints();
                    }
                }

                // move head forward
                mPoints[0].set(x, y);

                // apply
                //setPoints(mNumPointsUsed, mPoints);
            }
        }
    }

    /**
     * Need to also override this since we override setPosition()
     *
     * @return
     */
    @Override
    public PointF getPosition() {
        return mNumPoints > 0 ? mPoints[0] : null;
    }

    @Override
    public void move(final float dx, final float dy) {
        if (mNumPoints > 0) {
            final PointF p0 = mPoints[0];
            setPosition(p0.x + dx, p0.y + dy);
        }
    }

    @Override
    public boolean update(final int deltaTime) {

        // follow the target
        if (mTarget != null) {
            // set the head
            final PointF pos = mTarget.getPosition();
            // move head
            setPosition(pos.x + mTargetOffset.x, pos.y + mTargetOffset.y);
        }

        // force invalidate
        setPoints(mNumPointsUsed, mPoints);

        return super.update(deltaTime);
    }

    /*@Override
    protected void validateVertices() {
        super.validateVertices();

        if (mTotalLength <= mMinLength) {
            // hide all vertices
            Arrays.fill(mColorMultipliers, 0);
        }
    }*/

    public int getNumPoints() {
        return mNumPoints;
    }

    public void setNumPoints(final int numPoints) {
        mNumPointsUsed = 1; // start with 1 point
        mNumPoints = numPoints;

        if (numPoints < 2) {
            mPoints = null;
            return;
        }

        if (mPoints == null || mPoints.length != numPoints) {
            mPoints = new PointF[numPoints];

            final PointF pos = (mTarget != null) ? mTarget.getPosition() : null;
            for (int i = 0; i < numPoints; i++) {
                mPoints[i] = new PointF();

                if (pos != null) {
                    mPoints[i].set(pos.x + mTargetOffset.x, pos.y + mTargetOffset.y);
                }
            }

            // find the length
            mSegmentLength = mMinLength / (mNumPoints < 2 ? 1 : mNumPoints - 1);

            // optimize
            mTotalLength = 0;
        }

        // re-count, each point has 2 vertices
        allocateVertices(numPoints * 2, VERTEX_POINTER_SIZE);
    }

    public Manipulatable getTarget() {
        return mTarget;
    }

    public void setTarget(final Manipulatable target) {
        mTarget = target;

        if (mTarget != null) {
            final PointF pos = mTarget.getPosition();
            setPointsAt(pos.x + mTargetOffset.x, pos.y + mTargetOffset.y);
        }
    }

    public void setPointsAt(final PointF p) {
        setPointsAt(p.x, p.y);
    }

    public void setPointsAt(final float x, final float y) {
        for (int i = 0; i < mNumPoints; i++) {
            mPoints[i].set(x, y);
        }

        // start with the first point
        setPoints(1, mPoints);
    }

    protected int shiftPoints() {
        // add another point if available
        if (mNumPointsUsed < mNumPoints) {
            mNumPointsUsed++;
        }

        // shift them
        for (int i = mNumPointsUsed - 1; i > 0; i--) {
            mPoints[i].set(mPoints[i - 1]);
        }

        return mNumPointsUsed;
    }

    public float getMinLength() {
        return mMinLength;
    }

    public void setMinLength(final float totalLength) {
        mMinLength = totalLength;
        mSegmentLength = mMinLength / (mNumPoints < 2 ? 1 : mNumPoints - 1);
    }

    public PointF getTargetOffset() {
        return mTargetOffset;
    }

    public void setTargetOffset(final float offsetX, final float offsetY) {
        mTargetOffset.set(offsetX, offsetY);
    }

}

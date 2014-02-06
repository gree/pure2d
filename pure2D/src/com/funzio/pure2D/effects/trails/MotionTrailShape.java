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
package com.funzio.pure2D.effects.trails;

import android.graphics.PointF;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.shapes.Polyline;

/**
 * @author long
 */
public class MotionTrailShape extends Polyline implements MotionTrail {
    public static final int DEFAULT_NUM_POINTS = 10;
    public static final float DEFAULT_MOTION_EASING = 0.5f;

    protected int mNumPoints = DEFAULT_NUM_POINTS;
    protected float mMotionEasingX = DEFAULT_MOTION_EASING;
    protected float mMotionEasingY = DEFAULT_MOTION_EASING;
    protected int mMinLength = 1;
    protected int mSegmentLength = 0;

    protected Manipulatable mTarget;
    protected PointF mTargetOffset = new PointF(0, 0);
    protected Object mData;
    private boolean mFollowingHead = false;

    public MotionTrailShape() {
        this(null);
    }

    public MotionTrailShape(final Manipulatable target) {
        super();

        // set default num points
        setNumPoints(mNumPoints);

        if (target != null) {
            setTarget(target);
        }
    }

    @Override
    public void reset(final Object... params) {
        mMotionEasingX = mMotionEasingY = DEFAULT_MOTION_EASING;

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
                mPoints[0].set(x, y);

                // flag
                mFollowingHead = true;
            }
        }
    }

    @Override
    public void move(final float dx, final float dy) {
        if (mNumPoints > 0) {
            mPoints[0].offset(dx, dy);

            // flag
            mFollowingHead = true;
        }
    }

    @Override
    public boolean update(final int deltaTime) {

        if (mNumPoints > 0) {

            if (!mFollowingHead && mTarget != null) {
                final PointF p1 = mTarget.getPosition();
                final float dx = p1.x - (mPoints[0].x - mTargetOffset.x);
                final float dy = p1.y - (mPoints[0].y - mTargetOffset.y);
                if (Math.abs(dx) >= 1 || Math.abs(dy) >= 1) {
                    // flag
                    mFollowingHead = true;
                }
            }

            if (mFollowingHead) {
                // calculate time loop for consistency with different framerate
                final int loop = deltaTime / Scene.DEFAULT_MSPF;
                PointF p1, p2;
                float dx, dy;
                for (int n = 0; n < loop; n++) {
                    for (int i = mNumPoints - 1; i > 0; i--) {
                        p1 = mPoints[i];
                        p2 = mPoints[i - 1];
                        dx = p2.x - p1.x;
                        dy = p2.y - p1.y;
                        if (mMinLength <= 1 || (dx * dx + dy * dy) > (mSegmentLength * mSegmentLength)) {
                            // move toward the leading point
                            p1.x += dx * mMotionEasingX;
                            p1.y += dy * mMotionEasingY;
                        }
                    }
                }

                // follow the target
                if (mTarget != null) {
                    // set the head
                    final PointF pos = mTarget.getPosition();
                    mPoints[0].set(pos.x + mTargetOffset.x, pos.y + mTargetOffset.y);
                }

                // apply
                setPoints(mPoints);
            }
        }

        return super.update(deltaTime);
    }

    @Override
    protected void validateVertices() {
        super.validateVertices();

        if (mTotalLength <= mMinLength) {
            // flag done
            mFollowingHead = false;
        }
    }

    public int getNumPoints() {
        return mNumPoints;
    }

    public void setNumPoints(final int numPoints) {
        mNumPointsUsed = mNumPoints = numPoints;

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
            mFollowingHead = false;
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
            for (int i = 0; i < mNumPoints; i++) {
                mPoints[i].set(pos.x + mTargetOffset.x, pos.y + mTargetOffset.y);
            }

            // apply
            setPoints(mPoints);
        }
    }

    public void setPointsAt(final float x, final float y) {
        for (int i = 0; i < mNumPoints; i++) {
            mPoints[i].set(x, y);
        }

        // apply
        setPoints(mPoints);
    }

    public void setPointsAt(final PointF p) {
        for (int i = 0; i < mNumPoints; i++) {
            mPoints[i].set(p.x, p.y);
        }

        // apply
        setPoints(mPoints);
    }

    public int getMinLength() {
        return mMinLength;
    }

    public void setMinLength(final int totalLength) {
        mMinLength = totalLength;
        mSegmentLength = mMinLength / (mNumPoints < 2 ? 1 : mNumPoints - 1);
    }

    public float getMotionEasingX() {
        return mMotionEasingX;
    }

    public float getMotionEasingY() {
        return mMotionEasingY;
    }

    /**
     * @param easing, must be from 0 to 1
     */
    public void setMotionEasing(final float easing) {
        mMotionEasingX = mMotionEasingY = easing;
    }

    public void setMotionEasing(final float easingX, final float easingY) {
        mMotionEasingX = easingX;
        mMotionEasingY = easingY;
    }

    public PointF getTargetOffset() {
        return mTargetOffset;
    }

    public void setTargetOffset(final float offsetX, final float offsetY) {
        mTargetOffset.set(offsetX, offsetY);
    }

}

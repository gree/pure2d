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

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.geom.Point3D;

/**
 * @author long
 */
@Deprecated
// NOT READY YET!
public class MotionTrailPlot3D extends MotionTrailPlot implements MotionTrail {
    protected float mMotionEasingZ = DEFAULT_MOTION_EASING;

    public MotionTrailPlot3D() {
        this(null);
    }

    public MotionTrailPlot3D(final Manipulatable target) {
        super(target);
    }

    @Override
    public void reset(final Object... params) {
        mMotionEasingX = mMotionEasingY = mMotionEasingZ = DEFAULT_MOTION_EASING;
    }

    public final void setPosition(final float x, final float y, final float z) {
        if (mNumPoints > 0) {
            ((Point3D) mPoints[0]).set(x, y, z);
        }
    }

    @Override
    public void setZ(final float z) {
        if (mNumPoints > 0) {
            ((Point3D) mPoints[0]).set(mPoints[0].x, mPoints[0].y, z);
        }
    }

    public void move(final float dx, final float dy, final float dz) {
        if (mNumPoints > 0) {
            ((Point3D) mPoints[0]).offset(dx, dy, dz);
        }
    }

    @Override
    public boolean update(final int deltaTime) {
        if (mNumPoints > 0) {

            // calculate time loop for consistency with different framerate
            final int loop = deltaTime / Scene.DEFAULT_MSPF;
            Point3D p1, p2;
            float dx, dy, dz;
            for (int n = 0; n < loop; n++) {
                for (int i = mNumPoints - 1; i > 0; i--) {
                    p1 = (Point3D) mPoints[i];
                    p2 = (Point3D) mPoints[i - 1];
                    dx = p2.x - p1.x;
                    dy = p2.y - p1.y;
                    dz = p2.z - p1.z;
                    if (mMinLength == 0 || Math.sqrt(dx * dx + dy * dy + dz * dz) > mSegmentLength) {
                        // move toward the leading point
                        p1.x += dx * mMotionEasingX;
                        p1.y += dy * mMotionEasingY;
                        p1.z += dz * mMotionEasingZ;
                    }
                }
            }

            // follow the target
            if (mTarget != null && mTarget instanceof DisplayObject) {
                // set the head
                final PointF pos = mTarget.getPosition();
                ((Point3D) mPoints[0]).set(pos.x + mTargetOffset.x, pos.y + mTargetOffset.y, ((DisplayObject) mTarget).getZ() + ((Point3D) mTargetOffset).z);
            }

            // apply
            setPoints(mPoints);
        }

        return super.update(deltaTime);
    }

    @Override
    public void setNumPoints(final int numPoints) {
        mNumPoints = numPoints;

        if (numPoints < 2) {
            mPoints = null;
            return;
        }

        if (mPoints == null || mPoints.length != numPoints) {
            mPoints = new Point3D[numPoints];

            final PointF pos = (mTarget != null) ? mTarget.getPosition() : null;
            for (int i = 0; i < numPoints; i++) {
                mPoints[i] = new Point3D();

                if (pos != null && mTarget instanceof DisplayObject) {
                    ((Point3D) mPoints[i]).set(pos.x + mTargetOffset.x, pos.y + mTargetOffset.y, ((DisplayObject) mTarget).getZ() + ((Point3D) mTargetOffset).z);
                }
            }

            // find the
            mSegmentLength = mMinLength / (numPoints - 1);
        }
    }

    @Override
    public void setTarget(final Manipulatable target) {
        mTarget = target;

        if (mTarget != null && mTarget instanceof DisplayObject) {
            final PointF pos = mTarget.getPosition();
            for (int i = 0; i < mNumPoints; i++) {
                ((Point3D) mPoints[i]).set(pos.x + mTargetOffset.x, pos.y + mTargetOffset.y, ((DisplayObject) mTarget).getZ() + ((Point3D) mTargetOffset).z);
            }
        }

        // apply
        setPoints(mPoints);
    }

    @Override
    public void setPointsAt(final float x, final float y, final float z) {
        for (int i = 0; i < mNumPoints; i++) {
            ((Point3D) mPoints[i]).set(x, y, z);
        }

        // apply
        setPoints(mPoints);
    }

    public void setPointsAt(final Point3D p) {
        for (int i = 0; i < mNumPoints; i++) {
            ((Point3D) mPoints[i]).set(p.x, p.y, p.z);
        }

        // apply
        setPoints(mPoints);
    }

    public float getMotionEasingZ() {
        return mMotionEasingZ;
    }

    /**
     * @param easing, must be from 0 to 1
     */
    @Override
    public void setMotionEasing(final float easing) {
        mMotionEasingX = mMotionEasingY = mMotionEasingZ = easing;
    }

    public void setMotionEasing(final float easingX, final float easingY, final float easingZ) {
        mMotionEasingX = easingX;
        mMotionEasingY = easingY;
        mMotionEasingZ = easingZ;
    }

    public void setTargetOffset(final float offsetX, final float offsetY, final float offsetZ) {
        ((Point3D) mTargetOffset).set(offsetX, offsetY, offsetZ);
    }

}

/**
 * 
 */
package com.funzio.pure2D.effects.trails;

import android.graphics.PointF;

import com.funzio.pure2D.DisplayObject;
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

    public MotionTrailPlot3D(final DisplayObject target) {
        super(target);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.utils.Reusable#reset(java.lang.Object[])
     */
    @Override
    public void reset(final Object... params) {
        mMotionEasingX = mMotionEasingY = mMotionEasingZ = DEFAULT_MOTION_EASING;
    }

    public final void setPosition(final float x, final float y, final float z) {
        if (mNumPoints > 0) {
            ((Point3D) mPoints[0]).set(x, y, z);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#setZ(float)
     */
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

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#update(int)
     */
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
            if (mTarget != null) {
                // set the head
                final PointF pos = mTarget.getPosition();
                ((Point3D) mPoints[0]).set(pos.x + mTargetOffset.x, pos.y + mTargetOffset.y, mTarget.getZ() + ((Point3D) mTargetOffset).z);
            }

            // apply
            setPoints(mPoints);
        }

        return super.update(deltaTime);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.effects.trails.MotionTrailDots#drawPoint(com.funzio.pure2D.gl.gl10.GLState, android.graphics.PointF, float, float)
     */
    // @Override
    // protected void drawDot(final GLState glState, final int index, final float width, final float height) {
    // super.drawDot(glState, index, width, height);
    //
    // // TODO apply Z
    // }

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

                if (pos != null) {
                    ((Point3D) mPoints[i]).set(pos.x + mTargetOffset.x, pos.y + mTargetOffset.y, mTarget.getZ() + ((Point3D) mTargetOffset).z);
                }
            }

            // find the
            mSegmentLength = mMinLength / (numPoints - 1);
        }
    }

    @Override
    public void setTarget(final DisplayObject target) {
        mTarget = target;

        if (mTarget != null) {
            final PointF pos = mTarget.getPosition();
            for (int i = 0; i < mNumPoints; i++) {
                ((Point3D) mPoints[i]).set(pos.x + mTargetOffset.x, pos.y + mTargetOffset.y, mTarget.getZ() + ((Point3D) mTargetOffset).z);
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

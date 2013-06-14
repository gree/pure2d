/**
 * 
 */
package com.funzio.pure2D.effects.trails;

import android.graphics.PointF;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.geom.Point3D;
import com.funzio.pure2D.shapes.Polyline3D;

/**
 * @author long
 */
public class MotionTrailShape3D extends Polyline3D implements MotionTrail {
    public static final int DEFAULT_NUM_POINTS = 10;
    public static final float DEFAULT_MOTION_EASING = 0.5f;

    protected int mNumPoints = DEFAULT_NUM_POINTS;
    protected float mMotionEasingX = DEFAULT_MOTION_EASING;
    protected float mMotionEasingY = DEFAULT_MOTION_EASING;
    protected float mMotionEasingZ = DEFAULT_MOTION_EASING;
    protected int mMinLength = 0;
    protected int mSegmentLength = 0;

    protected DisplayObject mTarget;
    protected Point3D mTargetOffset = new Point3D(0, 0, 0);
    protected Object mData;

    public MotionTrailShape3D() {
        this(null);
    }

    public MotionTrailShape3D(final DisplayObject target) {
        super();

        // set default num points
        setNumPoints(mNumPoints);

        if (target != null) {
            setTarget(target);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.utils.Reusable#reset(java.lang.Object[])
     */
    @Override
    public void reset(final Object... params) {
        mMotionEasingX = mMotionEasingY = DEFAULT_MOTION_EASING;
    }

    @Override
    public Object getData() {
        return mData;
    }

    @Override
    public void setData(final Object data) {
        mData = data;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#setPosition(float, float)
     */
    @Override
    public void setPosition(final float x, final float y) {
        if (mNumPoints > 0) {
            mPoints[0].set(x, y);
        }
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

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#move(float, float)
     */
    @Override
    public void move(final float dx, final float dy) {
        if (mNumPoints > 0) {
            mPoints[0].offset(dx, dy);
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
                    if (mSegmentLength == 0 || Math.sqrt(dx * dx + dy * dy + dz * dz) > mSegmentLength) {
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
                ((Point3D) mPoints[0]).set(pos.x + mTargetOffset.x, pos.y + mTargetOffset.y, mTarget.getZ() + mTargetOffset.z);
            }

            // apply
            setPoints(mPoints);
        }

        return super.update(deltaTime);
    }

    public int getNumPoints() {
        return mNumPoints;
    }

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
                    ((Point3D) mPoints[i]).set(pos.x + mTargetOffset.x, pos.y + mTargetOffset.y, mTarget.getZ() + mTargetOffset.z);
                }
            }

            // find the
            mSegmentLength = mMinLength / (numPoints - 1);
        }

        // re-count, each point has 2 vertices
        allocateVertices(numPoints * 2, VERTEX_POINTER_SIZE);
    }

    public DisplayObject getTarget() {
        return mTarget;
    }

    public void setTarget(final DisplayObject target) {
        mTarget = target;

        if (mTarget != null) {
            final PointF pos = mTarget.getPosition();
            for (int i = 0; i < mNumPoints; i++) {
                ((Point3D) mPoints[i]).set(pos.x + mTargetOffset.x, pos.y + mTargetOffset.y, mTarget.getZ() + mTargetOffset.z);
            }
        }

        // apply
        setPoints(mPoints);
    }

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

    public float getMotionEasingZ() {
        return mMotionEasingZ;
    }

    /**
     * @param easing, must be from 0 to 1
     */
    public void setMotionEasing(final float easing) {
        mMotionEasingX = mMotionEasingY = easing;
    }

    public void setMotionEasing(final float easingX, final float easingY, final float easingZ) {
        mMotionEasingX = easingX;
        mMotionEasingY = easingY;
        mMotionEasingZ = easingZ;
    }

    public Point3D getTargetOffset() {
        return mTargetOffset;
    }

    public void setTargetOffset(final float offsetX, final float offsetY, final float offsetZ) {
        mTargetOffset.set(offsetX, offsetY, offsetZ);
    }

}

/**
 * 
 */
package com.funzio.pure2D.effects.trails;

import android.graphics.PointF;
import android.util.FloatMath;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.shapes.PolyLine;

/**
 * @author long
 */
public class TrailShape extends PolyLine {

    protected int mNumPoints = 10;
    protected int mMinLength = 0;
    protected int mSegmentLength;
    protected float mMotionEasing = 0.5f;
    protected DisplayObject mTarget;

    public TrailShape() {
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (mTarget != null && mNumPoints > 0) {

            PointF p1, p2;
            float dx, dy;
            float delta = 0;
            for (int i = mNumPoints - 1; i > 0; i--) {
                p1 = mPoints[i];
                p2 = mPoints[i - 1];
                dx = p2.x - p1.x;
                dy = p2.y - p1.y;
                delta = FloatMath.sqrt(dx * dx + dy * dy);
                if (delta > mSegmentLength) {
                    p1.x += dx * mMotionEasing;
                    p1.y += dy * mMotionEasing;
                }
            }

            // follow the target
            // set the head
            mPoints[0].set(mTarget.getPosition());

            // apply
            setPoints(mPoints);

            return true;
        }

        return false;
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
            mPoints = new PointF[numPoints];

            for (int i = 0; i < numPoints; i++) {
                mPoints[i] = new PointF();

                if (mTarget != null) {
                    mPoints[i].set(mTarget.getPosition());
                }
            }

            // find the
            mSegmentLength = mMinLength / (numPoints - 1);
        }
    }

    public DisplayObject getTarget() {
        return mTarget;
    }

    public void setTarget(final DisplayObject target) {
        mTarget = target;

        if (mTarget != null) {
            for (int i = 0; i < mNumPoints; i++) {
                mPoints[i].set(mTarget.getPosition());
            }
        }
    }

    public int getMinLength() {
        return mMinLength;
    }

    public void setMinLength(final int totalLength) {
        mMinLength = totalLength;
        mSegmentLength = mMinLength / (mNumPoints < 2 ? 1 : mNumPoints - 1);
    }

    public float getMotionEasing() {
        return mMotionEasing;
    }

    /**
     * @param easing, must be from 0 to 1
     */
    public void setMotionEasing(final float easing) {
        mMotionEasing = easing;
    }

}

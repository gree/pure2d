/**
 * 
 */
package com.funzio.pure2D.animators;

import android.graphics.PointF;
import android.util.FloatMath;
import android.view.animation.Interpolator;

/**
 * @author long
 */
@Deprecated
// NOT READY
public class PathAnimator extends TweenAnimator {
    protected PointF[] mPoints;
    protected float[] mSin;
    protected float[] mCos;
    protected float mDistance;

    public PathAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final PointF... points) {
        mPoints = points;

        final int n = points.length;
        mSin = new float[n - 1];
        mCos = new float[n - 1];

        float dx, dy, angle;
        mDistance = 0;
        for (int i = 0; i < n - 1; i++) {
            dx = points[i + 1].x - points[i].x;
            dy = points[i + 1].y - points[i].y;
            angle = (float) Math.atan2(dy, dx);
            mSin[i] = FloatMath.sin(angle);
            mCos[i] = FloatMath.cos(angle);
            mDistance += FloatMath.sqrt(dx * dx + dy * dy);
        }
    }

    public void start(final PointF... points) {
        setValues(points);

        start();
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {
            // mTarget.setPosition(mSrcX + value * mDelta.x, mSrcY + value * mDelta.y);
        }

        super.onUpdate(value);
    }

    public PointF[] getPoints() {
        return mPoints;
    }
}

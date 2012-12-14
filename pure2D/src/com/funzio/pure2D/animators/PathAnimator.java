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
public class PathAnimator extends TweenAnimator {
    protected PointF[] mPoints;
    protected float[] mSin;
    protected float[] mCos;
    protected float[] mSegments;
    protected float mTotalSegment;
    protected PointF mCurrentPoint;

    public PathAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final PointF... points) {
        mPoints = points;

        final int n = points.length;
        mSin = new float[n - 1];
        mCos = new float[n - 1];
        mSegments = new float[n - 1];

        float dx, dy, angle;
        mTotalSegment = 0;
        for (int i = 0; i < n - 1; i++) {
            dx = points[i + 1].x - points[i].x;
            dy = points[i + 1].y - points[i].y;
            angle = (float) Math.atan2(dy, dx);
            mSin[i] = FloatMath.sin(angle);
            mCos[i] = FloatMath.cos(angle);
            mSegments[i] = FloatMath.sqrt(dx * dx + dy * dy);
            mTotalSegment += mSegments[i];
        }
    }

    public void start(final PointF... points) {
        setValues(points);

        start();
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {
            final float valueLen = value * mTotalSegment;
            final int size = mSegments.length;
            float len = 0;
            float segment;
            for (int i = 0; i < size; i++) {
                segment = mSegments[i];
                if (len + segment >= valueLen) {
                    final float delta = valueLen - len;
                    mTarget.setPosition(mPoints[i].x + delta * mCos[i], mPoints[i].y + delta * mSin[i]);
                    return;
                }

                // add up
                len += segment;
            }
        }

        super.onUpdate(value);
    }

    public PointF[] getPoints() {
        return mPoints;
    }
}

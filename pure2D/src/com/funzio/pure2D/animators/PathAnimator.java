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
    protected float mTotalLength;

    // current velocity
    protected PointF mVelocity = new PointF();

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
        mTotalLength = 0;
        for (int i = 0; i < n - 1; i++) {
            dx = points[i + 1].x - points[i].x;
            dy = points[i + 1].y - points[i].y;
            angle = (float) Math.atan2(dy, dx);
            mSin[i] = FloatMath.sin(angle);
            mCos[i] = FloatMath.cos(angle);
            mSegments[i] = FloatMath.sqrt(dx * dx + dy * dy);
            mTotalLength += mSegments[i];
        }
    }

    public void start(final PointF... points) {
        setValues(points);

        start();
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {
            final float valueLen = value * mTotalLength;
            final int size = mSegments.length;
            float len = 0;

            // find the right segment
            float segment;
            for (int i = 0; i < size; i++) {
                segment = mSegments[i];

                // bingo?
                if (len + segment >= valueLen) {
                    final float delta = valueLen - len;
                    PointF currentPos = mTarget.getPosition();
                    float lastX = currentPos.x;
                    float lastY = currentPos.y;

                    // new position
                    mTarget.setPosition(mPoints[i].x + delta * mCos[i], mPoints[i].y + delta * mSin[i]);

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

    public PointF[] getPoints() {
        return mPoints;
    }

    /**
     * @return the current velocity
     */
    public PointF getVelocity() {
        return mVelocity;
    }

}

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
    protected float[] mSin;
    protected float[] mCos;
    protected float[] mSegments;
    protected float mTotalLength;

    // current velocity
    protected PointF mVelocity = new PointF();
    protected int mCurrentSegment = 0;
    protected boolean mSnapEnabled = false;

    public PathAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final PointF... points) {
        mPoints = points;

        // safety check
        final int n = points.length;
        if (n < 2) {
            return;
        }

        mSin = new float[n - 1];
        mCos = new float[n - 1];
        mSegments = new float[n - 1];

        float dx, dy, angle;
        mTotalLength = 0;
        for (int i = 0; i < n - 1; i++) {
            dx = points[i + 1].x - points[i].x;
            dy = points[i + 1].y - points[i].y;
            angle = (float) Math.atan2(dy, dx);
            mSin[i] = (float) Math.sin(angle);
            mCos[i] = (float) Math.cos(angle);
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
            final int size = mSegments.length;
            float len = 0;

            // find the right segment
            float segment;
            for (int i = 0; i < size; i++) {
                segment = mSegments[i];

                // bingo?
                if (len + segment >= valueLen) {
                    mCurrentSegment = i;
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

    protected int getSegment(final float value) {
        final float valueLen = value * mTotalLength;
        final int size = mSegments.length;
        float len = 0;

        // find the right segment
        for (int i = 0; i < size; i++) {
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

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.TweenAnimator#onLoop()
     */
    @Override
    protected void onLoop() {
        super.onLoop();

        mCurrentSegment = 0;
    }

}

/**
 * 
 */
package com.funzio.pure2D.animators;

import android.graphics.PointF;
import android.view.animation.Interpolator;

import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class RotateAnimator extends TweenAnimator {
    public static final int PIVOT_CLEAR = -9999;

    protected float mSrc = 0;
    protected float mDst = 0;
    protected float mDelta = 0;

    protected float mPivotX = PIVOT_CLEAR;
    protected float mPivotY = PIVOT_CLEAR;
    private float mRadius = 0;

    public RotateAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.BaseAnimator#reset(java.lang.Object[])
     */
    @Override
    public void reset(final Object... params) {
        super.reset(params);

        clearPivot();
    }

    public void setValues(final float src, final float dst) {
        mSrc = src;
        mDst = dst;
        mDelta = mDst - mSrc;
    }

    public void setDelta(final float delta) {
        mDelta = delta;
    }

    /**
     * The center of the rotation.
     * 
     * @param x
     * @param y
     * @see #clearPivot()
     */
    public void setPivot(final float x, final float y) {
        mPivotX = x;
        mPivotY = y;

        // find the radius
        if (hasPivot() && mTarget != null) {
            final PointF pt = mTarget.getPosition();
            mRadius = (float) Math.sqrt((pt.x - mPivotX) * (pt.x - mPivotX) + (pt.y - mPivotY) + (pt.y - mPivotY));
        } else {
            mRadius = 0;
        }
    }

    /**
     * Unset the pivot.
     */
    public void clearPivot() {
        mPivotX = mPivotY = PIVOT_CLEAR;
        mRadius = 0;
    }

    public boolean hasPivot() {
        return mPivotX != PIVOT_CLEAR && mPivotY != PIVOT_CLEAR;
    }

    public void start(final float src, final float dst) {
        mSrc = src;
        mDst = dst;
        mDelta = mDst - mSrc;

        start();
    }

    public void start(final float dest) {
        if (mTarget != null) {
            start(mTarget.getRotation(), dest);

            // find the radius
            if (hasPivot()) {
                final PointF pt = mTarget.getPosition();
                mRadius = (float) Math.sqrt((pt.x - mPivotX) * (pt.x - mPivotX) + (pt.y - mPivotY) + (pt.y - mPivotY));
            }
        }
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {
            if (mAccumulating) {
                mTarget.rotate((value - mLastValue) * mDelta);
            } else {
                mTarget.setRotation(mSrc + value * mDelta);
            }

            if (mRadius != 0) {
                final float radian = (mSrc + value * mDelta) * Pure2DUtils.DEGREE_TO_RADIAN;
                final float dx = mRadius * (float) Math.cos(radian);
                final float dy = mRadius * (float) Math.sin(radian);
                mTarget.setPosition(mPivotX + dx, mPivotY + dy);
            }
        }

        super.onUpdate(value);
    }

    public float getDelta() {
        return mDelta;
    }
}

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
public class MoveAnimator extends TweenAnimator {
    protected float mSrcX = 0;
    protected float mSrcY = 0;
    protected PointF mDelta = new PointF();

    public MoveAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final float srcX, final float srcY, final float dstX, final float dstY) {
        mSrcX = srcX;
        mSrcY = srcY;

        mDelta.x = dstX - srcX;
        mDelta.y = dstY - srcY;
    }

    public void setDelta(final float dx, final float dy) {
        mDelta.x = dx;
        mDelta.y = dy;
    }

    public void setDistance(final float distance, final float radianAngle) {
        setDelta(distance * (float) Math.cos(radianAngle), distance * (float) Math.sin(radianAngle));
    }

    public void setDistance(final float distance, final int degreeAngle) {
        final float radianAngle = degreeAngle * Pure2DUtils.DEGREE_TO_RADIAN;
        setDelta(distance * (float) Math.cos(radianAngle), distance * (float) Math.sin(radianAngle));
    }

    public void start(final float srcX, final float srcY, final float dstX, final float dstY) {
        mSrcX = srcX;
        mSrcY = srcY;

        mDelta.x = dstX - srcX;
        mDelta.y = dstY - srcY;

        start();
    }

    public void start(final float destX, final float destY) {
        if (mTarget != null) {
            final PointF position = mTarget.getPosition();
            start(position.x, position.y, destX, destY);
        }
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {
            if (mAccumulating) {
                mTarget.moveBy((value - mLastValue) * mDelta.x, (value - mLastValue) * mDelta.y);
            } else {
                mTarget.setPosition(mSrcX + value * mDelta.x, mSrcY + value * mDelta.y);
            }
        }

        super.onUpdate(value);
    }

    public PointF getDelta() {
        return mDelta;
    }
}

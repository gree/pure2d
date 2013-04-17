/**
 * 
 */
package com.funzio.pure2D.animators;

import android.graphics.PointF;
import android.view.animation.Interpolator;

/**
 * @author long
 */
public class MoveRadiusAnimator extends TweenAnimator {
    protected float mSrcX = 0;
    protected float mSrcY = 0;
    protected PointF mDelta = new PointF();

    public MoveRadiusAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final float srcX, final float srcY, final float radius, final float radianAngle) {
        mSrcX = srcX;
        mSrcY = srcY;

        mDelta.x = radius * (float) Math.cos(radianAngle) - srcX;
        mDelta.y = radius * (float) Math.sin(radianAngle) - srcY;
    }

    public void setValues(final float srcX, final float srcY, final float radius, final int degreeAngle) {
        mSrcX = srcX;
        mSrcY = srcY;

        mDelta.x = radius * (float) Math.cos(degreeAngle * Math.PI / 180f) - srcX;
        mDelta.y = radius * (float) Math.sin(degreeAngle * Math.PI / 180f) - srcY;
    }

    public void setValues(final float radius, final float radianAngle) {
        mDelta.x = radius * (float) Math.cos(radianAngle);
        mDelta.y = radius * (float) Math.sin(radianAngle);
    }

    public void setValues(final float radius, final int degreeAngle) {
        mDelta.x = radius * (float) Math.cos(degreeAngle * Math.PI / 180f);
        mDelta.y = radius * (float) Math.sin(degreeAngle * Math.PI / 180f);
    }

    public void start(final float srcX, final float srcY, final float radius, final float radianAngle) {
        setValues(srcX, srcY, radius, radianAngle);

        start();
    }

    public void start(final float srcX, final float srcY, final float radius, final int degreeAngle) {
        setValues(srcX, srcY, radius, degreeAngle);

        start();
    }

    public void start(final float radius, final float radianAngle) {
        if (mTarget != null) {
            final PointF position = mTarget.getPosition();
            start(position.x, position.y, radius, radianAngle);
        }
    }

    public void start(final float radius, final int degreeAngle) {
        if (mTarget != null) {
            final PointF position = mTarget.getPosition();
            start(position.x, position.y, radius, degreeAngle);
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

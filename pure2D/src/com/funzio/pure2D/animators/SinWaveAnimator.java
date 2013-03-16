/**
 * 
 */
package com.funzio.pure2D.animators;

import android.graphics.PointF;
import android.view.animation.Interpolator;

/**
 * @author long
 */
public class SinWaveAnimator extends TweenAnimator {
    protected float mSrcX = 0;
    protected float mSrcY = 0;
    protected PointF mDelta = new PointF();
    protected float mDeltaLength = 0;

    protected int mWaveNum = 2;
    protected float mWaveRadius = 10;
    protected float mAngle;
    protected float mSinAngle;
    protected float mCosAngle;

    private float mLastX;
    private float mLastY;

    public SinWaveAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final float srcX, final float srcY, final float dstX, final float dstY) {
        mSrcX = srcX;
        mSrcY = srcY;

        mDelta.x = dstX - srcX;
        mDelta.y = dstY - srcY;
        mDeltaLength = (float) Math.sqrt(mDelta.x * mDelta.x + mDelta.y * mDelta.y);

        // pre-cals
        mAngle = (float) Math.atan2(mDelta.y, mDelta.x);
        mSinAngle = (float) Math.sin(mAngle);
        mCosAngle = (float) Math.cos(mAngle);
    }

    public void setDelta(final float dx, final float dy) {
        mDelta.x = dx;
        mDelta.y = dy;
        mDeltaLength = (float) Math.sqrt(mDelta.x * mDelta.x + mDelta.y * mDelta.y);

        // pre-cals
        mAngle = (float) Math.atan2(mDelta.y, mDelta.x);
        mSinAngle = (float) Math.sin(mAngle);
        mCosAngle = (float) Math.cos(mAngle);
    }

    public void start(final float srcX, final float srcY, final float dstX, final float dstY) {
        setValues(srcX, srcY, dstX, dstY);

        start();
    }

    public void start(final float destX, final float destY) {
        if (mTarget != null) {
            final PointF position = mTarget.getPosition();
            start(position.x, position.y, destX, destY);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.TweenAnimator#startElapse(int)
     */
    @Override
    public void startElapse(final int elapsedTime) {
        super.startElapse(elapsedTime);

        mLastX = mLastY = 0;
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {
            final float currentAngle = value * mWaveNum * (float) Math.PI;
            final float dx = value * mDeltaLength;
            final float dy = (float) Math.sin(currentAngle) * mWaveRadius;
            final float newX = dx * mCosAngle - dy * mSinAngle;
            final float newY = dx * mSinAngle + dy * mCosAngle;

            if (mAccumulating) {
                mTarget.moveBy(newX - mLastX, newY - mLastY);
            } else {
                mTarget.setPosition(mSrcX + newX, mSrcY + newY);
            }

            mLastX = newX;
            mLastY = newY;
        }

        super.onUpdate(value);
    }

    public PointF getDelta() {
        return mDelta;
    }

    public int getWaveNum() {
        return mWaveNum;
    }

    public void setWaveNum(final int waveNum) {
        mWaveNum = waveNum;
    }

    public float getWaveRadius() {
        return mWaveRadius;
    }

    public void setWaveRadius(final float waveRadius) {
        mWaveRadius = waveRadius;
    }
}

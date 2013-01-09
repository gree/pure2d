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
public class SinWaveAnimator extends TweenAnimator {
    protected float mSrcX = 0;
    protected float mSrcY = 0;
    protected float mDstX = 0;
    protected float mDstY = 0;
    protected PointF mDelta = new PointF();
    protected float mDeltaLength = 0;

    protected int mWaveNum = 2;
    protected float mWaveRadius = 10;
    protected float mAngle;
    protected float mSinAngle;
    protected float mCosAngle;

    public SinWaveAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final float srcX, final float srcY, final float dstX, final float dstY) {
        mSrcX = srcX;
        mSrcY = srcY;
        mDstX = dstX;
        mDstY = dstY;

        mDelta.x = dstX - srcX;
        mDelta.y = dstY - srcY;
        mDeltaLength = FloatMath.sqrt(mDelta.x * mDelta.x + mDelta.y * mDelta.y);

        // pre-cals
        mAngle = (float) Math.atan2(mDelta.y, mDelta.x);
        mSinAngle = FloatMath.sin(mAngle);
        mCosAngle = FloatMath.cos(mAngle);
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

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {
            final float da = value * mWaveNum * (float) Math.PI;
            final float dx = value * mDeltaLength;
            final float dy = FloatMath.sin(da) * mWaveRadius;
            mTarget.setPosition(mSrcX + dx * mCosAngle - dy * mSinAngle, mSrcY + dx * mSinAngle + dy * mCosAngle);
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

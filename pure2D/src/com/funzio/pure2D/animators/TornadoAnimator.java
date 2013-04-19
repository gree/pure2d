/**
 * 
 */
package com.funzio.pure2D.animators;

import android.graphics.PointF;
import android.view.animation.Interpolator;

/**
 * @author long
 */
public class TornadoAnimator extends TweenAnimator {
    protected float mSrcX = 0;
    protected float mSrcY = 0;
    protected PointF mDelta = new PointF();

    private float mRadius = 100;
    private float mCircleRatio = 0.25f;
    private float mNumCircles = 10;
    private float mRadianLength = (float) Math.PI * (mNumCircles * 2);

    private float mLastX;
    private float mLastY;
    private float mLengthX;
    private float mLengthY;
    private float mSinAngle;
    private float mCosAngle;
    private Interpolator mRadiusInterpolator;

    public TornadoAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public Interpolator getRadiusInterpolator() {
        return mRadiusInterpolator;
    }

    public void setRadiusInterpolator(final Interpolator radiusInterpolator) {
        mRadiusInterpolator = radiusInterpolator;
    }

    public void setValues(final float srcX, final float srcY, final float dstX, final float dstY) {
        mSrcX = srcX;
        mSrcY = srcY;

        // apply the delta
        setDelta(dstX - srcX, dstY - srcY);
    }

    public void setCircles(final float radius, final float numCircles, final float circleRatio) {
        mRadius = radius;
        mNumCircles = numCircles;
        mRadianLength = (float) Math.PI * (mNumCircles * 2);
        mCircleRatio = circleRatio;

        // travel length for the center
        mLengthX = mDelta.x - mRadius * mCircleRatio * mCosAngle;
        mLengthY = mDelta.y - mRadius * mCircleRatio * mSinAngle;
    }

    public void setDelta(final float dx, final float dy) {
        mDelta.x = dx;
        mDelta.y = dy;

        // pre-calculate
        final float angle = (float) Math.atan2(mDelta.y, mDelta.x);
        mSinAngle = (float) Math.sin(angle);
        mCosAngle = (float) Math.cos(angle);
        // travel length for the center
        mLengthX = mDelta.x - mRadius * mCircleRatio * mCosAngle;
        mLengthY = mDelta.y - mRadius * mCircleRatio * mSinAngle;
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

    public float getRadius() {
        return mRadius;
    }

    public float getNumCircles() {
        return mNumCircles;
    }

    public float getCircleRatio() {
        return mCircleRatio;
    }

    public PointF getDelta() {
        return mDelta;
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {

            final float centerX = value * mLengthX;
            final float centerY = value * mLengthY;
            final float angle = value * mRadianLength;
            final float radius = mRadius * (mRadiusInterpolator == null ? value : mRadiusInterpolator.getInterpolation(value));

            final float dx = radius * mCircleRatio * (float) Math.cos(angle);
            final float dy = radius * (float) Math.sin(angle);
            // rotate to the main direction
            final float newX = centerX + dx * mCosAngle - dy * mSinAngle;
            final float newY = centerY + dx * mSinAngle + dy * mCosAngle;

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
}

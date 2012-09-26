/**
 * 
 */
package com.funzio.pure2D.animators;

import android.view.animation.Interpolator;

/**
 * @author long
 */
public class RotateAnimator extends TweenAnimator {
    protected float mSrc = 0;
    protected float mDst = 0;
    protected float mDelta = 0;

    public RotateAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final float src, final float dst) {
        mSrc = src;
        mDst = dst;
        mDelta = mDst - mSrc;
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
        }
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {
            mTarget.setRotation(mSrc + value * mDelta);
        }

        super.onUpdate(value);
    }

    public float getDelta() {
        return mDelta;
    }
}

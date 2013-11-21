/**
 * 
 */
package com.funzio.pure2D.animators;

import android.view.animation.Interpolator;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.uni.UniObject;

/**
 * @author long
 */
public class AlphaAnimator extends TweenAnimator {
    protected float mSrc = 0;
    protected float mDst = 0;
    protected float mDelta = 0;

    public AlphaAnimator(final Interpolator interpolator) {
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
        if (mTarget instanceof DisplayObject) {
            start(((DisplayObject) mTarget).getAlpha(), dest);
        } else if (mTarget instanceof UniObject) {
            start(((UniObject) mTarget).getAlpha(), dest);
        }
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget instanceof DisplayObject) {
            ((DisplayObject) mTarget).setAlpha(mSrc + value * mDelta);
        } else if (mTarget instanceof UniObject) {
            ((UniObject) mTarget).setAlpha(mSrc + value * mDelta);
        }

        super.onUpdate(value);
    }

    public float getDelta() {
        return mDelta;
    }
}

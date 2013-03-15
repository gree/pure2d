/**
 * 
 */
package com.funzio.pure2D.animators;

import android.view.animation.Interpolator;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.gl.GLColor;

/**
 * @author juni.kim
 */
public class ColorAnimator extends TweenAnimator {
    protected GLColor mSrc = new GLColor(1f, 1f, 1f, 1f);
    protected GLColor mDst = new GLColor(1f, 1f, 1f, 1f);
    protected GLColor mTargetColor = new GLColor(1f, 1f, 1f, 1f);
    protected GLColor mDelta = new GLColor(0f, 0f, 0f, 0f);

    public ColorAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final GLColor src, final GLColor dst) {
        mSrc.setValues(src);
        mDst.setValues(dst);
        mDelta.setValues(mDst.r - mSrc.r, mDst.g - mSrc.g, mDst.b - mSrc.b, mDst.a - mSrc.a);
    }

    public void setValues(final float srcR, final float srcG, final float srcB, final float srcA, final float dstR, final float dstG, final float dstB, final float dstA) {
        mSrc.setValues(srcR, srcG, srcB, srcA);
        mDst.setValues(dstR, dstG, dstB, dstA);
        mDelta.setValues(dstR - srcR, dstG - srcG, dstB - srcB, dstA - srcA);
    }

    public void start(final GLColor src, final GLColor dst) {
        setValues(src, dst);

        start();
    }

    public void start(final GLColor dst) {
        if (mTarget instanceof DisplayObject) {
            start(((DisplayObject) mTarget).getColor(), dst);
        }
    }

    public void start(final float srcR, final float srcG, final float srcB, final float srcA, final float dstR, final float dstG, final float dstB, final float dstA) {
        setValues(srcR, srcG, srcB, srcA, dstR, dstG, dstB, dstA);

        start();
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget instanceof DisplayObject) {
            float r, g, b, a;
            r = mSrc.r + mDelta.r * value;
            g = mSrc.g + mDelta.g * value;
            b = mSrc.b + mDelta.b * value;
            a = mSrc.a + mDelta.a * value;

            mTargetColor.setValues(r, g, b, a);
            ((DisplayObject) mTarget).setColor(mTargetColor);
        }

        super.onUpdate(value);
    }

    public GLColor getDelta() {
        return mDelta;
    }

}

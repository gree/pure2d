/**
 * 
 */
package com.funzio.pure2D.animators;

import android.graphics.PointF;
import android.view.animation.Interpolator;

import com.funzio.pure2D.DisplayObject;

/**
 * @author long
 */
public class SkewAnimator extends TweenAnimator {
    protected float mSkewX1 = 0;
    protected float mSkewY1 = 0;
    protected float mSkewX2 = 0;
    protected float mSkewY2 = 0;
    protected PointF mDelta = new PointF();

    public SkewAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final float skewX1, final float skewY1, final float skewX2, final float skewY2) {
        mSkewX1 = skewX1;
        mSkewY1 = skewY1;
        mSkewX2 = skewX2;
        mSkewY2 = skewY2;

        mDelta.x = skewX2 - skewX1;
        mDelta.y = skewY2 - skewY1;
    }

    public void start(final float skewX1, final float skewY1, final float skewX2, final float skewY2) {
        mSkewX1 = skewX1;
        mSkewY1 = skewY1;
        mSkewX2 = skewX2;
        mSkewY2 = skewY2;

        mDelta.x = skewX2 - skewX1;
        mDelta.y = skewY2 - skewY1;

        start();
    }

    public void start(final float skewX, final float skewY) {
        if (mTarget instanceof DisplayObject) {
            final PointF skew = ((DisplayObject) mTarget).getSkew();
            start(skew != null ? skew.x : 0, skew != null ? skew.y : 0, skewX, skewY);
        }
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget instanceof DisplayObject) {
            ((DisplayObject) mTarget).setSkew(mSkewX1 + value * mDelta.x, mSkewY1 + value * mDelta.y);
        }

        super.onUpdate(value);
    }

    public PointF getDelta() {
        return mDelta;
    }
}

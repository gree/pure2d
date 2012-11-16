/**
 * 
 */
package com.funzio.pure2D.animators;

import android.graphics.PointF;
import android.view.animation.Interpolator;

/**
 * @author long
 */
public class ScaleAnimator extends TweenAnimator {
    protected float mScaleX1 = 0;
    protected float mScaleY1 = 0;
    protected float mScaleX2 = 0;
    protected float mScaleY2 = 0;
    protected PointF mDelta = new PointF();

    public ScaleAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final float scaleX1, final float scaleY1, final float scaleX2, final float scaleY2) {
        mScaleX1 = scaleX1;
        mScaleY1 = scaleY1;
        mScaleX2 = scaleX2;
        mScaleY2 = scaleY2;

        mDelta.x = scaleX2 - scaleX1;
        mDelta.y = scaleY2 - scaleY1;
    }

    public void start(final float scaleX1, final float scaleY1, final float scaleX2, final float scaleY2) {
        mScaleX1 = scaleX1;
        mScaleY1 = scaleY1;
        mScaleX2 = scaleX2;
        mScaleY2 = scaleY2;

        mDelta.x = scaleX2 - scaleX1;
        mDelta.y = scaleY2 - scaleY1;

        start();
    }

    public void start(final float scaleX, final float scaleY) {
        if (mTarget != null) {
            final PointF scale = mTarget.getScale();
            start(scale.x, scale.y, scaleX, scaleY);
        }
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {
            mTarget.setScale(mScaleX1 + value * mDelta.x, mScaleY1 + value * mDelta.y);
        }

        super.onUpdate(value);
    }

    public PointF getDelta() {
        return mDelta;
    }
}

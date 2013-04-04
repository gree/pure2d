/**
 * 
 */
package com.funzio.pure2D.animators;

import android.graphics.PointF;
import android.view.animation.Interpolator;

/**
 * @author long
 */
public class ResizeAnimator extends TweenAnimator {
    protected float mWidth1 = 0;
    protected float mHeight1 = 0;
    protected float mWidth2 = 0;
    protected float mHeight2 = 0;
    protected PointF mDelta = new PointF();

    public ResizeAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final float width1, final float height1, final float width2, final float height2) {
        mWidth1 = width1;
        mHeight1 = height1;
        mWidth2 = width2;
        mHeight2 = height2;

        mDelta.x = width2 - width1;
        mDelta.y = height2 - height1;
    }

    public void start(final float width1, final float height1, final float width2, final float height2) {
        mWidth1 = width1;
        mHeight1 = height1;
        mWidth2 = width2;
        mHeight2 = height2;

        mDelta.x = width2 - width1;
        mDelta.y = height2 - height1;

        start();
    }

    public void start(final float width, final float height) {
        if (mTarget != null) {
            final PointF size = mTarget.getSize();
            start(size.x, size.y, width, height);
        }
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null) {
            mTarget.setSize(mWidth1 + value * mDelta.x, mHeight1 + value * mDelta.y);
        }

        super.onUpdate(value);
    }

    public PointF getDelta() {
        return mDelta;
    }
}

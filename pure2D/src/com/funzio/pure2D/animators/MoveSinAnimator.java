/**
 * 
 */
package com.funzio.pure2D.animators;

import android.graphics.PointF;
import android.view.animation.Interpolator;

/**
 * @author long
 */
public class MoveSinAnimator extends TweenAnimator {
    protected float mSrcX = 0;
    protected float mSrcY = 0;
    protected float mDstX = 0;
    protected float mDstY = 0;
    protected PointF mDelta = new PointF();
    protected float mFactor;

    public MoveSinAnimator(final Interpolator interpolator, final float factor) {
        super(interpolator);

        mFactor = factor;
    }

    public void setValues(final float srcX, final float srcY, final float dstX, final float dstY) {
        mSrcX = srcX;
        mSrcY = srcY;
        mDstX = dstX;
        mDstY = dstY;

        mDelta.x = dstX - srcX;
        mDelta.y = dstY - srcY;
    }

    public void start(final float srcX, final float srcY, final float dstX, final float dstY) {
        mSrcX = srcX;
        mSrcY = srcY;
        mDstX = dstX;
        mDstY = dstY;

        mDelta.x = dstX - srcX;
        mDelta.y = dstY - srcY;

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
            mTarget.setPosition(mSrcX + value * mDelta.x, mSrcY + value * mDelta.y + (float) Math.sin((float) Math.PI * value) * mFactor);
        }

        super.onUpdate(value);
    }

    public PointF getDelta() {
        return mDelta;
    }
}

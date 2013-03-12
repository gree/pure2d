/**
 * 
 */
package com.funzio.pure2D.animators;

import android.graphics.PointF;
import android.view.animation.Interpolator;

/**
 * @author long
 */
public class MoveAnimator extends TweenAnimator {
    protected float mSrcX = 0;
    protected float mSrcY = 0;
    protected float mDstX = 0;
    protected float mDstY = 0;
    protected PointF mDelta = new PointF();

    public MoveAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final float srcX, final float srcY, final float dstX, final float dstY) {
        mSrcX = srcX;
        mSrcY = srcY;
        mDstX = dstX;
        mDstY = dstY;

        mDelta.x = dstX - srcX;
        mDelta.y = dstY - srcY;
    }

    public void setDelta(final float dx, final float dy) {
        mDelta.x = dx;
        mDelta.y = dy;
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
            if (mAccumulating) {
                mTarget.moveBy(value * mDelta.x, value * mDelta.y);
            } else {
                mTarget.setPosition(mSrcX + value * mDelta.x, mSrcY + value * mDelta.y);
            }
        }

        super.onUpdate(value);
    }

    public PointF getDelta() {
        return mDelta;
    }
}

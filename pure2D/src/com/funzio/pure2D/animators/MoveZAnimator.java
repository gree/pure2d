/**
 * 
 */
package com.funzio.pure2D.animators;

import android.view.animation.Interpolator;

import com.funzio.pure2D.DisplayObject;

/**
 * @author long
 */
public class MoveZAnimator extends TweenAnimator {
    protected float mSrc = 0;
    protected float mDelta = 0;

    public MoveZAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final float src, final float dst) {
        mSrc = src;
        mDelta = dst - src;
    }

    public void setDelta(final float delta) {
        mDelta = delta;
    }

    public void start(final float src, final float dst) {
        mSrc = src;
        mDelta = dst - src;

        start();
    }

    public void start(final float dst) {
        if (mTarget != null && mTarget instanceof DisplayObject) {
            start(((DisplayObject) mTarget).getZ(), dst);
        }
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget != null && mTarget instanceof DisplayObject) {
            final DisplayObject obj = ((DisplayObject) mTarget);
            if (mAccumulating) {
                obj.setZ(obj.getZ() + (value - mLastValue) * mDelta);
            } else {
                obj.setZ(mSrc + value * mDelta);
            }
        }

        super.onUpdate(value);
    }

    public float getDelta() {
        return mDelta;
    }
}

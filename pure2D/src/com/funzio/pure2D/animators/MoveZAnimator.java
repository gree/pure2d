/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
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

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

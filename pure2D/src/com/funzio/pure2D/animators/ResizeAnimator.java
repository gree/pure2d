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

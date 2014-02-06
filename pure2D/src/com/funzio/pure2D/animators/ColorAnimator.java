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
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.uni.UniObject;

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

    public void setValues(final int srcR, final int srcG, final int srcB, final int srcA, final int dstR, final int dstG, final int dstB, final int dstA) {
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
        } else if (mTarget instanceof UniObject) {
            start(((UniObject) mTarget).getColor(), dst);
        }
    }

    public void start(final float srcR, final float srcG, final float srcB, final float srcA, final float dstR, final float dstG, final float dstB, final float dstA) {
        setValues(srcR, srcG, srcB, srcA, dstR, dstG, dstB, dstA);

        start();
    }

    public void start(final int srcR, final int srcG, final int srcB, final int srcA, final int dstR, final int dstG, final int dstB, final int dstA) {
        setValues(srcR, srcG, srcB, srcA, dstR, dstG, dstB, dstA);

        start();
    }

    @Override
    protected void onUpdate(final float value) {
        float r, g, b, a;
        r = mSrc.r + mDelta.r * value;
        g = mSrc.g + mDelta.g * value;
        b = mSrc.b + mDelta.b * value;
        a = mSrc.a + mDelta.a * value;

        mTargetColor.setValues(r, g, b, a);
        if (mTarget instanceof DisplayObject) {
            ((DisplayObject) mTarget).setColor(mTargetColor);
        } else if (mTarget instanceof UniObject) {
            ((UniObject) mTarget).setColor(mTargetColor);
        }

        super.onUpdate(value);
    }

    public GLColor getDelta() {
        return mDelta;
    }

}

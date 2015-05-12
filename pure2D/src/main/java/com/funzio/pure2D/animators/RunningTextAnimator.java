/**
 * ****************************************************************************
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
 * ****************************************************************************
 */
/**
 *
 */
package com.funzio.pure2D.animators;

import android.view.animation.Interpolator;

import com.funzio.pure2D.text.BmfTextObject;
import com.funzio.pure2D.text.TextObject;

/**
 * @author long
 */
public class RunningTextAnimator extends TweenAnimator {
    protected float mSrc = 0;
    protected float mDst = 0;
    protected float mDelta = 0;
    protected String mFormat = "%.0f";

    public RunningTextAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setValues(final float src, final float dst) {
        mSrc = src;
        mDst = dst;
        mDelta = mDst - mSrc;
    }

    public void start(final float src, final float dst) {
        mSrc = src;
        mDst = dst;
        mDelta = mDst - mSrc;

        start();
    }

    public String getFormat() {
        return mFormat;
    }

    public RunningTextAnimator setFormat(final String format) {
        mFormat = format;
        return this;
    }

    @Override
    protected void onUpdate(final float value) {
        if (mTarget instanceof BmfTextObject) {
            ((BmfTextObject) mTarget).setText(String.format(mFormat, mSrc + value * mDelta));
        } else if (mTarget instanceof TextObject) {
            ((TextObject) mTarget).setText(String.format(mFormat, mSrc + value * mDelta), ((TextObject) mTarget).getOptions(), true);
        }

        super.onUpdate(value);
    }

    public float getDelta() {
        return mDelta;
    }
}

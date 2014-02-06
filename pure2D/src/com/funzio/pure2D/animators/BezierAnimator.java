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

import com.funzio.pure2D.geom.Bezier;

/**
 * @author long
 */
public class BezierAnimator extends TweenAnimator {
    protected PointF mStart = new PointF();
    protected PointF mEnd = new PointF();

    protected PointF mControl1 = new PointF();
    protected PointF mControl2 = new PointF();

    private float[] mCurrentPoint = new float[2];

    public BezierAnimator(final Interpolator interpolator) {
        super(interpolator);
    }

    public void setControlPoints(final PointF c1, final PointF c2) {
        mControl1.set(c1);
        mControl2.set(c2);
    }

    public void setControlPoints(final float c1x, final float c1y, final float c2x, final float c2y) {
        mControl1.set(c1x, c1y);
        mControl2.set(c2x, c2y);
    }

    public void setValues(final float srcX, final float srcY, final float dstX, final float dstY) {
        mStart.set(srcX, srcY);
        mEnd.set(dstX, dstY);
    }

    public void start(final float srcX, final float srcY, final float dstX, final float dstY) {
        mStart.set(srcX, srcY);
        mEnd.set(dstX, dstY);

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
            Bezier.getCubicBezierPoint(value, mStart, mControl1, mControl2, mEnd, mCurrentPoint);
            mTarget.setPosition(mCurrentPoint[0], mCurrentPoint[1]);
        }

        super.onUpdate(value);
    }
}

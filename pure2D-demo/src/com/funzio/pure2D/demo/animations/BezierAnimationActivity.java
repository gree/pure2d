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
package com.funzio.pure2D.demo.animations;

import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.BezierAnimator;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.shapes.Rectangular;

public class BezierAnimationActivity extends AnimationActivity {

    private Rectangular mControl1;
    private Rectangular mControl2;
    private int mPointer1 = -1;
    private int mPointer2 = -1;

    @Override
    protected Animator createAnimator() {
        final BezierAnimator animator = new BezierAnimator(null);
        animator.setDuration(1000);

        mControl1 = new Rectangular();
        mControl1.setAutoUpdateBounds(true);
        mControl1.setSize(100, 100);
        mControl1.setColor(new GLColor(1f, 0, 0, 1f));
        mControl1.setOriginAtCenter();
        mControl1.setPosition(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
        mScene.addChild(mControl1);

        mControl2 = new Rectangular();
        mControl2.setAutoUpdateBounds(true);
        mControl2.setSize(100, 100);
        mControl2.setColor(new GLColor(0, 0, 1f, 1f));
        mControl2.setOriginAtCenter();
        mControl2.setPosition(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
        mScene.addChild(mControl2);

        animator.setControlPoints(mControl1.getPosition(), mControl2.getPosition());
        animator.start(0, 0, mDisplaySize.x, mDisplaySize.y);

        return animator;
    };

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        final int len = event.getPointerCount();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {

            for (int i = 0; i < len; i++) {
                int pointerId = event.getPointerId(i);
                float x = event.getX(i);
                float y = mDisplaySize.y - event.getY(i);

                if (mControl1.getBounds().contains(x, y)) {
                    mPointer1 = pointerId;
                } else if (mControl2.getBounds().contains(x, y)) {
                    mPointer2 = pointerId;
                }
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            for (int i = 0; i < len; i++) {
                int pointerId = event.getPointerId(i);
                float x = event.getX(i);
                float y = mDisplaySize.y - event.getY(i);

                if (mPointer1 == pointerId) {
                    mControl1.setPosition(x, y);
                } else if (mPointer2 == pointerId) {
                    mControl2.setPosition(x, y);
                }

            }
            ((BezierAnimator) mAnimator).setControlPoints(mControl1.getPosition(), mControl2.getPosition());
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            for (int i = 0; i < len; i++) {
                int pointerId = event.getPointerId(i);
                if (mPointer1 == pointerId) {
                    mPointer1 = -1;
                } else if (mPointer2 == pointerId) {
                    mPointer2 = -1;
                }
            }
        }

        return true;
    }

}

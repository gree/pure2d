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
package com.funzio.pure2D.utils.ui;

import android.view.MotionEvent;
import android.view.View;

/**
 * @author long
 */
public class SwipeDetector implements View.OnTouchListener {

    private SwipeListener mListener;
    private int mMinDistance = 100;
    private float mDownX, mDownY;

    public SwipeDetector(final SwipeListener listener, final View view) {
        mListener = listener;

        if (view != null) {
            view.setOnTouchListener(this);
        }
    }

    public SwipeDetector(final SwipeListener listener) {
        mListener = listener;
    }

    public SwipeDetector() {
    }

    public SwipeListener getListener() {
        return mListener;
    }

    public void setListener(final SwipeListener listener) {
        mListener = listener;
    }

    public int getMinDistance() {
        return mMinDistance;
    }

    public void setMinDistance(final int minDistance) {
        mMinDistance = minDistance;
    }

    public boolean onTouch(final View view, final MotionEvent event) {
        if (mListener == null) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mDownX = event.getX();
                mDownY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                float deltaX = mDownX - event.getX();
                float deltaY = mDownY - event.getY();

                // horizontal
                if (Math.abs(deltaX) > mMinDistance) {
                    // left or right
                    if (deltaX < 0) {
                        mListener.onSwipeLeftToRight();
                    } else if (deltaX > 0) {
                        mListener.onSwipeRightToLeft();
                    }
                }

                // vertical
                if (Math.abs(deltaY) > mMinDistance) {
                    // top or down
                    if (deltaY < 0) {
                        mListener.onSwipeTopToBottom();
                    } else if (deltaY > 0) {
                        mListener.onSwipeBottomToTop();
                    }
                }

                return true;
            }
        }
        return false;
    }

    public static interface SwipeListener {
        public void onSwipeRightToLeft();

        public void onSwipeLeftToRight();

        public void onSwipeTopToBottom();

        public void onSwipeBottomToTop();
    }
}

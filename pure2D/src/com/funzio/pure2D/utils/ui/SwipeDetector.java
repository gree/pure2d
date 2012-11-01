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

    public SwipeDetector(final SwipeListener listener, final int minDistance) {
        mListener = listener;
        mMinDistance = minDistance;
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

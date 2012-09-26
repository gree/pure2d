/**
 * 
 */
package com.funzio.pure2D.animators;

import android.os.Handler;

/**
 * @author long
 */

@Deprecated
public class SimpleTweenAnimator extends SimpleAnimator {
    private float mStartValue = 0;
    private float mEndValue = 1;
    private float mCurrentValue = 0;
    private float mDelta = 0;

    public SimpleTweenAnimator(final Handler handler) {
        super(handler);
    }

    @Override
    protected int step() {
        super.step();

        final float scale = (float) mFrameIndex / (float) (mFrameNum - 1);
        if (mInterpolator == null) {
            mCurrentValue = mStartValue + mDelta * scale;
        } else {
            mCurrentValue = mStartValue + mDelta * mInterpolator.getInterpolation(scale);
        }

        return mFrameIndex;
    }

    @Override
    public void end(final boolean sendEvent) {
        mCurrentValue = mEndValue;
        super.end(sendEvent);
    }

    /**
     * @return the startValue
     */
    public float getStartValue() {
        return mStartValue;
    }

    /**
     * @param startValue the startValue to set
     */
    public void setStartValue(final float startValue) {
        if (isStarted()) {
            cancel();
        }

        mStartValue = startValue;
        mDelta = mEndValue - mStartValue;
    }

    /**
     * @return the endValue
     */
    public float getEndValue() {
        return mEndValue;
    }

    /**
     * @param endValue the endValue to set
     */
    public void setEndValue(final float endValue) {
        if (isStarted()) {
            cancel();
        }

        mEndValue = endValue;
        mDelta = mEndValue - mStartValue;
    }

    /**
     * @return the currentValue
     */
    public float getCurrentValue() {
        return mCurrentValue;
    }

    public void start(final float start, final float end) {
        setStartValue(start);
        setEndValue(end);

        start();
    }
}

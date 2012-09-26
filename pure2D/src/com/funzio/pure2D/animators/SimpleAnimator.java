/**
 * 
 */
package com.funzio.pure2D.animators;

import android.os.Handler;
import android.view.animation.Interpolator;

/**
 * @author long
 */

@Deprecated
public abstract class SimpleAnimator {

    private Object mTarget;
    private boolean mStarted;
    private int mDuration;
    private int mStartDelay;
    private boolean mRunning;

    protected int mFrameDelay;
    protected int mFrameNum;
    protected int mFrameIndex;

    protected Interpolator mInterpolator;
    protected AnimatorListener mListener;

    private Handler mHandler;
    private Runnable mRunable = new Runnable() {

        @Override
        public void run() {
            // done check
            if (!hasMore()) {
                return;
            }

            // next step
            step();

            if (!mStarted) {
                mStarted = true;
                // start event
                if (mListener != null) {
                    mListener.onAnimationStart(SimpleAnimator.this);
                }
            } else {
                // step event
                if (mListener != null) {
                    mListener.onAnimationStep(SimpleAnimator.this);
                }
            }

            if (hasMore()) {
                mHandler.postDelayed(mRunable, mFrameDelay);
            } else {
                end(true);
            }
        }
    };

    public SimpleAnimator(final Handler handler) {
        mHandler = handler;
    }

    /**
     * @return the target
     */
    public Object getTarget() {
        return mTarget;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(final Object target) {
        mTarget = target;
    }

    /**
     * @return the duration
     */
    public int getDuration() {
        return mDuration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(final int duration) {
        mDuration = duration;

        mFrameNum = mFrameDelay > 0 ? mDuration / mFrameDelay : mDuration;
    }

    /**
     * @return the frameDelay
     */
    public int getFrameDelay() {
        return mFrameDelay;
    }

    /**
     * @param frameDelay the frameDelay to set
     */
    public void setFrameDelay(final int frameDelay) {
        mFrameDelay = frameDelay;

        mFrameNum = mFrameDelay > 0 ? mDuration / mFrameDelay : mDuration;
    }

    /**
     * @return the startDelay
     */
    public int getStartDelay() {
        return mStartDelay;
    }

    /**
     * @param startDelay the startDelay to set
     */
    public void setStartDelay(final int startDelay) {
        mStartDelay = startDelay;
    }

    /**
     * @return the interpolator
     */
    public Interpolator getInterpolator() {
        return mInterpolator;
    }

    /**
     * @param interpolator the interpolator to set
     */
    public void setInterpolator(final Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    public void start() {
        // check and end
        if (mRunning) {
            cancel();
        }

        // start index
        mFrameIndex = 0;

        // flag
        mRunning = true;

        // start the thread
        mHandler.postDelayed(mRunable, mStartDelay);
    }

    protected int step() {
        mFrameIndex++;

        return mFrameIndex;
    }

    protected boolean hasMore() {
        return mFrameIndex < mFrameNum - 1;
    }

    protected void stop() {
        if (!mRunning) {
            return;
        }

        // start the thread
        mHandler.removeCallbacks(mRunable);

        mStarted = false;
        mRunning = false;
    }

    public void end(final boolean sendEvent) {
        stop();

        mFrameIndex = mFrameNum - 1;

        // event
        if (sendEvent && mListener != null) {
            mListener.onAnimationEnd(SimpleAnimator.this);
        }
    }

    public void cancel() {
        stop();

        if (mListener != null) {
            mListener.onAnimationCancel(this);
        }
    }

    /**
     * @return the started
     */
    public boolean isStarted() {
        return mStarted;
    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return mRunning;
    }

    /**
     * @return the listener
     */
    public AnimatorListener getListener() {
        return mListener;
    }

    /**
     * @param listener the listener to set
     */
    public void setListener(final AnimatorListener listener) {
        mListener = listener;
    }

    /**
     * @return the handler
     */
    public Handler getHandler() {
        return mHandler;
    }

    /**
     * @param handler the handler to set
     */
    public void setHandler(final Handler handler) {
        mHandler = handler;
    }

    public interface AnimatorListener {
        public void onAnimationCancel(SimpleAnimator animator);

        public void onAnimationEnd(SimpleAnimator animator);

        // public void onAnimationRepeat(Animator animator);

        public void onAnimationStart(SimpleAnimator animator);

        public void onAnimationStep(SimpleAnimator animator);
    }

}

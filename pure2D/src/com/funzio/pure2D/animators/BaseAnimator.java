/**
 * 
 */
package com.funzio.pure2D.animators;

import com.funzio.pure2D.Manipulatable;

/**
 * @author long
 */
public class BaseAnimator implements Animator {

    protected Manipulatable mTarget;
    protected AnimatorListener mListener;

    protected int mElapsedTime = 0;
    protected boolean mRunning = false;

    @Override
    public void setTarget(final Manipulatable target) {
        mTarget = target;
    }

    @Override
    public Manipulatable getTarget() {
        return mTarget;
    }

    @Override
    public boolean update(final int deltaTime) {
        if (mRunning) {
            mElapsedTime += deltaTime;
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.Animator#start()
     */
    @Override
    public void start() {
        if (mRunning) {
            end();
        }

        mElapsedTime = 0;
        mRunning = true;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.Animator#stop()
     */
    @Override
    public void stop() {
        mRunning = false;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.Animator#end()
     */
    @Override
    public void end() {
        mRunning = false;

        if (mListener != null) {
            mListener.onAnimationEnd(this);
        }
    }

    public boolean isRunning() {
        return mRunning;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.Animator#setListener(com.funzio.pure2D.animators.Animator.AnimatorListener)
     */
    @Override
    public void setListener(final AnimatorListener listener) {
        mListener = listener;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.Animator#getListener()
     */
    @Override
    public AnimatorListener getListener() {
        return mListener;
    }

}

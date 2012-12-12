package com.funzio.pure2D.containers;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.VelocityAnimator;

/**
 * @author long
 */
public class VWheel extends VGroup implements Wheel, Animator.AnimatorListener {
    // spinning
    protected VelocityAnimator mAnimator;
    protected boolean mStopping = false;

    public VWheel() {
        // always, because this is a wheel
        mRepeating = true;

        // animator
        mAnimator = new VelocityAnimator();
        mAnimator.setListener(this);
        addManipulator(mAnimator);
    }

    public void spin(final float veloc) {
        spin(veloc, 0, 0);
    }

    public void spin(final float veloc, final float acceleration) {
        spin(veloc, acceleration, 0);
    }

    public void spin(final float veloc, final float acceleration, final int maxSpinTime) {
        mAnimator.start(veloc, acceleration, maxSpinTime);
    }

    public void stop() {
        mAnimator.stop();
    }

    /**
     * @return the velocity
     */
    public float getVelocity() {
        return mAnimator.getVelocity();
    }

    /**
     * @return the Acceleration
     */
    public float getAcceleration() {
        return mAnimator.getAcceleration();
    }

    /**
     * @return the maxSpinTime
     */
    public int getMaxSpinTime() {
        return mAnimator.getDuration();
    }

    @Override
    protected void positionChildren() {
        super.positionChildren();

        if (mStopping) {
            mStopping = false;
            onStop();
        }
    }

    protected void onStop() {
        // TODO to be overridden
    }

    @Override
    public void onAnimationEnd(final Animator animator) {
        mStopping = true;
    }

    @Override
    public void onAnimationUpdate(final Animator animator, final float value) {
        scrollBy(0, -value);
    }
}

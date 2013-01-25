package com.funzio.pure2D.containers;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.VelocityAnimator;

/**
 * @author long
 */
public class VWheel extends VGroup implements Wheel, Animator.AnimatorListener {
    // spinning
    protected VelocityAnimator mAnimator;

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

    /**
     * Spin the the closest child based on the specified direction which is either positive or negative
     * 
     * @param positive
     * @param acceleration
     * @param duration
     */
    public void spinToSnap(final boolean positive, final float acceleration, final int duration) {
        final float distance2Travel = getSnapDelta(positive);
        final float accel = distance2Travel > 0 ? -acceleration : acceleration;
        float veloc = distance2Travel / duration - 0.5f * accel * duration; // Real physics!
        spin(-veloc, -accel, duration);
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

    public void onAnimationEnd(final Animator animator) {
        // TODO to be overriden
    }

    public void onAnimationUpdate(final Animator animator, final float value) {
        scrollBy(0, -value);
    }
}

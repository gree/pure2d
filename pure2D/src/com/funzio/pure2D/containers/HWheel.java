package com.funzio.pure2D.containers;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.Animator.AnimatorListener;
import com.funzio.pure2D.animators.VelocityAnimator;

/**
 * @author long
 */
public class HWheel extends HGroup implements Wheel, AnimatorListener {

    // spinning
    protected VelocityAnimator mAnimator;

    public HWheel() {
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

    public void onAnimationEnd(final Animator animator) {
        // TODO to be overriden
    }

    public void onAnimationUpdate(final Animator animator, final float value) {
        scrollBy(-value, 0);
    }
}

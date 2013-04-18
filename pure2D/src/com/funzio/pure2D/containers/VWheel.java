package com.funzio.pure2D.containers;

import android.view.MotionEvent;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.VelocityAnimator;

/**
 * Wheel is an extended Group that allows you to spin!
 * 
 * @author long
 */
public class VWheel extends VGroup implements Wheel, Animator.AnimatorListener {
    // spinning
    protected VelocityAnimator mAnimator;

    protected float mSwipeDelta = 0;
    protected float mSwipeVelocity = 0;
    protected boolean mStoppable = true;

    public VWheel() {
        // always, because this is a wheel
        mRepeating = true;

        // animator
        mAnimator = new VelocityAnimator();
        mAnimator.setListener(this);
        addManipulator(mAnimator);
    }

    @Override
    protected void swipe(final float delta) {
        super.swipe(delta);

        // average velocity
        mSwipeVelocity = (mSwipeVelocity + (delta - mSwipeDelta) / Scene.DEFAULT_MSPF) * 0.5f;
        mSwipeDelta = delta;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.VGroup#stopSwipe(float)
     */
    @Override
    protected void stopSwipe() {
        super.stopSwipe();

        // spin
        spin(mSwipeVelocity, mSwipeVelocity > 0 ? -DEFAULT_SPIN_ACCELERATION : DEFAULT_SPIN_ACCELERATION);

        // reset
        mSwipeDelta = 0;
        mSwipeVelocity = 0;
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
     * Spin a specified distance
     * 
     * @param distance
     * @param acceleration
     * @param duration
     */
    public void spinDistance(final float distance, final float acceleration, final int duration) {
        final float accel = distance > 0 ? -acceleration : acceleration; // against veloc
        final float veloc = distance / duration + 0.5f * accel * duration; // Real physics!
        mAnimator.start(-veloc, accel, duration);
    }

    /**
     * Spin the the closest child based on the specified direction which is either positive or negative
     * 
     * @param positive
     * @param acceleration
     * @param duration
     */
    public void spinToSnap(final boolean positive, final float acceleration, final int duration) {
        spinDistance(getSnapDelta(positive), acceleration, duration);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.VGroup#startSwipe()
     */
    @Override
    protected void startSwipe() {
        // stop animation first
        mAnimator.stop();

        super.startSwipe();
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

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.VGroup#onTouchDown(android.view.MotionEvent)
     */
    @Override
    protected void onTouchDown(final MotionEvent event) {
        super.onTouchDown(event);

        // stop spining
        if (mAnimator.isRunning() && mStoppable) {
            mAnimator.stop();
        }
    }
}

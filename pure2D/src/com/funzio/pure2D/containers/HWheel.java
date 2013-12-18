package com.funzio.pure2D.containers;

import android.view.MotionEvent;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.Animator.AnimatorListener;
import com.funzio.pure2D.animators.MoveAnimator;
import com.funzio.pure2D.animators.VelocityAnimator;
import com.funzio.pure2D.particles.nova.NovaConfig;
import com.funzio.pure2D.ui.UIManager;

/**
 * Wheel is an extended Group that allows you to spin!
 * 
 * @author long
 */
public class HWheel extends HGroup implements Wheel, AnimatorListener {
    // spinning
    protected float mSpinVelocity;
    protected VelocityAnimator mVelocAnimator;

    // snapping
    protected boolean mSnapEnabled = false;
    private MoveAnimator mSnapAnimator;
    private float mSnapAnchor;

    protected float mSwipeDelta = 0;
    protected float mSwipeVelocity = 0;
    protected boolean mStoppable = true;

    public HWheel() {
        // always, because this is a wheel
        mRepeating = true;

        // animator
        mVelocAnimator = new VelocityAnimator();
        mVelocAnimator.setListener(this);
        addManipulator(mVelocAnimator);
    }

    @Override
    protected void swipe(final float delta) {
        super.swipe(delta);

        // average velocity
        mSwipeVelocity = (mSwipeVelocity + (delta - mSwipeDelta) / Scene.DEFAULT_MSPF) * 0.5f;
        mSwipeDelta = delta;
    }

    @Override
    protected void stopSwipe() {
        super.stopSwipe();

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
        if (mSnapAnimator != null) {
            mSnapAnimator.stop();
        }

        mSpinVelocity = veloc;
        mVelocAnimator.start(veloc, acceleration, maxSpinTime);
    }

    /**
     * Spin a specified distance
     * 
     * @param distance
     * @param acceleration
     * @param duration
     */
    public void spinDistance(final float distance, final float acceleration, final int duration) {
        // range check
        if (Math.abs(distance) < 1) {
            return;
        }

        if (mSnapAnimator != null) {
            mSnapAnimator.stop();
        }

        final float accel = distance > 0 ? -acceleration : acceleration; // against veloc
        final float veloc = distance / duration + 0.5f * accel * duration; // Real physics!
        mSpinVelocity = veloc;
        mVelocAnimator.start(-veloc, accel, duration);
    }

    /**
     * Spin to the closest child based on the specified direction which is either positive or negative
     * 
     * @param positive
     * @param acceleration
     * @param duration
     */
    @Deprecated
    public void spinToSnap(final boolean positive, final float acceleration, final int duration) {
        spinDistance(getSnapDelta(positive), acceleration, duration);
    }

    public void snapTo(final float position) {
        if (mSnapAnimator == null) {
            mSnapAnimator = new MoveAnimator(NovaConfig.INTER_DECELERATE);
            addManipulator(mSnapAnimator);
            mSnapAnimator.setTarget(null); // no target
            mSnapAnimator.setListener(this);
        }

        mSnapAnchor = mScrollPosition.x;
        mSnapAnimator.setDuration((int) Math.abs(position - mScrollPosition.x));
        mSnapAnimator.start(mScrollPosition.x, 0, position, 0);
    }

    /**
     * Spin to a specific position
     * 
     * @param position
     * @param acceleration
     * @param duration
     */
    public void spinTo(final float position, final float acceleration, final int duration) {
        spinDistance(position - mScrollPosition.x, acceleration, duration);
    }

    /**
     * Spin to the Start
     * 
     * @param acceleration
     * @param duration
     */
    public void spinToStart(final float acceleration, final int duration) {
        spinTo(0, acceleration, duration);
    }

    /**
     * Spin to the End
     * 
     * @param acceleration
     * @param duration
     */
    public void spinToEnd(final float acceleration, final int duration) {
        spinTo(mScrollMax.x, acceleration, duration);
    }

    @Override
    protected void startSwipe() {
        // stop animation first
        mVelocAnimator.stop();

        if (mSnapAnimator != null) {
            mSnapAnimator.stop();
        }

        super.startSwipe();
    }

    public void stop() {
        mVelocAnimator.stop();
    }

    /**
     * @return the velocity
     */
    public float getVelocity() {
        return mVelocAnimator.getVelocity();
    }

    /**
     * @return the Acceleration
     */
    public float getAcceleration() {
        return mVelocAnimator.getAcceleration();
    }

    /**
     * @return the maxSpinTime
     */
    public int getMaxSpinTime() {
        return mVelocAnimator.getDuration();
    }

    public boolean isSnapEnabled() {
        return mSnapEnabled;
    }

    public void setSnapEnabled(final boolean snapEnabled) {
        mSnapEnabled = snapEnabled;

        if (snapEnabled && mSnapAnimator == null) {
            mSnapAnimator = new MoveAnimator(NovaConfig.INTER_DECELERATE);
            addManipulator(mSnapAnimator);
            mSnapAnimator.setTarget(null); // no target
            mSnapAnimator.setListener(this);
        }
    }

    public void onAnimationEnd(final Animator animator) {
        if (animator == mVelocAnimator) {
            if (mSnapEnabled) {
                final float snapDelta = getSnapDelta(mSpinVelocity < 0);
                mSnapAnchor = mScrollPosition.x;
                mSnapAnimator.setDuration((int) Math.abs(snapDelta) * 5);
                mSnapAnimator.start(0, 0, snapDelta, 0);
            }
        }
    }

    public void onAnimationUpdate(final Animator animator, final float value) {
        if (animator == mVelocAnimator) {
            scrollBy(-value, 0);
        } else if (animator == mSnapAnimator) {
            scrollTo(mSnapAnchor + mSnapAnimator.getDelta().x * value, 0);
        }
    }

    @Override
    protected void onTouchDown(final MotionEvent event) {
        super.onTouchDown(event);

        // stop spining
        if (mVelocAnimator.isRunning() && mStoppable) {
            mVelocAnimator.stop();
        }
    }

    @Override
    public void setXMLAttributes(final XmlPullParser xmlParser, final UIManager manager) {
        super.setXMLAttributes(xmlParser, manager);

        final String snapEnabled = xmlParser.getAttributeValue(null, ATT_SNAP_ENABLED);
        if (snapEnabled != null) {
            setSnapEnabled(Boolean.valueOf(snapEnabled));
        }
    }
}

/**
 * 
 */
package com.funzio.pure2D.animators;

import com.funzio.pure2D.Scene;

/**
 * @author long
 */
public class RecursiveTrajectoryAnimator extends TrajectoryAnimator {

    protected float mDecelerationRate = 0.75f;
    protected float mMinVelocity = 1;

    public RecursiveTrajectoryAnimator() {
        super();
    }

    /**
     * @param ground
     */
    public RecursiveTrajectoryAnimator(final float ground) {
        super(ground);
    }

    @Override
    public void end() {
        final float newVelocity = mVelocity * mDecelerationRate;
        if (Math.abs(newVelocity) >= mMinVelocity) {
            stop();

            // and restart
            if (mAxisSystem == Scene.AXIS_BOTTOM_LEFT) {
                start(newVelocity, mSin < 0.0f ? -mAngle : mAngle);
            } else {
                start(newVelocity, mSin > 0.0f ? -mAngle : mAngle);
            }
        } else {
            super.end();
        }
    }

    public float getDecelerationRate() {
        return mDecelerationRate;
    }

    public void setDecelerationRate(final float decelerationRate) {
        mDecelerationRate = decelerationRate;
    }

    public float getMinVelocity() {
        return mMinVelocity;
    }

    public void setMinVelocity(final float minVelocity) {
        mMinVelocity = minVelocity;
    }

}

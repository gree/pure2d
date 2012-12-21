/**
 * 
 */
package com.funzio.pure2D.animators;

/**
 * @author long
 */
public class RecursiveTrajectoryAnimator extends TrajectoryAnimator {

    protected float mDecelerationRate = 0.5f;
    protected float mMinVelocity = 1;

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
            start(newVelocity, mAngle);
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

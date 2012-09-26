package com.funzio.pure2D.containers;

/**
 * @author long
 */
public class VWheel extends VGroup implements Wheel {

    protected float mAcceleration = -0.002f;
    protected float mVelocity = 0;

    private int mMaxSpinTime = 0; // 0: unlimited
    private int mElapsedSpinTime = 0;

    public VWheel() {
        // always, because this is a wheel
        mRepeating = true;
    }

    public void spin(final float veloc) {
        mVelocity = veloc;
        mAcceleration = 0;
        mMaxSpinTime = 0;

        // reset elapsed time
        mElapsedSpinTime = 0;
    }

    public void spin(final float veloc, final float acceleration) {
        mVelocity = veloc;
        mAcceleration = acceleration;
        mMaxSpinTime = 0;

        // reset elapsed time
        mElapsedSpinTime = 0;
    }

    public void spin(final float veloc, final float acceleration, final int maxSpinTime) {
        mVelocity = veloc;
        mAcceleration = acceleration;
        mMaxSpinTime = maxSpinTime;

        // reset elapsed time
        mElapsedSpinTime = 0;
    }

    public void stop() {
        mVelocity = 0;
        mElapsedSpinTime = 0;
    }

    /**
     * @return the velocity
     */
    public float getVelocity() {
        return mVelocity;
    }

    /**
     * @return the Acceleration
     */
    public float getAcceleration() {
        return mAcceleration;
    }

    /**
     * @return the maxSpinTime
     */
    public int getMaxSpinTime() {
        return mMaxSpinTime;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.DisplayGroup#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (mVelocity != 0 && ((mMaxSpinTime > 0 && mElapsedSpinTime < mMaxSpinTime) || mMaxSpinTime <= 0)) {

            int myDeltaTime = deltaTime;
            boolean isTimeUp = false;
            // if there is a time limit
            if (mMaxSpinTime > 0 && mElapsedSpinTime + myDeltaTime > mMaxSpinTime) {
                // uh oh, time's up!
                myDeltaTime = mMaxSpinTime - mElapsedSpinTime;
                isTimeUp = true;
            }
            mElapsedSpinTime += myDeltaTime;

            float deltaVeloc = mAcceleration * myDeltaTime;
            float newVeloc = mVelocity + deltaVeloc;

            // scroll now
            float delta = mVelocity * myDeltaTime + 0.5f * deltaVeloc * myDeltaTime; // Real physics, Newton's
            mScrollPosition.y -= delta;
            positionChildren();

            // direction changed or time's up?
            if ((mMaxSpinTime <= 0 && newVeloc * mVelocity <= 0) || isTimeUp) {
                mVelocity = 0;
                // done! do callback
                onStop();
            } else {
                // update veloc
                mVelocity = newVeloc;
            }
        } else if (mVelocity != 0) {
            mVelocity = 0;
            // done! do callback
            onStop();
        }

        return super.update(deltaTime);
    }

    /**
     * Called when the wheel stops from spinning
     */
    protected void onStop() {
        // TODO to be overriden
    }
}

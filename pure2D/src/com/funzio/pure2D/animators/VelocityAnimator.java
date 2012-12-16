package com.funzio.pure2D.animators;

/**
 * @author long
 */
public class VelocityAnimator extends BaseAnimator {

    protected float mAcceleration = 0;
    protected float mVelocity = 0;
    protected int mDuration = 0; // 0: unlimited
    protected int mPendingElapse = 0;

    public VelocityAnimator() {
        super();
    }

    public VelocityAnimator(final float veloc, final float acceleration, final int duration) {
        super();

        mVelocity = veloc;
        mAcceleration = acceleration;
        mDuration = duration;
        mPendingElapse = 0;
    }

    public void start(final float veloc, final float acceleration, final int duration) {
        mVelocity = veloc;
        mAcceleration = acceleration;
        mDuration = duration;
        mPendingElapse = 0;

        super.start();
    }

    @Override
    public void stop() {
        mVelocity = 0;

        super.stop();
    }

    /**
     * @return the velocity
     */
    public float getVelocity() {
        return mVelocity;
    }

    public void setVelocity(final float velocity) {
        mVelocity = velocity;
    }

    /**
     * @return the Acceleration
     */
    public float getAcceleration() {
        return mAcceleration;
    }

    public void setAcceleration(final float acceleration) {
        mAcceleration = acceleration;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.BaseAnimator#elapse(int)
     */
    @Override
    public void elapse(final int elapsedTimeDelta) {
        mPendingElapse += elapsedTimeDelta;
    }

    /**
     * @return the Duration in ms
     */
    public int getDuration() {
        return mDuration;
    }

    public int getRemainingTime() {
        return mDuration - mElapsedTime;
    }

    @Override
    public boolean update(final int deltaTime) {
        if (mRunning) {

            if (mVelocity != 0 && ((mDuration > 0 && mElapsedTime < mDuration) || mDuration <= 0)) {

                int myDeltaTime = deltaTime + mPendingElapse;
                mPendingElapse = 0;
                boolean isTimeUp = false;
                // if there is a time limit
                if (mDuration > 0 && mElapsedTime + myDeltaTime > mDuration) {
                    // uh oh, time's up!
                    myDeltaTime = mDuration - mElapsedTime;
                    isTimeUp = true;
                }
                mElapsedTime += myDeltaTime;

                float deltaVeloc = mAcceleration * myDeltaTime;
                float newVeloc = mVelocity + deltaVeloc;

                // scroll now
                float delta = mVelocity * myDeltaTime + 0.5f * deltaVeloc * myDeltaTime; // Real physics, Newton's
                onUpdate(delta);

                // direction changed or time's up?
                if ((mDuration <= 0 && newVeloc * mVelocity <= 0) || isTimeUp) {
                    mVelocity = 0;
                    // done! do callback
                    end();
                } else {
                    // update veloc
                    mVelocity = newVeloc;
                }
            } else if (mVelocity != 0) {
                mVelocity = 0;
                // done! do callback
                end();
            }

            return true;
        }

        return false;
    }

    protected void onUpdate(final float value) {
        if (mListener != null) {
            mListener.onAnimationUpdate(this, value);
        }
    }
}

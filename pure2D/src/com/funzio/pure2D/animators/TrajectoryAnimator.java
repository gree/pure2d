/**
 * All formulas in this are based on: http://en.wikipedia.org/wiki/Trajectory_of_a_projectile
 */
package com.funzio.pure2D.animators;

import android.graphics.PointF;
import android.util.FloatMath;

/**
 * @author long
 */
public class TrajectoryAnimator extends BaseAnimator {
    public static float TIME_FACTOR = 100;

    protected float mGravity = 10f;
    protected float mSrcX = 0;
    protected float mSrcY = 0;

    protected float mGround = 0;
    protected float mVelocity;
    protected float mAngle;
    protected float mDistance;
    protected float mDuration;
    protected float mSin;
    protected float mCos;

    public TrajectoryAnimator(final float ground) {
        super();

        mGround = ground;
    }

    public void start(final float srcX, final float srcY, final float velocity, final float angle) {
        mSrcX = srcX;
        mSrcY = srcY;

        mVelocity = velocity;
        mAngle = angle;
        mSin = FloatMath.sin(mAngle);
        mCos = FloatMath.cos(mAngle);

        // pre-cals
        final float vcos = mVelocity * mCos;
        final float vsin = mVelocity * mSin;
        mDistance = (vcos / mGravity) * (vsin + FloatMath.sqrt(vsin * vsin + 2 * mGravity * (mSrcY - mGround)));
        mDuration = TIME_FACTOR * mDistance / vcos;

        start();
    }

    public void start(final float velocity, final float angle) {
        if (mTarget != null) {
            final PointF position = mTarget.getPosition();
            start(position.x, position.y, velocity, angle);
        }
    }

    public void start(final float srcX, final float srcY, final float velocity, final float dstX, final float dstY) {
        mSrcX = srcX;
        mSrcY = srcY;
        mVelocity = velocity;

        final float deltaX = dstX - srcX;
        final float deltaY = dstY - srcY;

        final float v2 = mVelocity * mVelocity;
        // find the angle to hit the destination
        mAngle = (float) Math.atan((v2 + FloatMath.sqrt(v2 * v2 - mGravity * (mGravity * deltaX * deltaX + 2 * deltaY * v2))) / (mGravity * deltaX));
        mSin = FloatMath.sin(mAngle);
        mCos = FloatMath.cos(mAngle);

        final float vcos = mVelocity * mCos;
        // final float vsin = mVelocity * mSin;
        mDistance = deltaX;// (vcos / GRAVITY) * (vsin + FloatMath.sqrt(vsin * vsin + 2 * GRAVITY * mSrcY));
        mDuration = TIME_FACTOR * mDistance / vcos;

        start();
    }

    public void start(final float velocity, final float dstX, final float dstY) {
        if (mTarget != null) {
            final PointF position = mTarget.getPosition();
            start(position.x, position.y, velocity, dstX, dstY);
        }
    }

    @Override
    public boolean update(final int deltaTime) {
        if (super.update(deltaTime)) {

            mElapsedTime += deltaTime;

            if (mTarget != null) {
                final float t = Math.min(mElapsedTime, mDuration) / TIME_FACTOR;
                final float x = mSrcX + mVelocity * t * mCos;
                final float y = mSrcY + mVelocity * t * mSin - 0.5f * mGravity * t * t;
                mTarget.setPosition(x, y);
            }

            if (mElapsedTime >= mDuration) {
                // force end
                end();
            }

            return true;
        }

        return false;
    }

    public float getGravity() {
        return mGravity;
    }

    public void setGravity(final float gravity) {
        mGravity = gravity;
    }
}

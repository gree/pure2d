/**
 * 
 */
package com.funzio.pure2D.containers;

import android.util.FloatMath;

import com.funzio.pure2D.DisplayObject;

/**
 * @author long
 */
public class Wheel3D extends DisplayGroup {
    public static final int ORIENTATION_X = 0;
    public static final int ORIENTATION_Y = 1;

    protected float mStartAngle = 0;
    protected float mGapAngle = 0;
    protected int mOrientation = ORIENTATION_X;
    protected float mRadius = 0;

    // alpha range
    protected float mAlpha1 = 0.5f;
    protected float mAlpha2 = 1f;
    // z range
    protected float mZ1 = -1f;
    protected float mZ2 = 1f;

    // spinning
    protected float mAcceleration = -0.002f;
    protected float mVelocity = 0;
    private int mMaxSpinTime = 0; // 0: unlimited
    private int mElapsedSpinTime = 0;

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(final float radius) {
        mRadius = radius;

        positionChildren();
    }

    /**
     * @return the gap
     */
    public float getGap() {
        return mGapAngle;
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(final float angle) {
        mStartAngle = angle;

        // reposition the children
        positionChildren();
    }

    public void scrollToChild(final DisplayObject child) {
        scrollToChild(mChildren.indexOf(child));
    }

    public void scrollToChild(final int childIndex) {
        if (childIndex < 0 || childIndex >= mNumChildren) {
            return;
        }

        // bring the selected child to center-front
        mStartAngle = -childIndex * mGapAngle + 90;

        // reposition the children
        positionChildren();
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setOrientation(final int orientation) {
        mOrientation = orientation;
        positionChildren();
    }

    public void scrollToAngle(final float angle) {
        mStartAngle = angle;

        // reposition the children
        positionChildren();
    }

    public void scrollByAngle(final float deltaAngle) {
        mStartAngle += deltaAngle;

        // reposition the children
        positionChildren();
    }

    public void scrollByDistance(final float deltaDistance) {
        mStartAngle += 180 * deltaDistance / (mRadius * 2);

        // reposition the children
        positionChildren();
    }

    public void scrollToDistance(final float distance) {
        mStartAngle = 180 * distance / (mRadius * 2);

        // reposition the children
        positionChildren();
    }

    /**
     * Set alpha range. To ignore alpha, set either alpha1 or alpha2 to a negative number.
     * 
     * @param alpha1
     * @param alpha2
     */
    public void setAlphaRange(final float alpha1, final float alpha2) {
        mAlpha1 = alpha1;
        mAlpha2 = alpha2;

        positionChildren();
    }

    public void setZRange(final float z1, final float z2) {
        mZ1 = z1;
        mZ2 = z2;

        positionChildren();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.DisplayGroup#onAddedChild(com.funzio.pure2D.DisplayObject)
     */
    @Override
    protected void onAddedChild(final DisplayObject child) {
        super.onAddedChild(child);

        // angle
        mGapAngle = mNumChildren == 0 ? 0 : (360f / mNumChildren);

        positionChildren();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.DisplayGroup#onRemovedChild(com.funzio.pure2D.DisplayObject)
     */
    @Override
    protected void onRemovedChild(final DisplayObject child) {
        super.onRemovedChild(child);

        // angle
        mGapAngle = mNumChildren == 0 ? 0 : (360f / mNumChildren);

        positionChildren();
    }

    protected void getAnglePoint(final float angle, final float xy[]) {
        final float radian = (float) ((angle / 180f) * Math.PI);
        xy[0] = FloatMath.cos(radian);
        xy[1] = FloatMath.sin(radian);
    }

    protected void positionChildren() {
        float angle = mStartAngle;
        float x, z, z2, radian;
        DisplayObject child;
        final float alphaRange = mAlpha2 - mAlpha1;
        final float zRange = mZ2 - mZ1;
        for (int i = 0; i < mNumChildren; i++) {
            child = mChildren.get(i);
            radian = (float) ((angle / 180f) * Math.PI);
            x = FloatMath.cos(radian);
            z = FloatMath.sin(radian);
            z2 = (z + 1) / 2f;

            // orientation
            if (mOrientation == ORIENTATION_X) {
                child.setX(x * mRadius);
            } else {
                child.setY(x * mRadius);
            }

            // set z
            child.setZ(mZ1 + zRange * z2);

            // set alpha
            if (mAlpha1 > 0 && mAlpha2 > 0) {
                child.setAlpha(mAlpha1 + alphaRange * z2);
            }

            // next
            angle += mGapAngle;
        }
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
            scrollByAngle(-delta);

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

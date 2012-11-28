/**
 * 
 */
package com.funzio.pure2D.containers;

import android.util.FloatMath;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.VelocityAnimator;

/**
 * @author long
 */
public class Wheel3D extends DisplayGroup implements Animator.AnimatorListener {
    public static final int ORIENTATION_X = 0;
    public static final int ORIENTATION_Y = 1;

    protected float mStartAngle = 0;
    protected float mGapAngle = -1;
    protected float mCurrentGapAngle = 0;
    protected int mOrientation = ORIENTATION_X;
    protected float mRadius = 0;

    // alpha range
    protected float mAlpha1 = 0.5f;
    protected float mAlpha2 = 1f;
    // z/depth range
    protected float mDepth1 = -1f;
    protected float mDepth2 = 1f;

    // spinning
    protected VelocityAnimator mAnimator;

    private boolean mChildrenPositionInvalidated = false;

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.DisplayGroup#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (mChildrenPositionInvalidated) {
            positionChildren();
            mChildrenPositionInvalidated = false;
        }

        return super.update(deltaTime);
    }

    protected void invalidateChildrenPosition() {
        mChildrenPositionInvalidated = true;
        invalidate();
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(final float radius) {
        mRadius = radius;

        invalidateChildrenPosition();
    }

    /**
     * @return the gap angle
     */
    public float getGapAngle() {
        return mGapAngle;
    }

    public void setGapAngle(final float angle) {
        mGapAngle = angle;

        // angle
        mCurrentGapAngle = (mGapAngle >= 0) ? mGapAngle : (mNumChildren == 0 ? 0 : (360f / mNumChildren));

        invalidateChildrenPosition();
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(final float angle) {
        mStartAngle = angle;

        // reposition the children
        invalidateChildrenPosition();
    }

    public void scrollToChild(final DisplayObject child) {
        scrollToChild(mChildren.indexOf(child));
    }

    public void scrollToChild(final int childIndex) {
        if (childIndex < 0 || childIndex >= mNumChildren) {
            return;
        }

        // bring the selected child to center-front
        mStartAngle = -childIndex * mCurrentGapAngle + 90;

        // reposition the children
        invalidateChildrenPosition();
    }

    public int getFrontChildIndex() {
        return (int) ((360 + 90 - mStartAngle % 360) / mCurrentGapAngle) % mNumChildren;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setOrientation(final int orientation) {
        mOrientation = orientation;
        invalidateChildrenPosition();
    }

    public void scrollToAngle(final float angle) {
        mStartAngle = angle;

        // reposition the children
        invalidateChildrenPosition();
    }

    public void scrollByAngle(final float deltaAngle) {
        mStartAngle += deltaAngle;

        // reposition the children
        invalidateChildrenPosition();
    }

    public void scrollByDistance(final float deltaDistance) {
        mStartAngle += 180 * deltaDistance / (mRadius * 2);

        // reposition the children
        invalidateChildrenPosition();
    }

    public void scrollToDistance(final float distance) {
        mStartAngle = 180 * distance / (mRadius * 2);

        // reposition the children
        invalidateChildrenPosition();
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

        invalidateChildrenPosition();
    }

    public void setDepthRange(final float z1, final float z2) {
        mDepth1 = z1;
        mDepth2 = z2;

        invalidateChildrenPosition();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.DisplayGroup#onAddedChild(com.funzio.pure2D.DisplayObject)
     */
    @Override
    protected void onAddedChild(final DisplayObject child) {
        super.onAddedChild(child);

        // angle
        mCurrentGapAngle = (mGapAngle >= 0) ? mGapAngle : (mNumChildren == 0 ? 0 : (360f / mNumChildren));

        invalidateChildrenPosition();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.DisplayGroup#onRemovedChild(com.funzio.pure2D.DisplayObject)
     */
    @Override
    protected void onRemovedChild(final DisplayObject child) {
        super.onRemovedChild(child);

        // angle
        mCurrentGapAngle = (mGapAngle >= 0) ? mGapAngle : (mNumChildren == 0 ? 0 : (360f / mNumChildren));

        invalidateChildrenPosition();
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
        final float zRange = mDepth2 - mDepth1;
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
            child.setZ(mDepth1 + zRange * z2);

            // set alpha
            if (mAlpha1 > 0 && mAlpha2 > 0) {
                child.setAlpha(mAlpha1 + alphaRange * z2);
            }

            // next
            angle += mCurrentGapAngle;
        }
    }

    public void spin(final float veloc) {
        spin(veloc, 0, 0);
    }

    public void spin(final float veloc, final float acceleration) {
        spin(veloc, acceleration, 0);
    }

    public void spin(final float veloc, final float acceleration, final int maxSpinTime) {
        if (mAnimator == null) {
            mAnimator = new VelocityAnimator();
            mAnimator.setListener(this);
            addManipulator(mAnimator);
        }
        mAnimator.start(veloc, acceleration, maxSpinTime);
    }

    public void spinToChild(final int index, float acceleration, final int duration, final boolean rightDirection) {
        // bring the selected index to center-front
        final float newAngle = -index * mCurrentGapAngle + 90;

        float distance2Travel = (mStartAngle - newAngle) % 360;
        if (rightDirection) {
            if (distance2Travel < 0) {
                distance2Travel += 360;
            }
        } else {
            acceleration = -acceleration; // the other way around
            if (distance2Travel > 0) {
                distance2Travel -= 360;
            }
        }

        final float veloc = distance2Travel / duration - 0.5f * (acceleration) * duration; // Real physics, Newton's
        spin(veloc, acceleration, duration);
    }

    public void stop() {
        if (mAnimator != null) {
            mAnimator.stop();
        }
    }

    public void onAnimationEnd(final Animator animator) {
        // TODO
    }

    public void onAnimationUpdate(final Animator animator, final float value) {
        scrollByAngle(-value);
    }

}

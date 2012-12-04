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
    protected int mViewAngle = -90;

    // alpha range
    protected float mAlpha1 = 0.5f;
    protected float mAlpha2 = 1f;
    // scale range
    protected float mScale1 = 1f;
    protected float mScale2 = 1f;
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
        mStartAngle = mViewAngle - childIndex * mCurrentGapAngle;

        // reposition the children
        invalidateChildrenPosition();
    }

    /**
     * @param childIndex
     * @return the view angle of a specified child's index
     */
    public float getViewAngleAtChild(final int childIndex) {
        return formatAngle(mViewAngle - childIndex * mCurrentGapAngle);
    }

    /**
     * @return the child index of the item in front
     */
    public int getFrontChildIndex() {
        return getFrontChildIndex(mStartAngle);
    }

    /**
     * @return the child index of the item in front
     */
    public int getFrontChildIndex(final float angle) {
        final float viewAngle = formatAngle(mViewAngle - (angle - mCurrentGapAngle / 2));
        return (int) FloatMath.floor(viewAngle / mCurrentGapAngle);
    }

    /**
     * @param angle
     * @return a positive angle in range of [0, 360)
     */
    protected float formatAngle(final float angle) {
        return (360 + angle % 360) % 360;
    }

    /**
     * @param ceiling
     * @return the closest snapped angle
     */
    public float getSnapAngle(final boolean ceiling) {
        final float segment = formatAngle(mStartAngle) / mCurrentGapAngle;
        return mCurrentGapAngle * (ceiling ? FloatMath.ceil(segment) : FloatMath.floor(segment));
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

    public void setScaleRange(final float scale1, final float scale2) {
        mScale1 = scale1;
        mScale2 = scale2;

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

    public int getViewAngle() {
        return mViewAngle;
    }

    public void setViewAngle(final int viewAngle) {
        mViewAngle = viewAngle;
    }

    protected void positionChildren() {
        float angle = mStartAngle;
        float x, z, z2, radian;
        DisplayObject child;
        final float alphaRange = mAlpha2 - mAlpha1;
        final float scaleRange = mScale2 - mScale1;
        final float zRange = mDepth2 - mDepth1;

        // DisplayObject frontChild = null;
        // float maxZ = Float.MIN_VALUE;

        for (int i = 0; i < mNumChildren; i++) {
            child = mChildren.get(i);
            radian = (float) ((angle / 180f) * Math.PI);
            x = FloatMath.cos(radian);
            z = -FloatMath.sin(radian);
            z2 = (z + 1) / 2f;

            // orientation
            if (mOrientation == ORIENTATION_X) {
                child.setX(x * mRadius);
            } else {
                child.setY(x * mRadius);
            }

            // set z
            float childZ = mDepth1 + zRange * z2;
            child.setZ(childZ);
            // if (maxZ < childZ || frontChild == null) {
            // maxZ = childZ;
            // frontChild = child;
            // }

            // set alpha
            if (mAlpha1 > 0 && mAlpha2 > 0) {
                child.setAlpha(mAlpha1 + alphaRange * z2);
            }

            // set scale
            if (mScale1 != 1 || mScale2 != 1) {
                child.setScale(mScale1 + scaleRange * z2);
            }

            // next
            angle += mCurrentGapAngle;
        }

        // if (frontChild != null) {
        // sendChildToTop(frontChild);
        // }
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

    public void spinToChild(final int index, final float acceleration, final int duration, final boolean rightDirection) {
        // bring the selected index to center-front
        spinToAngle(mViewAngle - index * mCurrentGapAngle, acceleration, duration, rightDirection);
    }

    public void spinToAngle(final float newAngle, float acceleration, int durationPerDegree, final boolean rightDirection) {

        float degree2Travel = (mStartAngle - newAngle) % 360;
        if (rightDirection) {
            acceleration = -acceleration; // the other way around
            if (degree2Travel > 0) {
                degree2Travel -= 360;
            }
        } else {
            if (degree2Travel < 0) {
                degree2Travel += 360;
            }
        }

        durationPerDegree *= Math.abs(degree2Travel);
        final float veloc = degree2Travel / durationPerDegree - 0.5f * (acceleration) * durationPerDegree; // Real physics, Newton's
        spin(veloc, acceleration, durationPerDegree);
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

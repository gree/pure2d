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
    protected float mGapAngle = 0;
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

    public void setDepthRange(final float z1, final float z2) {
        mDepth1 = z1;
        mDepth2 = z2;

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
            angle += mGapAngle;
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

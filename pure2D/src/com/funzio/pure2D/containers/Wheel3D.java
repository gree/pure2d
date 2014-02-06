/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
/**
 * 
 */
package com.funzio.pure2D.containers;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.FloatMath;
import android.view.MotionEvent;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.InvalidateFlags;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.VelocityAnimator;

/**
 * @author long
 */
public class Wheel3D extends DisplayGroup implements Animator.AnimatorListener, Wheel {
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

    // swiping
    protected boolean mSwipeEnabled = false;
    protected float mSwipeMinThreshold = 0;
    protected boolean mSwiping = false;
    protected float mSwipeDelta = 0;
    protected float mSwipeVelocity = 0;
    protected boolean mStoppable = true;
    private float mSwipeAnchor = -1;
    private float mAnchoredScroll = -1;
    private int mSwipePointerID = -1;

    private RectF mTouchBounds;

    public Wheel3D() {
        super();

        mAnimator = new VelocityAnimator();
        mAnimator.setListener(this);
        addManipulator(mAnimator);
    }

    @Override
    public void updateChildren(final int deltaTime) {
        super.updateChildren(deltaTime);

        // adjust the positions when necessary
        if (mChildrenPositionInvalidated) {
            positionChildren();
            mChildrenPositionInvalidated = false;

            // only apply constraints when size or parent changed
            if (mUIConstraint != null && (mInvalidateFlags & (SIZE | PARENT)) != 0) {
                mUIConstraint.apply(this, mParent);
            }
        }

        if (mTouchBounds == null) {
            mTouchBounds = new RectF(getBounds());
        } else {
            mTouchBounds.set(getBounds());
        }
        float childX = mNumChildren > 0 ? mChildren.get(0).getOrigin().x : 0;
        float childY = mNumChildren > 0 ? mChildren.get(0).getOrigin().y : 0;
        if (mOrientation == ORIENTATION_X) {
            mTouchBounds.offset(-mRadius - childX, -childY);
        } else {
            mTouchBounds.offset(-childX, -mRadius - childY);
        }
        // check clipping
        if (mClippingEnabled && mClipStageRect != null) {
            mTouchBounds.intersect(mClipStageRect);
        }

    }

    protected void invalidateChildrenPosition() {
        mChildrenPositionInvalidated = true;
        invalidate(InvalidateFlags.CHILDREN);
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
        return getFrontChildIndex(mStartAngle, false);
    }

    /**
     * @return the child index of the item in front
     */
    public int getFrontChildIndex(final float angle, final boolean ceiling) {
        final float viewAngle = formatAngle(mViewAngle - (angle - mCurrentGapAngle / 2));
        return (int) (ceiling ? FloatMath.ceil(viewAngle / mCurrentGapAngle) : FloatMath.floor(viewAngle / mCurrentGapAngle));
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

    @Override
    protected void onAddedChild(final DisplayObject child) {
        super.onAddedChild(child);

        // angle
        mCurrentGapAngle = (mGapAngle >= 0) ? mGapAngle : (mNumChildren == 0 ? 0 : (360f / mNumChildren));

        invalidateChildrenPosition();
    }

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

    @Override
    public float getVelocity() {
        return mAnimator.getVelocity();
    }

    public void stop() {
        if (mAnimator != null) {
            mAnimator.stop();
        }
    }

    public boolean isSwipeEnabled() {
        return mSwipeEnabled;
    }

    public void setSwipeEnabled(final boolean swipeEnabled) {
        mSwipeEnabled = swipeEnabled;
        if (swipeEnabled) {
            mSwipeAnchor = -1;
        }
    }

    public float getSwipeMinThreshold() {
        return mSwipeMinThreshold;
    }

    public void setSwipeMinThreshold(final float swipeMinThreshold) {
        mSwipeMinThreshold = swipeMinThreshold;
    }

    protected void startSwipe() {
        mAnchoredScroll = mStartAngle;
        mSwiping = true;
    }

    protected void stopSwipe() {
        mSwipeAnchor = -1;
        mSwiping = false;
        mSwipePointerID = -1;

        spin(mSwipeVelocity, mSwipeVelocity > 0 ? -DEFAULT_SPIN_ACCELERATION : DEFAULT_SPIN_ACCELERATION);

        // reset
        mSwipeDelta = 0;
        mSwipeVelocity = 0;
    }

    protected void swipe(final float delta) {
        scrollToDistance(-mAnchoredScroll + delta);

        // average velocity
        mSwipeVelocity = (mSwipeVelocity + (delta - mSwipeDelta) / Scene.DEFAULT_MSPF) * 0.5f;
        mSwipeDelta = delta;
    }

    public boolean isSwiping() {
        return mSwiping;
    }

    public void onAnimationEnd(final Animator animator) {
        // TODO
    }

    public void onAnimationUpdate(final Animator animator, final float value) {
        scrollByAngle(value);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (mNumChildren == 0) {
            return false;
        }

        final boolean controlled = super.onTouchEvent(event);

        // swipe enabled?
        if (mSwipeEnabled) {
            if (mScene == null) {
                return controlled;
            }

            final int action = event.getActionMasked();
            final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                final PointF global = mScene.getTouchedPoint(pointerIndex);
                if (mTouchBounds.contains(global.x, global.y)) {
                    if (!mSwiping) {
                        if (mOrientation == ORIENTATION_X) {
                            mSwipeAnchor = global.x;
                        } else {
                            mSwipeAnchor = global.y;
                        }

                        // keep pointer id
                        mSwipePointerID = event.getPointerId(pointerIndex);
                    }

                    // callback
                    onTouchDown(event);
                }

            } else if (action == MotionEvent.ACTION_MOVE) {
                final int swipePointerIndex = event.findPointerIndex(mSwipePointerID);
                if (swipePointerIndex >= 0) {

                    if (mOrientation == ORIENTATION_X) {
                        final float deltaX = mScene.getTouchedPoint(swipePointerIndex).x - mSwipeAnchor;
                        if (mSwipeAnchor >= 0) {
                            if (!mSwiping) {
                                if (Math.abs(deltaX) >= mSwipeMinThreshold) {
                                    // re-anchor
                                    mSwipeAnchor = mScene.getTouchedPoint(swipePointerIndex).x;

                                    startSwipe();
                                }
                            } else {
                                swipe(deltaX);
                            }
                        }

                    } else {

                        float deltaY = mScene.getTouchedPoint(swipePointerIndex).y - mSwipeAnchor;
                        if (mScene.getAxisSystem() == Scene.AXIS_TOP_LEFT) {
                            // flip
                            deltaY = -deltaY;
                        }

                        if (mSwipeAnchor >= 0) {
                            if (!mSwiping) {
                                if (Math.abs(deltaY) >= mSwipeMinThreshold) {
                                    // re-anchor
                                    mSwipeAnchor = mScene.getTouchedPoint(swipePointerIndex).y;

                                    startSwipe();
                                }
                            } else {
                                swipe(deltaY);
                            }
                        }
                    }

                }

            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
                if (mSwiping) {
                    // check pointer
                    if (event.getPointerId(pointerIndex) == mSwipePointerID) {
                        stopSwipe();
                    }
                } else {
                    // clear anchor, important!
                    mSwipeAnchor = -1;
                }
            }
        }

        return controlled;
    }

    /**
     * This is called when a touch down
     * 
     * @param event
     */
    protected void onTouchDown(final MotionEvent event) {
        // stop spining
        if (mAnimator.isRunning() && mStoppable) {
            mAnimator.stop();
        }
    }

}

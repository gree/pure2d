/**
 * 
 */
package com.funzio.pure2D.containers;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.FloatMath;
import android.view.MotionEvent;

import com.funzio.pure2D.DisplayObject;
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

        spin(mSwipeVelocity, mSwipeVelocity > 0 ? -SPIN_ACCELERATION : SPIN_ACCELERATION);

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

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.DisplayGroup#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (mNumChildren == 0) {
            return false;
        }

        final boolean controlled = super.onTouchEvent(event);

        // swipe enabled?
        if (mSwipeEnabled) {
            final int action = event.getActionMasked();
            final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                final PointF global = getScene().getTouchedPoint(pointerIndex);
                if (mTouchBounds.contains(global.x, global.y)) {
                    if (!mSwiping) {
                        if (mOrientation == ORIENTATION_X) {
                            mSwipeAnchor = event.getX(pointerIndex);
                        } else {
                            mSwipeAnchor = event.getY(pointerIndex);
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
                        final float deltaX = event.getX(swipePointerIndex) - mSwipeAnchor;
                        if (mSwipeAnchor >= 0) {
                            if (!mSwiping) {
                                if (Math.abs(deltaX) >= mSwipeMinThreshold) {
                                    // re-anchor
                                    mSwipeAnchor = event.getX(swipePointerIndex);

                                    startSwipe();
                                }
                            } else {
                                swipe(deltaX);
                            }
                        }

                    } else {

                        final Scene scene = getScene();
                        float deltaY = event.getY(swipePointerIndex) - mSwipeAnchor;
                        if (scene.getAxisSystem() == Scene.AXIS_BOTTOM_LEFT) {
                            // flip
                            deltaY = -deltaY;
                        }

                        if (mSwipeAnchor >= 0) {
                            if (!mSwiping) {
                                if (Math.abs(deltaY) >= mSwipeMinThreshold) {
                                    // re-anchor
                                    mSwipeAnchor = event.getY(swipePointerIndex);

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
                        return true;
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
        mAnimator.stop();
    }

}

/**
 * 
 */
package com.funzio.pure2D.containers;

import com.funzio.pure2D.animators.Animator;

/**
 * List is an extended Wheel that also handles masking and snapping. Mainly used for UI.
 * 
 * @author long
 */
public class VList extends VWheel implements List {
    protected boolean mSnapping = false;

    public VList() {
        super();

        // default values
        setAlignment(Alignment.HORIZONTAL_CENTER);
        setSwipeEnabled(true);
        setRepeating(false);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.HWheel#startSwipe()
     */
    @Override
    protected void startSwipe() {
        super.startSwipe();

        mSnapping = false;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.HWheel#stopSwipe()
     */
    @Override
    protected void stopSwipe() {
        super.stopSwipe();

        mSnapping = false;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.LinearGroup#scrollTo(float, float)
     */
    @Override
    public void scrollTo(final float x, float y) {

        // add friction when scroll out of bounds
        if (y < 0) {
            y *= SCROLL_OOB_FRICTION;
        } else if (y > mScrollMax.y) {
            y = mScrollMax.y + (y - mScrollMax.y) * SCROLL_OOB_FRICTION;
        }

        super.scrollTo(x, y);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.VWheel#onAnimationUpdate(com.funzio.pure2D.animators.Animator, float)
     */
    @Override
    public void onAnimationUpdate(final Animator animator, final float value) {
        super.onAnimationUpdate(animator, value);

        if (!mSnapping) {
            // out of range?
            if (mScrollPosition.y < 0 || mScrollPosition.y > mScrollMax.y) {
                stop();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.HWheel#onAnimationEnd(com.funzio.pure2D.animators.Animator)
     */
    @Override
    public void onAnimationEnd(final Animator animator) {
        super.onAnimationEnd(animator);

        if (!mSnapping) {
            if (mScrollPosition.y < 0) {
                mSnapping = true;
                spinDistance(-mScrollPosition.y, DEFAULT_SNAP_ACCELERATION, DEFAULT_SNAP_DURATION);
            } else if (mScrollPosition.y > mScrollMax.y) {
                mSnapping = true;
                spinDistance(-mScrollPosition.y + mScrollMax.y, DEFAULT_SNAP_ACCELERATION, DEFAULT_SNAP_DURATION);
            }
        }
    }

}

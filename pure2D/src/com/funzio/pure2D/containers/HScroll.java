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
public class HScroll extends HWheel implements List {
    protected boolean mSnapping = false;

    public HScroll() {
        super();

        // default values
        setAlignment(Alignment.VERTICAL_CENTER);
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
    public void scrollTo(float x, final float y) {

        // add friction when scroll out of bounds
        if (x < 0) {
            x *= SCROLL_OOB_FRICTION;
        } else if (x > mScrollMax.x) {
            x = mScrollMax.x + (x - mScrollMax.x) * SCROLL_OOB_FRICTION;
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
            if (mScrollPosition.x < 0 || mScrollPosition.x > mScrollMax.x) {
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
            if (mScrollPosition.x < 0) {
                mSnapping = true;
                spinDistance(-mScrollPosition.x, DEFAULT_SNAP_ACCELERATION, DEFAULT_SNAP_DURATION);
            } else if (mScrollPosition.x > mScrollMax.x) {
                mSnapping = true;
                spinDistance(-mScrollPosition.x + mScrollMax.x, DEFAULT_SNAP_ACCELERATION, DEFAULT_SNAP_DURATION);
            }
        }
    }

}

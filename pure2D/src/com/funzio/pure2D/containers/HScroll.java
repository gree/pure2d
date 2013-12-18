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
    public HScroll() {
        super();

        // default values
        setAlignment(Alignment.VERTICAL_CENTER);
        setSwipeEnabled(true);
        setRepeating(false);
    }

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

    @Override
    public void onAnimationUpdate(final Animator animator, final float value) {
        super.onAnimationUpdate(animator, value);

        // out of range?
        if (animator == mVelocAnimator) {
            if (mScrollPosition.x < 0 || mScrollPosition.x > mScrollMax.x) {
                mVelocAnimator.end();
            }
        }
    }

    @Override
    public void onAnimationEnd(final Animator animator) {

        if (animator == mVelocAnimator) {
            if (mScrollPosition.x < 0) {
                snapTo(0);
            } else if (mScrollPosition.x > mScrollMax.x) {
                snapTo(mScrollMax.x);
            } else {
                super.onAnimationEnd(animator);
            }
        }
    }

}

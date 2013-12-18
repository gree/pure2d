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
public class VScroll extends VWheel implements List {
    public VScroll() {
        super();

        // default values
        setAlignment(Alignment.HORIZONTAL_CENTER);
        setSwipeEnabled(true);
        setRepeating(false);
    }

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

    @Override
    public void onAnimationUpdate(final Animator animator, final float value) {
        super.onAnimationUpdate(animator, value);

        // out of range?
        if (animator == mVelocAnimator) {
            if (mScrollPosition.y < 0 || mScrollPosition.y > mScrollMax.y) {
                mVelocAnimator.end();
            }
        }
    }

    @Override
    public void onAnimationEnd(final Animator animator) {
        if (animator == mVelocAnimator) {
            if (mScrollPosition.y < 0) {
                snapTo(0);
            } else if (mScrollPosition.y > mScrollMax.y) {
                snapTo(mScrollMax.y);
            } else {
                super.onAnimationEnd(animator);
            }
        }
    }

}

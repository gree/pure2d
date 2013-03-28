/**
 * 
 */
package com.funzio.pure2D.containers;

import com.funzio.pure2D.animators.Animator;

/**
 * @author long
 */
public class HList extends HWheel {
    public HList() {
        super();

        // default values
        setAlignment(Alignment.VERTICAL_CENTER);
        setSwipeEnabled(true);
        setRepeating(false);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.VWheel#onAnimationUpdate(com.funzio.pure2D.animators.Animator, float)
     */
    @Override
    public void onAnimationUpdate(final Animator animator, final float value) {
        super.onAnimationUpdate(animator, value);

        if (mScrollPosition.x < 0) {
            mScrollPosition.x = 0;
            stop();
        } else if (mScrollPosition.x > mScrollMax.x) {
            mScrollPosition.x = mScrollMax.x;
            stop();
        }
    }

}

/**
 * 
 */
package com.funzio.pure2D.containers;

import com.funzio.pure2D.animators.Animator;

/**
 * @author long
 */
public class VList extends VWheel {

    public VList() {
        super();

        // default values
        setAlignment(Alignment.HORIZONTAL_CENTER);
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

        if (mScrollPosition.y < 0) {
            mScrollPosition.y = 0;
            stop();
        } else if (mScrollPosition.y > mScrollMax.y) {
            mScrollPosition.y = mScrollMax.y;
            stop();
        }
    }

}

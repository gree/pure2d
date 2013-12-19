/**
 * 
 */
package com.funzio.pure2D.containers;

/**
 * Scroll is an extended Wheel that also handles masking and snapping. Mainly used for UI.
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

}

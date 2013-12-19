/**
 * 
 */
package com.funzio.pure2D.containers;

/**
 * Scroll is an extended Wheel that also handles masking and snapping. Mainly used for UI.
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

}

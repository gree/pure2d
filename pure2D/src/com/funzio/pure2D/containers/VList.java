/**
 * 
 */
package com.funzio.pure2D.containers;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.shapes.Rectangular;

/**
 * @author long
 */
public class VList extends VWheel {

    protected MaskGroup mMask;
    protected Rectangular mMaskRect;

    public VList() {
        super();

        // default values
        setAlignment(Alignment.HORIZONTAL_CENTER);
        setSwipeEnabled(true);
        setRepeating(false);

        // prepare the mask
        mMask = new MaskGroup();
        // create a rect for the mask group
        mMask.addChild(mMaskRect = new Rectangular());

        // apply the mask
        setMask(mMask);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.VGroup#setSize(float, float)
     */
    @Override
    public void setSize(final float w, final float h) {
        super.setSize(w, h);

        // match the size
        mMaskRect.setSize(w, h);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#onPreAdded(com.funzio.pure2D.containers.Container)
     */
    @Override
    public void onPreAdded(final Container container) {
        super.onPreAdded(container);

        // mask needs to be added first
        container.addChild(mMask);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#onRemoved()
     */
    @Override
    public void onRemoved() {
        super.onRemoved();

        // also remove the mask
        mMask.removeFromParent();
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

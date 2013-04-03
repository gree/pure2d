/**
 * 
 */
package com.funzio.pure2D.containers;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.shapes.Rectangular;

/**
 * @author long
 */
public class HList extends HWheel {
    protected MaskGroup mMaskGroup;
    protected Rectangular mMaskRect;

    public HList() {
        super();

        // default values
        setAlignment(Alignment.VERTICAL_CENTER);
        setSwipeEnabled(true);
        setRepeating(false);

        // prepare the mask
        mMaskGroup = new MaskGroup();
        // create a rect for the mask group
        mMaskGroup.addChild(mMaskRect = new Rectangular());

        // apply the mask
        setMask(mMaskGroup);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#setPosition(float, float)
     */
    @Override
    public void setPosition(final float x, final float y) {
        super.setPosition(x, y);

        // follow me
        mMaskGroup.setPosition(x, y);
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
        container.addChild(mMaskGroup);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#onRemoved()
     */
    @Override
    public void onRemoved() {
        super.onRemoved();

        // remove the mask
        mMaskGroup.removeFromParent();
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

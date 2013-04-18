/**
 * 
 */
package com.funzio.pure2D.containers;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.shapes.Rectangular;

/**
 * List is an extended Wheel that also handles masking and snapping. Mainly used for UI.
 * 
 * @author long
 */
public class HList extends HWheel {
    protected MaskGroup mMaskGroup;
    protected Rectangular mMaskRect;

    protected boolean mSnapping = false;

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
     * @see com.funzio.pure2D.BaseDisplayObject#onAdded(com.funzio.pure2D.containers.Container)
     */
    @Override
    public void onAdded(final Container container) {
        super.onAdded(container);

        // mask needs to be added first
        container.addChild(mMaskGroup, container.getChildIndex(this));
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

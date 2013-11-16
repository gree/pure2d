/**
 * 
 */
package com.funzio.pure2D.containers;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.ui.UIManager;

/**
 * List is an extended Wheel that also handles masking and snapping. Mainly used for UI.
 * 
 * @author long
 */
public class HScroll extends HWheel implements List {
    protected boolean mSnapEnabled = false;
    protected boolean mSnapping2Bound = false;

    public HScroll() {
        super();

        // default values
        setAlignment(Alignment.VERTICAL_CENTER);
        setSwipeEnabled(true);
        setRepeating(false);
    }

    @Override
    protected void startSwipe() {
        super.startSwipe();

        mSnapping2Bound = false;
    }

    @Override
    protected void stopSwipe() {
        super.stopSwipe();

        mSnapping2Bound = false;
    }

    public boolean isSnapEnabled() {
        return mSnapEnabled;
    }

    public void setSnapEnabled(final boolean snapEnabled) {
        mSnapEnabled = snapEnabled;
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

        if (!mSnapping2Bound) {
            // out of range?
            if (mScrollPosition.x < 0 || mScrollPosition.x > mScrollMax.x) {
                stop();
            }
        }
    }

    @Override
    public void onAnimationEnd(final Animator animator) {
        super.onAnimationEnd(animator);

        if (mSnapEnabled) {
            // TODO put snapping logic here
        }

        if (!mSnapping2Bound) {
            if (mScrollPosition.x < 0) {
                mSnapping2Bound = true;
                spinDistance(-mScrollPosition.x, DEFAULT_SNAP_ACCELERATION, DEFAULT_SNAP_DURATION);
            } else if (mScrollPosition.x > mScrollMax.x) {
                mSnapping2Bound = true;
                spinDistance(-mScrollPosition.x + mScrollMax.x, DEFAULT_SNAP_ACCELERATION, DEFAULT_SNAP_DURATION);
            }
        }
    }

    @Override
    public void setXMLAttributes(final XmlPullParser xmlParser, final UIManager manager) {
        super.setXMLAttributes(xmlParser, manager);

        final String snapEnabled = xmlParser.getAttributeValue(null, ATT_SNAP_ENABLED);
        if (snapEnabled != null) {
            setSnapEnabled(Boolean.valueOf(snapEnabled));
        }
    }

}

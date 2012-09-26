/**
 * 
 */
package com.funzio.pure2D.containers;

import android.graphics.PointF;

import com.funzio.pure2D.DisplayObject;

/**
 * @author long
 */
public abstract class LinearGroup extends DisplayGroup {

    protected float mGap = 0;
    protected PointF mScrollPosition = new PointF();
    protected boolean mRepeating = false;
    protected boolean mClipping = true;

    /**
     * @return the gap
     */
    public float getGap() {
        return mGap;
    }

    /**
     * @param gap the gap to set
     */
    public void setGap(final float gap) {
        mGap = gap;

        // reposition the children
        positionChildren();
    }

    public PointF getScrollPosition() {
        return mScrollPosition;
    }

    public void scrollTo(final float x, final float y) {
        mScrollPosition.x = x;
        mScrollPosition.y = y;

        // reposition the children
        positionChildren();
    }

    public void scrollTo(final DisplayObject child) {
        PointF pos = child.getPosition();
        mScrollPosition.x = pos.x;
        mScrollPosition.y = pos.y;
    }

    public void scrollBy(final float dx, final float dy) {
        mScrollPosition.x += dx;
        mScrollPosition.y += dy;

        // reposition the children
        positionChildren();
    }

    /**
     * @return the Repeating
     */
    public boolean isRepeating() {
        return mRepeating;
    }

    /**
     * @param Repeating the Repeating to set
     */
    public void setRepeating(final boolean Repeating) {
        mRepeating = Repeating;
        positionChildren();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.DisplayGroup#onAddedChild(com.funzio.pure2D.DisplayObject)
     */
    @Override
    protected void onAddedChild(final DisplayObject child) {
        super.onAddedChild(child);

        positionChildren();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.DisplayGroup#onRemovedChild(com.funzio.pure2D.DisplayObject)
     */
    @Override
    protected void onRemovedChild(final DisplayObject child) {
        super.onRemovedChild(child);

        positionChildren();
    }

    /**
     * @return the clipping
     */
    public boolean isClipping() {
        return mClipping;
    }

    /**
     * @param clipping the clipping to set
     */
    public void setClipping(final boolean clipping) {
        mClipping = clipping;
    }

    protected abstract void positionChildren();

    public abstract PointF getContentSize();

    public abstract PointF getScrollMax();
}

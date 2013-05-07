/**
 * 
 */
package com.funzio.pure2D.containers;

import android.graphics.PointF;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.InvalidateFlags;

/**
 * @author long
 */
public abstract class LinearGroup extends DisplayGroup {

    protected float mGap = 0;
    protected int mAlignment = Alignment.NONE;
    protected PointF mScrollPosition = new PointF();
    protected boolean mRepeating = false;
    protected boolean mClipping = true;
    protected boolean mAutoSleepChildren = false;

    private boolean mChildrenPositionInvalidated = false;

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.DisplayGroup#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (mChildrenPositionInvalidated) {
            positionChildren();
            mChildrenPositionInvalidated = false;
        }

        return super.update(deltaTime);
    }

    protected void invalidateChildrenPosition() {
        mChildrenPositionInvalidated = true;
        invalidate();
    }

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
        invalidateChildrenPosition();
    }

    public PointF getScrollPosition() {
        return mScrollPosition;
    }

    public void scrollTo(final float x, final float y) {
        mScrollPosition.x = x;
        mScrollPosition.y = y;

        // reposition the children
        invalidateChildrenPosition();
    }

    public void scrollTo(final DisplayObject child) {
        PointF pos = child.getPosition();
        mScrollPosition.x = pos.x;
        mScrollPosition.y = pos.y;

        // reposition the children
        invalidateChildrenPosition();
    }

    public void scrollBy(final float dx, final float dy) {
        mScrollPosition.x += dx;
        mScrollPosition.y += dy;

        // reposition the children
        invalidateChildrenPosition();
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
        invalidateChildrenPosition();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.DisplayGroup#onAddedChild(com.funzio.pure2D.DisplayObject)
     */
    @Override
    protected void onAddedChild(final DisplayObject child) {
        super.onAddedChild(child);

        invalidateChildrenPosition();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.DisplayGroup#onRemovedChild(com.funzio.pure2D.DisplayObject)
     */
    @Override
    protected void onRemovedChild(final DisplayObject child) {
        super.onRemovedChild(child);

        invalidateChildrenPosition();
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

        invalidate(InvalidateFlags.VISIBILITY);
    }

    public boolean isAutoSleepChildren() {
        return mAutoSleepChildren;
    }

    public void setAutoSleepChildren(final boolean autoSleepChildren) {
        mAutoSleepChildren = autoSleepChildren;
    }

    public int getAlignment() {
        return mAlignment;
    }

    public void setAlignment(final int alignment) {
        mAlignment = alignment;

        invalidateChildrenPosition();
    }

    protected abstract void positionChildren();

    public abstract PointF getContentSize();

    public abstract PointF getScrollMax();

}

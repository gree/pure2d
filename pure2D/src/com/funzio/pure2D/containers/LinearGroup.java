/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
/**
 * 
 */
package com.funzio.pure2D.containers;

import android.graphics.PointF;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.InvalidateFlags;
import com.funzio.pure2D.ui.UIConfig;
import com.funzio.pure2D.ui.UIManager;

/**
 * @author long
 */
public abstract class LinearGroup extends DisplayGroup {

    protected static final String ATT_ALIGN = "align";
    protected static final String ATT_REPEATING = "repeating";
    protected static final String ATT_BOUNDS_CHECK_ENABLED = "boundsCheckEnabled";
    protected static final String ATT_AUTO_SLEEP_CHILDREN = "autoSleepChildren";
    protected static final String ATT_MIN_CELL_SIZE = "minCellSize";

    protected float mGap = 0;
    protected float mOffsetX = 0;
    protected float mOffsetY = 0;

    protected int mAlignment = Alignment.NONE;
    protected PointF mScrollPosition = new PointF();
    protected boolean mRepeating = false;
    protected boolean mBoundsCheckEnabled = true;
    protected boolean mAutoSleepChildren = false;
    protected float mMinCellSize;

    private boolean mChildrenPositionInvalidated = false;

    @Override
    public void updateChildren(final int deltaTime) {
        super.updateChildren(deltaTime);

        // adjust the positions when necessary
        if (mChildrenPositionInvalidated) {
            positionChildren();
            mChildrenPositionInvalidated = false;

            // only apply constraints when size or parent changed
            if (mUIConstraint != null && (mInvalidateFlags & (SIZE | PARENT)) != 0) {
                mUIConstraint.apply(this, mParent);
            }
        }

    }

    protected void invalidateChildrenPosition() {
        mChildrenPositionInvalidated = true;
        invalidate(InvalidateFlags.CHILDREN);
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

    public void setOffset(final float offsetX, final float offsetY) {
        mOffsetX = offsetX;
        mOffsetY = offsetY;

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

    @Override
    protected void onAddedChild(final DisplayObject child) {
        super.onAddedChild(child);

        invalidateChildrenPosition();
    }

    @Override
    protected void onRemovedChild(final DisplayObject child) {
        super.onRemovedChild(child);

        invalidateChildrenPosition();
    }

    /**
     * @return the checking bounds
     */
    public boolean isBoundsCheckEnabled() {
        return mBoundsCheckEnabled;
    }

    /**
     * Toggle checking bounds and draw children
     * 
     * @param checking the bounds to set
     */
    public void setBoundsCheckEnabled(final boolean checking) {
        mBoundsCheckEnabled = checking;

        invalidate(InvalidateFlags.CHILDREN);
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

    public float getMinCellSize() {
        return mMinCellSize;
    }

    public void setMinCellSize(final float minCellSize) {
        mMinCellSize = minCellSize;

        // reposition the children
        invalidateChildrenPosition();
    }

    @Override
    public void setWrapContentWidth(final boolean wrapWidth) {
        super.setWrapContentWidth(wrapWidth);

        invalidateChildrenPosition();
    }

    @Override
    public void setWrapContentHeight(final boolean wrapHeight) {
        super.setWrapContentHeight(wrapHeight);

        invalidateChildrenPosition();
    }

    @Override
    public void setXMLAttributes(final XmlPullParser xmlParser, final UIManager manager) {
        super.setXMLAttributes(xmlParser, manager);

        final String align = xmlParser.getAttributeValue(null, ATT_ALIGN);
        if (align != null) {
            setAlignment(UIConfig.getAlignment(align));
        }

        final String repeating = xmlParser.getAttributeValue(null, ATT_REPEATING);
        if (repeating != null) {
            setRepeating(Boolean.valueOf(repeating));
        }

        final String boundsCheck = xmlParser.getAttributeValue(null, ATT_BOUNDS_CHECK_ENABLED);
        if (boundsCheck != null) {
            setBoundsCheckEnabled(Boolean.valueOf(boundsCheck));
        }

        final String autoSleepChildren = xmlParser.getAttributeValue(null, ATT_AUTO_SLEEP_CHILDREN);
        if (autoSleepChildren != null) {
            setAutoSleepChildren(Boolean.valueOf(autoSleepChildren));
        }

        final String minCellSize = xmlParser.getAttributeValue(null, ATT_MIN_CELL_SIZE);
        if (minCellSize != null) {
            setMinCellSize(Float.valueOf(minCellSize) * manager.getConfig().screen_scale);
        }
    }

    protected abstract void positionChildren();

    public abstract PointF getContentSize();

    public abstract PointF getScrollMax();

}

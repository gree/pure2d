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

import java.util.ArrayList;

import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Touchable;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.ui.UIManager;
import com.funzio.pure2D.ui.UIObject;

/**
 * @author long
 */
public class HGroup extends LinearGroup implements UIObject {
    // xml attributes
    protected static final String ATT_SWIPE_ENABLED = "swipeEnabled";

    protected PointF mContentSize = new PointF();
    protected PointF mScrollMax = new PointF();

    private int mStartIndex = 0;
    private float mStartX = 0;

    protected boolean mSwipeEnabled = false;
    protected float mSwipeMinThreshold = 0;
    protected boolean mSwiping = false;

    private float mSwipeAnchor = -1;
    private float mAnchoredScroll = -1;
    private int mSwipePointerID = -1;

    public HGroup() {
        super();
    }

    private void findStartIndex() {
        mStartX = mStartIndex = 0;
        if (mContentSize.x <= 0) {
            return;
        }

        int offset = Math.round(mScrollPosition.x % mContentSize.x); // needs to be rounded up
        offset += (offset < 0) ? mContentSize.x : 0;
        // easy case
        if (offset == 0) {
            return;
        }

        float itemPos = 0;
        for (int i = 0; i < mNumChildren; i++) {
            if (offset <= itemPos) {
                mStartIndex = i;
                mStartX = itemPos - offset;
                return;
            }
            if (i == mNumChildren - 1) {
                mStartX = mContentSize.x - offset;
            } else {
                itemPos += Math.max(mMinCellSize, mChildren.get(i).getSize().x) + mGap;
            }
        }
    }

    protected void updateContentSize() {
        mContentSize.x = mContentSize.y = 0;
        for (int i = 0; i < mNumChildren; i++) {
            final PointF childSize = mChildren.get(i).getSize();
            mContentSize.x += Math.max(mMinCellSize, childSize.x) + mGap;
            mContentSize.y = childSize.y > mContentSize.y ? childSize.y : mContentSize.y;
        }

        // update scroll max
        mScrollMax.x = Math.max(0, mContentSize.x - mSize.x);
        mScrollMax.y = Math.max(0, mContentSize.y - mSize.y);

    }

    public int getStartIndex() {
        return mStartIndex;
    }

    public float getStartX() {
        return mStartX;
    }

    @Override
    public void scrollTo(final DisplayObject child) {
        mScrollPosition.x = child.getPosition().x;

        // reposition the children
        invalidateChildrenPosition();
    }

    /**
     * @param positive
     * @return the delta to the closest child based on the specified direction which is either positive or negative
     */
    protected float getSnapDelta(final boolean positive) {
        if (mNumChildren == 0) {
            return 0;
        }

        final DisplayObject startChild = getChildAt(mStartIndex);
        final float x = startChild.getX();

        if (x < 0) {
            if (positive) {
                return x + (Math.max(mMinCellSize, startChild.getSize().x) + mGap);
            } else {
                return x;
            }
        } else {
            if (positive) {
                return x;
            } else {
                int newIndex = mStartIndex == 0 ? mNumChildren - 1 : mStartIndex - 1;
                final DisplayObject newChild = getChildAt(newIndex);
                return x - (Math.max(mMinCellSize, newChild.getSize().x) + mGap);
            }
        }
    }

    @Override
    protected boolean drawChildren(final GLState glState) {
        if (mTouchable) {
            if (mVisibleTouchables == null) {
                mVisibleTouchables = new ArrayList<Touchable>();
            } else {
                mVisibleTouchables.clear();
            }
        }

        // draw the children
        DisplayObject child;
        final int numChildren = mChildrenDisplayOrder.size();
        for (int i = 0; i < numChildren; i++) {
            child = mChildrenDisplayOrder.get(i);

            if (child.isVisible() && (!mBoundsCheckEnabled || isChildInBounds(child))) {
                if (mAutoSleepChildren) {
                    // wake child up
                    child.setAlive(true);
                }

                // draw frame
                child.draw(glState);

                // stack the visible child
                if (mTouchable && child instanceof Touchable && ((Touchable) child).isTouchable()) {
                    float childZ = child.getZ();
                    int j = mVisibleTouchables.size();
                    while (j > 0 && ((DisplayObject) mVisibleTouchables.get(j - 1)).getZ() > childZ) {
                        j--;
                    }
                    mVisibleTouchables.add(j, (Touchable) child);
                }
            } else {
                if (mAutoSleepChildren) {
                    // send child to slepp
                    child.setAlive(false);
                }
            }
        }

        // if there's an empty space at the beginning
        // if (mRepeating && mStartX > mGap) {
        // // draw the first item to fill the space
        // int index = mStartIndex - 1;
        // if (index < 0) {
        // index += mNumChildren;
        // }
        // DisplayObject child = mChildren.get(index);
        // PointF oldPos = child.getPosition();
        // float oldX = oldPos.x;
        // float oldY = oldPos.y;
        // child.setPosition(mStartX - child.getSize().x - mGap, oldY);
        // child.draw(glState);
        // child.setPosition(oldX, oldY);
        // }

        return true;
    }

    @Override
    protected void positionChildren() {
        float nextY = -mScrollPosition.y;
        float alignedY = 0;
        DisplayObject child;
        PointF childSize;

        findStartIndex();
        if (mRepeating) {
            float nextX = mStartX;
            for (int i = 0; i < mNumChildren; i++) {
                child = mChildren.get((i + mStartIndex) % mNumChildren);
                childSize = child.getSize();
                if ((mAlignment & Alignment.VERTICAL_CENTER) != 0) {
                    alignedY = (mSize.y - childSize.y) * 0.5f;
                } else if ((mAlignment & Alignment.TOP) != 0) {
                    alignedY = (mSize.y - childSize.y);
                }
                child.setPosition(mOffsetX + nextX, mOffsetY + nextY + alignedY);

                // find nextX
                nextX += Math.max(mMinCellSize, childSize.x) + mGap;
            }

            if (mStartX > mGap) {
                // draw the first item to fill the space
                int index = mStartIndex - 1;
                if (index < 0) {
                    index += mNumChildren;
                }
                child = mChildren.get(index);
                child.setPosition(mStartX - Math.max(mMinCellSize, child.getSize().x) - mGap, child.getPosition().y);
            }
        } else {
            // update content size
            updateContentSize();

            float nextX = -mScrollPosition.x;
            // alignment
            if ((mAlignment & Alignment.HORIZONTAL_CENTER) > 0) {
                nextX += (mSize.x - mContentSize.x) * 0.5f;
            } else if ((mAlignment & Alignment.RIGHT) > 0) {
                nextX += (mSize.x - mContentSize.x);
            }

            for (int i = 0; i < mNumChildren; i++) {
                child = mChildren.get(i);
                childSize = child.getSize();
                if ((mAlignment & Alignment.VERTICAL_CENTER) != 0) {
                    alignedY = (mSize.y - childSize.y) * 0.5f;
                } else if ((mAlignment & Alignment.TOP) != 0) {
                    alignedY = (mSize.y - childSize.y);
                }
                child.setPosition(mOffsetX + nextX, mOffsetY + nextY + alignedY);

                // update sizes
                nextX += Math.max(mMinCellSize, childSize.x) + mGap;
            }
        }
    }

    @Override
    public void setSize(final float w, final float h) {
        super.setSize(w, h);

        // update scroll max
        mScrollMax.x = Math.max(0, mContentSize.x - w);
        mScrollMax.y = Math.max(0, mContentSize.y - h);

        invalidateChildrenPosition();
    }

    // @Override
    // public void setGap(final float gap) {
    // // dangerous
    // updateContentSize();
    //
    // super.setGap(gap);
    // }
    //
    // @Override
    // public void setMinCellSize(final float minCellSize) {
    // // dangerous
    // updateContentSize();
    //
    // super.setMinCellSize(minCellSize);
    // }

    @Override
    protected void onAddedChild(final DisplayObject child) {
        final PointF childSize = child.getSize();
        mContentSize.x += Math.max(mMinCellSize, childSize.x) + mGap;
        mContentSize.y = childSize.y > mContentSize.y ? childSize.y : mContentSize.y;

        // update scroll max
        mScrollMax.x = Math.max(0, mContentSize.x - mSize.x);
        mScrollMax.y = Math.max(0, mContentSize.y - mSize.y);

        super.onAddedChild(child);
    }

    @Override
    protected void onRemovedChild(final DisplayObject child) {
        final PointF childSize = child.getSize();
        mContentSize.x -= Math.max(mMinCellSize, childSize.x) + mGap;

        // update scroll max
        mScrollMax.x = Math.max(0, mContentSize.x - mSize.x);
        mScrollMax.y = Math.max(0, mContentSize.y - mSize.y);

        super.onRemovedChild(child);
    }

    @Override
    public PointF getContentSize() {
        return mContentSize;
    }

    @Override
    public PointF getScrollMax() {
        return mScrollMax;
    }

    public boolean isSwipeEnabled() {
        return mSwipeEnabled;
    }

    public void setSwipeEnabled(final boolean swipeEnabled) {
        mSwipeEnabled = swipeEnabled;
        if (swipeEnabled) {
            mSwipeAnchor = -1;
        }
    }

    public float getSwipeMinThreshold() {
        return mSwipeMinThreshold;
    }

    public void setSwipeMinThreshold(final float swipeMinThreshold) {
        mSwipeMinThreshold = swipeMinThreshold;
    }

    protected void startSwipe() {
        mAnchoredScroll = mScrollPosition.x;
        mSwiping = true;
    }

    protected void stopSwipe() {
        mSwipeAnchor = -1;
        mSwiping = false;
        mSwipePointerID = -1;
    }

    protected void swipe(final float delta) {
        scrollTo(mAnchoredScroll - delta, 0);
    }

    public boolean isSwiping() {
        return mSwiping;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (mNumChildren == 0) {
            return false;
        }

        final boolean controlled = super.onTouchEvent(event);

        // swipe enabled?
        if (mSwipeEnabled) {
            if (mScene == null) {
                return controlled;
            }

            final int action = event.getActionMasked();
            final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                final RectF bounds = (mClippingEnabled && mClipStageRect != null) ? mClipStageRect : mBounds;
                final PointF global = mScene.getTouchedPoint(pointerIndex);
                if (bounds.contains(global.x, global.y)) {
                    if (!mSwiping) {
                        mSwipeAnchor = global.x;
                        // keep pointer id
                        mSwipePointerID = event.getPointerId(pointerIndex);
                    }

                    // callback
                    onTouchDown(event);
                }

            } else if (action == MotionEvent.ACTION_MOVE) {
                final int swipePointerIndex = event.findPointerIndex(mSwipePointerID);
                if (swipePointerIndex >= 0) {
                    final float deltaX = mScene.getTouchedPoint(swipePointerIndex).x - mSwipeAnchor;
                    if (mSwipeAnchor >= 0) {
                        if (!mSwiping) {
                            if (Math.abs(deltaX) >= mSwipeMinThreshold) {
                                // re-anchor
                                mSwipeAnchor = mScene.getTouchedPoint(swipePointerIndex).x;

                                startSwipe();
                            }
                        } else {
                            swipe(deltaX);
                        }
                    }
                }

            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
                if (mSwiping) {
                    // check pointer
                    if (event.getPointerId(pointerIndex) == mSwipePointerID) {
                        stopSwipe();
                    }
                } else {
                    // clear anchor, important!
                    mSwipeAnchor = -1;
                }
            }
        }

        return controlled;
    }

    /**
     * This is called when a touch down
     * 
     * @param event
     */
    protected void onTouchDown(final MotionEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setXMLAttributes(final XmlPullParser xmlParser, final UIManager manager) {
        super.setXMLAttributes(xmlParser, manager);

        final String swipeEnabled = xmlParser.getAttributeValue(null, ATT_SWIPE_ENABLED);
        if (swipeEnabled != null) {
            setSwipeEnabled(Boolean.valueOf(swipeEnabled));
        }
    }

}

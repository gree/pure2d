/**
 * ****************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * ****************************************************************************
 */
/**
 *
 */
package com.funzio.pure2D.containers;

import android.graphics.PointF;
import android.util.Log;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.animators.Animator;

/**
 * List is an extended Wheel that can render an array of data
 *
 * @author long
 */
public class VList<T extends Object> extends VWheel implements List {
    protected static final String TAG = VList.class.getSimpleName();

    protected Class<? extends ListItem> mItemClass;
    protected java.util.List<T> mData;

    protected PointF mVirtualContentSize = new PointF();
    protected PointF mVirtualScrollMax = new PointF();
    protected PointF mItemSize = new PointF();
    private int mDataStartIndex = -1;

    private boolean mRepeating = false;

    public VList() {
        super();

        // default values
        setAlignment(Alignment.HORIZONTAL_CENTER);
        setSwipeEnabled(true);

        // NOTE: important to recycle the display children
        super.setRepeating(true);
    }

    @Override
    /**
     * Completely override the super method
     */
    public void setRepeating(boolean repeating) {
        mRepeating = repeating;
    }

    @Override
    public void scrollTo(final float x, float y) {

        // add friction when scroll out of bounds
        if (!mRepeating) {
            if (y < 0) {
                y *= SCROLL_OOB_FRICTION;
            } else if (y > mVirtualScrollMax.y) {
                y = mVirtualScrollMax.y + (y - mVirtualScrollMax.y) * SCROLL_OOB_FRICTION;
            }
        }

        super.scrollTo(x, y);
    }

    @Override
    public void onAnimationEnd(final Animator animator) {
        super.onAnimationEnd(animator);

        // out of range?
        if (!mRepeating && animator == mVelocAnimator) {
            final int round = Math.round(mScrollPosition.y);
            if (round < 0) {
                snapTo(0);
                return;
            } else if (round >= mVirtualScrollMax.y) {
                snapTo(mVirtualScrollMax.y);
                return;
            }
        }
    }

    @Override
    public void onAnimationUpdate(final Animator animator, final float value) {
        super.onAnimationUpdate(animator, value);

        // out of range?
        if (!mRepeating && animator == mVelocAnimator) {
            final int round = Math.round(mScrollPosition.y);
            if (round < 0 || round >= mVirtualScrollMax.y) {
                mVelocAnimator.end();
            }
        }
    }

    public void setItemClass(Class<? extends ListItem> clazz) {
        mItemClass = clazz;

        if (mData != null) {
            initItems();
        }
    }

    public Class<? extends ListItem> getItemClass() {
        return mItemClass;
    }

    public java.util.List<T> getData() {
        return mData;
    }

    public void setData(java.util.List<T> data) {
        mData = data;

        if (mItemClass != null) {
            initItems();
        }
    }

    /**
     * Create the items
     */
    protected void initItems() {
        removeAllChildren();

        final int dataLen = mData.size();
        int i = 0;
        ListItem item = null;
        float h = 0;
        while (i < dataLen && h <= mSize.y) {
            try {
                item = mItemClass.newInstance();
                addChild((DisplayObject) item);

                i++;
                if (h > 0) h += mGap;
                h += item.getHeight();
            } catch (InstantiationException e) {
                Log.e(TAG, "", e);
                break;
            } catch (IllegalAccessException e) {
                Log.e(TAG, "", e);
                break;
            }
        }
        // add another extra item
        if (i < dataLen) {
            try {
                item = mItemClass.newInstance();
                addChild((DisplayObject) item);
            } catch (InstantiationException e) {
                Log.e(TAG, "", e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "", e);
            }
        }

        if (item != null) {
            updateVirtualContentSize(item.getSize());
        }

        invalidateChildrenPosition();
    }

    @Override
    public void setSize(float w, float h) {
        super.setSize(w, h);

        // re-init items
        if (mItemClass != null && mData != null) {
            initItems();
        }
    }

    @Override
    protected void positionChildren() {
        final int oldStartIndex = getStartIndex();
        super.positionChildren();
        final int newStartIndex = getStartIndex();
        final int dataSize = mData.size();

        // find which data item index to start
        int itemIndex = 0;
        if (mScrollPosition.y > 0) {
            int numClippedItems = (int) Math.ceil(mScrollPosition.y / (mItemSize.y + mGap));
            itemIndex = numClippedItems;
        } else if (mScrollPosition.y < 0) {
            int numLoopedItems = (int) (-mScrollPosition.y / (mItemSize.y + mGap));
            itemIndex = dataSize - numLoopedItems % dataSize;
        }

        // diff check
        if (oldStartIndex != newStartIndex || itemIndex != mDataStartIndex) {
            mDataStartIndex = itemIndex;

//            Log.v(TAG, newStartIndex + " --- " + itemIndex);

            ListItem child;
            for (int i = 0; i < mNumChildren; i++) {
                child = (ListItem) mChildren.get((newStartIndex + i) % mNumChildren);
                child.setData(mData.get((mDataStartIndex + i) % dataSize));
            }

            // base on VGroup logic
            if (getStartY() > mGap) {
                // fill the first item in to fill the space
                int index = newStartIndex - 1;
                if (index < 0) {
                    index += mNumChildren;
                }

                // draw the first item to fill the space
                int listIndex = mDataStartIndex - 1;
                if (listIndex < 0) {
                    listIndex += dataSize;
                }
                child = (ListItem) mChildren.get(index);
                child.setData(mData.get(listIndex % dataSize));
            }
        }
    }

    protected void updateVirtualContentSize(PointF childSize) {
        mDataStartIndex = -1;
        mItemSize.set(childSize);
        int len = mData != null ? mData.size() : 0;

        mVirtualContentSize.x = childSize.x > mContentSize.x ? childSize.x : mContentSize.x;
        mVirtualContentSize.y = Math.max(mMinCellSize, childSize.y) * len + mGap * (len - 1);

        // update scroll max
        mVirtualScrollMax.x = Math.max(0, mVirtualContentSize.x - mSize.x);
        mVirtualScrollMax.y = Math.max(0, mVirtualContentSize.y - mSize.y);
    }

}

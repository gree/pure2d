/**
 * 
 */
package com.funzio.pure2D.containers;

import android.graphics.PointF;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.gl.gl10.GLState;

/**
 * @author long
 */
public class VGroup extends LinearGroup {
    protected PointF mContentSize = new PointF();
    protected PointF mScrollMax = new PointF();

    private int mStartIndex = 0;
    private float mStartY = 0;

    public VGroup() {
        super();
    }

    private void findStartIndex() {
        mStartY = mStartIndex = 0;
        if (mContentSize.y <= 0) {
            return;
        }

        int offset = Math.round(mScrollPosition.y % mContentSize.y); // needs to be rounded up
        offset += (offset < 0) ? mContentSize.y : 0;
        // easy case
        if (offset == 0) {
            return;
        }

        float itemPos = 0;
        for (int i = 0; i < mNumChildren; i++) {
            if (offset <= itemPos) {
                mStartIndex = i;
                mStartY = itemPos - offset;
                return;
            }
            if (i == mNumChildren - 1) {
                mStartY = mContentSize.y - offset;
                // Log.v("long", ">>>i: " + i + " mStartIndex: " + mStartIndex + " mStartY: " + mStartY + " offset: " + offset + " itemPos: " + itemPos);
            } else {
                itemPos += mChildren.get(i).getSize().y + mGap;
            }
        }
    }

    public int getStartIndex() {
        return mStartIndex;
    }

    public float getStartY() {
        return mStartY;
    }

    @Override
    public void scrollTo(final DisplayObject child) {
        mScrollPosition.y = child.getPosition().y;

        // reposition the children
        invalidateChildrenPosition();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.DisplayGroup#drawChildren(javax.microedition.khronos.opengles.GL10)
     */
    @Override
    protected void drawChildren(final GLState glState) {
        // draw the children
        for (int i = 0; i < mNumChildren; i++) {
            DisplayObject child = mChildren.get(i);
            if (child.isVisible() && (!mClipping || isChildInBounds(child))) {
                // draw frame
                child.draw(glState);
            }
        }

        // if there's an empty space at the beginning
        // if (mRepeating && mStartY > mGap) {
        // // draw the first item to fill the space
        // int index = mStartIndex - 1;
        // if (index < 0) {
        // index += mNumChildren;
        // }
        // DisplayObject child = mChildren.get(index);
        // PointF oldPos = child.getPosition();
        // float oldX = oldPos.x;
        // float oldY = oldPos.y;
        // child.setPosition(oldX, mStartY - child.getSize().y - mGap);
        // child.draw(glState);
        // child.setPosition(oldX, oldY);
        // }
    }

    @Override
    protected void positionChildren() {
        float nextX = -mScrollPosition.x;

        if (mRepeating) {
            findStartIndex();
            float nextY = mStartY;
            for (int i = 0; i < mNumChildren; i++) {
                DisplayObject child = mChildren.get((i + mStartIndex) % mNumChildren);
                PointF childSize = child.getSize();
                child.setPosition(nextX, nextY);

                // find nextY
                nextY += childSize.y + mGap;
            }

            if (mStartY > mGap) {
                // draw the first item to fill the space
                int index = mStartIndex - 1;
                if (index < 0) {
                    index += mNumChildren;
                }
                final DisplayObject child = mChildren.get(index);
                child.setPosition(child.getPosition().x, mStartY - child.getSize().y - mGap);
            }
        } else {
            float nextY = -mScrollPosition.y;
            for (int i = 0; i < mNumChildren; i++) {
                DisplayObject child = mChildren.get(i);
                child.setPosition(nextX, nextY);

                // update sizes
                PointF childSize = child.getSize();
                nextY += childSize.y + mGap;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#setSize(float, float)
     */
    @Override
    public void setSize(final float w, final float h) {
        super.setSize(w, h);

        // update scroll max
        mScrollMax.x = Math.max(0, mContentSize.x - mSize.x);
        mScrollMax.y = Math.max(0, mContentSize.y - mSize.y);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.LinearGroup#setGap(float)
     */
    @Override
    public void setGap(final float gap) {
        mContentSize.y += (gap - mGap) * mNumChildren;

        super.setGap(gap);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.LinearGroup#onAddedChild(com.funzio.pure2D.DisplayObject)
     */
    @Override
    protected void onAddedChild(final DisplayObject child) {
        PointF childSize = child.getSize();
        mContentSize.x = childSize.x > mContentSize.x ? childSize.x : mContentSize.x;
        mContentSize.y += childSize.y + mGap;

        // update scroll max
        mScrollMax.x = Math.max(0, mContentSize.x - mSize.x);
        mScrollMax.y = Math.max(0, mContentSize.y - mSize.y);

        super.onAddedChild(child);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.LinearGroup#onRemovedChild(com.funzio.pure2D.DisplayObject)
     */
    @Override
    protected void onRemovedChild(final DisplayObject child) {
        PointF childSize = child.getSize();
        mContentSize.y -= childSize.y + mGap;

        // update scroll max
        mScrollMax.x = Math.max(0, mContentSize.x - mSize.x);
        mScrollMax.y = Math.max(0, mContentSize.y - mSize.y);

        super.onRemovedChild(child);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.LinearGroup#getContentSize()
     */
    @Override
    public PointF getContentSize() {
        return mContentSize;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.LinearGroup#getScrollMax()
     */
    @Override
    public PointF getScrollMax() {
        return mScrollMax;
    }
}

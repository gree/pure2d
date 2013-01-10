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
public class HGroup extends LinearGroup {
    protected PointF mContentSize = new PointF();
    protected PointF mScrollMax = new PointF();

    private int mStartIndex = 0;
    private float mStartX = 0;

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
                itemPos += mChildren.get(i).getSize().x + mGap;
            }
        }
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

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.DisplayGroup#drawChildren(javax.microedition.khronos.opengles.GL10)
     */
    @Override
    protected void drawChildren(final GLState glState) {
        // draw the children
        for (int i = 0; i < mNumChildren; i++) {
            final DisplayObject child = mChildren.get(i);
            if (child.isVisible() && (!mClipping || isChildInBounds(child))) {
                // draw frame
                child.draw(glState);
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
    }

    @Override
    protected void positionChildren() {
        float nextY = -mScrollPosition.y;

        if (mRepeating) {
            findStartIndex();
            float nextX = mStartX;
            for (int i = 0; i < mNumChildren; i++) {
                DisplayObject child = mChildren.get((i + mStartIndex) % mNumChildren);
                PointF childSize = child.getSize();
                child.setPosition(nextX, nextY);

                // find nextY
                nextX += childSize.x + mGap;
            }

            if (mStartX > mGap) {
                // draw the first item to fill the space
                int index = mStartIndex - 1;
                if (index < 0) {
                    index += mNumChildren;
                }
                final DisplayObject child = mChildren.get(index);
                child.setPosition(mStartX - child.getSize().x - mGap, child.getPosition().y);
            }
        } else {
            float nextX = -mScrollPosition.x;
            for (int i = 0; i < mNumChildren; i++) {
                DisplayObject child = mChildren.get(i);
                child.setPosition(nextX, nextY);

                // update sizes
                PointF childSize = child.getSize();
                nextX += childSize.x + mGap;
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
        mContentSize.x += (gap - mGap) * mNumChildren;

        super.setGap(gap);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.LinearGroup#onAddedChild(com.funzio.pure2D.DisplayObject)
     */
    @Override
    protected void onAddedChild(final DisplayObject child) {
        PointF childSize = child.getSize();
        mContentSize.x += childSize.x + mGap;
        mContentSize.y += childSize.y > mContentSize.y ? childSize.y : mContentSize.y;

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
        mContentSize.x -= childSize.x + mGap;

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

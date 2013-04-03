/**
 * 
 */
package com.funzio.pure2D.containers;

import java.util.ArrayList;

import android.graphics.PointF;
import android.view.MotionEvent;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.Touchable;
import com.funzio.pure2D.gl.gl10.GLState;

/**
 * @author long
 */
public class VGroup extends LinearGroup {
    protected PointF mContentSize = new PointF();
    protected PointF mScrollMax = new PointF();

    private int mStartIndex = 0;
    private float mStartY = 0;

    // swiping
    protected boolean mSwipeEnabled = false;
    protected float mSwipeMinThreshold = 0;
    protected float mSwipeAnchor;
    protected float mAnchoredScroll;
    protected boolean mSwiping = false;

    // positive orientation should be true by default
    protected boolean mPositiveOrientation = true;

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

    /**
     * @param positive
     * @return the delta to the closest child based on the specified direction which is either positive or negative
     */
    protected float getSnapDelta(final boolean positive) {
        if (mNumChildren == 0) {
            return 0;
        }

        final DisplayObject startChild = getChildAt(mStartIndex);
        final float y = startChild.getY();

        if (y < 0) {
            if (positive) {
                return y + (startChild.getSize().y + mGap);
            } else {
                return y;
            }
        } else {
            if (positive) {
                return y;
            } else {
                int newIndex = mStartIndex == 0 ? mNumChildren - 1 : mStartIndex - 1;
                final DisplayObject newChild = getChildAt(newIndex);
                return y - (newChild.getSize().y + mGap);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.containers.DisplayGroup#drawChildren(javax.microedition.khronos.opengles.GL10)
     */
    @Override
    protected void drawChildren(final GLState glState) {
        if (mTouchable) {
            if (mVisibleTouchables == null) {
                mVisibleTouchables = new ArrayList<Touchable>();
            } else {
                mVisibleTouchables.clear();
            }
        }

        // draw the children
        for (int i = 0; i < mNumChildren; i++) {
            DisplayObject child = mChildren.get(i);
            if (child.isVisible() && (!mClipping || isChildInBounds(child))) {
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

    protected float convertY(final float y, final float size) {
        return mPositiveOrientation ? y : mSize.y - y - size;
    }

    @Override
    protected void positionChildren() {
        float nextX = -mScrollPosition.x;
        float alignedX = 0;
        DisplayObject child;
        PointF childSize;

        if (mRepeating) {
            findStartIndex();
            float nextY = mStartY;
            for (int i = 0; i < mNumChildren; i++) {
                child = mChildren.get((i + mStartIndex) % mNumChildren);
                childSize = child.getSize();
                if ((mAlignment & Alignment.HORIZONTAL_CENTER) != 0) {
                    alignedX = (mSize.x - childSize.x) * 0.5f;
                } else if ((mAlignment & Alignment.RIGHT) != 0) {
                    alignedX = (mSize.x - childSize.x);
                }
                child.setPosition(nextX + alignedX, convertY(nextY, childSize.y));

                // find nextY
                nextY += childSize.y + mGap;
            }

            if (mStartY > mGap) {
                // draw the first item to fill the space
                int index = mStartIndex - 1;
                if (index < 0) {
                    index += mNumChildren;
                }
                child = mChildren.get(index);
                child.setPosition(child.getPosition().x, convertY(mStartY - child.getSize().y - mGap, child.getSize().y));
            }
        } else {
            float nextY = -mScrollPosition.y;
            for (int i = 0; i < mNumChildren; i++) {
                child = mChildren.get(i);
                childSize = child.getSize();
                if ((mAlignment & Alignment.HORIZONTAL_CENTER) != 0) {
                    alignedX = (mSize.x - childSize.x) * 0.5f;
                } else if ((mAlignment & Alignment.RIGHT) != 0) {
                    alignedX = (mSize.x - childSize.x);
                }
                child.setPosition(nextX + alignedX, convertY(nextY, childSize.y));

                // update sizes
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
        mScrollMax.x = Math.max(0, mContentSize.x - w);
        mScrollMax.y = Math.max(0, mContentSize.y - h);

        invalidateChildrenPosition();
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
        final PointF childSize = child.getSize();
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
        final PointF childSize = child.getSize();
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
        mAnchoredScroll = mScrollPosition.y;
        mSwiping = true;
    }

    protected void stopSwipe(final float delta) {
        mSwipeAnchor = -1;
        mSwiping = false;
    }

    protected void swipe(final float delta) {
        scrollTo(0, mAnchoredScroll - delta);
    }

    public boolean isSwiping() {
        return mSwiping;
    }

    public boolean isPositiveOrientation() {
        return mPositiveOrientation;
    }

    /**
     * Change display order of the children
     * 
     * @param positiveOrder
     */
    public void setPositiveOrientation(final boolean positive) {
        mPositiveOrientation = positive;

        invalidateChildrenPosition();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (mNumChildren == 0) {
            return false;
        }

        final boolean controlled = super.onTouchEvent(event);

        // swipe enabled?
        if (mSwipeEnabled) {
            final int action = event.getAction() & MotionEvent.ACTION_MASK;
            float deltaY = event.getY() - mSwipeAnchor;
            final Scene scene = getScene();
            if (scene.getAxisSystem() == Scene.AXIS_BOTTOM_LEFT) {
                // flip
                deltaY = -deltaY;
            }

            if (!mPositiveOrientation) {
                // flip again
                deltaY = -deltaY;
            }

            if (action == MotionEvent.ACTION_DOWN) {
                final PointF global = scene.getTouchedPoint();
                if (getBounds().contains(global.x, global.y)) {
                    mSwipeAnchor = event.getY();

                    // callback
                    onTouchDown(event);
                }
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (mSwipeAnchor >= 0) {
                    if (!mSwiping) {
                        if (Math.abs(deltaY) >= mSwipeMinThreshold) {
                            // re-anchor
                            mSwipeAnchor = event.getY();

                            startSwipe();
                        }
                    } else {
                        swipe(deltaY);
                    }
                }

            } else if (action == MotionEvent.ACTION_UP) {
                if (mSwiping) {
                    stopSwipe(deltaY);
                    return true;
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
}

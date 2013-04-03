/**
 * 
 */
package com.funzio.pure2D.containers;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Touchable;
import com.funzio.pure2D.gl.gl10.GLState;

/**
 * @author long
 */
public class DisplayGroup extends BaseDisplayObject implements Container, Touchable {

    protected List<DisplayObject> mChildren = new ArrayList<DisplayObject>();
    protected int mNumChildren = 0;

    // UI
    protected List<Touchable> mVisibleTouchables;
    protected boolean mTouchable = true; // true by default

    public DisplayGroup() {
        super();

        // auto update is enabled by default for Containers
        setAutoUpdateBounds(true);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.IDisplayObject#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        boolean ret = super.update(deltaTime);

        for (int i = 0; i < mNumChildren; i++) {
            DisplayObject child = mChildren.get(i);
            if (child.isAlive()) {
                // update child
                child.update(deltaTime);
            }
        }

        return ret || mNumChildren >= 0;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#updateBounds()
     */
    @Override
    public RectF updateBounds() {
        final RectF rect = super.updateBounds();

        // if bounds changed, all children's bounds should also be changed
        for (int i = 0; i < mNumChildren; i++) {
            final DisplayObject child = mChildren.get(i);
            if (child.isAutoUpdateBounds()) {
                child.updateBounds();
            }
        }

        return rect;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.IDisplayObject#draw(javax.microedition.khronos.opengles.GL10, int)
     */
    @Override
    public boolean draw(final GLState glState) {
        drawStart(glState);

        // final GLState glState = GLState.getInstance();
        // blend mode
        // boolean blendChanged = glState.setBlendFunc(mBlendFunc);

        // draw the children
        drawChildren(glState);

        // if (blendChanged) {
        // recover the blending
        // glState.setBlendFunc(null);
        // }

        drawEnd(glState);

        return true;
    }

    @Override
    protected boolean drawChildren(final GLState glState) {
        if (mNumChildren == 0) {
            return false;
        }

        if (mTouchable) {
            if (mVisibleTouchables == null) {
                mVisibleTouchables = new ArrayList<Touchable>();
            } else {
                mVisibleTouchables.clear();
            }
        }

        // draw the children
        for (int i = 0; i < mNumChildren; i++) {
            final DisplayObject child = mChildren.get(i);
            if (child.isVisible() && (glState.mCamera == null || glState.mCamera.isViewable(child))) {
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

        return true;
    }

    /**
     * Test to see if a child can be seen in this container.
     * 
     * @param child
     * @return true if at least one of the corners of the child is in this container's rect.
     */
    protected boolean isChildInBounds(final DisplayObject child) {
        // null check
        if (child == null) {
            return false;
        }

        final PointF pos = child.getPosition();
        final PointF size = child.getSize();
        return ((pos.x >= 0 && pos.x < mSize.x) && (pos.y >= 0 && pos.y < mSize.y)) // TL
                || ((pos.x + size.x >= 0 && pos.x + size.x < mSize.x) && (pos.y >= 0 && pos.y < mSize.y)) // TR
                || ((pos.x + size.x >= 0 && pos.x + size.x < mSize.x) && (pos.y + size.y >= 0 && pos.y + size.y < mSize.y))// BR
                || ((pos.x >= 0 && pos.x < mSize.x) && (pos.y + size.y >= 0 && pos.y + size.y < mSize.y)); // BL
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.IDisplayObject#dispose()
     */
    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    public boolean addChild(final DisplayObject child) {
        if (mChildren.indexOf(child) < 0) {
            mChildren.add(child);
            mNumChildren++;
            child.onAdded(this);
            invalidate();

            onAddedChild(child);
            return true;
        }
        return false;
    }

    public boolean addChild(final DisplayObject child, final int index) {
        if (index <= mNumChildren && mChildren.indexOf(child) < 0) {
            mChildren.add(index, child);
            mNumChildren++;
            child.onAdded(this);
            invalidate();

            onAddedChild(child);
            return true;
        }
        return false;
    }

    public boolean removeChild(final DisplayObject child) {
        if (mChildren.remove(child)) {
            mNumChildren--;
            child.onRemoved();
            invalidate();

            onRemovedChild(child);
            return true;
        }

        return false;
    }

    public boolean removeChild(final int index) {
        if (index < mNumChildren) {
            DisplayObject child = mChildren.remove(index);
            mNumChildren--;
            child.onRemoved();
            invalidate();

            onRemovedChild(child);
            return true;
        }

        return false;
    }

    public void removeAllChildren() {
        // update children
        for (int i = 0; i < mNumChildren; i++) {
            DisplayObject child = mChildren.get(i);
            child.onRemoved();
            onRemovedChild(child);
        }

        mChildren.clear();
        mNumChildren = 0;
        invalidate();
    }

    public DisplayObject getChildAt(final int index) {
        return index < mChildren.size() ? mChildren.get(index) : null;
    }

    public int getChildIndex(final DisplayObject child) {
        return mChildren.indexOf(child);
    }

    /**
     * Swap the indeces of 2 children. This can be used for display ordering.
     * 
     * @param child1
     * @param child2
     * @return
     */
    public boolean swapChildren(final DisplayObject child1, final DisplayObject child2) {
        // check child 1
        final int index1 = mChildren.indexOf(child1);
        if (index1 < 0) {
            return false;
        }
        // check child 2
        final int index2 = mChildren.indexOf(child2);
        if (index2 < 0) {
            return false;
        }

        mChildren.set(index1, child2);
        mChildren.set(index2, child1);
        invalidate();

        return true;
    }

    /**
     * Swap the indeces of 2 children. This can be used for display ordering.
     * 
     * @param index1
     * @param index2
     * @return
     */
    public boolean swapChildren(final int index1, final int index2) {
        // check child 1
        final DisplayObject child1 = mChildren.get(index1);
        if (child1 == null) {
            return false;
        }
        // check child 2
        final DisplayObject child2 = mChildren.get(index2);
        if (child2 == null) {
            return false;
        }

        mChildren.set(index1, child2);
        mChildren.set(index2, child1);
        invalidate();

        return true;
    }

    public boolean sendChildToTop(final DisplayObject child) {
        if (mNumChildren < 2) {
            return false;
        }
        // check child 1
        final int index = mChildren.indexOf(child);
        if (index < 0) {
            return false;
        }

        // shift other children down
        for (int i = index; i < mNumChildren - 1; i++) {
            mChildren.set(i, mChildren.get(i + 1));
        }
        mChildren.set(mNumChildren - 1, child);
        invalidate();

        return true;
    }

    public boolean sendChildToBottom(final DisplayObject child) {
        if (mNumChildren < 2) {
            return false;
        }
        // check child 1
        final int index = mChildren.indexOf(child);
        if (index < 0) {
            return false;
        }

        // shift other children up
        for (int i = index; i > 0; i--) {
            mChildren.set(i, mChildren.get(i - 1));
        }
        mChildren.set(0, child);
        invalidate();

        return true;
    }

    /**
     * Get the number of children, not including grand children
     * 
     * @see #getNumGrandChildren()
     */
    public int getNumChildren() {
        return mNumChildren;
    }

    /**
     * Get number of children including grand children
     * 
     * @see #getNumChildren()
     */
    public int getNumGrandChildren() {
        int n = mNumChildren;
        for (int i = 0; i < mNumChildren; i++) {
            DisplayObject child = mChildren.get(i);
            if (child instanceof Container) {
                n += ((Container) child).getNumGrandChildren();
            }
        }

        return n;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {

        if (mNumChildren > 0 && mVisibleTouchables != null) {
            // start from front to back
            for (int i = mVisibleTouchables.size() - 1; i >= 0; i--) {
                if (mVisibleTouchables.get(i).onTouchEvent(event)) {
                    // break here
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void setTouchable(final boolean touchable) {
        mTouchable = touchable;
    }

    @Override
    public boolean isTouchable() {
        return mTouchable;
    }

    protected void onAddedChild(final DisplayObject child) {
        // TODO
    }

    protected void onRemovedChild(final DisplayObject child) {
        // TODO
    }

    /**
     * for Debugging
     * 
     * @return a string that has all the children in Tree format
     */
    @Override
    public String getTrace(final String prefix) {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.getTrace(prefix));
        sb.append("\n");

        for (int i = 0; i < mNumChildren; i++) {
            DisplayObject child = mChildren.get(i);
            sb.append(child.getTrace(prefix + "   "));
            sb.append("\n");
        }

        return sb.toString();
    }
}

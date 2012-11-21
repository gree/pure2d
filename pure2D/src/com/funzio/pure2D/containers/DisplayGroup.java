/**
 * 
 */
package com.funzio.pure2D.containers;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.gl.gl10.GLState;

/**
 * @author long
 */
public class DisplayGroup extends BaseDisplayObject implements Container {

    protected List<DisplayObject> mChildren = new ArrayList<DisplayObject>();
    protected int mNumChildren = 0;

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

    protected void drawChildren(final GLState glState) {
        // draw the children
        for (int i = 0; i < mNumChildren; i++) {
            final DisplayObject child = mChildren.get(i);
            if (child.isVisible() && (glState.mCamera == null || glState.mCamera.isViewable(child))) {
                // draw frame
                child.draw(glState);
            }
        }
    }

    /**
     * Test to see if a child can be seen in this container.
     * 
     * @param child
     * @return true if at least one of the corners of the child is in this container's rect.
     */
    protected boolean isChildInBounds(final DisplayObject child) {
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
        return index < mNumChildren ? mChildren.get(index) : null;
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

    protected void onAddedChild(final DisplayObject child) {
        // TODO
    }

    protected void onRemovedChild(final DisplayObject child) {
        // TODO
    }
}

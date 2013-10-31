/**
 * 
 */
package com.funzio.pure2D.containers;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.Cacheable;
import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.Touchable;
import com.funzio.pure2D.exceptions.Pure2DException;
import com.funzio.pure2D.geom.Rectangle;
import com.funzio.pure2D.gl.gl10.FrameBuffer;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.shapes.DummyDrawer;
import com.funzio.pure2D.ui.UIManager;

/**
 * @author long
 */
public class DisplayGroup extends BaseDisplayObject implements Container, Cacheable, Touchable {

    protected static final String ATT_TOUCHABLE = "touchable";
    protected static final String ATT_CLIPPING_ENABLED = "clippingEnabled";
    protected static final String ATT_CACHE_ENABLED = "cacheEnabled";

    protected ArrayList<DisplayObject> mChildren = new ArrayList<DisplayObject>();
    protected ArrayList<DisplayObject> mChildrenDisplayOrder = mChildren;
    protected HashMap<String, DisplayObject> mChildrenIds = new HashMap<String, DisplayObject>();
    protected int mNumChildren = 0;

    // UI
    protected ArrayList<Touchable> mVisibleTouchables;
    protected boolean mTouchable = true; // true by default

    // cache
    protected FrameBuffer mCacheFrameBuffer;
    protected DummyDrawer mCacheDrawer;
    protected boolean mCacheEnabled = false;
    protected int mCacheProjection = Scene.AXIS_BOTTOM_LEFT;
    protected int mCachePolicy = CACHE_WHEN_CHILDREN_STABLE; // best perf

    // clipping
    protected boolean mClippingEnabled = false;
    protected boolean mOriginalScissorEnabled = false;
    protected int[] mOriginalScissor;
    protected RectF mClipStageRect;

    protected boolean mWrapContentWidth = false;
    protected boolean mWrapContentHeight = false;

    public DisplayGroup() {
        super();

        // auto update is enabled by default for Containers
        setAutoUpdateBounds(true);
    }

    @Override
    public boolean update(final int deltaTime) {
        final boolean forceChildrenConstraints = ((mInvalidateFlags & (SIZE | PARENT)) != 0);
        // super here
        boolean ret = super.update(deltaTime);

        DisplayObject child;
        float temp, sx = mSize.x, sy = mSize.y;
        for (int i = 0; i < mNumChildren; i++) {
            child = mChildren.get(i);

            if (forceChildrenConstraints) {
                child.invalidate(PARENT);
            }

            if (child.isAlive()) {
                // update child
                child.update(deltaTime);
            }

            // match content size
            if (mWrapContentWidth) {
                temp = child.getX() + child.getWidth();
                if (temp > sx) {
                    sx = temp;
                }
            }
            if (mWrapContentHeight) {
                temp = child.getY() + child.getHeight();
                if (temp > sy) {
                    sy = temp;
                }
            }
        }

        // diff check
        if (sx != mSize.x || sy != mSize.y) {
            // apply
            setSize(sx, sy);
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
        DisplayObject child;
        for (int i = 0; i < mNumChildren; i++) {
            child = mChildren.get(i);
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
        if (mNumChildren == 0) {
            return false;
        }

        drawStart(glState);

        // NOTE: this clipping method doesn't work for Rotation!!!
        if (mClippingEnabled) {
            mOriginalScissorEnabled = glState.isScissorTestEnabled();
            if (mOriginalScissorEnabled) {
                // backup the current scissor
                if (mOriginalScissor == null) {
                    mOriginalScissor = new int[4];
                }
                glState.getScissor(mOriginalScissor);
            } else {
                // need to enable scissor test
                glState.setScissorTestEnabled(true);
            }

            // instantiate the Clip rect
            if (mClipStageRect == null) {
                mClipStageRect = new RectF();
            }

            final Scene scene = getScene();
            if (scene != null) {
                // find the rect on stage, needed when there is a Camera!
                scene.globalToStage(mBounds, mClipStageRect);
            } else {
                mClipStageRect.set(mBounds);
            }

            // check parent group
            if (mParent instanceof DisplayGroup) {
                final DisplayGroup parentGroup = (DisplayGroup) mParent;
                if (parentGroup.mClippingEnabled) {
                    mClipStageRect.intersect(parentGroup.mClipStageRect);
                }
            }

            // set the new scissor rect, only take position and scale into account!
            glState.setScissor(Math.round(mClipStageRect.left), Math.round(mClipStageRect.top), Math.round(mClipStageRect.right - mClipStageRect.left + 1),
                    Math.round(mClipStageRect.bottom - mClipStageRect.top + 1));
        }

        // check cache enabled, only draw cache when Children stop changing or the policy equals CACHE_WHEN_CHILDREN_CHANGED
        if (mCacheEnabled && ((mInvalidateFlags & (CHILDREN | VISUAL)) == 0 || mCachePolicy == CACHE_WHEN_CHILDREN_CHANGED)) {
            // check invalidate flags, either CACHE or CHILDREN
            if ((mInvalidateFlags & (CACHE | CHILDREN | VISUAL)) != 0 || (mCacheFrameBuffer != null && !mCacheFrameBuffer.verifyGLState(glState))) {

                // when surface got reset, the old framebuffer and texture need to be re-created!
                if (mCacheFrameBuffer != null && !mCacheFrameBuffer.verifyGLState(glState)) {
                    // unload and remove the old texture
                    glState.getTextureManager().removeTexture(mCacheFrameBuffer.getTexture());
                    // flag for a new frame buffer
                    mCacheFrameBuffer = null;
                }

                // init frame buffer
                if (mCacheFrameBuffer == null || !mCacheFrameBuffer.hasSize(mSize)) {
                    initCache(glState, mSize);
                }

                // cache to FBO
                mCacheFrameBuffer.bind(mCacheProjection);
                mCacheFrameBuffer.clear();
                drawChildren(glState);
                mCacheFrameBuffer.unbind();

                // validate cache
                validate(CACHE);
            }

            // no color buffer supported
            glState.setColorArrayEnabled(false);
            // now the real blend mode
            glState.setBlendFunc(getInheritedBlendFunc());
            // color and alpha
            glState.setColor(getInheritedColor());
            // now draw the cache
            mCacheDrawer.draw(glState);
        } else {
            // draw the children directly
            drawChildren(glState);

            // invalidate cache
            invalidate(CACHE);
        }

        if (mClippingEnabled) {
            if (mOriginalScissorEnabled) {
                // restore original scissor
                glState.setScissor(mOriginalScissor);
            } else {
                // disable scissor test
                glState.setScissorTestEnabled(false);
            }
        }

        drawEnd(glState);

        // validate visual and children, NOT bounds
        mInvalidateFlags &= ~(VISUAL | CHILDREN);

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
        int numVisibles = 0;
        final boolean uiEnabled = getScene().isUIEnabled() && mTouchable;
        DisplayObject child;
        final int numChildren = mChildrenDisplayOrder.size();
        for (int i = 0; i < numChildren; i++) {
            child = mChildrenDisplayOrder.get(i);

            if (child.isVisible() && (glState.mCamera == null || glState.mCamera.isViewable(child))) {
                // draw frame, check alpha for optimization
                if (child.getAlpha() > 0) {
                    child.draw(glState);
                }

                // stack the visible child
                if (uiEnabled && child instanceof Touchable && ((Touchable) child).isTouchable()) {
                    float childZ = child.getZ();
                    int j = numVisibles;
                    while (j > 0 && ((DisplayObject) mVisibleTouchables.get(j - 1)).getZ() > childZ) {
                        j--;
                    }
                    mVisibleTouchables.add(j, (Touchable) child);
                    numVisibles++;
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
        return Rectangle.intersects(pos.x, pos.y, pos.x + size.x - 1, pos.y + size.y - 1, 0, 0, mSize.x - 1, mSize.y - 1);
        // return ((pos.x >= 0 && pos.x < mSize.x) && (pos.y >= 0 && pos.y < mSize.y)) // TL
        // || ((pos.x + size.x >= 0 && pos.x + size.x < mSize.x) && (pos.y >= 0 && pos.y < mSize.y)) // TR
        // || ((pos.x + size.x >= 0 && pos.x + size.x < mSize.x) && (pos.y + size.y >= 0 && pos.y + size.y < mSize.y))// BR
        // || ((pos.x >= 0 && pos.x < mSize.x) && (pos.y + size.y >= 0 && pos.y + size.y < mSize.y)); // BL
    }

    protected void initCache(final GLState glState, final PointF size) {
        if (mCacheFrameBuffer != null) {
            mCacheFrameBuffer.unload();
            mCacheFrameBuffer.getTexture().unload();
        }
        mCacheFrameBuffer = new FrameBuffer(glState, size.x, size.y, true);

        // init drawer
        if (mCacheDrawer == null) {
            mCacheDrawer = new DummyDrawer();
            // framebuffer is inverted
            if (glState.getAxisSystem() == Scene.AXIS_BOTTOM_LEFT) {
                mCacheDrawer.flipTextureCoordBuffer(FLIP_Y);
            }
        }
        // set texture for drawer
        mCacheDrawer.setTexture(mCacheFrameBuffer.getTexture());
    }

    public void clearCache() {
        if (mCacheFrameBuffer != null) {
            mCacheFrameBuffer.getTexture().unload();
            mCacheFrameBuffer.unload();
            mCacheFrameBuffer = null;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.IDisplayObject#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();

        // clear cache
        clearCache();

        if (mCacheDrawer != null) {
            mCacheDrawer.dispose();
            mCacheDrawer = null;
        }
    }

    public boolean addChild(final DisplayObject child) {
        if (mChildren.indexOf(child) < 0) {

            // check id
            final String childId = child.getId();
            if (mChildrenIds.containsKey(childId)) {
                throw new Pure2DException("There is already a child with ID: " + childId);
            }

            // child callback
            // child.onPreAdded(this);

            mChildrenIds.put(childId, child);
            mChildren.add(child);
            mNumChildren++;

            // child callback
            child.onAdded(this);
            invalidate(CHILDREN);

            // internal callback
            onAddedChild(child);
            return true;
        }
        return false;
    }

    public boolean addChild(final DisplayObject child, final int index) {
        if (index <= mNumChildren && mChildren.indexOf(child) < 0) {

            // check id
            final String childId = child.getId();
            if (mChildrenIds.containsKey(childId)) {
                throw new Pure2DException("There is already a child with ID: " + childId);
            }

            // child callback
            // child.onPreAdded(this);

            mChildrenIds.put(childId, child);
            mChildren.add(index, child);
            mNumChildren++;

            // child callback
            child.onAdded(this);
            invalidate(CHILDREN);

            onAddedChild(child);
            return true;
        }
        return false;
    }

    public boolean removeChild(final DisplayObject child) {
        if (mChildren.indexOf(child) >= 0) {

            // child callback
            // child.onPreRemoved();

            mChildrenIds.remove(child.getId());
            mChildren.remove(child);
            mNumChildren--;

            // child callback
            child.onRemoved();
            invalidate(CHILDREN);

            onRemovedChild(child);
            return true;
        }

        return false;
    }

    public boolean removeChild(final int index) {
        if (index < mNumChildren) {
            final DisplayObject child = mChildren.get(index);

            // child callback
            // child.onPreRemoved();

            mChildrenIds.remove(child.getId());
            mChildren.remove(child);
            mNumChildren--;

            // child callback
            child.onRemoved();
            invalidate(CHILDREN);

            onRemovedChild(child);
            return true;
        }

        return false;
    }

    public void removeAllChildren() {
        // call children
        // for (int i = 0; i < mNumChildren; i++) {
        // final DisplayObject child = mChildren.get(i);
        // // pre callback
        // child.onPreRemoved();
        // }

        DisplayObject child;
        for (int i = 0; i < mNumChildren; i++) {
            child = mChildren.get(i);
            // callback
            child.onRemoved();
            onRemovedChild(child);
        }

        mChildrenIds.clear();
        mChildren.clear();
        mNumChildren = 0;
        invalidate(CHILDREN);
    }

    public DisplayObject getChildAt(final int index) {
        return index < mChildren.size() ? mChildren.get(index) : null;
    }

    public int getChildIndex(final DisplayObject child) {
        return mChildren.indexOf(child);
    }

    public DisplayObject getChildById(final String id) {
        DisplayObject child = mChildrenIds.get(id);
        if (child != null) {
            return child;
        }

        // into the grand children
        for (int i = 0; i < mNumChildren; i++) {
            child = mChildren.get(i);

            if (child instanceof Container) {
                final DisplayObject grandChild = ((Container) child).getChildById(id);
                if (grandChild != null) {
                    return grandChild;
                }
            }
        }

        return null;
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
        invalidate(CHILDREN);

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
        invalidate(CHILDREN);

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
        invalidate(CHILDREN);

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
        invalidate(CHILDREN);

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
        DisplayObject child;
        for (int i = 0; i < mNumChildren; i++) {
            child = mChildren.get(i);
            if (child instanceof Container) {
                n += ((Container) child).getNumGrandChildren();
            }
        }

        return n;
    }

    /**
     * Note: This is called from UI-Thread
     */
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
        return mTouchable && mAlive;
    }

    public boolean isClippingEnabled() {
        return mClippingEnabled;
    }

    /**
     * Enable/Disable Bound-clipping. Note this does not work for rotation.
     * 
     * @param clippingEnabled
     */
    public void setClippingEnabled(final boolean clippingEnabled) {
        mClippingEnabled = clippingEnabled;

        invalidate(VISUAL);
    }

    public boolean isCacheEnabled() {
        return mCacheEnabled;
    }

    /**
     * Enable/disable cache. Use this when there are many static children to improve performance. This also clips the children inside the bounds.
     * 
     * @param cacheEnabled
     */
    public void setCacheEnabled(final boolean cacheEnabled) {
        // diff check
        if (mCacheEnabled == cacheEnabled) {
            return;
        }

        mCacheEnabled = cacheEnabled;

        invalidate(CACHE);
    }

    public int getCachePolicy() {
        return mCachePolicy;
    }

    /**
     * Set how to you want to cache
     * 
     * @param cachePolicy
     * @see #Cacheable
     */
    public void setCachePolicy(final int cachePolicy) {
        // diff check
        if (mCachePolicy == cachePolicy) {
            return;
        }

        mCachePolicy = cachePolicy;

        invalidate(CACHE);
    }

    public int getCacheProjection() {
        return mCacheProjection;
    }

    public void setCacheProjection(final int cacheProjection) {
        // diff check
        if (mCacheProjection == cacheProjection) {
            return;
        }

        mCacheProjection = cacheProjection;

        invalidate(CACHE);
    }

    // public ArrayList<DisplayObject> getChildrenDisplayOrder() {
    // return mChildrenDisplayOrder;
    // }

    public void setChildrenDisplayOrder(final ArrayList<DisplayObject> childrenDisplayOrder) {
        if (childrenDisplayOrder.size() != mNumChildren) {
            Log.e(TAG, "Invalid Children array!");
            return;
        }

        mChildrenDisplayOrder = childrenDisplayOrder;

        invalidate(CHILDREN);
    }

    public boolean isWrapContentWidth() {
        return mWrapContentWidth;
    }

    public void setWrapContentWidth(final boolean wrapWidth) {
        mWrapContentWidth = wrapWidth;

        invalidate(SIZE);
    }

    public void setWrapContentHeight(final boolean wrapHeight) {
        mWrapContentHeight = wrapHeight;

        invalidate(SIZE);
    }

    public boolean isWrapContenHeight() {
        return mWrapContentHeight;
    }

    @Override
    public void setXMLAttributes(final XmlPullParser xmlParser, final UIManager manager) {
        super.setXMLAttributes(xmlParser, manager);

        final String cacheEnabled = xmlParser.getAttributeValue(null, ATT_CACHE_ENABLED);
        if (cacheEnabled != null) {
            setCacheEnabled(Boolean.valueOf(cacheEnabled));
        }

        final String clippingEnabled = xmlParser.getAttributeValue(null, ATT_CLIPPING_ENABLED);
        if (clippingEnabled != null) {
            setClippingEnabled(Boolean.valueOf(clippingEnabled));
        }

        final String touchable = xmlParser.getAttributeValue(null, ATT_TOUCHABLE);
        if (touchable != null) {
            setTouchable(Boolean.valueOf(touchable));
        }
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
    public String getObjectTree(final String prefix) {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.getObjectTree(prefix));
        sb.append("\n");

        DisplayObject child;
        for (int i = 0; i < mNumChildren; i++) {
            child = mChildren.get(i);
            sb.append(child.getObjectTree(prefix + "   "));
            sb.append("\n");
        }

        return sb.toString();
    }
}

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
package com.funzio.pure2D.uni;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.Cacheable;
import com.funzio.pure2D.Displayable;
import com.funzio.pure2D.Parentable;
import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.StackableObject;
import com.funzio.pure2D.Touchable;
import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.exceptions.Pure2DException;
import com.funzio.pure2D.geom.Rectangle;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.ColorBuffer;
import com.funzio.pure2D.gl.gl10.FrameBuffer;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.VertexBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;
import com.funzio.pure2D.shapes.DummyDrawer;
import com.funzio.pure2D.ui.UIManager;
import com.funzio.pure2D.uni.UniContainer;

/**
 * @author long
 */
abstract public class AbstractFastUniGroup extends BaseDisplayObject implements UniContainer, Cacheable, Touchable {

    private static final String ATT_TOUCHABLE = "touchable";
    private static final String ATT_CLIPPING_ENABLED = "clippingEnabled";
    private static final String ATT_CACHE_ENABLED = "cacheEnabled";

    protected SparseArray<StackableObject> mChildren = new SparseArray<>();
    protected HashMap<StackableObject, Integer> mChildrenMap = new HashMap<StackableObject, Integer>();
    private int mChildIndex = 0;
    int mNumDrawingChildren = 0;

    // UI
    private ArrayList<Touchable> mVisibleTouchables;
    private boolean mTouchable = false;
    private boolean mModal = false;

    // cache
    private FrameBuffer mCacheFrameBuffer;
    private DummyDrawer mCacheDrawer;
    private boolean mCacheEnabled = false;
    private int mCacheProjection = Scene.AXIS_BOTTOM_LEFT;
    private int mCachePolicy = CACHE_WHEN_CHILDREN_STABLE; // best perf

    // clipping
    private boolean mClippingEnabled = false;
    private boolean mOriginalScissorEnabled = false;
    private int[] mOriginalScissor;
    private RectF mClipStageRect;

    private boolean mWrapContentWidth = false;
    private boolean mWrapContentHeight = false;
    protected Texture mTexture;
    private boolean mTextureLoaded;

    private UniContainer mUniParent;
    Matrix mMatrixForVertices;

    private VertexBuffer mVertexBuffer;
    private TextureCoordBuffer mTextureCoordBuffer;
    private ColorBuffer mColorBuffer;

    AbstractFastUniGroup() {
        super();

        // create buffers
        mVertexBuffer = createVertexBuffer();
        mTextureCoordBuffer = createTextureCoordBuffer();
        mColorBuffer = createColorBuffer();

        // auto update is enabled by default for Containers
        setAutoUpdateBounds(true);

        // no need to check me, but my children
        setBypassCameraClipping(true);
    }

    abstract protected VertexBuffer createVertexBuffer();

    abstract protected TextureCoordBuffer createTextureCoordBuffer();

    abstract protected ColorBuffer createColorBuffer();

    @Override
    protected void updateChildren(final int deltaTime) {
        super.updateChildren(deltaTime);

        final boolean forceChildrenConstraints = ((mInvalidateFlags & (SIZE | PARENT | PARENT_BOUNDS)) != 0);

        int numDrawingChildren = 0;
        float temp, sx = mSize.x, sy = mSize.y;
        for (int i = mChildren.size() - 1; i >= 0; --i) {
            final StackableObject child = mChildren.valueAt(i);

            if (forceChildrenConstraints) {
                child.invalidate(PARENT);
            }

            if (child.isAlive()) {
                // hint child to update bounds
                if ((mInvalidateFlags & BOUNDS) != 0) {
                    child.invalidate(PARENT_BOUNDS);
                }
                // update child
                child.update(deltaTime);
            }

            if (child.shouldDraw(mScene != null ? mScene.getCameraRect() : null)) {
                final int num2Draw = child.getNumStackedChildren();
                // there is something to draw
                if (num2Draw > 0) {
                    numDrawingChildren += num2Draw;
                    child.setStackable(true);
                } else {
                    child.setStackable(false);
                }
            } else {
                child.setStackable(false);
            }

            // match content size
            if (mWrapContentWidth) {
                temp = child.getX() + child.getWidth() - child.getOrigin().x;
                if (temp > sx) {
                    sx = temp;
                }
            }
            if (mWrapContentHeight) {
                temp = child.getY() + child.getHeight() - child.getOrigin().y;
                if (temp > sy) {
                    sy = temp;
                }
            }
        }

        setNumDrawingChildren(numDrawingChildren);

        // diff check
        if (sx != mSize.x || sy != mSize.y) {
            // apply
            setSize(sx, sy);

            // size changed? need to re-update bounds, in the same frame
            if ((mAutoUpdateBounds || Pure2D.AUTO_UPDATE_BOUNDS)) {
                // check constraints first ,only apply it when size or parent changed
                if (mUIConstraint != null) {
                    mUIConstraint.apply(this, mParent);
                }

                // re-cal the matrix
                updateBounds();

                // hint the children to update bounds next frame
                for (int i = mChildren.size() - 1; i >= 0; --i) {
                    final StackableObject child = mChildren.valueAt(i);
                    child.invalidate(PARENT_BOUNDS);
                }
            }
        }
    }

    @Override
    public boolean draw(final GLState glState) {
        if (mChildren.size() <= 0) {
            return false;
        }

        drawStart(glState);

        // no color buffer supported
        glState.setColorArrayEnabled(false);
        // now the real blend mode
        glState.setBlendFunc(getInheritedBlendFunc());
        // color and alpha
        glState.setColor(getInheritedColor());

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

            if (mScene != null) {
                // find the rect on stage, needed when there is a Camera!
                mScene.globalToStage(mBounds, mClipStageRect);
            } else {
                mClipStageRect.set(mBounds);
            }

            // check parent group
            if (mParent instanceof AbstractFastUniGroup) {
                final AbstractFastUniGroup parentGroup = (AbstractFastUniGroup) mParent;
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

    boolean stackChildren(final GLState glState) {
        if (mNumDrawingChildren == 0) {
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
        final boolean uiEnabled = mTouchable && mScene != null && mScene.isUIEnabled();
        int stackIndex = 0;
        for (int i = mChildren.size() - 1; i >= 0; --i) {
            final StackableObject child = mChildren.valueAt(i);
            if (child.isStackable()) {
                // draw frame, check alpha for optimization
                if (stackIndex < mNumDrawingChildren) {
                    stackIndex += child.stack(glState, stackIndex, mVertexBuffer, mColorBuffer, mTexture != null ? mTextureCoordBuffer : null);
                } else {
                    Log.wtf(TAG, "This should NEVER happen: " + stackIndex + " >= " + mNumDrawingChildren);
                }

                // stack the visible child
                if (uiEnabled && child instanceof Touchable && ((Touchable) child).isTouchable()) {
                    float childZ = child.getZ();
                    int j = numVisibles;
                    while (j > 0 && ((StackableObject) mVisibleTouchables.get(j - 1)).getZ() > childZ) {
                        j--;
                    }
                    mVisibleTouchables.add(j, (Touchable) child);
                    numVisibles++;
                }
            }
        }

        return true;
    }

    @Override
    protected void onPreConcatParentMatrix() {
        super.onPreConcatParentMatrix();

        if (mMatrixForVertices == null) {
            mMatrixForVertices = new Matrix(mMatrix);
        } else {
            mMatrixForVertices.set(mMatrix);
        }

    }

    public Texture getTexture() {
        return mTexture;
    }

    /**
     * @param texture the texture to set
     */
    public void setTexture(final Texture texture) {
        mTexture = texture;
        mTextureLoaded = mTexture != null && mTexture.isLoaded();

        invalidate(mTextureLoaded ? (TEXTURE | TEXTURE_COORDS) : TEXTURE);

        if (mTextureLoaded) {
            onTextureLoaded(texture);
        }
    }

    @Override
    public GLColor getInheritedColor() {
        final GLColor color = super.getInheritedColor();

        if (mParent == null) {
            // multiply by parent's attributes
            if (mUniParent != null && mUniParent instanceof Displayable) {
                final Displayable parent = (Displayable) mUniParent;
                final GLColor parentColor = parent.getInheritedColor();
                if (parentColor != null) {
                    color.multiply(parentColor);
                }
            }
        }

        return color;
    }

    @Override
    public boolean update(final int deltaTime) {
        // async support: texture loaded detection
        if (!mTextureLoaded && mTexture != null && mTexture.isLoaded()) {
            // flag
            mTextureLoaded = true;
            invalidate(TEXTURE_COORDS);

            // internal callback
            onTextureLoaded(mTexture);
        }

        return super.update(deltaTime);
    }

    @Override
    public final void invalidate(final int flags) {
        super.invalidate(flags);

        if (mUniParent != null) {
            mUniParent.invalidate(CHILDREN);
        }
    }

    /**
     * Test to see if a child can be seen in this container.
     *
     * @param child
     * @return true if at least one of the corners of the child is in this container's rect.
     */
    protected boolean isChildInBounds(final StackableObject child) {
        // null check
        if (child == null) {
            return false;
        }

        final PointF pos = child.getPosition();
        final PointF size = child.getSize();
        return Rectangle.intersects(pos.x, pos.y, pos.x + size.x - 1, pos.y + size.y - 1, 0, 0, mSize.x - 1, mSize.y - 1);
    }

    void initCache(final GLState glState, final PointF size) {
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

    public boolean addChild(final StackableObject child) {
        if (!mChildrenMap.containsKey(child)) {
            // child callback
            // child.onPreAdded(this);

            Integer index = mChildIndex++;
            return addChild(child, index);
        }
        return false;
    }

    public boolean addChild(final StackableObject child, int index) {
        if (!mChildrenMap.containsKey(child)) {

            mChildren.put(index, child);
            mChildrenMap.put(child, index);

            // child callback
            child.onAdded(this);
            invalidate(CHILDREN);

            // internal callback
            onAddedChild(child);
            return true;
        }
        return false;
    }

    public boolean removeChild(final StackableObject child) {
        final Integer index = mChildrenMap.get(child);
        if (index != null) {

            // child callback
            // child.onPreRemoved();

            mChildren.remove(index);
            mChildrenMap.remove(child);

            // child callback
            child.onRemoved();
            invalidate(CHILDREN);

            onRemovedChild(child);
            return true;
        }

        return false;
    }

    public boolean removeChild(final int index) {
        final StackableObject child = mChildren.get(index);
        if (child != null) {
            mChildren.remove(index);
            mChildrenMap.remove(child);

            // child callback
            child.onRemoved();
            invalidate(CHILDREN);

            onRemovedChild(child);
            return true;
        }

        return false;
    }

    public void removeAllChildren() {
        for (int i = mChildren.size() - 1; i >= 0; --i) {
            final StackableObject child = mChildren.valueAt(i);
            // callback
            child.onRemoved();
            onRemovedChild(child);
        }

        mChildren.clear();
        mChildrenMap.clear();
        mChildIndex = 0;
        invalidate(CHILDREN);
    }

    public StackableObject getChildAt(final int index) {
        return getChildById(index);
    }

    public int getChildIndex(final StackableObject child) {
        return mChildrenMap.get(child);
    }

    public StackableObject getChildById(final int id) {
        return mChildren.get(id);
    }


    @Override
    public StackableObject getChildById(final String id) {
        throw new Pure2DException("AbstractFastUniGroup.getChildById");
    }
    /**
     * Swap the indeces of 2 children. This can be used for display ordering.
     *
     * @param child1
     * @param child2
     * @return
     */
    public boolean swapChildren(final StackableObject child1, final StackableObject child2) {
        throw new Pure2DException("AbstractFastUniGroup.swapChildren");
    }

    /**
     * Swap the indeces of 2 children. This can be used for display ordering.
     *
     * @param index1
     * @param index2
     * @return
     */
    public boolean swapChildren(final int index1, final int index2) {
        throw new Pure2DException("AbstractFastUniGroup.swapChildren");
    }

    public boolean sendChildToTop(final StackableObject child) {
        throw new Pure2DException("AbstractFastUniGroup.sendChildToTop");
    }

    public boolean sendChildToBottom(final StackableObject child) {
        throw new Pure2DException("AbstractFastUniGroup.sendChildToBottom");
    }

    /**
     * Get the number of children, not including grand children
     *
     * @see #getNumGrandChildren()
     */
    public int getNumChildren() {
        return mChildren.size();
    }

    /**
     * Get number of children including grand children
     *
     * @see #getNumChildren()
     */
    public int getNumGrandChildren() {
        return getNumStackedChildren();
    }

    //Use this when calling from the GL thread! getNumChildren/getNumGrandChildren aren't thread safe!
    public int getNumStackedChildren() {
        return mNumDrawingChildren;
    }

    void setNumDrawingChildren(final int num) {
        mNumDrawingChildren = num;
    }

    /**
     * Note: This is called from UI-Thread
     */
    @Override
    public boolean onTouchEvent(final MotionEvent event) {

        if (mChildren.size() > 0 && mVisibleTouchables != null) {
            // start from front to back
            for (int i = mVisibleTouchables.size() - 1; i >= 0; --i) {
                if (mVisibleTouchables.get(i).onTouchEvent(event)) {
                    // break here
                    return true;
                }
            }
        }

        // also take control if this is modal
        return mModal;
    }

    @Override
    public void setTouchable(final boolean touchable) {
        mTouchable = touchable;
    }

    @Override
    public boolean isTouchable() {
        return mTouchable && mAlive;
    }

    @Override
    public void setModal(final boolean modal) {
        mModal = modal;
    }

    @Override
    public boolean isModal() {
        return mModal;
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

    @Override
    public void onAdded(final Container container) {
        super.onAdded(container);

        // only one parent
        mUniParent = null;
    }

    public void onAdded(final UniContainer container) {
        mUniParent = container;

        final Scene scene = container.getScene();
        if (scene != null) {
            onAddedToScene(scene);
        }

        // use parent's texture
        setTexture(container.getTexture());

        // flag the bounds are changed now
        invalidate(PARENT);

        // only one parent
        mParent = null;
    }

    @Override
    public void onRemoved() {
        super.onRemoved();

        mUniParent = null;
    }

    void onAddedChild(final StackableObject child) {
        // TODO
    }

    void onRemovedChild(final StackableObject child) {
        // TODO
    }

    @Override
    public void onAddedToScene(final Scene scene) {
        super.onAddedToScene(scene);

        // forward to all children
        for (int i = mChildren.size() - 1; i >= 0; --i) {
            final StackableObject child = mChildren.valueAt(i);
            child.onAddedToScene(scene);
        }
    }

    @Override
    public void onRemovedFromScene() {
        super.onRemovedFromScene();

        // forward to all children
        for (int i = mChildren.size() - 1; i >= 0; --i) {
            final StackableObject child = mChildren.valueAt(i);
            child.onRemovedFromScene();
        }
    }

    @Override
    public boolean removeFromParent() {
        return super.removeFromParent() || mUniParent != null && mUniParent.removeChild((StackableObject) this);

    }

    @Override
    final public Parentable getParent() {
        return mParent != null ? mParent : mUniParent;
    }

    @Override
    protected Matrix getParentMatrix() {
        return mParent != null ? mParent.getMatrix() : (mUniParent != null ? mUniParent.getMatrix() : null);
    }

    /**
     * @param texture
     */
    protected void onTextureLoaded(final Texture texture) {
        // TODO Auto-generated method stub

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

        for (int i = mChildren.size() - 1; i >= 0; --i) {
            final StackableObject child = mChildren.valueAt(i);
            sb.append(child.getObjectTree(prefix + "   "));
            sb.append("\n");
        }

        return sb.toString();
    }
}

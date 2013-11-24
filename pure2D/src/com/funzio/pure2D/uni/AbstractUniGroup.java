/**
 * 
 */
package com.funzio.pure2D.uni;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.Cacheable;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.Touchable;
import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.exceptions.Pure2DException;
import com.funzio.pure2D.geom.Rectangle;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.FrameBuffer;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.DummyDrawer;
import com.funzio.pure2D.ui.UIManager;

/**
 * @author long
 */
abstract public class AbstractUniGroup extends BaseDisplayObject implements UniContainer, Cacheable, Touchable {

    protected static final String ATT_TOUCHABLE = "touchable";
    protected static final String ATT_CLIPPING_ENABLED = "clippingEnabled";
    protected static final String ATT_CACHE_ENABLED = "cacheEnabled";

    protected ArrayList<Uniable> mChildren = new ArrayList<Uniable>();
    protected ArrayList<Uniable> mChildrenDisplayOrder = mChildren;
    protected HashMap<String, Uniable> mChildrenIds = new HashMap<String, Uniable>();
    protected int mNumChildren = 0;
    protected int mNumDrawingChildren = 0;

    // UI
    protected ArrayList<Touchable> mVisibleTouchables;
    protected boolean mTouchable = false; // false by default

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
    protected Texture mTexture;
    protected boolean mTextureLoaded;

    protected UniContainer mUniParent;
    protected Matrix mMatrixForVertices;

    public AbstractUniGroup() {
        super();

        // auto update is enabled by default for Containers
        setAutoUpdateBounds(true);

        // no need to check me, but my children
        setBypassCameraClipping(true);
    }

    @Override
    protected void updateChildren(final int deltaTime) {
        super.updateChildren(deltaTime);

        final boolean forceChildrenConstraints = ((mInvalidateFlags & (SIZE | PARENT)) != 0);

        int numDrawingChildren = 0;
        Uniable child;
        float temp, sx = mSize.x, sy = mSize.y;
        for (int i = 0; i < mNumChildren; i++) {
            child = mChildren.get(i);

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

            if (child.shouldDraw(mScene != null ? mScene.getCamera() : null)) {
                int num2Draw = 0;
                if (child instanceof UniContainer) {
                    num2Draw = ((UniContainer) child).getNumDrawingChildren();
                } else {
                    num2Draw = 1;
                }

                // there is something to draw
                if (num2Draw > 0) {
                    numDrawingChildren += num2Draw;
                    child.setStackable(true);
                }
            } else {
                child.setStackable(false);
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

        setNumDrawingChildren(numDrawingChildren);

        // diff check
        if (sx != mSize.x || sy != mSize.y) {
            // apply
            setSize(sx, sy);

            // size changed? need to re-update bounds, in the same frame
            if (mAutoUpdateBounds) {
                // check constraints first ,only apply it when size or parent changed
                if (mUIConstraint != null) {
                    mUIConstraint.apply(this, mParent);
                }

                // re-cal the matrix
                updateBounds();
            }
        }
    }

    @Override
    public boolean draw(final GLState glState) {
        if (mNumChildren == 0) {
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
            if (mParent instanceof AbstractUniGroup) {
                final AbstractUniGroup parentGroup = (AbstractUniGroup) mParent;
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

    @Override
    protected boolean drawChildren(final GLState glState) {
        if (!stackChildren(glState)) {
            return false;
        }

        // TODO override this

        return true;
    }

    protected boolean stackChildren(final GLState glState) {
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
        Uniable child;
        final int numChildren = mChildrenDisplayOrder.size();
        int stackIndex = 0;
        for (int i = 0; i < numChildren; i++) {
            child = mChildrenDisplayOrder.get(i);

            if (child.isStackable()) {
                // draw frame, check alpha for optimization
                if (stackIndex < mNumDrawingChildren) {
                    stackIndex += stackChildAt(glState, child, stackIndex);
                } else {
                    // FIXME :((
                    // Log.wtf(TAG, "This should NOT happen: " + stackIndex + " >= " + mNumDrawingChildren);
                }

                // stack the visible child
                if (uiEnabled && child instanceof Touchable && ((Touchable) child).isTouchable()) {
                    float childZ = child.getZ();
                    int j = numVisibles;
                    while (j > 0 && ((Uniable) mVisibleTouchables.get(j - 1)).getZ() > childZ) {
                        j--;
                    }
                    mVisibleTouchables.add(j, (Touchable) child);
                    numVisibles++;
                }
            }
        }

        return true;
    }

    abstract protected int stackChildAt(GLState glState, final Uniable child, final int index);

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
        mTextureLoaded = mTexture != null ? mTexture.isLoaded() : false;

        invalidate(mTextureLoaded ? (TEXTURE | TEXTURE_COORDS) : TEXTURE);

        if (mTextureLoaded) {
            onTextureLoaded(texture);
        }
    }

    @Override
    protected GLColor getInheritedColor() {
        final GLColor color = super.getInheritedColor();

        if (mParent == null) {
            // multiply by parent's attributes
            if (mUniParent != null && mUniParent instanceof AbstractUniGroup) {
                final AbstractUniGroup parent = (AbstractUniGroup) mUniParent;
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
    protected boolean isChildInBounds(final Uniable child) {
        // null check
        if (child == null) {
            return false;
        }

        final PointF pos = child.getPosition();
        final PointF size = child.getSize();
        return Rectangle.intersects(pos.x, pos.y, pos.x + size.x - 1, pos.y + size.y - 1, 0, 0, mSize.x - 1, mSize.y - 1);
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

    public boolean addChild(final Uniable child) {
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

    public boolean addChild(final Uniable child, final int index) {
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

    public boolean removeChild(final Uniable child) {
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
            final Uniable child = mChildren.get(index);

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
        // final UnifiedObject child = mChildren.get(i);
        // // pre callback
        // child.onPreRemoved();
        // }

        Uniable child;
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

    public Uniable getChildAt(final int index) {
        return index < mChildren.size() ? mChildren.get(index) : null;
    }

    public int getChildIndex(final Uniable child) {
        return mChildren.indexOf(child);
    }

    public Uniable getChildById(final String id) {
        Uniable child = mChildrenIds.get(id);
        if (child != null) {
            return child;
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
    public boolean swapChildren(final Uniable child1, final Uniable child2) {
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
        final Uniable child1 = mChildren.get(index1);
        if (child1 == null) {
            return false;
        }
        // check child 2
        final Uniable child2 = mChildren.get(index2);
        if (child2 == null) {
            return false;
        }

        mChildren.set(index1, child2);
        mChildren.set(index2, child1);
        invalidate(CHILDREN);

        return true;
    }

    public boolean sendChildToTop(final Uniable child) {
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

    public boolean sendChildToBottom(final Uniable child) {
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
        Uniable child;
        for (int i = 0; i < mNumChildren; i++) {
            child = mChildren.get(i);
            if (child instanceof UniContainer) {
                n += ((UniContainer) child).getNumGrandChildren();
            }
        }

        return n;
    }

    public int getNumDrawingChildren() {
        return mNumDrawingChildren;
    }

    protected void setNumDrawingChildren(final int num) {
        mNumDrawingChildren = num;
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

    // public ArrayList<UnifiedObject> getChildrenDisplayOrder() {
    // return mChildrenDisplayOrder;
    // }

    public void setChildrenDisplayOrder(final ArrayList<Uniable> childrenDisplayOrder) {
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

    protected void onAddedChild(final Uniable child) {
        // TODO
    }

    protected void onRemovedChild(final Uniable child) {
        // TODO
    }

    @Override
    public void onAddedToScene(final Scene scene) {
        super.onAddedToScene(scene);

        // forward to all children
        Uniable child;
        for (int i = 0; i < mNumChildren; i++) {
            child = mChildren.get(i);
            child.onAddedToScene(scene);
        }
    }

    @Override
    public void onRemovedFromScene() {
        super.onRemovedFromScene();

        // forward to all children
        Uniable child;
        for (int i = 0; i < mNumChildren; i++) {
            child = mChildren.get(i);
            child.onRemovedFromScene();
        }
    }

    @Override
    public boolean removeFromParent() {
        if (super.removeFromParent()) {
            return true;
        }

        if (mUniParent != null) {
            return mUniParent.removeChild((Uniable) this);
        } else {
            return false;
        }
    }

    public UniContainer getUniParent() {
        return mUniParent;
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

        Uniable child;
        for (int i = 0; i < mNumChildren; i++) {
            child = mChildren.get(i);
            sb.append(child.getObjectTree(prefix + "   "));
            sb.append("\n");
        }

        return sb.toString();
    }
}

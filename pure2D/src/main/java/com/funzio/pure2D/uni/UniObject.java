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
package com.funzio.pure2D.uni;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Displayable;
import com.funzio.pure2D.InvalidateFlags;
import com.funzio.pure2D.Parentable;
import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.StackableObject;
import com.funzio.pure2D.animators.Manipulator;
import com.funzio.pure2D.exceptions.Pure2DException;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.BlendFunc;
import com.funzio.pure2D.gl.gl10.BlendModes;
import com.funzio.pure2D.gl.gl10.GLState;

/**
 * @author long
 */
public abstract class UniObject implements StackableObject, InvalidateFlags {
    public static final String TAG = UniObject.class.getSimpleName();

    // for debugging
    protected int mDebugFlags = 0;

    protected String mId = getClass().getSimpleName() + '_' + Integer.toHexString(hashCode());

    // dimensions and size
    protected PointF mPosition = new PointF(0, 0);
    protected PointF mOrigin = new PointF(0, 0);
    protected PointF mSize = new PointF(1, 1);
    protected PointF mScale = new PointF(1, 1);
    protected PointF mPivot = new PointF(0, 0);
    // rotation
    protected float mRotation = 0;
    protected float mZ = 0;// z-depth
    // skewing
    protected PointF mSkew;

    // life
    protected boolean mAlive = true;
    protected boolean mVisible = true;

    // framerate
    private int mFps = 0; // frames per second
    protected float mFrameDuration = 0; // ms per frame

    // reference to the parent container
    protected UniContainer mParent;
    protected Scene mScene;

    // extra
    protected GLColor mColor;
    protected GLColor mBlendColor;
    protected BlendFunc mBlendFunc;
    protected float mAlpha = 1;

    protected boolean mHasOrigin = false;
    protected boolean mOriginAtCenter = false;

    protected ArrayList<Manipulator> mManipulators;
    protected int mNumManipulators = 0;

    // rect and bounds
    protected int mInvalidateFlags = 0;
    protected Matrix mMatrix;
    protected Matrix mMatrixForVertices;
    // global bounds
    protected RectF mBounds = new RectF(-mOrigin.x, -mOrigin.y, -mOrigin.x + mSize.x - 1, -mOrigin.y + mSize.y - 1);
    protected boolean mAutoUpdateBounds;
    protected boolean mBypassCameraClipping = false;
    protected PointF mGlobalPosition;

    // interface
    protected float[] mVertices;
    protected boolean mStackable;

    abstract protected void resetVertices();

    public UniObject() {
        // TODO nothing
    }

    protected void drawWireframe(final GLState glState) {
        if (mVertices != null && mSize.x > 0 && mSize.y > 0) {
            Pure2D.drawDebugVertices(glState, Pure2D.DEBUG_FLAG_WIREFRAME, mVertices);
        }
    }

    protected void drawBounds(final GLState glState) {
        if (mBounds.width() > 0 && mBounds.height() > 0) {
            final GL10 gl = glState.mGL;
            gl.glPushMatrix();
            gl.glLoadIdentity();
            Pure2D.drawDebugRect(glState, mBounds.left, mBounds.bottom, mBounds.right, mBounds.top, Pure2D.DEBUG_FLAG_GLOBAL_BOUNDS);
            gl.glPopMatrix();
        }
    }

    public boolean update(final int deltaTime) {
        // update the manipulators if there's any
        if (mNumManipulators > 0) {
            for (int i = 0; i < mNumManipulators; i++) {
                mManipulators.get(i).update(deltaTime);
            }
        }

        // finally update bounds and vertices
        if ((mInvalidateFlags & BOUNDS) != 0) {
            // re-cal the matrix
            updateMatrix();

            if ((mAutoUpdateBounds || Pure2D.AUTO_UPDATE_BOUNDS)) {
                // update bounds
                updateBounds();
            }

            // bounds change leads to vertices change
            mInvalidateFlags |= VERTICES;
        }

        // apply to the vertices
        if ((mInvalidateFlags & VERTICES) != 0) {
            // check visibility
            if (shouldDraw(mScene != null ? mScene.getCameraRect() : null)) {
                resetVertices();
                mMatrixForVertices.mapPoints(mVertices);

                // validate vertices only
                mInvalidateFlags &= ~VERTICES;
            }
        }

        // validate transform AFTER updateMatrix()
        mInvalidateFlags &= ~(BOUNDS);

        return mNumManipulators > 0;
    }

    /**
     * @hide
     */
    final public void invalidate() {
        // invalidate generally, NOT!
        // mInvalidateFlags = ALL;

        if (mParent != null) {
            mParent.invalidate(CHILDREN);
        }
    }

    /**
     * @hide
     */
    final public void invalidate(final int flags) {
        mInvalidateFlags |= flags;

        if (mParent != null) {
            mParent.invalidate(CHILDREN);
        }
    }

    /**
     * @hide
     */
    final protected void validate(final int flags) {
        mInvalidateFlags &= ~flags;
    }

    /**
     * Toggles the heart-beat. If set to false, the update() does NOT get called. This can be used for optimization.
     * 
     * @see #update(int)
     */
    public void setAlive(final boolean value) {
        mAlive = value;
    }

    final public boolean isAlive() {
        return mAlive;
    }

    public void setVisible(final boolean value) {
        mVisible = value;
        invalidate(VISIBILITY);
    }

    final public boolean isVisible() {
        return mVisible;
    }

    @Override
    public boolean shouldDraw(final RectF globalViewRect) {
        return mVisible && mAlpha > 0 && (mBypassCameraClipping || globalViewRect == null || RectF.intersects(globalViewRect, mBounds));
    }

    /**
     * @hide for internal use
     */
    @Override
    public void setStackable(final boolean value) {
        mStackable = value;
    }

    /**
     * @hide for internal use
     */
    @Override
    public boolean isStackable() {
        return mStackable;
    }

    /**
     * @return the position
     */
    final public PointF getPosition() {
        return mPosition;
    }

    /**
     * @param position the position to set
     */
    final public void setPosition(final PointF position) {
        setPosition(position.x, position.y);
    }

    /**
     * @param position the position to set
     */
    public void setPosition(final float x, final float y) {
        mPosition.x = x;
        mPosition.y = y;
        invalidate(POSITION);
    }

    public void setX(final float x) {
        mPosition.x = x;
        invalidate(POSITION);
    }

    final public float getX() {
        return mPosition.x;
    }

    public void setY(final float y) {
        mPosition.y = y;
        invalidate(POSITION);
    }

    final public float getY() {
        return mPosition.y;
    }

    /**
     * Set the Z-depth
     * 
     * @see #setAlphaTestEnabled(boolean)
     */
    public void setZ(final float z) {
        mZ = z;
        invalidate(POSITION);
    }

    final public float getZ() {
        return mZ;
    }

    public void moveTo(final float x, final float y) {
        mPosition.x = x;
        mPosition.y = y;
        invalidate(POSITION);
    }

    public void move(final float dx, final float dy) {
        mPosition.x += dx;
        mPosition.y += dy;
        invalidate(POSITION);
    }

    /**
     * @return the origin
     */
    final public PointF getOrigin() {
        return mOrigin;
    }

    /**
     * @param origin the origin to set. Origin is the local point and (0,0) by default. Origin is used to define offset of this object and also the center of rotation and scaling.
     */
    final public void setOrigin(final PointF origin) {
        setOrigin(origin.x, origin.y);
    }

    public void setOrigin(final float x, final float y) {
        mOrigin.x = x;
        mOrigin.y = y;
        mHasOrigin = mOrigin.x != 0 || mOrigin.y != 0;

        // check center
        if (mOriginAtCenter) {
            if (x != mSize.x * 0.5f || y != mSize.y * 0.5f) {
                // no longer center
                mOriginAtCenter = false;
            }
        }

        invalidate(ORIGIN);
    }

    public void setOriginAtCenter() {
        setOrigin(mSize.x * 0.5f, mSize.y * 0.5f);

        // flag
        mOriginAtCenter = true;
    }

    public boolean isOriginAtCenter() {
        return mOriginAtCenter;
    }

    public PointF getPivot() {
        return mPivot;
    }

    public void setPivot(final PointF pivot) {
        mPivot.x = pivot.x;
        mPivot.y = pivot.y;

        invalidate(PIVOT);
    }

    public void setPivot(final float x, final float y) {
        mPivot.x = x;
        mPivot.y = y;

        invalidate(PIVOT);
    }

    public void setPivotAtCenter() {
        mPivot.x = mSize.x * 0.5f;
        mPivot.y = mSize.y * 0.5f;

        invalidate(PIVOT);
    }

    /**
     * @return the size
     */
    final public PointF getSize() {
        return mSize;
    }

    final public float getWidth() {
        return mSize.x;
    }

    final public float getHeight() {
        return mSize.y;
    }

    /**
     * @param size the size to set
     */
    final public void setSize(final PointF size) {
        setSize(size.x, size.y);
    }

    /**
     * @param size the size to set
     */
    public void setSize(final float w, final float h) {
        mSize.x = w;
        mSize.y = h;

        if (mOriginAtCenter) {
            // auto center
            setOrigin(w * 0.5f, h * 0.5f);
        }

        invalidate(SIZE);
    }

    /**
     * @param screen_scale the scale to set
     */
    public void setScale(final float sx, final float sy) {
        mScale.x = sx;
        mScale.y = sy;
        invalidate(SCALE);
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(final float scale) {
        mScale.x = mScale.y = scale;
        invalidate(SCALE);
    }

    final public PointF getScale() {
        return mScale;
    }

    public void setRotation(final float degree) {
        mRotation = degree;
        invalidate(ROTATION);
    }

    public void rotate(final float degreeDelta) {
        mRotation += degreeDelta;
        invalidate(ROTATION);
    }

    final public float getRotation() {
        return mRotation;
    }

    public void setSkew(final float kx, final float ky) {
        // sanity check for optimization
        if (kx == 0 && ky == 0 && mSkew == null) {
            return;
        }

        if (mSkew == null) {
            mSkew = new PointF(kx, ky);
        } else {
            mSkew.x = kx;
            mSkew.y = ky;
        }

        invalidate(SKEW);
    }

    public PointF getSkew() {
        return mSkew;
    }

    /**
     * @return the color
     */
    final public GLColor getColor() {
        return mColor;
    }

    /**
     * @param color the color to set
     */
    public void setColor(final GLColor color) {
        mColor = color;
        invalidate(COLOR);
    }

    /**
     * @return the blendFunc
     */
    public BlendFunc getBlendFunc() {
        return mBlendFunc;
    }

    /**
     * @param blendFunc the blendFunc to set
     */
    public void setBlendFunc(final BlendFunc blendFunc) {
        mBlendFunc = blendFunc;
        invalidate(BLEND);
    }

    /**
     * @return the final color which takes parent's color and alpha into account
     */
    final public GLColor getInheritedColor() {
        if (BlendModes.isInterpolate(mBlendFunc)) {
            if (mBlendColor == null) {
                // init
                mBlendColor = (mColor == null) ? new GLColor(1f, 1f, 1f, mAlpha) : new GLColor(mColor.r, mColor.g, mColor.b, mColor.a * mAlpha);
            } else {
                if (mColor == null) {
                    mBlendColor.setValues(1f, 1f, 1f, mAlpha);
                } else {
                    mBlendColor.setValues(mColor.r, mColor.g, mColor.b, mColor.a * mAlpha);
                }
            }

        } else {

            if (mBlendColor == null) {
                // init
                mBlendColor = (mColor == null) ? new GLColor(mAlpha, mAlpha, mAlpha, mAlpha) : new GLColor(mColor.r * mAlpha, mColor.g * mAlpha, mColor.b * mAlpha, mColor.a * mAlpha);
            } else {
                if (mColor == null) {
                    mBlendColor.setValues(mAlpha, mAlpha, mAlpha, mAlpha);
                } else {
                    mBlendColor.setValues(mColor.r * mAlpha, mColor.g * mAlpha, mColor.b * mAlpha, mColor.a * mAlpha);
                }
            }
        }

        // multiply by parent's attributes
        if (mParent != null && mParent instanceof Displayable) {
            final Displayable parent = (Displayable) mParent;
            final GLColor parentColor = parent.getInheritedColor();
            if (parentColor != null) {
                mBlendColor.multiply(parentColor);
            }
        }

        return mBlendColor;
    }

    final public BlendFunc getInheritedBlendFunc() {
        if (mBlendFunc != null) {
            return mBlendFunc;
        } else if (mParent != null && mParent instanceof Displayable) {
            return ((Displayable) mParent).getInheritedBlendFunc();
        } else {
            return null;
        }
    }

    /**
     * @return the alpha
     */
    final public float getAlpha() {
        return mAlpha;
    }

    /**
     * @param alpha the alpha to set
     */
    public void setAlpha(final float alpha) {
        mAlpha = alpha;
        invalidate(ALPHA);
    }

    /**
     * @return the fps
     */
    final public int getFps() {
        return mFps;
    }

    /**
     * @param fps the fps to set
     */
    public void setFps(final int fps) {
        mFps = fps;

        if (mFps > 0) {
            mFrameDuration = 1000f / mFps;
        } else {
            mFrameDuration = Scene.DEFAULT_MSPF;
        }
    }

    public boolean addManipulator(final Manipulator manipulator) {
        if (mManipulators == null) {
            // init
            mManipulators = new ArrayList<Manipulator>();
        }

        if (mManipulators.add(manipulator)) {
            manipulator.setTarget(this);
            mNumManipulators++;
            return true;
        }

        return false;
    }

    public boolean removeManipulator(final Manipulator manipulator) {
        // null check
        if (mManipulators != null && mManipulators.remove(manipulator)) {
            manipulator.setTarget(null);
            mNumManipulators--;
            return true;
        }

        return false;
    }

    public int removeAllManipulators() {
        // null check
        if (mManipulators == null) {
            return 0;
        }

        final int n = mNumManipulators;
        mManipulators.clear();
        mNumManipulators = 0;

        return n;
    }

    public Manipulator getManipulator(final int index) {
        return index < mNumManipulators ? mManipulators.get(index) : null;
    }

    public int getNumManipulators() {
        return mNumManipulators;
    }

    @Override
    final public Parentable getParent() {
        return mParent;
    }

    final public boolean queueEvent(final Runnable r) {
        if (mParent != null) {
            return mParent.queueEvent(r);
        } else {
            // run directly
            r.run();
        }

        // always success now
        return true;
    }

    public boolean removeFromParent() {
        if (mParent != null) {
            return mParent.removeChild(this);
        }
        return false;
    }

    /**
     * Converts a local point to a global point, without allocating new PointF
     * 
     * @param local
     * @param result
     */
    final public void localToGlobal(final PointF local, final PointF result) {
        result.x = (local == null ? 0 : local.x) + mPosition.x;
        result.y = (local == null ? 0 : local.y) + mPosition.y;
        if (mParent != null && !(mParent instanceof Scene)) {
            mParent.localToGlobal(result, result);

            if (mParent instanceof DisplayObject) {
                // apply parent's origin
                final PointF parentOrigin = ((DisplayObject) mParent).getOrigin();
                result.x -= parentOrigin.x;
                result.y -= parentOrigin.y;
            }
        }
    }

    /**
     * Converts a global point to a local point, without allocating new PointF
     * 
     * @param global
     * @param result
     */
    final public void globalToLocal(final PointF global, final PointF result) {
        if (mParent != null && !(mParent instanceof Scene)) {
            mParent.globalToLocal(global, result);

            if (mParent instanceof DisplayObject) {
                // apply parent's origin
                final PointF parentOrigin = ((DisplayObject) mParent).getOrigin();
                result.x += parentOrigin.x;
                result.y += parentOrigin.y;
            }
        } else {
            result.x = global.x;
            result.y = global.y;
        }

        result.x -= mPosition.x;
        result.y -= mPosition.y;
    }

    /**
     * Find the global bounds of this object that takes position, scale, rotation, skew... into account. Used mainly for Camera clipping and bounds hit-testing.
     */
    protected void updateMatrix() {
        boolean changed = false;

        // init
        if (mMatrix == null) {
            mMatrix = new Matrix();
            changed = true;
        }
        final Matrix parentMatrix = (mParent == null) ? null : mParent.getMatrix();

        // skew
        if (mSkew != null) {
            mMatrix.setSkew(mSkew.x, mSkew.y);
            // flag
            changed = true;
        }

        // rotation first
        if (mRotation != 0) {
            if (changed) {
                mMatrix.postRotate(mRotation, mOrigin.x + mPivot.x, mOrigin.y + mPivot.y);
            } else {
                mMatrix.setRotate(mRotation, mOrigin.x + mPivot.x, mOrigin.y + mPivot.y);
            }
            // flag
            changed = true;
        }

        // scale next
        if (mScale.x != 1 || mScale.y != 1) {
            if (changed) {
                mMatrix.postScale(mScale.x, mScale.y, mOrigin.x + mPivot.x, mOrigin.y + mPivot.y);
            } else {
                mMatrix.setScale(mScale.x, mScale.y, mOrigin.x + mPivot.x, mOrigin.y + mPivot.y);
                // flag
                changed = true;
            }
        }

        // clear flags: bounds
        // mInvalidateFlags &= ~BOUNDS;

        // translate later
        if ((mPosition.x - mOrigin.x) != 0 || (mPosition.y - mOrigin.y) != 0) {
            if (changed) {
                mMatrix.postTranslate(mPosition.x - mOrigin.x, mPosition.y - mOrigin.y);
            } else {
                mMatrix.setTranslate(mPosition.x - mOrigin.x, mPosition.y - mOrigin.y);
                // flag
                changed = true;
            }
        } else {
            if (!changed) {
                // reset to identity
                mMatrix.reset();
            }
        }

        // find the bounds
        if (changed || parentMatrix != null) {
            if (mMatrixForVertices == null) {
                mMatrixForVertices = new Matrix(mMatrix);
            } else {
                mMatrixForVertices.set(mMatrix);
            }

            // apply the parent's matrix
            if (parentMatrix != null) {
                mMatrix.postConcat(parentMatrix);
            }
        }
    }

    /**
     * Get global position of this object
     * 
     * @return the global point
     */
    public PointF getGlobalPosition() {
        // null check
        if (mGlobalPosition == null) {
            mGlobalPosition = new PointF();
        }
        localToGlobal(null, mGlobalPosition);

        return mGlobalPosition;
    }

    public RectF updateBounds() {
        if (mMatrix != null) {
            // prepare to map to the matrix
            mBounds.left = 0;
            mBounds.top = 0;
            mBounds.right = mSize.x - 1;
            mBounds.bottom = mSize.y - 1;

            // apply to the bounds
            mMatrix.mapRect(mBounds);
        }

        return mBounds;
    }

    final public Matrix getMatrix() {
        return mMatrix;
    }

    /**
     * Get the Global Bounds of this object that takes translation, rotation and scale factors into account.
     * 
     * @return
     */
    final public RectF getBounds() {
        return mBounds;
    }

    /**
     * @return the autoUpdateBounds
     */
    final public boolean isAutoUpdateBounds() {
        return mAutoUpdateBounds;
    }

    public boolean isBypassCameraClipping() {
        return mBypassCameraClipping;
    }

    public void setBypassCameraClipping(final boolean ignoreCameraClipping) {
        mBypassCameraClipping = ignoreCameraClipping;
    }

    /**
     * This needs to be set to true if using Camera clipping.
     * 
     * @param autoUpdateBounds the autoUpdateBounds to set
     */
    public void setAutoUpdateBounds(final boolean autoUpdateBounds) {
        mAutoUpdateBounds = autoUpdateBounds;
    }

    public int getNumStackedChildren() {
        return 1;
    }

    public void dispose() {
        // TODO
    }

    /**
     * @return
     * @see Pure2D.DEBUG_FLAG_LOCAL_SHAPE, Pure2D.DEBUG_FLAG_GLOBAL_BOUNDS
     */
    public int getDebugFlags() {
        return mDebugFlags;
    }

    /**
     * @param flags
     * @see Pure2D.DEBUG_FLAG_SHAPE, Pure2D.DEBUG_FLAG_BOUNDS
     */
    public void setDebugFlags(final int flags) {
        mDebugFlags = flags;
        invalidate(VISUAL);
    }

    /**
     * for debugging
     */
    public String getObjectTree(final String prefix) {
        return prefix + toString();
    }

    /**
     * for ui
     */
    public String getId() {
        return mId;
    }

    public void setId(final String id) {
        if (mParent != null) {
            throw new Pure2DException("Object is already contained. ID cannot be changed!");
        }

        mId = id;
    }

    public Scene getScene() {
        return mScene;
    }

    /**
     * This is called after this object is added to a Container
     */
    public void onAdded(final UniContainer container) {
        mParent = container;

        final Scene scene = container.getScene();
        if (scene != null) {
            onAddedToScene(scene);
        }

        // flag the bounds are changed now
        invalidate(PARENT);
    }

    /**
     * This is called after this object is removed from a Container
     */
    public void onRemoved() {
        if (mScene != null) {
            onRemovedFromScene();
        }

        mParent = null;
    }

    /**
     * @hide For internal use
     */
    public void onAddedToScene(final Scene scene) {
        mScene = scene;
    }

    /**
     * @hide For internal use
     */
    public void onRemovedFromScene() {
        mScene = null;
    }

}

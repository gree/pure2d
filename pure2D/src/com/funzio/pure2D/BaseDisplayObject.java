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
package com.funzio.pure2D;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.Manipulator;
import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.exceptions.Pure2DException;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.BlendFunc;
import com.funzio.pure2D.gl.gl10.BlendModes;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.particles.nova.vo.AnimatorVO;
import com.funzio.pure2D.ui.UIConstraint;
import com.funzio.pure2D.ui.UIManager;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public abstract class BaseDisplayObject implements DisplayObject {
    public static final String TAG = BaseDisplayObject.class.getSimpleName();

    // xml attributes
    protected static final String ATT_COLOR = "color";
    protected static final String ATT_ALPHA = "alpha";
    protected static final String ATT_BLEND_MODE = "blendMode";
    protected static final String ATT_ORIGIN_AT_CENTER = "originAtCenter";
    protected static final String ATT_ROTATION = "rotation";
    protected static final String ATT_VISIBLE = "visible";
    protected static final String ATT_AUTO_UPDATE_BOUNDS = "autoUpdateBounds";
    protected static final String ATT_ANIMATOR = "animator";
    protected static final String ATT_DEBUG = "debug";

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
    protected float mRotationVectorX = 0;
    protected float mRotationVectorY = 0;
    protected float mRotationVectorZ = 1;
    protected float mZ = 0;// z-depth
    // skewing
    protected PointF mSkew;

    // extra transformation
    protected Matrix mTransformMatrix;
    // some scratch
    protected float[] mTransformMatrixValues;
    protected boolean mHasTransformValues = false;

    // life
    protected boolean mVisible = true;
    protected boolean mAlive = true;

    // framerate
    private int mFps = 0; // frames per second
    protected float mFrameDuration = 0; // ms per frame

    // reference to the parent container
    protected Container mParent;
    protected Scene mScene;

    protected Maskable mMask;

    // extra
    protected GLColor mColor;
    protected float mAlpha = 1;
    protected BlendFunc mBlendFunc;
    // protected boolean mDepthTestEnabled = false;
    protected boolean mAlphaTestEnabled = false;

    private boolean mHasOrigin = false;
    private boolean mOriginAtCenter = false;
    private GLColor mBlendColor;
    private PointF mGlobalPosition;

    protected ArrayList<Manipulator> mManipulators;
    protected int mNumManipulators = 0;

    // rect and bounds
    protected int mInvalidateFlags = 0;
    protected Matrix mMatrix;
    protected boolean mAutoUpdateBounds = false;
    // global bounds
    protected RectF mBounds = new RectF(-mOrigin.x, -mOrigin.y, -mOrigin.x + mSize.x - 1, -mOrigin.y + mSize.y - 1);
    protected boolean mBypassCameraClipping = false;

    // perspective projection
    protected boolean mPerspectiveEnabled = false;
    private boolean mPerspectiveProjecting = false; // flag to prevent matrix out of sync caused by Threads
    private float[] mOriginalProjection;
    protected PointF mSceneSize;

    protected UIConstraint mUIConstraint;

    abstract protected boolean drawChildren(final GLState glState);

    /**
     * This is used for 3D projection
     * 
     * @return
     * @see #setPerspectiveEnabled(boolean)
     */
    protected PointF getSceneSize() {
        return (mScene != null) ? mScene.getSize() : null;
    }

    @Override
    public boolean draw(final GLState glState) {
        drawStart(glState);

        // blend mode
        glState.setBlendFunc(getInheritedBlendFunc());
        // color and alpha
        glState.setColor(getInheritedColor());

        // draw the content
        drawChildren(glState);

        // wrap up
        drawEnd(glState);

        // validate visual only
        mInvalidateFlags &= ~VISUAL;

        return true;
    }

    protected void drawStart(final GLState glState) {
        final GL10 gl = glState.mGL;

        // keep the model matrix
        gl.glPushMatrix();

        // perspective projection
        if (mPerspectiveEnabled) {
            if (mSceneSize == null) {
                mSceneSize = getSceneSize();
            }

            // screen size is a requirement
            if (mSceneSize != null) {
                mPerspectiveProjecting = true; // flag
                gl.glMatrixMode(GL10.GL_PROJECTION);
                // gl.glPushMatrix();
                gl.glLoadIdentity();

                // keep original projection
                if (mOriginalProjection == null) {
                    mOriginalProjection = new float[5];
                }
                glState.getProjection(mOriginalProjection);

                // set new projection
                glState.setProjection(Scene.PROJECTION_PERSPECTIVE, 0, mSceneSize.x - 1, 0, mSceneSize.y - 1);

                // back to modelview
                gl.glMatrixMode(GL10.GL_MODELVIEW);
            }
        }

        // translating
        if (mPosition.x != 0 || mPosition.y != 0 || mZ != 0) {
            gl.glTranslatef(mPosition.x, mPosition.y, mZ);
        }

        if (mPivot.x != 0 || mPivot.y != 0) {
            gl.glTranslatef(mPivot.x, mPivot.x, 0);
        }

        // scaling
        if (mScale.x != 1 || mScale.y != 1) {
            gl.glScalef(mScale.x, mScale.y, 0);
        }
        // rotating
        if (mRotation != 0) {
            gl.glRotatef(mRotation, mRotationVectorX, mRotationVectorY, mRotationVectorZ);
        }

        // extra transformation
        if (mHasTransformValues) {
            gl.glMultMatrixf(mTransformMatrixValues, 0);
        }

        if (mPivot.x != 0 || mPivot.y != 0) {
            gl.glTranslatef(-mPivot.x, -mPivot.x, 0);
        }

        // shift off the origin
        if (mHasOrigin) {
            gl.glTranslatef(-mOrigin.x, -mOrigin.y, 0);
        }

        // check and turn on depth test
        // glState.setDepthTestEnabled(mDepthTestEnabled);
        // check and turn on alpha test
        glState.setAlphaTestEnabled(mAlphaTestEnabled);

        // check mask
        if (mMask != null) {
            mMask.enableMask();
        }
        // glState.setMask(mMask); // why this doesn't work?
    }

    protected void drawEnd(final GLState glState) {
        final GL10 gl = glState.mGL;

        // for debugging
        final int debugFlags = Pure2D.DEBUG_FLAGS | mDebugFlags;
        // local rect
        if ((debugFlags & Pure2D.DEBUG_FLAG_WIREFRAME) != 0 && mSize.x > 0 && mSize.y > 0) {
            drawWireframe(glState);
        }

        // check mask
        if (mMask != null) {
            mMask.disableMask();
        }

        // restore the matrix
        if (mPerspectiveProjecting) {
            gl.glMatrixMode(GL10.GL_PROJECTION);
            // gl.glPopMatrix();
            gl.glLoadIdentity();
            glState.setProjection(mOriginalProjection);

            gl.glMatrixMode(GL10.GL_MODELVIEW);
            mPerspectiveProjecting = false; // unflag
        }
        // restore the model matrix
        gl.glPopMatrix();

        // debug global bounds
        if ((debugFlags & Pure2D.DEBUG_FLAG_GLOBAL_BOUNDS) != 0) {
            drawBounds(glState);
        }
    }

    protected void drawWireframe(final GLState glState) {
        // TODO to be overriden
        // Pure2D.drawDebugRect(glState, 0, 0, mSize.x - 1, mSize.y - 1, Pure2D.DEBUG_FLAG_WIREFRAME);
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

    @Override
    public boolean update(final int deltaTime) {
        // check constraints first ,only apply it when size or parent changed
        if (mUIConstraint != null && (mInvalidateFlags & (SIZE | PARENT | PARENT_BOUNDS)) != 0) {
            mUIConstraint.apply(this, mParent);
        }

        // update the manipulators if there's any
        if (mNumManipulators > 0) {
            for (int i = 0; i < mNumManipulators; i++) {
                mManipulators.get(i).update(deltaTime);
            }
        }

        // check for extra transformation
        if ((mInvalidateFlags & TRANSFORM_MATRIX) != 0) {
            // check and init matrix
            if (mTransformMatrix == null) {
                mTransformMatrix = new Matrix();
            } else {
                mTransformMatrix.reset();
            }
            // apply to 2d matrix
            if (mSkew != null) {
                mTransformMatrix.setSkew(mSkew.x, mSkew.y);
            }
            mHasTransformValues = !mTransformMatrix.isIdentity();

            // convert to 3d matrix
            if (mHasTransformValues) {
                if (mTransformMatrixValues == null) {
                    mTransformMatrixValues = new float[16];
                }
                // get values
                Pure2DUtils.getMatrix3DValues(mTransformMatrix, mTransformMatrixValues);
            }
        }

        // update bounds for the children to calculate their bounds correctly
        if ((mAutoUpdateBounds || Pure2D.AUTO_UPDATE_BOUNDS) && (mInvalidateFlags & BOUNDS) != 0) {
            // re-cal the matrix
            updateBounds();
        }

        // now update children
        updateChildren(deltaTime);

        // validate transform AFTER updateBounds()
        mInvalidateFlags &= ~(BOUNDS | TRANSFORM_MATRIX);

        return mNumManipulators > 0;
    }

    protected void updateChildren(final int deltaTime) {
        // TODO nothing
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
    public void invalidate(final int flags) {
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
     * @see com.funzio.pure2D.IDisplayObject#setAlive(boolean)
     * @see #update(int)
     */
    @Override
    public void setAlive(final boolean value) {
        mAlive = value;
    }

    @Override
    final public boolean isAlive() {
        return mAlive;
    }

    @Override
    public void setVisible(final boolean value) {
        mVisible = value;
        invalidate(VISIBILITY);
    }

    @Override
    final public boolean isVisible() {
        return mVisible;
    }

    @Override
    public boolean shouldDraw(final RectF globalViewRect) {
        return mVisible && mAlpha > 0 && (mBypassCameraClipping || globalViewRect == null || RectF.intersects(globalViewRect, mBounds));
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

    // public void setRotationX(final float degree) {
    // mRotationX = degree;
    // invalidate(ROTATION);
    // }
    //
    // public void rotateX(final float degreeDelta) {
    // mRotationX += degreeDelta;
    // invalidate(ROTATION);
    // }
    //
    // final public float getRotationX() {
    // return mRotationX;
    // }
    //
    // public void setRotationY(final float degree) {
    // mRotationY = degree;
    // invalidate(ROTATION);
    // }
    //
    // public void rotateY(final float degreeDelta) {
    // mRotationY += degreeDelta;
    // invalidate(ROTATION);
    // }
    //
    // final public float getRotationY() {
    // return mRotationY;
    // }

    public void setRotationVector(final float x, final float y, final float z) {
        mRotationVectorX = x;
        mRotationVectorY = y;
        mRotationVectorZ = z;

        invalidate(ROTATION);
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
     * @return the final color which takes parent's color and alpha into account
     */
    public GLColor getInheritedColor() {
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

    // final public boolean isDepthTestEnabled() {
    // return mDepthTestEnabled;
    // }
    //
    // public void setDepthTestEnabled(final boolean depthTestEnabled) {
    // mDepthTestEnabled = depthTestEnabled;
    //
    // invalidate(DEPTH);
    // }

    final public boolean isAlphaTestEnabled() {
        return mAlphaTestEnabled;
    }

    /**
     * Enable Alpha Test. This can be useful when doing depth sorting but also a Performance-Killer. Make sure you know what you're doing!
     * 
     * @param alphaTestEnabled
     * @see #setZ(float)
     */
    public void setAlphaTestEnabled(final boolean alphaTestEnabled) {
        mAlphaTestEnabled = alphaTestEnabled;

        invalidate(ALPHA);
    }

    final public Maskable getMask() {
        return mMask;
    }

    public void setMask(final Maskable mask) {
        mMask = mask;

        invalidate(VISIBILITY);
    }

    // protected Scene findScene() {
    // if (mParent instanceof Scene) {
    // return (Scene) mParent;
    // } else if (mParent instanceof DisplayObject) {
    // return ((DisplayObject) mParent).getScene();
    // }
    //
    // return null;
    // }

    final public Scene getScene() {
        return mScene;
    }

    @Override
    public Parentable getParent() {
        return mParent;
    }

    final public boolean queueEvent(final Runnable r) {
        if (mScene != null) {
            return mScene.queueEvent(r);
        } else if (Pure2D.ADAPTER != null) {
            // use the adapter
            if (Pure2D.ADAPTER.getSurface() != null) {
                Pure2D.ADAPTER.getSurface().queueEvent(r);
            } else {
                // run directly
                r.run();
            }
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

    /**
     * Find the global bounds of this object that takes position, scale, rotation, skew... into account. Used mainly for Camera clipping and bounds hit-testing.
     */
    public RectF updateBounds() {
        boolean changed = false;

        // init
        if (mMatrix == null) {
            mMatrix = new Matrix();
            changed = true;
        }
        final Matrix parentMatrix = getParentMatrix();

        if (mHasTransformValues) {
            mMatrix.setTranslate(-mOrigin.x - mPivot.x, -mOrigin.y - mPivot.y);
            mMatrix.postConcat(mTransformMatrix);
            mMatrix.postTranslate(mOrigin.x + mPivot.x, mOrigin.y + mPivot.y);
            // flag
            changed = true;
        }

        // rotation first
        if (mRotation != 0 && (mRotationVectorX == 0 && mRotationVectorY == 0 && mRotationVectorZ == 1)) {
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

        // translate later
        if ((mPosition.x - mOrigin.x) != 0 || (mPosition.y - mOrigin.y) != 0) {
            if (changed) {
                mMatrix.postTranslate(mPosition.x - mOrigin.x, mPosition.y - mOrigin.y);
            } else {
                mMatrix.setTranslate(mPosition.x - mOrigin.x, mPosition.y - mOrigin.y);
                // flag
                changed = true;

                if (parentMatrix == null) {
                    onPreConcatParentMatrix();
                    // easy case: only translation needs to be applied. No need to use matrix!
                    mBounds.left = mPosition.x - mOrigin.x;
                    mBounds.top = mPosition.y - mOrigin.y;
                    mBounds.right = mBounds.left + mSize.x - 1;
                    mBounds.bottom = mBounds.top + mSize.y - 1;
                    // done, no need to go further!
                    return mBounds;
                }
            }
        } else {
            if (!changed) {
                // reset to identity
                mMatrix.reset();
            }
        }

        // prepare to map to the matrix
        mBounds.left = 0;
        mBounds.top = 0;
        mBounds.right = mSize.x - 1;
        mBounds.bottom = mSize.y - 1;

        onPreConcatParentMatrix();
        // find the bounds
        if (changed || parentMatrix != null) {

            // apply the parent's matrix
            if (parentMatrix != null) {
                mMatrix.postConcat(parentMatrix);
            }

            // apply to the bounds
            mMatrix.mapRect(mBounds);
        }

        return mBounds;
    }

    protected void onPreConcatParentMatrix() {
        // TODO override
    }

    final public Matrix getMatrix() {
        return mMatrix;
    }

    protected Matrix getParentMatrix() {
        return mParent != null ? mParent.getMatrix() : null;
    }

    /**
     * Get the Global Bounds of this object that takes translation, rotation and scale factors into account.
     * 
     * @return
     */
    final public RectF getBounds() {
        return mBounds;
    }

    public boolean isBypassCameraClipping() {
        return mBypassCameraClipping;
    }

    public void setBypassCameraClipping(final boolean ignoreCameraClipping) {
        mBypassCameraClipping = ignoreCameraClipping;
    }

    /**
     * @return the autoUpdateBounds
     */
    final public boolean isAutoUpdateBounds() {
        return mAutoUpdateBounds;
    }

    /**
     * This needs to be set to true if using Camera clipping.
     * 
     * @param autoUpdateBounds the autoUpdateBounds to set
     */
    public void setAutoUpdateBounds(final boolean autoUpdateBounds) {
        mAutoUpdateBounds = autoUpdateBounds;
    }

    public boolean isPerspectiveEnabled() {
        return mPerspectiveEnabled;
    }

    /**
     * @see {@link Scene#setDepthRange(float, float)}, {@link #getSceneSize()}
     */
    public void setPerspectiveEnabled(final boolean perspectiveEnabled) {
        mPerspectiveEnabled = perspectiveEnabled;

        invalidate(PERSPECTIVE);
    }

    @Override
    public void dispose() {
        mTransformMatrix = null;
        mTransformMatrixValues = null;
        mGlobalPosition = null;
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
     * This is called after this object is added to a Container
     */
    public void onAdded(final Container container) {
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

    @Override
    public String toString() {
        return mId;
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

    public void setXMLAttributes(final XmlPullParser xmlParser, final UIManager manager) {
        // set id
        final String id = xmlParser.getAttributeValue(null, "id");
        if (id != null && !id.equals("")) {
            setId(id);
        }

        // get constraint
        if (mUIConstraint == null) {
            mUIConstraint = new UIConstraint(xmlParser, manager);
        } else {
            mUIConstraint.mergeAttributes(xmlParser);
        }

        // more attributes
        final String debug = xmlParser.getAttributeValue(null, ATT_DEBUG);
        if (debug != null) {
            mDebugFlags = Boolean.valueOf(debug) ? Pure2D.DEBUG_FLAG_GLOBAL_BOUNDS | Pure2D.DEBUG_FLAG_WIREFRAME : 0;
        }

        final String originAtCenter = xmlParser.getAttributeValue(null, ATT_ORIGIN_AT_CENTER);
        if (originAtCenter != null) {
            if (Boolean.valueOf(originAtCenter)) {
                setOriginAtCenter();
            }
        }

        final String updateBounds = xmlParser.getAttributeValue(null, ATT_AUTO_UPDATE_BOUNDS);
        if (updateBounds != null) {
            setAutoUpdateBounds(Boolean.valueOf(updateBounds));
        }

        final String color = xmlParser.getAttributeValue(null, ATT_COLOR);
        if (color != null) {
            mColor = new GLColor(Color.parseColor(color));
        }

        final String alpha = xmlParser.getAttributeValue(null, ATT_ALPHA);
        if (alpha != null) {
            mAlpha = Float.valueOf(alpha);
        }

        final String rotation = xmlParser.getAttributeValue(null, ATT_ROTATION);
        if (rotation != null) {
            mRotation = Float.valueOf(rotation);
        }

        final String blendMode = xmlParser.getAttributeValue(null, ATT_BLEND_MODE);
        if (blendMode != null) {
            mBlendFunc = BlendModes.getBlendFunc(blendMode);
        }

        final String visible = xmlParser.getAttributeValue(null, ATT_VISIBLE);
        if (visible != null) {
            mVisible = Boolean.valueOf(visible);
        }

        final String animator = xmlParser.getAttributeValue(null, ATT_ANIMATOR);
        if (animator != null) {
            final AnimatorVO animatorVO = manager.getConfig().getAnimatorVO(animator);
            if (animatorVO != null) {
                final Animator ani = animatorVO.createAnimator(0, this);
                addManipulator(ani);
                ani.start();
            } else {
                Log.e(TAG, "Animator not found: " + animator, new Exception());
            }
        }
    }

    /**
     * @hide For internal use
     */
    public void onCreateChildren(final UIManager manager) {
        // TODO override this
    }

    public UIConstraint getUIConstraint() {
        return mUIConstraint;
    }

    public void setUIConstraint(final UIConstraint uiConstraint) {
        mUIConstraint = uiConstraint;
    }

}

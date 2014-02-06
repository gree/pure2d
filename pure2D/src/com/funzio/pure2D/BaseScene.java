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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.exceptions.Pure2DException;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.BlendFunc;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;
import com.funzio.pure2D.ui.UITextureManager;

/**
 * @author long
 */
public class BaseScene implements Scene {
    public static final String TAG = BaseScene.class.getSimpleName();

    protected int mBuffersToInvalidate = 4; // number of buffer to invalidate. 2 for old devices but 3+ for Jelly Bean or later
    protected Stage mStage;
    protected GLState mGLState;
    protected Camera mCamera;
    protected boolean mClippingEnabled = false;
    protected TextureManager mTextureManager;
    protected ArrayList<DisplayObject> mChildren = new ArrayList<DisplayObject>();
    protected HashMap<String, DisplayObject> mChildrenIds = new HashMap<String, DisplayObject>();
    private int mNumChildren;

    private PointF mSize = new PointF();
    private RectF mBounds = new RectF();

    private long mStartTime = 0;
    private boolean mPaused = false;
    private boolean mAutoClear = true;
    private int mInvalidated = 0;
    private boolean mRenderContinueously = false;

    // frame rate
    private int mFrameCount = 0;
    private float mFrameCountDuration = 0;
    private volatile int mCurrentFps = 0; // thread independent
    private int mTargetFps = 0; // 0 = unlimited
    private int mTargetDuration = 1000 / (mTargetFps > 0 ? mTargetFps : DEFAULT_FPS);
    private int mTargetDurationJitter = (int) (mTargetDuration * 0.15f);
    private int mDownTime = 0;

    // extra
    private GLColor mColor = new GLColor(0f, 0f, 0f, 1f);
    private BlendFunc mDefaultBlendFunc = BlendFunc.getInterpolate();
    private Listener mListener;

    // axis system
    private int mAxisSystem = AXIS_BOTTOM_LEFT;

    // UI
    private final Object mUILock = new Object();
    private boolean mUIEnabled = false;
    private ArrayList<Touchable> mVisibleTouchables;
    private ArrayList<PointF> mTouchedPoints = new ArrayList<PointF>();
    private int mPointerCount = 0;

    public BaseScene() {
    }

    /**
     * @return the stage
     */
    final public Stage getStage() {
        return mStage;
    }

    final public boolean queueEvent(final Runnable r) {
        if (mStage != null) {
            mStage.queueEvent(r);
        } else {
            r.run();
        }

        // always success now
        return true;
    }

    final public boolean queueEvent(final Runnable r, final int delayMillis) {
        if (mStage != null && mStage.getHandler() != null) {
            mStage.getHandler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // queue on GL thread
                    queueEvent(r);
                }
            }, delayMillis);
        } else {
            r.run();
        }

        // always success now
        return true;
    }

    /**
     * @param stage the stage to set
     */
    public void setStage(final Stage stage) {
        mStage = stage;

        final Rect stageRect = mStage.getRect();
        // update size
        mSize.x = stageRect.right - stageRect.left + 1;
        mSize.y = stageRect.bottom - stageRect.top + 1;
        // update bounds
        mBounds.right = mSize.x - 1;
        mBounds.bottom = mSize.y - 1;
    }

    public void setAxisSystem(final int value) {
        mAxisSystem = value;

        // reset the camera
        if (mGLState != null) {
            mGLState.setAxisSystem(mAxisSystem);
            setCamera(mCamera);
        }
    }

    /**
     * @see {@link DisplayObject#setPerspectiveEnabled(boolean)}, {@link PerspectiveCamera}
     * @param zNear
     * @param zFar
     */
    public void setDepthRange(final float zNear, final float zFar) {
        if (mGLState != null) {
            mGLState.mGL.glDepthRangef(zNear, zFar);
        }
    }

    final public int getAxisSystem() {
        return mAxisSystem;
    }

    final public int getCurrentFps() {
        return mCurrentFps;
    }

    /**
     * @return the target fps
     */
    @Deprecated
    final public int getTargetFps() {
        return mTargetFps;
    }

    /**
     * @param fps the fps to set
     */
    @Deprecated
    public void setTargetFps(final int fps) {
        mTargetFps = fps;

        mTargetDuration = 1000 / (mTargetFps > 0 ? mTargetFps : DEFAULT_FPS);
        mTargetDurationJitter = (int) (mTargetDuration * 0.15f);
    }

    public void setListener(final Listener listener) {
        mListener = listener;
    }

    final public GLState getGLState() {
        return mGLState;
    }

    protected TextureManager createDefaultTextureManager() {
        return new UITextureManager(this, mStage.getResources());
    }

    public void setTextureManager(final TextureManager textureManager) {
        mTextureManager = textureManager;
    }

    final public TextureManager getTextureManager() {
        return mTextureManager;
    }

    @Override
    public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
        Log.v(TAG, "onSurfaceCreated()");
        // this might help but I have not seen any difference yet!
        // Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        // init GL properties, ONCE!
        if (Pure2D.GL_MAX_TEXTURE_SIZE == 0) {
            Pure2D.initGLProperties(gl);
        }

        // Set the background color to black ( rgba ).
        gl.glClearColor(mColor.r, mColor.g, mColor.b, mColor.a);
        // Enable Smooth Shading, default not really needed.
        gl.glShadeModel(GL10.GL_SMOOTH);

        // Depth buffer setup.
        gl.glClearDepthf(1.0f);
        // Enables depth testing.
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL); // The type of depth testing to do.

        // this is helpful for depth-rendering but tremendously reduces framerate, should be disabled by default!
        // gl.glEnable(GL10.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL10.GL_GREATER, 0f);

        // or disable depth test completely
        // this does NOT necessary increase any FPS based on my test
        // gl.glDisable(GL10.GL_DEPTH_TEST);
        // gl.glDepthFunc(GL10.GL_NEVER);
        // gl.glDepthMask(false);

        // perspective calculations.
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        // Enable alpha blending
        gl.glEnable(GL10.GL_BLEND);
        // gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
        // if (gl instanceof GL11ExtensionPack) {
        // final GL11ExtensionPack gl11 = (GL11ExtensionPack) gl;
        // // gl11.glBlendEquationSeparate(GL11ExtensionPack.GL_FUNC_ADD, GL11ExtensionPack.GL_FUNC_ADD);
        // }

        // Enable Texture, not here!
        // gl.glEnable(GL10.GL_TEXTURE_2D);

        // Counter-clockwise winding.
        gl.glFrontFace(GL10.GL_CCW);
        // Enable face culling.
        // gl.glEnable(GL10.GL_CULL_FACE);
        // What faces to remove with the face culling.
        // gl.glCullFace(GL10.GL_BACK);

        // Clears the screen and depth buffer.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // unload the textures on the old GL
        // if (mTextureManager != null) {
        // mTextureManager.unloadAllTextures();
        // }

        // init GL state
        boolean firstTime;
        if (mGLState == null) {
            mGLState = new GLState(gl, mStage);
            mGLState.setAxisSystem(mAxisSystem);
            mGLState.setDefaultBlendFunc(mDefaultBlendFunc);
            // mGLState.setCamera(mCamera);

            // init Texture manager with the new GL
            mTextureManager = createDefaultTextureManager();
            // assign to GLState
            mGLState.setTextureManager(mTextureManager);

            mStartTime = SystemClock.elapsedRealtime();
            mDownTime = 0;
            mFrameCount = 0;
            mCurrentFps = 0;
            mFrameCountDuration = 0;
            firstTime = true;
        } else {
            // reload with new gl
            mGLState.reset(gl);
            final long idleTime = SystemClock.elapsedRealtime() - mStartTime;
            mTextureManager.update((int) idleTime);
            // reload all textures
            mTextureManager.reload(mGLState, mStage.getResources());

            // simulate surface changed
            onSurfaceChanged(gl, (int) mSize.x, (int) mSize.y);
            firstTime = false;
        }

        // callback
        if (mListener != null) {
            mListener.onSurfaceCreated(mGLState, firstTime);
        }

    }

    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        // update size
        mSize.x = width;
        mSize.y = height;
        // update bounds
        mBounds.right = mSize.x - 1;
        mBounds.bottom = mSize.y - 1;

        // Sets the current view port to the new size.
        // gl.glViewport(0, 0, width, height);
        mGLState.setViewport(0, 0, width, height);

        // Select the projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // Reset the projection matrix
        gl.glLoadIdentity();

        // reapply the camera
        setCamera(mCamera);

        // Select the modelview matrix
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        // Reset the modelview matrix
        gl.glLoadIdentity();
    }

    @Override
    public void onSurfacePaused() {
        Log.v(TAG, "onSurfacePaused()");
    }

    @Override
    public void onSurfaceResumed() {
        Log.v(TAG, "onSurfaceResumed()");
    }

    @Override
    public void onDrawFrame(final GL10 gl) {
        // state check
        if (mGLState == null) {
            return;
        }
        DisplayObject child;

        // pause check
        if (!mPaused) {
            // delta time
            final long now = SystemClock.elapsedRealtime();
            // final float delta = ((now - mStartTime) / 1000000f);
            final long delta = now - mStartTime;

            if (delta == 0) {
                // NOTE: delta can be 0 (when nothing draws) on some devices such as S2, S3...
                // We need to force invalidate!
                invalidate();
            } else {
                mStartTime = now;

                int sleepTime = 0;
                // compensate the framerate around the target fps
                if (mTargetFps > 0) {
                    float targetDelta = (mTargetDuration - delta);
                    if (targetDelta > 0) { // too fast?
                        if (mDownTime > targetDelta) {
                            mDownTime -= targetDelta;
                        } else {
                            sleepTime = (int) targetDelta - mDownTime;
                            mDownTime = 0;
                            if (sleepTime > mTargetDurationJitter) {
                                try {
                                    Thread.sleep(sleepTime);
                                } catch (Exception e) {
                                    // TODO: nothing
                                }
                            } else {
                                sleepTime = 0;
                            }
                        }
                    } else if (targetDelta < 0) { // too slow?
                        mDownTime -= targetDelta;
                    }
                }

                // calculate frame rate
                mFrameCountDuration += delta + sleepTime;
                if (mFrameCountDuration <= 1000) {
                    mFrameCount++;
                } else {
                    mCurrentFps = mFrameCount;
                    mFrameCount = 0;
                    mFrameCountDuration = 0;
                }

                // camera update
                if (mCamera != null) {
                    // update the camera
                    mCamera.update((int) delta);
                }

                // update children
                for (int i = 0; i < mNumChildren; i++) {
                    child = mChildren.get(i);
                    if (child.isAlive()) {
                        // heart beat
                        child.update((int) delta);
                    }
                }
            }

            // update texture manager
            mTextureManager.update((int) delta);
        }

        // draw children if needed
        if (mInvalidated > 0 || mRenderContinueously) {
            // camera
            if (mCamera != null) {
                // validate the camera
                mCamera.apply(mGLState);
            }

            if (mAutoClear) {
                // Clears the screen and depth buffer.
                gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            }

            if (mUIEnabled) {
                // lock the array
                synchronized (mUILock) {
                    if (mVisibleTouchables == null) {
                        mVisibleTouchables = new ArrayList<Touchable>();
                    } else {
                        mVisibleTouchables.clear();
                    }

                    for (int i = 0; i < mNumChildren; i++) {
                        child = mChildren.get(i);
                        if (child.shouldDraw(getCameraRect())) {
                            // draw frame, check alpha for optimization
                            child.draw(mGLState);

                            // stack the visible child
                            if (child instanceof Touchable && ((Touchable) child).isTouchable()) {
                                float childZ = child.getZ();
                                int j = mVisibleTouchables.size();
                                while (j > 0 && ((DisplayObject) mVisibleTouchables.get(j - 1)).getZ() > childZ) {
                                    j--;
                                }
                                mVisibleTouchables.add(j, (Touchable) child);
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < mNumChildren; i++) {
                    child = mChildren.get(i);
                    if (child.shouldDraw(getCameraRect())) {
                        // draw frame
                        child.draw(mGLState);
                    }
                }
            }

            // camera
            if (mCamera != null) {
                // validate the camera
                mCamera.unapply(mGLState);
            }

            // validate state
            // mGLState.validate();

            // validate scene
            mInvalidated--;
        }
    }

    public void pause() {
        if (mPaused) {
            return;
        }

        mPaused = true;
    }

    public void resume() {
        if (!mPaused) {
            return;
        }

        mPaused = false;
        mStartTime = SystemClock.elapsedRealtime();
    }

    public boolean isPaused() {
        return mPaused;
    }

    public void stop() {
        // TODO
    }

    final public void invalidate() {
        if (mInvalidated < mBuffersToInvalidate) {
            mInvalidated = mBuffersToInvalidate;
        }
    }

    final public void invalidate(final int flags) {
        if (mInvalidated < mBuffersToInvalidate) {
            mInvalidated = mBuffersToInvalidate;
        }
    }

    final public void invalidateBuffers(final int numBuffers) {
        mInvalidated = numBuffers;
    }

    public boolean isRenderContinueously() {
        return mRenderContinueously;
    }

    public void setRenderContinueously(final boolean renderContinueously) {
        mRenderContinueously = renderContinueously;
    }

    /**
     * @return the color
     */
    final public GLColor getColor() {
        return mColor;
    }

    /**
     * This needs to be called on GL Thread
     * 
     * @param color the color to set.
     */
    public void setColor(final GLColor color) {
        mColor.setValues(color);

        // apply
        if (mGLState != null) {
            mGLState.mGL.glClearColor(mColor.r, mColor.g, mColor.b, mColor.a);

            invalidate();
        }
    }

    /**
     * @return the current default Blending function
     */
    public BlendFunc getDefaultBlendFunc() {
        return mDefaultBlendFunc;
    }

    /**
     * Set the default Blending function for all child objects
     * 
     * @param defaultBlendFunc
     */
    public void setDefaultBlendFunc(final BlendFunc defaultBlendFunc) {
        mDefaultBlendFunc.set(defaultBlendFunc);

        // null check
        if (mGLState != null) {
            // apply to gl state
            mGLState.setDefaultBlendFunc(defaultBlendFunc);
        }
    }

    protected void clear() {
        // Clears the screen and depth buffer.
        mGLState.mGL.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * @return the autoClear
     */
    final public boolean isAutoClear() {
        return mAutoClear;
    }

    /**
     * @param autoClear the autoClear to set
     */
    public void setAutoClear(final boolean autoClear) {
        mAutoClear = autoClear;
        invalidate();
    }

    public boolean addChild(final DisplayObject child) {
        if (mChildren.indexOf(child) < 0) {
            // check id
            final String childId = child.getId();
            if (mChildrenIds.containsKey(childId)) {
                throw new Pure2DException("There is already a child with ID: " + childId);
            }

            mChildrenIds.put(childId, child);
            mChildren.add(child);
            mNumChildren++;

            child.onAdded(this);
            invalidate();

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

            mChildrenIds.put(childId, child);
            mChildren.add(index, child);
            mNumChildren++;

            // child callback
            child.onAdded(this);
            invalidate();

            return true;
        }
        return false;
    }

    public boolean removeChild(final DisplayObject child) {
        if (mChildren.remove(child)) {
            mChildrenIds.remove(child.getId());
            mNumChildren--;

            child.onRemoved();
            invalidate();

            return true;
        }

        return false;
    }

    public void removeAllChildren() {
        // update children
        for (int i = 0; i < mNumChildren; i++) {
            DisplayObject child = mChildren.get(i);
            child.onRemoved();
        }

        mChildrenIds.clear();
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
            if (child instanceof Parentable) {
                n += ((Parentable) child).getNumGrandChildren();
            }
        }

        return n;
    }

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
     * @return the camera
     */
    final public Camera getCamera() {
        return mCamera;
    }

    /**
     * @param camera the camera to set
     */
    public void setCamera(final Camera camera) {
        mCamera = camera;
        // if (mGLState != null) {
        // // set the camera
        // mGLState.setCamera(mCamera);
        // }

        // if there is NO camera, use default view
        if (mCamera == null) {
            // Select the projection matrix
            mGLState.mGL.glMatrixMode(GL10.GL_PROJECTION);
            // Reset the projection matrix
            mGLState.mGL.glLoadIdentity();
            // default axis system and projection
            mGLState.setProjection(mAxisSystem, 0, mSize.x - 1, 0, mSize.y - 1);

            // back to model
            mGLState.mGL.glMatrixMode(GL10.GL_MODELVIEW);

            // invalidate to redraw children
            invalidate();
        } else {
            // assign the scene
            mCamera.setScene(this);
        }
    }

    final public RectF getCameraRect() {
        return mCamera != null && mCamera.mClipping ? mCamera.mBounds : (mClippingEnabled ? mBounds : null);
    }

    public boolean isClippingEnabled() {
        return mClippingEnabled;
    }

    /**
     * @param clippingEnabled
     */
    public void setClippingEnabled(final boolean clippingEnabled) {
        mClippingEnabled = clippingEnabled;

        invalidate();
    }

    /**
     * Converts a local point to a global point, without allocating new PointF
     * 
     * @param local
     * @param result
     */
    final public void localToGlobal(final PointF local, final PointF result) {
        result.x = (local == null ? 0 : local.x);
        result.y = (local == null ? 0 : local.y);
    }

    /**
     * Scene is the top level of the hierachy.
     * 
     * @return the copied point of the input
     */
    final public PointF globalToLocal(final PointF global) {
        return new PointF(global.x, global.y);
    }

    /**
     * Converts a global point to a local point, without allocating new PointF
     * 
     * @param global
     * @param result
     */
    final public void globalToLocal(final PointF global, final PointF result) {
        result.x = global.x;
        result.y = global.y;
    }

    /**
     * Get the Screen's coordinates from a global point relative to this scene
     * 
     * @param globalX
     * @param globalY
     */
    final public void globalToScreen(final float globalX, final float globalY, final PointF result) {
        final Rect stageRect = mStage.getRect();
        final PointF stageScale = mStage.getFixedScale();
        // check the camera
        if (mCamera != null) {
            mCamera.globalToLocal(globalX, globalY, result);

            final RectF cameraRect = mCamera.mZoomRect;
            result.x /= (cameraRect.width() / mSize.x) * stageScale.x;
            result.y /= (cameraRect.height() / mSize.y) * stageScale.y;
        } else {
            result.x = globalX / stageScale.x;
            result.y = globalY / stageScale.y;
        }

        result.x += stageRect.left;

        if (mAxisSystem == Scene.AXIS_TOP_LEFT) {
            result.y += stageRect.top;
        } else {
            // inverse y
            result.y = stageRect.bottom - result.y;
        }
    }

    /**
     * Get the global coordinates from the Screen's coordinates
     * 
     * @param screenX
     * @param screenY
     */
    final public void screenToGlobal(final float screenX, final float screenY, final PointF result) {
        final Rect stageRect = mStage.getRect();
        final PointF stageScale = mStage.getFixedScale();
        float localX = screenX - stageRect.left;
        float localY;

        if (mAxisSystem == Scene.AXIS_TOP_LEFT) {
            localY = screenY - stageRect.top;
        } else {
            // inverse y
            localY = stageRect.bottom - screenY;
        }

        // check the camera
        if (mCamera != null) {
            final RectF cameraRect = mCamera.mZoomRect;
            localX *= (cameraRect.width() / mSize.x) * stageScale.x;
            localY *= (cameraRect.height() / mSize.y) * stageScale.y;
            mCamera.localToGlobal(localX, localY, result);
        } else {
            result.x = localX * stageScale.x;
            result.y = localY * stageScale.y;
        }
    }

    /**
     * @param screen
     * @return
     * @see BaseScene#screenToGlobal(float, float)
     */
    final public void screenToGlobal(final PointF screen, final PointF result) {
        screenToGlobal(screen.x, screen.y, result);
    }

    /**
     * Convert a Global Point to a Pixel Point on the Stage
     * 
     * @param globalX
     * @param globalY
     * @param result
     */
    final public void globalToStage(final float globalX, final float globalY, final PointF result) {
        // check the camera
        if (mCamera != null) {
            mCamera.globalToLocal(globalX, globalY, result);

            final RectF cameraRect = mCamera.mZoomRect;
            result.x /= cameraRect.width() / mSize.x;
            result.y /= cameraRect.height() / mSize.y;
        } else {
            result.x = globalX;
            result.y = globalY;
        }

        if (mAxisSystem == Scene.AXIS_TOP_LEFT) {
            result.y = mSize.y - result.y;
        }
    }

    /**
     * Convert a Global Rectangle to a Pixel Rectangle on the Stage
     * 
     * @param globalRect
     * @param result
     */
    final public void globalToStage(final RectF globalRect, final RectF result) {
        // check the camera
        if (mCamera != null) {
            mCamera.globalToLocal(globalRect, result);

            final RectF cameraRect = mCamera.mZoomRect;
            final float scaleX = cameraRect.width() / mSize.x;
            final float scaleY = cameraRect.height() / mSize.y;
            result.left /= scaleX;
            result.top /= scaleY;
            result.right /= scaleX;
            result.bottom /= scaleY;
        } else {
            result.left = globalRect.left;
            result.top = globalRect.top;
            result.right = globalRect.right;
            result.bottom = globalRect.bottom;
        }

        if (mAxisSystem == Scene.AXIS_TOP_LEFT) {
            final float height = result.bottom - result.top + 1;
            result.top = convertY(result.top, height);
            result.bottom = result.top + height - 1;
        }
    }

    /**
     * Convert y based on the Axis system
     * 
     * @param y
     * @param size
     * @return
     */
    protected float convertY(final float y, final float size) {
        return mAxisSystem == Scene.AXIS_TOP_LEFT ? mSize.y - y - size : y;
    }

    /**
     * Scene got no matrix
     */
    final public Matrix getMatrix() {
        return null;
    }

    final public int getBuffersToInvalidate() {
        return mBuffersToInvalidate;
    }

    public void setBuffersToInvalidate(final int buffersToInvalidate) {
        mBuffersToInvalidate = buffersToInvalidate;
        invalidate();
    }

    /**
     * Dispose everything
     */
    public void dispose() {
        if (mChildren != null) {
            removeAllChildren();
            // mChildren = null;

            if (mTextureManager != null) {
                mTextureManager.removeAllTextures();
                // mTextureManager = null;
            }

            // mColor = null;
            // mSize = null;
            // mGLState = null;
        }
    }

    public final boolean isUIEnabled() {
        return mUIEnabled;
    }

    /**
     * Enables the touch interface for UI objects
     */
    public void setUIEnabled(final boolean enabled) {
        mUIEnabled = enabled;
    }

    /**
     * Note: only use this within onTouchEvent()
     * 
     * @hide
     */
    public PointF getTouchedPoint() {
        return mTouchedPoints.get(0);
    }

    /**
     * Note: only use this within onTouchEvent()
     * 
     * @hide
     */
    public PointF getTouchedPoint(final int pointerIndex) {
        return mTouchedPoints.get(pointerIndex);
    }

    /**
     * Note: only use this within onTouchEvent()
     * 
     * @hide
     */
    public int getPointerCount() {
        return mPointerCount;
    }

    /**
     * Note: This is called from UI-Thread
     */
    public boolean onTouchEvent(final MotionEvent event) {
        if (mUIEnabled) {
            // NOTE: event is NOT safe to queue because it's recycled by Android. So we do this approach...
            // lock the array
            synchronized (mUILock) {
                mTouchedPoints.clear();
                mPointerCount = event.getPointerCount();
                for (int i = 0; i < mPointerCount; i++) {
                    // FIXME optimize this with pool
                    final PointF p = new PointF();
                    screenToGlobal(event.getX(i), event.getY(i), p);
                    mTouchedPoints.add(p);
                }

                if (mVisibleTouchables != null) {
                    // start from front to back
                    for (int i = mVisibleTouchables.size() - 1; i >= 0; i--) {
                        if (mVisibleTouchables.get(i).onTouchEvent(event)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public Scene getScene() {
        return this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
    }

    /**
     * for Debugging.
     * 
     * @return a string that has all the children in Tree format
     */
    public String getObjectTree() {
        final StringBuilder sb = new StringBuilder();
        sb.append(toString());
        sb.append("\n");

        for (int i = 0; i < mNumChildren; i++) {
            DisplayObject child = mChildren.get(i);
            sb.append(child.getObjectTree("   "));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * For Debugging. Group object with the same type and count
     * 
     * @return
     */
    public String countObjectsByType() {
        final Map<String, Integer> map = new HashMap<String, Integer>();
        countObjectsByType(this, map);

        // Sort keys by values.
        List<String> sortedKeys = new ArrayList<String>(map.keySet());
        Collections.sort(sortedKeys, new Comparator<String>() {
            public int compare(final String left, final String right) {
                return map.get(right) - map.get(left);
            }
        });

        final StringBuilder sb = new StringBuilder();
        int total = 0;
        for (String key : sortedKeys) {
            final int count = map.get(key);
            sb.append("   ");
            sb.append(key);
            sb.append(": ");
            sb.append(count);
            sb.append("\n");

            total += count;
        }

        // total count
        sb.insert(0, toString() + ": " + total + "\n");

        return sb.toString();
    }

    protected void countObjectsByType(final Container container, final Map<String, Integer> map) {
        final int num = container.getNumChildren();
        for (int i = 0; i < num; i++) {
            final DisplayObject child = container.getChildAt(i);
            final String name = child.getClass().getSimpleName();
            if (map.containsKey(name)) {
                map.put(name, map.get(name) + 1);
            } else {
                map.put(name, 1);
            }

            if (child instanceof Container) {
                countObjectsByType((Container) child, map);
            }
        }
    }
}

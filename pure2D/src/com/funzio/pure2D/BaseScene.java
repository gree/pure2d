/**
 * 
 */
package com.funzio.pure2D;

import java.util.ArrayList;
import java.util.List;

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
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.BlendFunc;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;

/**
 * @author long
 */
public class BaseScene implements Scene {
    public static final String TAG = BaseScene.class.getSimpleName();

    protected int mBuffersToInvalidate = 4; // number of buffer to invalidate. 2 for old devices but 3+ for Jelly Bean or later
    protected Stage mStage;
    protected GLState mGLState;
    protected Camera mCamera;
    protected TextureManager mTextureManager;
    protected List<DisplayObject> mChildren = new ArrayList<DisplayObject>();
    private int mNumChildren;

    private PointF mSize = new PointF();

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
    private Listener mListener;

    // axis system
    private int mAxisSystem = AXIS_BOTTOM_LEFT;

    // UI
    private boolean mUIEnabled = false;
    private List<Touchable> mVisibleTouchables;
    private MotionEvent mMotionEvent = null;
    private PointF mTouchedPoint;

    // GL extensions
    protected boolean mNpotTextureSupported = false;

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
            return true;
        }

        return false;
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

            return true;
        }

        return false;
    }

    /**
     * @param stage the stage to set
     */
    public void setStage(final Stage stage) {
        mStage = stage;

        // size
        final Rect stageRect = mStage.getRect();
        mSize.x = stageRect.width();
        mSize.y = stageRect.height();
    }

    public void setAxisSystem(final int value) {
        mAxisSystem = value;

        // reset the camera
        if (mGLState != null) {
            mGLState.setAxisSystem(mAxisSystem);
            setCamera(mCamera);
        }
    }

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
    final public int getTargetFps() {
        return mTargetFps;
    }

    /**
     * @param fps the fps to set
     */
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
        return new TextureManager(this, mStage.getResources());
    }

    public void setTextureManager(final TextureManager textureManager) {
        mTextureManager = textureManager;
    }

    final public TextureManager getTextureManager() {
        return mTextureManager;
    }

    /*
     * (non-Javadoc)
     * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
     */
    @Override
    public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
        // this might help but I have not seen any difference yet!
        // Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        mStartTime = SystemClock.elapsedRealtime(); // System.nanoTime();
        mDownTime = 0;
        mFrameCount = 0;
        mCurrentFps = 0;
        mFrameCountDuration = 0;

        // find the extensions
        if (Pure2D.GL_EXTENSIONS == null) {
            Pure2D.GL_EXTENSIONS = gl.glGetString(GL10.GL_EXTENSIONS);
            Pure2D.GL_NPOT_TEXTURE_SUPPORTED = mNpotTextureSupported = Pure2D.GL_EXTENSIONS.contains("GL_OES_texture_npot") || Pure2D.GL_EXTENSIONS.contains("GL_ARB_texture_non_power_of_two");
            Pure2D.GL_STENCIL8_SUPPORTED = Pure2D.GL_EXTENSIONS.contains("GL_OES_stencil8");
        }

        Log.v(TAG, "onSurfaceCreated() | NPOT: " + Pure2D.GL_NPOT_TEXTURE_SUPPORTED);

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
        gl.glBlendFunc(BlendFunc.DEFAULT_SRC, BlendFunc.DEFAULT_DST);
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
        if (mTextureManager != null) {
            mTextureManager.unloadAllTextures();
        }

        // init GL state
        mGLState = new GLState(gl, mStage);
        mGLState.setCamera(mCamera);

        // init Texture manager with the new GL
        if (mTextureManager == null) {
            mTextureManager = createDefaultTextureManager();
        } else {
            mTextureManager.reload(mGLState, mStage.getResources());
        }

        // callback
        if (mListener != null) {
            mListener.onSurfaceCreated(gl);
        }
    }

    /*
     * (non-Javadoc)
     * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
     */
    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        mSize.x = width;
        mSize.y = height;

        // Sets the current view port to the new size.
        // gl.glViewport(0, 0, width, height);
        mGLState.setViewport(0, 0, width, height);

        // reapply the camera
        setCamera(mCamera);

        // Select the modelview matrix
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        // Reset the modelview matrix
        gl.glLoadIdentity();
    }

    /*
     * (non-Javadoc)
     * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
     */
    @Override
    public void onDrawFrame(final GL10 gl) {
        // pause check
        if (mPaused) {
            return;
        }

        // delta time
        final long now = SystemClock.elapsedRealtime(); // System.nanoTime();
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
                DisplayObject child = mChildren.get(i);
                if (child.isAlive()) {
                    // heart beat
                    child.update((int) delta);
                }
            }
        }

        // draw children if needed
        if (mInvalidated > 0 || mRenderContinueously) {
            // camera
            if (mCamera != null && mCamera.isInvalidated()) {
                // validate the camera
                mCamera.validate(mGLState);
            }

            if (mAutoClear) {
                // Clears the screen and depth buffer.
                gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            }

            if (mUIEnabled) {
                if (mVisibleTouchables == null) {
                    mVisibleTouchables = new ArrayList<Touchable>();
                } else {
                    mVisibleTouchables.clear();
                }
            }

            for (int i = 0; i < mNumChildren; i++) {
                DisplayObject child = mChildren.get(i);
                final boolean visible = child.isVisible() && ((mCamera == null) || mCamera.isViewable(child));
                if (visible) {
                    // draw frame
                    child.draw(mGLState);

                    // stack the visible child
                    if (mUIEnabled && child instanceof Touchable && ((Touchable) child).isTouchable()) {
                        float childZ = child.getZ();
                        int j = mVisibleTouchables.size();
                        while (j > 0 && ((DisplayObject) mVisibleTouchables.get(j - 1)).getZ() > childZ) {
                            j--;
                        }
                        mVisibleTouchables.add(j, (Touchable) child);
                    }
                }
            }

            // validated
            mInvalidated--;
            // mStage.requestRender();
        }

        if (mUIEnabled && mMotionEvent != null) {
            // start from front to back
            for (int i = mVisibleTouchables.size() - 1; i >= 0; i--) {
                if (mVisibleTouchables.get(i).onTouchEvent(mMotionEvent)) {
                    break;
                }
            }

            // clear
            mMotionEvent = null;
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
        mStartTime = SystemClock.elapsedRealtime(); // System.nanoTime();
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
     * @param color the color to set
     */
    public void setColor(final GLColor color) {
        mColor = color;
        invalidate();
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
            mChildren.add(child);
            mNumChildren++;
            child.onAdded(this);
            invalidate();

            return true;
        }
        return false;
    }

    public boolean removeChild(final DisplayObject child) {
        if (mChildren.remove(child)) {
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

        mChildren.clear();
        mNumChildren = 0;
        invalidate();
    }

    public DisplayObject getChildAt(final int index) {
        return index < mNumChildren ? mChildren.get(index) : null;
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
        if (mGLState != null) {
            // set the camera
            mGLState.setCamera(mCamera);
        }

        // if there is NO camera, use default view
        if (mCamera == null) {
            // Select the projection matrix
            mGLState.mGL.glMatrixMode(GL10.GL_PROJECTION);
            // Reset the projection matrix
            mGLState.mGL.glLoadIdentity();

            // default view and axis system
            if (mAxisSystem == AXIS_TOP_LEFT) {
                // invert the y-axis
                mGLState.mGL.glOrthof(0, mSize.x, mSize.y, 0, -1, 1);
            } else {
                mGLState.mGL.glOrthof(0, mSize.x, 0, mSize.y, -1, 1);
            }

            // testing perspective
            // GLU.gluPerspective(mGLState.mGL, 60, mSize.x / mSize.y, 0.1f, 1000f);
            // GLU.gluLookAt(mGLState.mGL, mSize.x / 2, mSize.y / 2, 1000f, mSize.x / 2, mSize.y / 2, 0, 0, 1, 0);

            // back to model
            mGLState.mGL.glMatrixMode(GL10.GL_MODELVIEW);

            // invalidate to redraw children
            invalidate();
        } else {
            // assign the scene
            mCamera.setScene(this);
        }
    }

    /**
     * Scene is the top level of the hierachy.
     * 
     * @return the copied point of the input
     */
    final public PointF localToGlobal(final PointF local) {
        return new PointF(local.x, local.y);
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
     * @return
     */
    final public PointF globalToScreen(final float globalX, final float globalY) {
        final Rect stageRect = mStage.getRect();
        final PointF screen;
        // check the camera
        if (mCamera != null) {
            screen = mCamera.globalToLocal(globalX, globalY);

            final RectF cameraRect = mCamera.getRect();
            screen.x /= cameraRect.width() / stageRect.width();
            screen.y /= cameraRect.height() / stageRect.height();
        } else {
            screen = new PointF(globalX, globalY);
        }

        screen.x += stageRect.left;

        if (mAxisSystem == Scene.AXIS_TOP_LEFT) {
            screen.y += stageRect.top;
        } else {
            // inverse y
            screen.y = stageRect.bottom - screen.y;
        }

        return screen;
    }

    /**
     * @param global
     * @return
     * @see #globalToScreen(float, float)
     */
    final public PointF globalToScreen(final PointF global) {
        return globalToScreen(global.x, global.y);
    }

    /**
     * Get the global coordinates from the Screen's coordinates
     * 
     * @param screenX
     * @param screenY
     * @return
     */
    final public PointF screenToGlobal(final float screenX, final float screenY) {
        final Rect stageRect = mStage.getRect();
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
            final RectF cameraRect = mCamera.getRect();
            localX *= cameraRect.width() / stageRect.width();
            localY *= cameraRect.height() / stageRect.height();
            return mCamera.localToGlobal(localX, localY);
        } else {
            return new PointF(localX, localY);
        }
    }

    /**
     * @param screen
     * @return
     * @see BaseScene#screenToGlobal(float, float)
     */
    final public PointF screenToGlobal(final PointF screen) {
        return screenToGlobal(screen.x, screen.y);
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
        removeAllChildren();
        mChildren = null;

        mTextureManager.removeAllTextures();
        mTextureManager = null;

        mColor = null;
        mSize = null;
        mGLState = null;
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

    public final boolean isNpotTextureSupported() {
        return mNpotTextureSupported;
    }

    public PointF getTouchedPoint() {
        return mTouchedPoint;
    }

    public boolean onTouchEvent(final MotionEvent event) {
        if (mUIEnabled) {
            // queue the touch event
            queueEvent(new Runnable() {

                @Override
                public void run() {
                    mTouchedPoint = screenToGlobal(event.getX(), event.getY());
                    mMotionEvent = event;
                }
            });
        }

        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
    }

    /**
     * for Debugging
     * 
     * @return a string that has all the children in Tree format
     */
    public String getTrace() {
        final StringBuilder sb = new StringBuilder();
        sb.append(toString());
        sb.append("\n");

        for (int i = 0; i < mNumChildren; i++) {
            DisplayObject child = mChildren.get(i);
            sb.append(child.getTrace("   "));
            sb.append("\n");
        }

        return sb.toString();
    }
}

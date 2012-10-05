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

import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.gl.GLColor;
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
    protected TextureManager mTextureManager;
    protected List<DisplayObject> mChildren = new ArrayList<DisplayObject>();
    private int mNumChildren;
    private Camera mCamera;

    private PointF mSize = new PointF();

    private long mStartTime = 0;
    private boolean mPaused = false;
    private boolean mAutoClear = true;
    private int mInvalidated = 0;

    // frame rate
    private int mFrameCount = 0;
    private float mFrameCountDuration = 0;
    private int mCurrentFps = 0;
    private int mTargetFps = 0; // 0 = unlimited
    private int mTargetDuration = 1000 / (mTargetFps > 0 ? mTargetFps : DEFAULT_FPS);
    private int mTargetDurationJitter = (int) (mTargetDuration * 0.15f);
    private int mDownTime = 0;

    // extra
    private GLColor mColor = new GLColor(0f, 0f, 0f, 1f);
    private Listener mListener;

    // axis system
    private int mAxisSystem = AXIS_BOTTOM_LEFT;

    public BaseScene() {
    }

    /**
     * @return the stage
     */
    public Stage getStage() {
        return mStage;
    }

    public void queueEvent(final Runnable r) {
        if (mStage != null) {
            mStage.queueEvent(r);
        }
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

    public int getAxisSystem() {
        return mAxisSystem;
    }

    public int getCurrentFps() {
        return mCurrentFps;
    }

    /**
     * @return the target fps
     */
    public int getTargetFps() {
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

    public GLState getGLState() {
        return mGLState;
    }

    protected TextureManager createDefaultTextureManager() {
        return new TextureManager(this, mStage.getResources());
    }

    public void setTextureManager(final TextureManager textureManager) {
        mTextureManager = textureManager;
    }

    public TextureManager getTextureManager() {
        return mTextureManager;
    }

    /*
     * (non-Javadoc)
     * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
     */
    @Override
    public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
        mStartTime = System.nanoTime();
        mDownTime = 0;
        mFrameCount = 0;
        mCurrentFps = 0;
        mFrameCountDuration = 0;

        // Set the background color to black ( rgba ).
        gl.glClearColor(mColor.r, mColor.g, mColor.b, mColor.a);
        // Enable Smooth Shading, default not really needed.
        gl.glShadeModel(GL10.GL_SMOOTH);

        // Depth buffer setup.
        gl.glClearDepthf(1.0f);
        // Enables depth testing.
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL); // The type of depth testing to do.

        // or disable depth test completely
        // this does NOT necessary increase any FPS based on my test
        // gl.glDisable(GL10.GL_DEPTH_TEST);
        // gl.glDepthFunc(GL10.GL_NEVER);
        // gl.glDepthMask(false);

        // Really nice perspective calculations.
        // gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        // Enable alpha blending
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        // Enable Texture
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
        mGLState = new GLState(gl);
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
        final long now = System.nanoTime();
        final float delta = ((now - mStartTime) / 1000000f);
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

        // update children
        for (int i = 0; i < mNumChildren; i++) {
            DisplayObject child = mChildren.get(i);
            if (child.isAlive()) {
                // heart beat
                child.update((int) delta);
            }
        }

        // draw children if needed
        // NOTE: some devices such as S2, S3... cause delta = 0 when nothing draws. We need to force invalidate!
        if (mInvalidated > 0 || (int) delta == 0) {
            // camera
            if (mCamera != null && mCamera.isInvalidated()) {
                // validate the camera
                mCamera.validate(mGLState);
            }

            if (mAutoClear) {
                // Clears the screen and depth buffer.
                gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            }

            for (int i = 0; i < mNumChildren; i++) {
                DisplayObject child = mChildren.get(i);
                final boolean visible = child.isVisible() && ((mCamera == null) || mCamera.isViewable(child));
                if (visible) {
                    // draw frame
                    child.draw(mGLState);
                }
            }

            // validated
            mInvalidated--;
            // mStage.requestRender();
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
        mStartTime = System.nanoTime();
    }

    public void stop() {
        // TODO
    }

    public void invalidate() {
        if (mInvalidated < mBuffersToInvalidate) {
            mInvalidated = mBuffersToInvalidate;
        }
    }

    public void invalidate(final int flags) {
        if (mInvalidated < mBuffersToInvalidate) {
            mInvalidated = mBuffersToInvalidate;
        }
    }

    public void invalidateBuffers(final int numBuffers) {
        mInvalidated = numBuffers;
    }

    /**
     * @return the color
     */
    public GLColor getColor() {
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
    public boolean isAutoClear() {
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

    public PointF getSize() {
        return mSize;
    }

    public float getWidth() {
        return mSize.x;
    }

    public float getHeight() {
        return mSize.y;
    }

    /**
     * @return the camera
     */
    public Camera getCamera() {
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
    public PointF localToGlobal(final PointF pt) {
        return new PointF(pt.x, pt.y);
    }

    /**
     * Scene is the top level of the hierachy.
     * 
     * @return the copied point of the input
     */
    public PointF globalToLocal(final PointF pt) {
        return new PointF(pt.x, pt.y);
    }

    /**
     * Get the Screen's coordinates from a global point relative to this scene
     * 
     * @param globalX
     * @param globalY
     * @return
     */
    public PointF globalToScreen(final float globalX, final float globalY) {
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
    public PointF globalToScreen(final PointF global) {
        return globalToScreen(global.x, global.y);
    }

    /**
     * Get the global coordinates from the Screen's coordinates
     * 
     * @param screenX
     * @param screenY
     * @return
     */
    public PointF screenToGlobal(final float screenX, final float screenY) {
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
    public PointF screenToGlobal(final PointF screen) {
        return screenToGlobal(screen.x, screen.y);
    }

    /**
     * Scene got no matrix
     */
    public Matrix getMatrix() {
        return null;
    }

    public int getBuffersToInvalidate() {
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
}

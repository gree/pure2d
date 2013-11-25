/**
 * 
 */
package com.funzio.pure2D;

import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;

import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;

/**
 * @author long
 */
public interface Scene extends Renderer, Container {

    // projection methods
    public static final int AXIS_BOTTOM_LEFT = 0; // orthor projection from bottom-left
    public static final int AXIS_TOP_LEFT = 1; // othor prjection from top-left
    public static final int PROJECTION_PERSPECTIVE = 2; // perspective projection

    public static final int DEFAULT_FPS = 60; // frame per second
    public static final int DEFAULT_MSPF = 1000 / DEFAULT_FPS; // ms per frame

    public void setStage(Stage stage);

    public Stage getStage();

    public void setAxisSystem(int value);

    public int getAxisSystem();

    public Camera getCamera();

    public void setCamera(final Camera camera);

    public RectF getViewRect();

    public void setDepthRange(final float zNear, final float zFar);

    public GLState getGLState();

    public TextureManager getTextureManager();

    public void globalToScreen(final float globalX, final float globalY, PointF result);

    public void globalToStage(final float globalX, float globalY, final PointF result);

    public void globalToStage(final RectF globalRect, final RectF result);

    public void screenToGlobal(final float screenX, final float screenY, PointF result);

    public void screenToGlobal(final PointF screen, PointF result);

    public boolean queueEvent(Runnable r);

    public boolean queueEvent(final Runnable r, final int delayMillis);

    public void pause();

    public void resume();

    public void dispose();

    public boolean isUIEnabled();

    public void setUIEnabled(final boolean enabled);

    public PointF getTouchedPoint();

    public PointF getTouchedPoint(final int pointerIndex);

    public int getPointerCount();

    public boolean onTouchEvent(final MotionEvent event);

    public void onSurfacePaused();

    public void onSurfaceResumed();

    public interface Listener {
        void onSurfaceCreated(final GLState glState, final boolean firstTime);
    }
}

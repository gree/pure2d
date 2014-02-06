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

    public RectF getCameraRect();

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

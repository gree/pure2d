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

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * @author long
 */
public class BaseStage extends GLSurfaceView implements Stage {
    public final static String TAG = BaseStage.class.getSimpleName();

    private Scene mScene;
    private Rect mRect;
    private Point mFixedSize;
    private PointF mFixedScale = new PointF(1, 1);

    public BaseStage(final Context context) {
        super(context);
    }

    public BaseStage(final Context context, final AttributeSet attributes) {
        super(context, attributes);
    }

    public void setScene(final Scene scene) {
        Log.v(TAG, "setScene(): " + scene);

        mScene = scene;
        mScene.setStage(this);

        setEGLContextClientVersion(2);
        final Pure2DEGL egl = new Pure2DEGL();
        setEGLContextFactory(egl);
        setEGLConfigChooser(egl);
        // setEGLContextClientVersion(2);

        // set the renderer which is the scene
        setRenderer(scene);
    }

    public Scene getScene() {
        return mScene;
    }

    public Rect getRect() {
        if (mRect == null) {
            mRect = new Rect();
        }
        if (mRect.width() == 0) {
            int[] viewOffset = new int[2];
            getLocationOnScreen(viewOffset);

            getGlobalVisibleRect(mRect);

            mRect.offset(-viewOffset[0], -viewOffset[1]);

            // find the stage scale
            if (mFixedSize != null) {
                mFixedScale.set((float) mFixedSize.x / (float) (mRect.width() + 1), (float) mFixedSize.y / (float) (mRect.height() + 1));
            }
        }

        return mRect;
    }

    /**
     * Use this to take advantage of the Hardware Scaler for scaling up scene in conjunction with Camera's zoom, without any additional cost. This can be called any time but must be on UI Thread.
     * 
     * @param width The surface's width. This can be < the resolution width
     * @param height The surface's height. This can be < the resolution height
     * @see SurfaceHolder#setFixedSize(int, int)
     * @see http://android-developers.blogspot.com/2013/09/using-hardware-scaler-for-performance.html
     */
    public void setFixedSize(final int width, final int height) {
        Log.v(TAG, "setFixedSize(): " + width + ", " + height);

        getHolder().setFixedSize(width, height);

        if (mFixedSize == null) {
            mFixedSize = new Point(width, height);
        } else {
            mFixedSize.set(width, height);
        }

        // find the stage scale
        if (mRect != null) {
            mFixedScale.set((float) mFixedSize.x / (float) (mRect.width() + 1), (float) mFixedSize.y / (float) (mRect.height() + 1));
        }
    }

    public Point getFixedSize() {
        return mFixedSize;
    }

    public PointF getFixedScale() {
        return mFixedScale;
    }

    public AssetManager getAssets() {
        return getContext().getAssets();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.v(TAG, "onPause()");
        if (mScene != null) {
            mScene.onSurfacePaused();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.v(TAG, "onResume()");
        if (mScene != null) {
            mScene.onSurfaceResumed();
        }
    }

    private static class Pure2DEGL implements EGLConfigChooser, EGLContextFactory {
        private static final String LOG_TAG = Pure2DEGL.class.getSimpleName();
        private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
        private static final int EGL_OPENGL_ES2_BIT = 4;

        private static final int[] CONTEXT_ATTRIBS = {
                EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE
        };

        private static final int[] CONFIG_ATTRIBS = {
                EGL10.EGL_RED_SIZE, 8, EGL10.EGL_GREEN_SIZE, 8, EGL10.EGL_BLUE_SIZE, 8, EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, EGL10.EGL_NONE
        };

        @Override
        public EGLContext createContext(final EGL10 egl, final EGLDisplay display, final EGLConfig eglConfig) {

            Log.d(LOG_TAG, "creating EGLContext.");
            printErrors(egl);
            final EGLContext ctx = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, CONTEXT_ATTRIBS);
            Log.d(LOG_TAG, "finished creating EGLContext.");
            printErrors(egl);

            return ctx;
        }

        @Override
        public void destroyContext(final EGL10 egl, final EGLDisplay display, final EGLContext context) {
            egl.eglDestroyContext(display, context);
        }

        @Override
        public EGLConfig chooseConfig(final EGL10 egl, final EGLDisplay display) {
            final int[] value = new int[1];
            egl.eglChooseConfig(display, CONFIG_ATTRIBS, null, 0, value);

            final int numConfigs = value[0];
            Log.d(LOG_TAG, String.format("choosing configs; found: %d", numConfigs));

            final EGLConfig[] configs = new EGLConfig[numConfigs];
            egl.eglChooseConfig(display, CONFIG_ATTRIBS, configs, numConfigs, value);

            for (int i = 0; i < numConfigs; i++) {
                final EGLConfig config = configs[i];
                final int depth = egl.eglGetConfigAttrib(display, config, EGL10.EGL_DEPTH_SIZE, value) ? value[0] : 0;
                final int stencil = egl.eglGetConfigAttrib(display, config, EGL10.EGL_STENCIL_SIZE, value) ? value[0] : 0;

                if (depth < 8 || stencil < 8) {
                    continue;
                }

                return config;
            }

            return null;
        }

        private static void printErrors(final EGL10 egl) {
            int error;
            while ((error = egl.eglGetError()) != EGL10.EGL_SUCCESS) {
                Log.e(LOG_TAG, String.format("EGL error: 0x%x", error));
            }
        }
    }
}

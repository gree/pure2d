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
}

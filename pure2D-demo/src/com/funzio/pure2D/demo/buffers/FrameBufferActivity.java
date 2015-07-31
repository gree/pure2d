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
package com.funzio.pure2D.demo.buffers;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.FrameBuffer;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Sprite;

public class FrameBufferActivity extends StageActivity {
    private static final int FB_WIDTH = 200;
    private static final int FB_HEIGHT = 200;

    private List<Texture> mTextures = new ArrayList<Texture>();
    private Texture mBufferTexture;
    private FrameBuffer mFrameBuffer;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    loadTextures();
                    createBufferTexture();
                    addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                } else {
                    // TODO regenerate textures...
                }
            }
        });
    }

    private void loadTextures() {
        final int[] ids = {
                R.drawable.cc_32, // cc
                R.drawable.mw_32, // mw
                R.drawable.ka_32, // ka
        };

        for (int id : ids) {
            // add texture to list
            mTextures.add(mScene.getTextureManager().createDrawableTexture(id, null));
        }
    }

    private void createBufferTexture() {
        if (!FrameBuffer.isSupported()) {
            Log.e(Pure2D.TAG, "FrameBuffer is not supported!");
            return;
        }

        // create a frame buffer
        mFrameBuffer = new FrameBuffer(mScene.getGLState(), FB_WIDTH, FB_HEIGHT, true);
        mBufferTexture = mFrameBuffer.getTexture();

        mFrameBuffer.bind(Scene.AXIS_TOP_LEFT); // invert
        // start drawing to the frame buffer
        Sprite sprite = new Sprite();
        for (int i = 0; i < 100; i++) {
            sprite.setTexture(mTextures.get(i % mTextures.size()));
            // random color and position
            sprite.moveTo((i % 10) * 32, (i / 10) * 32);
            // draw onto frame buffer
            sprite.draw(mScene.getGLState());
        }
        mFrameBuffer.unbind();

        // framebuffer no longer needed
        mFrameBuffer.unload();
    }

    private void addObject(final float x, final float y) {
        // create object
        Sprite obj = new Sprite();
        obj.setTexture(mBufferTexture);

        // center origin
        obj.setOriginAtCenter();

        // random position
        obj.setPosition(x, y);

        // add to scene
        mScene.addChild(obj);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    addObject(event.getX(), mDisplaySize.y - event.getY());
                }
            });
        }

        return true;
    }
}

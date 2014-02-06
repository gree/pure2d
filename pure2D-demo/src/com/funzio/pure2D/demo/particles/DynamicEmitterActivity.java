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
package com.funzio.pure2D.demo.particles;

import java.util.ArrayList;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;

public class DynamicEmitterActivity extends StageActivity {
    public static final int NUM = 3;

    private Texture mSmokeTexture;
    private Texture mFireTexture;
    private ArrayList<DynamicEmitter> mEmitters = new ArrayList<DynamicEmitter>();
    private Point mZoomSize = new Point();
    private PointF mTempPoint = new PointF();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // mScene.setColor(COLOR_GREEN);
        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    loadTextures();

                    // create emitters
                    DynamicEmitter emitter = new DynamicEmitter(mZoomSize, mFireTexture, mSmokeTexture);
                    mScene.addChild(emitter);
                    mEmitters.add(emitter);

                    emitter = new DynamicEmitter(mZoomSize, mFireTexture, mSmokeTexture);
                    emitter.setType(2);
                    mScene.addChild(emitter);
                    mEmitters.add(emitter);

                    emitter = new DynamicEmitter(mZoomSize, mFireTexture, mSmokeTexture);
                    emitter.setType(3);
                    mScene.addChild(emitter);
                    mEmitters.add(emitter);
                }
            }
        });
    }

    @Override
    protected BaseScene createScene() {

        mZoomSize.x = (int) (mDisplaySize.x * 0.25f);
        mZoomSize.y = (int) (mDisplaySize.y * 0.25f);
        mStage.setFixedSize(mZoomSize.x, mZoomSize.y);

        mScene = super.createScene();
        // mScene.setCamera(new Camera(mZoomSize.x, mZoomSize.y)); // 30x slower without setFixedSize()

        return mScene;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.samples.activities.StageActivity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
        Particle1.clearPool();
    }

    @Override
    protected int getLayout() {
        return R.layout.stage_bg_colors;
    }

    private void loadTextures() {
        TextureOptions options = TextureOptions.getDefault();
        options.inMipmaps = 1;

        // smoke
        mSmokeTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.smoke_small, options);

        // fire
        mFireTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.fireball_small, options);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            for (int i = 0; i < NUM; i++) {
                DynamicEmitter emitter = mEmitters.get(i);
                mScene.screenToGlobal(event.getX(), event.getY(), mTempPoint);
                emitter.setDestination(mTempPoint.x, mTempPoint.y);
                emitter.lockDestination(true);
            }
        } else if (action == MotionEvent.ACTION_UP) {
            for (int i = 0; i < NUM; i++) {
                DynamicEmitter emitter = mEmitters.get(i);
                emitter.lockDestination(false);
            }
        }

        return true;
    }

    public void onClickRadio(final View view) {

        mScene.queueEvent(new Runnable() {

            @Override
            public void run() {
                switch (view.getId()) {
                    case R.id.radio_black:
                        mScene.setColor(COLOR_BLACK);
                        break;

                    case R.id.radio_gray:
                        mScene.setColor(COLOR_GRAY);
                        break;

                    case R.id.radio_white:
                        mScene.setColor(COLOR_WHITE);
                        break;

                    case R.id.radio_red:
                        mScene.setColor(COLOR_RED);
                        break;

                    case R.id.radio_green:
                        mScene.setColor(COLOR_GREEN);
                        break;

                    case R.id.radio_blue:
                        mScene.setColor(COLOR_BLUE);
                        break;
                }
            }
        });

    }
}

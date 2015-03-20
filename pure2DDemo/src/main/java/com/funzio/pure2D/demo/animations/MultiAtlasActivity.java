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
package com.funzio.pure2D.demo.animations;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.atlas.AtlasFrameSet;
import com.funzio.pure2D.atlas.JsonAtlas;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Clip;
import com.funzio.pure2D.shapes.Sprite;
import com.longo.pure2D.demo.R;

public class MultiAtlasActivity extends StageActivity {
    private static final int NUM_FILES = 3;

    private Sprite[] mAtlasSprites = new Sprite[NUM_FILES];
    private AtlasFrameSet mAllFrames = new AtlasFrameSet("");

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {

                    // load the textures
                    loadTexture();

                    // generate a lot of squares
                    addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.samples.activities.StageActivity#getLayout()
     */
    @Override
    protected int getLayout() {
        return R.layout.stage_atlas;
    }

    private void loadTexture() {
        int nextX = 0;
        float displayScale = 0.5f;
        for (int i = 0; i < NUM_FILES; i++) {
            // load texture
            final Texture texture = mScene.getTextureManager().createAssetTexture("atlas/waterway_flower_" + i + ".png", null);

            // for debugging
            mAtlasSprites[i] = new Sprite();
            mAtlasSprites[i].setTexture(texture);
            mAtlasSprites[i].setX(nextX);
            mAtlasSprites[i].setScale(displayScale);
            mScene.addChild(mAtlasSprites[i]);
            nextX += texture.getSize().x * displayScale;

            // load atlas
            final JsonAtlas atlas = new JsonAtlas(mScene.getAxisSystem());
            try {
                atlas.load(getAssets(), "atlas/waterway_flower_" + i + ".json", 1);
                // attach texture
                atlas.getMasterFrameSet().setTexture(texture);
                // append to master frames
                mAllFrames.appendFrames(atlas.getMasterFrameSet());
            } catch (Exception e) {
                Log.e("MultiAtlasActivity", "Loading Atlas Error!", e);
            }

        }

        // create texture

    }

    private void addObject(final float screenX, final float screenY) {
        // create object
        Clip obj = new Clip(mAllFrames);
        obj.setFps(30);

        // center origin
        // obj.setOriginAtCenter();

        // position
        mScene.screenToGlobal(screenX, screenY, mTempPoint);
        obj.setPosition(mTempPoint);

        // add to scene
        mScene.addChild(obj);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            int len = event.getPointerCount();
            for (int i = 0; i < len; i++) {
                final float screenX = event.getX(i);
                final float screenY = event.getY(i);
                mStage.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        addObject(screenX, screenY);
                    }
                });
            }
        }

        return true;
    }

    public void onClickAtlas(final View view) {
        if (view.getId() == R.id.cb_show_atlas) {
            if (mAtlasSprites != null) {
                for (int i = 0; i < mAtlasSprites.length; i++) {
                    mAtlasSprites[i].setVisible(((CheckBox) findViewById(R.id.cb_show_atlas)).isChecked());
                }
            }
        }
    }
}

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
package com.funzio.pure2D.demo.textures;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.atlas.Atlas;
import com.funzio.pure2D.atlas.ImageSequenceAtlas;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.shapes.Clip;
import com.funzio.pure2D.shapes.Sprite;
import com.longo.pure2D.demo.R;

public class ImageSequenceActivity extends StageActivity {
    private static final String IMAGE_DIR = "mayan/symbols/majors/priest";
    // private static final String SDCARD_IMAGE_DIR = Environment.getExternalStorageDirectory() + "/funzio/casino/FarmRiches/images/symbols/majors/bonus";

    // private Texture mTexture;
    private ImageSequenceAtlas mAtlas;

    private Sprite mAtlasSprite;
    private Atlas.Listener mAtlasListener = new Atlas.Listener() {

        @Override
        public void onAtlasLoad(final Atlas atlas) {
            // get the generated texture
            // mTexture = mAtlas.getTexture();
            //
            // // create the big sprite for fun
            // mAtlasSprite = new Sprite();
            // mAtlasSprite.setTexture(mTexture);
            // float scale = mDisplaySize.x / mTexture.getSize().x;
            // mAtlasSprite.setScale(scale);
            // mScene.addChild(mAtlasSprite);
        }
    };

    // @Override
    // protected int getLayout() {
    // return R.layout.stage_atlas;
    // }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    // turn on debug
                    // GLDebugHelper.wrap(glState.mGL, GLDebugHelper.CONFIG_CHECK_GL_ERROR | GLDebugHelper.CONFIG_LOG_ARGUMENT_NAMES | GLDebugHelper.ERROR_WRONG_THREAD, new PrintWriter(System.out));

                    createAtlas();
                    addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                }
            }
        });
    }

    private void createAtlas() {
        mAtlas = new ImageSequenceAtlas(mScene.getGLState());
        mAtlas.setListener(mAtlasListener);
        mAtlas.loadDir(getAssets(), IMAGE_DIR, null); // load of assets
        // mAtlas.loadDirAsync(SDCARD_IMAGE_DIR, null); // load of sdcard
    }

    private void addObject(final float screenX, final float screenY) {
        if (mAtlas.getMasterFrameSet().getNumFrames() == 0) {
            return;
        }

        final Clip obj = new Clip(mAtlas.getMasterFrameSet());
        // center origin
        obj.setOriginAtCenter();

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
            if (mAtlasSprite != null) {
                mAtlasSprite.setVisible(((CheckBox) findViewById(R.id.cb_show_atlas)).isChecked());
            }
        }
    }
}

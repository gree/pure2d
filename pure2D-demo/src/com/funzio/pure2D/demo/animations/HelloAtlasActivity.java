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

import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.atlas.FunzioAtlas;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Clip;
import com.funzio.pure2D.shapes.Sprite;
import com.longo.pure2D.demo.R;

public class HelloAtlasActivity extends StageActivity {
    private Texture mTexture;
    private FunzioAtlas mAtlas;
    private String[] mFrameSetNames;
    private Sprite mAtlasSprite;

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

        Resources res = getResources();
        mAtlas = new FunzioAtlas(res.getXml(R.xml.atlas));
        mFrameSetNames = mAtlas.getSubFrameSets().keySet().toArray(new String[mAtlas.getNumSubFrameSets()]);
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
        // create texture
        mTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.atlas, null);

        mAtlasSprite = new Sprite();
        mAtlasSprite.setTexture(mTexture);
        float scale = mDisplaySize.x / mTexture.getSize().x;
        mAtlasSprite.setScale(scale);
        mScene.addChild(mAtlasSprite);
    }

    private void addObject(final float screenX, final float screenY) {
        // create object
        Clip obj = new Clip(mAtlas.getSubFrameSet(mFrameSetNames[mRandom.nextInt(mFrameSetNames.length)]));
        obj.setTexture(mTexture);
        // obj.setFps(30);

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
                // for testing clipping
                // if (((CheckBox) findViewById(R.id.cb_show_atlas)).isChecked()) {
                // mAtlasSprite.setPosition(0, 0);
                // } else {
                // mAtlasSprite.setPosition(0, mDisplaySize.y);
                // }
            }
        }
    }
}

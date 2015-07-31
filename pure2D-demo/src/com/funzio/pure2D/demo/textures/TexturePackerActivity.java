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

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.demo.objects.UniBouncer;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TexturePacker;
import com.funzio.pure2D.uni.UniGroup;

public class TexturePackerActivity extends StageActivity {
    private Texture mTexture;
    private TexturePacker mTexturePacker;
    private UniGroup mUniGroup;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    // load the textures
                    loadTextures();

                    mUniGroup = new UniGroup();
                    mUniGroup.setSize(mDisplaySize.x, mDisplaySize.y);
                    mUniGroup.setTexture(mTexture);
                    mScene.addChild(mUniGroup);

                    // generate a lot of squares
                    addObjects(OBJ_INIT_NUM);
                }
            }
        });
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    private void loadTextures() {

        mTexturePacker = new TexturePacker(getResources(), getApplicationContext().getPackageName(), null);
        mTexture = mTexturePacker.createTexture(mScene.getTextureManager(), "@drawable/cc_128", "@drawable/mw_128", "@drawable/ka_128");
    }

    private void addObjects(final int num) {
        // generate a lot of squares
        for (int i = 0; i < num; i++) {
            int random = mRandom.nextInt(mTexturePacker.getAtlas().getMasterFrameSet().getNumFrames());
            // create object
            UniBouncer sq = new UniBouncer();
            sq.setSizeToTexture(false);
            sq.setAtlasFrame((mTexturePacker.getAtlas().getMasterFrameSet().getFrame(random)));

            // random positions
            sq.setPosition(mRandom.nextInt(mDisplaySize.x), mRandom.nextInt(mDisplaySize.y));

            // add to scene
            mUniGroup.addChild(sq);
        }
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    addObjects(OBJ_STEP_NUM);
                }
            });
        }

        return true;
    }

}

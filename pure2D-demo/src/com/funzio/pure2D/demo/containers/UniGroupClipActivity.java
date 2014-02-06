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
package com.funzio.pure2D.demo.containers;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.atlas.JsonAtlas;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.uni.UniClip;
import com.funzio.pure2D.uni.UniGroup;

public class UniGroupClipActivity extends StageActivity {
    private Texture mTexture;
    private JsonAtlas mAtlas;
    private UniGroup mUniGroup;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to get the GL reference first
        mScene.setListener(new BaseScene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    // load the textures
                    loadTexture();

                    mUniGroup = new UniGroup();
                    mUniGroup.setId("Main Uni Group");
                    // generate a lot of squares
                    mUniGroup.setTexture(mTexture);
                    mScene.addChild(mUniGroup);

                    // generate a lot of squares
                    addGroup(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                }
            }
        });

        try {
            mAtlas = new JsonAtlas(mScene.getAxisSystem());
            mAtlas.load(getAssets(), "atlas/coin_01_60.json", 1);
        } catch (Exception e) {
            Log.e("UniGroupClipActivity", "Loading Atlas Error!", e);
        }
    }

    private void loadTexture() {
        // create texture
        mTexture = mScene.getTextureManager().createAssetTexture("atlas/coin_01_60.png", null);
    }

    private void addGroup(final float x, final float y) {

        for (int n = 0; n < 1000; n++) {
            // create object
            UniClip obj = new UniClip();
            obj.setAtlasFrameSet(mAtlas.getMasterFrameSet());
            obj.playAt(mRandom.nextInt(obj.getNumFrames()));
            // random positions
            obj.setPosition(mRandom.nextInt(mDisplaySize.x), mRandom.nextInt(mDisplaySize.y));

            // add to container
            mUniGroup.addChild(obj);
        }

        // UniGroup sub = new UniGroup();
        // // sub.setDebugFlags(Pure2D.DEBUG_FLAG_GLOBAL_BOUNDS);
        // sub.setSize(200, 200);
        // sub.setOriginAtCenter();
        // sub.setPosition(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
        // mUniGroup.addChild(sub);
        // for (int i = 0; i < 3; i++) {
        // UniSprite child = new UniSprite();
        // child.setAutoUpdateBounds(true);
        // // child.setAtlasFrameSet(mAtlas.getMasterFrameSet());
        // // child.setSize(200, 200);
        // // child.setPosition(100, 100);
        // child.setPosition(mRandom.nextInt(200), mRandom.nextInt(200));
        // child.setColor(COLOR_GREEN);
        // child.setOriginAtCenter();
        // sub.addChild(child);
        //
        // ScaleAnimator scaler = new ScaleAnimator(null);
        // scaler.setDuration(1000);
        // scaler.setLoop(LoopModes.LOOP_REVERSE);
        // child.addManipulator(scaler);
        // scaler.start(1.5f, 1.5f);
        // }
        // RotateAnimator rotator = new RotateAnimator(null);
        // rotator.setDuration(5000);
        // rotator.start(0, 360);
        // rotator.setLoop(LoopModes.LOOP_REPEAT);
        // sub.addManipulator(rotator);
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    addGroup(event.getX(), mDisplaySize.y - event.getY());
                }
            });
        }

        return true;
    }
}

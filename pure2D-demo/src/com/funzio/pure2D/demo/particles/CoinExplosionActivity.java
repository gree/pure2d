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

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.Animator.AnimatorListener;
import com.funzio.pure2D.animators.TrajectoryAnimator;
import com.funzio.pure2D.atlas.JsonAtlas;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.uni.UniClip;
import com.funzio.pure2D.uni.UniGroup;
import com.funzio.pure2D.uni.UniObject;

public class CoinExplosionActivity extends StageActivity implements AnimatorListener {
    private Texture mTexture;
    private JsonAtlas mAtlas;
    private UniGroup mUniGroup;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mAtlas = new JsonAtlas(mScene.getAxisSystem());
            mAtlas.load(getAssets(), "atlas/coin_01_60.json", 1);
        } catch (Exception e) {
            Log.e("JsonAtlasActivity", Log.getStackTraceString(e));
        }

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {

                    // load the textures
                    loadTexture();

                    mUniGroup = new UniGroup();
                    mUniGroup.setTexture(mTexture);
                    mScene.addChild(mUniGroup);

                    // generate a lot of squares
                    addSome(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                }
            }
        });

    }

    private void loadTexture() {
        // create texture
        mTexture = mScene.getTextureManager().createAssetTexture("atlas/coin_01_60.png", null);
    }

    private void addObject(final float x, final float y) {
        // create object
        UniClip obj = new UniClip(mAtlas.getMasterFrameSet());
        obj.playAt(mRandom.nextInt(obj.getNumFrames()));
        // obj.setRotation(mRandom.nextInt(360));
        // obj.setFps(30);

        // center origin
        obj.setOriginAtCenter();

        // position
        obj.setPosition(x, y);

        // add to scene
        mUniGroup.addChild(obj);

        // animation
        final TrajectoryAnimator animator = new TrajectoryAnimator(0);
        // animator.setTargetAngleFixed(false);
        // animator.setTargetAngleOffset(-90);
        obj.addManipulator(animator);
        animator.start(mRandom.nextInt(100), (float) (mRandom.nextInt(360) * Math.PI / 180));
        animator.setListener(this);
    }

    private void addSome(final float screenX, final float screenY) {
        mScene.screenToGlobal(screenX, screenY, mTempPoint);
        for (int i = 0; i < 10; i++) {
            addObject(mTempPoint.x, mTempPoint.y);
        }
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            final int num = event.getPointerCount();
            for (int i = 0; i < num; i++) {
                final float x = event.getX(i);
                final float y = event.getY(i);
                mStage.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        addSome(x, y);
                    }
                });
            }
        }

        return true;
    }

    @Override
    public void onAnimationEnd(final Animator animator) {
        mStage.queueEvent(new Runnable() {

            @Override
            public void run() {
                ((UniObject) animator.getTarget()).removeFromParent();
            }
        });
    }

    @Override
    public void onAnimationUpdate(final Animator animator, final float value) {
        // TODO Auto-generated method stub

    }

}

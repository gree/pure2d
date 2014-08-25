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
package com.funzio.pure2D.demo.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.atlas.FunzioAtlas;
import com.funzio.pure2D.containers.HScroll;
import com.funzio.pure2D.containers.VScroll;
import com.funzio.pure2D.containers.Wheel;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Clip;
import com.longo.pure2D.demo.R;

public class ScrollActivity extends StageActivity {
    private static final int LIST_WIDTH = 150;

    private FunzioAtlas mAtlas;
    private String[] mFrameSetNames;
    private Texture mTexture;

    private VScroll mVScroll;
    private HScroll mHScroll;

    @Override
    protected int getLayout() {
        return R.layout.stage_test;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // for swiping
        mScene.setUIEnabled(true);
        // need to get the GL reference first
        mScene.setListener(new BaseScene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {

                    // load the textures
                    loadTextures();

                    // generate the lists
                    addHScroll();
                    addVScroll();
                }
            }
        });

        Resources res = getResources();
        mAtlas = new FunzioAtlas(res.getXml(R.xml.atlas));
        mFrameSetNames = mAtlas.getSubFrameSets().keySet().toArray(new String[mAtlas.getNumSubFrameSets()]);
    }

    private void loadTextures() {
        // create texture
        mTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.atlas, null);
    }

    private void addVScroll() {
        mVScroll = new VScroll();
        // mVScroll.setCacheEnabled(true);
        mVScroll.setClippingEnabled(true);
        mVScroll.setSnapEnabled(true);
        // mVScroll.setPositiveOrientation(false);
        mVScroll.setGap(10);
        mVScroll.setSize(LIST_WIDTH, mDisplaySize.y - LIST_WIDTH);

        for (int n = 0; n < mFrameSetNames.length * 2; n++) {
            // create object
            Clip obj = new Clip(mAtlas.getSubFrameSet(mFrameSetNames[n % mFrameSetNames.length]));
            obj.setTexture(mTexture);

            // add to container
            mVScroll.addChild(obj);
        }
        mVScroll.setPosition(0, LIST_WIDTH);

        // add to scene
        mScene.addChild(mVScroll);
    }

    private void addHScroll() {
        mHScroll = new HScroll();
        // mHScroll.setCacheEnabled(true);
        mHScroll.setClippingEnabled(true);
        mHScroll.setSnapEnabled(true);
        mHScroll.setGap(10);
        mHScroll.setSize(mDisplaySize.x, LIST_WIDTH);

        for (int n = 0; n < mFrameSetNames.length * 2; n++) {
            // create object
            Clip obj = new Clip(mAtlas.getSubFrameSet(mFrameSetNames[n % mFrameSetNames.length]));
            obj.setTexture(mTexture);

            // add to container
            mHScroll.addChild(obj);
        }
        mHScroll.setPosition(0, 0);

        // add to scene
        mScene.addChild(mHScroll);
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        // forward the event to scene
        mScene.onTouchEvent(event);

        return true;
    }

    public void onClickTest(final View view) {
        if (mVScroll != null || mHScroll != null) {
            mVScroll.spinToEnd(Wheel.DEFAULT_SPIN_ACCELERATION, 300);
            mHScroll.spinToEnd(Wheel.DEFAULT_SPIN_ACCELERATION, 300);
        }
    }
}

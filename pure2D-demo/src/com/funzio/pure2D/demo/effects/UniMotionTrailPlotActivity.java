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
package com.funzio.pure2D.demo.effects;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.demo.objects.UniBouncer;
import com.funzio.pure2D.effects.trails.MotionTrailPlot;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.BlendModes;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.AssetTexture;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.uni.UniGroup;

public class UniMotionTrailPlotActivity extends StageActivity {

    private AssetTexture mTexture;
    private UniGroup mUniGroup;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    loadTextures();

                    mUniGroup = new UniGroup();
                    mUniGroup.setSize(mDisplaySize.x, mDisplaySize.y);
                    mUniGroup.setTexture(mTexture);
                    mScene.addChild(mUniGroup);

                    for (int i = 0; i < 100; i++) {
                        addObject(RANDOM.nextInt(mDisplaySize.x), RANDOM.nextInt(mDisplaySize.y));
                    }
                }
            }
        });
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    private void loadTextures() {
        final TextureOptions options = TextureOptions.getDefault();
        options.inMipmaps = 1;
        mTexture = mScene.getTextureManager().createAssetTexture("sprites/flare.png", options);
    }

    private void addObject(final float screenX, final float screenY) {
        mScene.screenToGlobal(screenX, screenY, mTempPoint);

        // create object
        final UniBouncer obj = new UniBouncer();
        obj.setSize(30, 30);
        obj.setOriginAtCenter();
        // obj.setColor(color1);
        obj.setPosition(mTempPoint);
        obj.setVisible(false);
        // add to scene
        mUniGroup.addChild(obj);

        final MotionTrailPlot trail = new MotionTrailPlot();
        // trail.setTexture(mTextures.get(RANDOM.nextInt(mTextures.size())));
        trail.setBlendFunc(BlendModes.PREMULTIPLIED_ALPHA_FUNC);
        final GLColor color1 = new GLColor(.5f + RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), 1f);
        final GLColor color2 = new GLColor(color1);// new GLColor(.5f + RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), 0.5f);
        color2.a = 0.1f;
        // trail.setColorRange(color1, color2);
        trail.setColorRange(new GLColor(0xFFFFFFFF), new GLColor(0x00000000));
        // trail.setAlpha(0.5f);
        // trail.setColor(new GLColor(1, 0, 0, 1f));
        trail.setScaleRange(1f, 0.25f);
        trail.setNumPoints(10);
        // trail.setMinLength(400);
        trail.setTarget(obj);
        mUniGroup.addChild(trail);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    for (int i = 0; i < 50; i++) {
                        addObject(event.getX(), event.getY());
                    }
                }
            });
        }

        return true;
    }

}

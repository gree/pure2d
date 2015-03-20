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

import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Sprite9;
import com.longo.pure2D.demo.R;

public class Sprite9Activity extends StageActivity {
    private Texture mTexture;
    protected boolean m9PatchEnabled = true;

    @Override
    protected int getLayout() {
        return R.layout.stage_texture_9_patch;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScene.setColor(COLOR_GREEN);
        // mScene.setAxisSystem(Scene.AXIS_TOP_LEFT);
        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    // load the textures
                    loadTexture();

                    // create first obj
                    addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                }
            }
        });
    }

    private void loadTexture() {
        // create texture
        mTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.panel_collection_reward, null);
    }

    private void addObject(final float x, final float y) {
        // create object
        Sprite9 obj = new Sprite9();
        obj.setTexture(mTexture);
        obj.setSize(mRandom.nextInt(400) + 100, mRandom.nextInt(400) + 100);
        obj.set9Patches(20, 20, 20, 20);
        obj.set9PatchEnabled(m9PatchEnabled);
        // obj.setRotation(mRandom.nextInt(360));
        // obj.setColor(new GLColor(mRandom.nextFloat(), mRandom.nextFloat(), mRandom.nextFloat(), 0.5f));

        // center origin
        obj.setOriginAtCenter();

        // set positions
        PointF global = new PointF();
        mScene.screenToGlobal(x, y, global);
        obj.setPosition(global);

        // add to scene
        mScene.addChild(obj);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    addObject(event.getX(), event.getY());
                }
            });
        }

        return true;
    }

    public void onClick9Patch(final View view) {
        if (view.getId() == R.id.cb_texture_9_patch) {
            m9PatchEnabled = ((CheckBox) findViewById(R.id.cb_texture_9_patch)).isChecked();
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    final int num = mScene.getNumChildren();
                    for (int n = 0; n < num; n++) {
                        Sprite9 obj = (Sprite9) mScene.getChildAt(n);
                        obj.set9PatchEnabled(m9PatchEnabled);
                    }
                }
            });
        }
    }
}

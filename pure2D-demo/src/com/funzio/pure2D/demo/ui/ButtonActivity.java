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

import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.ui.Button;
import com.longo.pure2D.demo.R;

public class ButtonActivity extends StageActivity {
    private Texture[] mTextures;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScene.setColor(COLOR_GREEN);
        mScene.setUIEnabled(true);
        // mScene.setAxisSystem(Scene.AXIS_TOP_LEFT);
        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    // load the textures
                    loadTexture();

                    // create first objs
                    addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                    addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y - 200);
                    addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y + 200);
                }
            }
        });
    }

    private void loadTexture() {
        // create texture
        mTextures = new Texture[] { //
                mScene.getTextureManager().createDrawableTexture(R.drawable.btn_up, null), mScene.getTextureManager().createDrawableTexture(R.drawable.btn_down, null),
                mScene.getTextureManager().createDrawableTexture(R.drawable.btn_disabled, null),
        };
    }

    private void addObject(final float x, final float y) {
        // create object
        Button obj = new Button();
        obj.setTextures(mTextures);
        obj.setOriginAtCenter();

        // set positions
        final PointF point = new PointF();
        mScene.screenToGlobal(x, y, point);
        obj.setPosition(point);

        // add to scene
        mScene.addChild(obj);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        mScene.onTouchEvent(event);

        return true;
    }
}

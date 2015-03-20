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
package com.funzio.pure2D.demo.pui;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.MenuActivity;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.ui.UIManager;
import com.funzio.pure2D.ui.UITextureManager;
import com.longo.pure2D.demo.Pure2DDemoApplication;

public class PUIActivity extends StageActivity {
    private UIManager mUIManager = Pure2DDemoApplication.getInstance().getUIManager();
    private int mXmlResId;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // just to include the class
        // MyDialog.class.getSimpleName();

        final String xmlName = getIntent().getExtras().getString(MenuActivity.EXTRA_TAG);
        mXmlResId = getResources().getIdentifier(xmlName, "xml", getApplicationContext().getPackageName());

        mScene.setColor(COLOR_GREEN);
        mScene.setUIEnabled(true);
        // mScene.setAxisSystem(Scene.AXIS_TOP_LEFT);
        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    final UITextureManager textureManager = (UITextureManager) mScene.getTextureManager();
                    mUIManager.setTextureManager(textureManager);

                    // preload some assets
                    textureManager.preloadAssets();

                    // add a sample object
                    addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // clear everything
        mUIManager.reset();
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    private void addObject(final float x, final float y) {
        // create object
        final DisplayObject obj = mUIManager.load(mXmlResId);
        if (obj == null) {
            return;
        }

        // Log.e("long", obj.getObjectTree(""));
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

        if (!mScene.onTouchEvent(event)) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mStage.queueEvent(new Runnable() {

                    @Override
                    public void run() {
                        addObject(event.getX(), event.getY());
                    }
                });
            }
        }

        return true;
    }
}

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
import com.funzio.pure2D.demo.objects.Bouncer;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.TextTexture;

public class HelloTextActivity extends StageActivity {
    private TextTexture mTexture;
    private PointF mTempPoint = new PointF();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // mScene.setColor(new GLColor(0, .8f, 0, 1f));
        mScene.setListener(new Scene.Listener() {
            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    createTexture();
                    addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                }
            }
        });
    }

    private void createTexture() {
        mTexture = mScene.getTextureManager().createTextTexture("Hello World!", null);
    }

    private void addObject(final float x, final float y) {
        // convert from screen to scene's coordinates
        mScene.screenToGlobal(x, y, mTempPoint);

        // create object
        Bouncer obj = new Bouncer();
        obj.setTexture(mTexture);
        obj.setColor(new GLColor(255, mRandom.nextInt(255), mRandom.nextInt(255), 255));

        // center origin
        // obj.setOriginAtCenter();

        // random positions
        obj.setPosition(mTempPoint.x, mTempPoint.y);

        // add to scene
        mScene.addChild(obj);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            mStage.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int len = event.getPointerCount();
                    for (int i = 0; i < len; i++) {
                        addObject(event.getX(i), event.getY(i));
                    }
                }
            });
        }

        return true;
    }
}

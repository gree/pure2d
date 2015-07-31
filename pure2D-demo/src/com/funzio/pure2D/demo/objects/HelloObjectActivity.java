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
package com.funzio.pure2D.demo.objects;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.textures.DrawableTexture;
import com.funzio.pure2D.shapes.Rectangular;

public class HelloObjectActivity extends StageActivity {

    protected DrawableTexture mTexture;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
    }

    private void addObject(final float screenX, final float screenY) {
        // create object
        final Rectangular obj = new Rectangular();
        obj.setColor(new GLColor(1f, mRandom.nextFloat(), mRandom.nextFloat(), mRandom.nextFloat() + 0.5f));
        obj.setSize(128, 128);
        // center origin
        obj.setOriginAtCenter();
        // position
        mScene.screenToGlobal(screenX, screenY, mTempPoint);
        obj.setPosition(mTempPoint);
        // add to scene
        mScene.addChild(obj);

        // // test polyline
        // Polyline line = new Polyline();
        // line.setStrokeRange(50, 150);
        // // line.setStrokeColors(new GLColor(1, 1, 0, 0.5f));
        // line.setPoints(new PointF(0, 0), new PointF(100, 50), new PointF(200, 0), new PointF(200, 300), new PointF(0, 300), new PointF(0, 200), new PointF(-100, 200), new PointF(-100, 400),
        // new PointF(-100, 500), new PointF(-100, 600), new PointF(0, 600), new PointF(-200, 800), new PointF(-200, 400), new PointF(-200, 500), new PointF(-200, 0), new PointF(-500, 500));
        // // line.setPoints(new PointF(0, 0), new PointF(100, 100), new PointF(200, 0), new PointF(0, 100));
        // line.setPosition(x, y);
        // if (mTexture == null) {
        // mTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.panel_collection_reward, null);
        // mTexture.setFilters(GL10.GL_LINEAR, GL10.GL_LINEAR);
        // // mTexture.setRepeat(GL10.GL_REPEAT, GL10.GL_REPEAT);
        // }
        // line.setTextureCaps(20, 20);
        // // line.setTextureRepeating(true);
        // line.setTexture(mTexture);
        // // line.setStrokeInterpolator(NovaConfig.INTER_BOUNCE);
        // // line.setDebugFlags(Pure2D.DEBUG_FLAG_WIREFRAME);
        // mScene.addChild(line);

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
}

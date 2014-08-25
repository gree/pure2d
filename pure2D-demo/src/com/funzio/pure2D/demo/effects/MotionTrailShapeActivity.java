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
import android.view.animation.Interpolator;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.demo.animations.AnimationActivity;
import com.funzio.pure2D.demo.objects.Bouncer;
import com.funzio.pure2D.effects.trails.MotionTrailShape;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.longo.pure2D.demo.R;

public class MotionTrailShapeActivity extends StageActivity {

    private Interpolator mInterpolator;

    @SuppressWarnings("unused")
    private Texture mTexture;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    loadTexture();

                    for (int i = 0; i < 100; i++) {
                        addObject(RANDOM.nextInt(mDisplaySize.x), RANDOM.nextInt(mDisplaySize.y));
                    }
                }
            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.stage_motion_trail;
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    // private void addObject(float x, float y) {
    // final GLColor color1 = new GLColor(.5f + RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), 1f);
    // final GLColor color2 = new GLColor(color1);
    // color2.a = 0;
    //
    // // create object
    // Polyline obj = new Polyline();
    // obj.setColor(color1);
    // obj.setStrokeRange(10, 50);
    //
    // PointF[] points = new PointF[5];
    // points[0] = new PointF(x, y);
    // for (int i = 1; i < points.length; i++) {
    // x += RANDOM.nextInt(300) - 150;
    // y += RANDOM.nextInt(300) - 150;
    // points[i] = new PointF(x, y);
    // }
    // obj.setPoints(points);
    //
    // // obj.setPoints(new PointF(x, y), new PointF(x + RANDOM.nextInt(300), y + RANDOM.nextInt(300)), new PointF(x + RANDOM.nextInt(300), y + RANDOM.nextInt(300)));
    // // obj.setPoints(new PointF(x, y), new PointF(x + 300, y), new PointF(x + 100, y + 100), new PointF(x + 400, y - 300), new PointF());
    //
    // // add to scene
    // mScene.addChild(obj);
    // }

    private void loadTexture() {
        // create texture
        mTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.cc_175, null);
    }

    private void addObject(final float screenX, final float screenY) {
        final GLColor color1 = new GLColor(.5f + RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), 1f);
        final GLColor color2 = new GLColor(color1);// new GLColor(.5f + RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), 0.5f);
        color2.a = 0.1f;

        mScene.screenToGlobal(screenX, screenY, mTempPoint);

        // create object
        Bouncer obj = new Bouncer();
        obj.setSize(30, 30);
        obj.setAutoUpdateBounds(true);
        obj.setOriginAtCenter();
        obj.setColor(color1);
        obj.setPosition(mTempPoint);
        // add to scene
        mScene.addChild(obj);

        MotionTrailShape trail = new MotionTrailShape();
        // trail.setColor(color1);
        trail.setStrokeRange(30, 1);
        trail.setNumPoints(20);
        trail.setStrokeColors(color1, color2);
        // trail.setTexture(mTexture);
        // trail.setTextureCoordBuffer(TextureCoordBuffer.getDefault());
        trail.setMinLength(100);
        trail.setTarget(obj);
        trail.setStrokeInterpolator(mInterpolator);
        mScene.addChild(trail);

    }

    private void setStrokeInterpolator(final Interpolator interpolator) {
        mInterpolator = interpolator;
        mScene.queueEvent(new Runnable() {

            @Override
            public void run() {
                final int num = mScene.getNumChildren();
                for (int i = 0; i < num; i++) {
                    Manipulatable child = mScene.getChildAt(i);
                    if (child instanceof MotionTrailShape) {
                        ((MotionTrailShape) child).setStrokeInterpolator(interpolator);
                    }
                }

            }
        });
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

    public void onClickRadio(final View view) {

        // tween specific switches
        switch (view.getId()) {
            case R.id.radio_linear:
                setStrokeInterpolator(null);
                break;

            case R.id.radio_accelarate:
                setStrokeInterpolator(AnimationActivity.ACCELERATE);
                break;

            case R.id.radio_decelarate:
                setStrokeInterpolator(AnimationActivity.DECELERATE);
                break;

            case R.id.radio_accelerate_decelarate:
                setStrokeInterpolator(AnimationActivity.ACCELERATE_DECELERATE);
                break;

            case R.id.radio_bounce:
                setStrokeInterpolator(AnimationActivity.BOUNCE);
                break;

            case R.id.radio_anticipate:
                setStrokeInterpolator(AnimationActivity.ANTICIPATE);
                break;

            case R.id.radio_anticipate_overshoot:
                setStrokeInterpolator(AnimationActivity.ANTICIPATE_OVERSHOOT);
                break;

            case R.id.radio_overshoot:
                setStrokeInterpolator(AnimationActivity.OVERSHOOT);
                break;

            case R.id.radio_cycle:
                setStrokeInterpolator(AnimationActivity.CYCLE);
                break;
        }
    }
}

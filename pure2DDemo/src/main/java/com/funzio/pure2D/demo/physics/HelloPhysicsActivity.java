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
/**
 * 
 */
package com.funzio.pure2D.demo.physics;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

import android.content.Context;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Sprite;
import com.longo.pure2D.demo.R;

public class HelloPhysicsActivity extends StageActivity implements SensorEventListener {
    private List<Texture> mTextures = new ArrayList<Texture>();
    protected boolean mUseTexture = true;

    private HelloPhysicsWorld mWorld;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private Body mTouchedBody;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    // load the textures
                    loadTextures();

                    // add boxes
                    for (int i = 0; i < 50; i++) {
                        addBox(mRandom.nextInt(mDisplaySize.x), 100 + mRandom.nextInt(300));
                    }
                }
            }
        });

        // init physics world
        mWorld = new HelloPhysicsWorld(mDisplaySize.x, mDisplaySize.y, new Vec2(0.0f, -0.1f));
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        mWorld.start();

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mWorld.stop();

        mSensorManager.unregisterListener(this);
    }

    private void loadTextures() {
        final int[] ids = {
                R.drawable.cc_32, // cc
                R.drawable.mw_32, // mw
                R.drawable.ka_32, // ka
                R.drawable.cc_128, // cc
                R.drawable.mw_128, // mw
                R.drawable.ka_128, // ka
        };

        for (int id : ids) {
            mTextures.add(mScene.getTextureManager().createDrawableTexture(id, null));
        }
    }

    private MouseJoint mMouseJoint;

    private void addBox(final float x, final float y) {

        mWorld.post(new Runnable() {

            @Override
            public void run() {
                final int size = mRandom.nextFloat() < 0.4 ? 32 : (mRandom.nextFloat() < 0.7 ? 64 : 128);
                final Body body = mWorld.addBox(mWorld.p2M(x), mWorld.p2M(y), mWorld.p2M(size), mWorld.p2M(size));
                // add to physics world
                if (body != null) {

                    final Texture texture = mTextures.get((size >= 64 ? 3 : 0) + mRandom.nextInt(mTextures.size() / 2));
                    // create object
                    final Sprite sprite = new Sprite();
                    // obj.setColor(new GLColor(1f, mRandom.nextFloat(), mRandom.nextFloat(), 1f));
                    sprite.setTexture(texture);
                    sprite.setSize(size, size);
                    sprite.setPosition(x, y);
                    // center origin
                    sprite.setOrigin(new PointF(size / 2, size / 2));

                    // attach to user data
                    body.setUserData(sprite);

                    mStage.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            // add to scene
                            mScene.addChild(sprite);
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        float x = event.getX();
        float y = mDisplaySize.y - event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            final Fixture fixture = mWorld.getFixtureAt(mWorld.p2M(x), mWorld.p2M(y));
            mTouchedBody = fixture == null ? null : fixture.getBody();

            if (mTouchedBody == null) {
                addBox(x, y);
            } else {

                // find the sprite
                final Sprite sprite = (Sprite) mTouchedBody.getUserData();
                if (sprite != null) {
                    // change color
                    sprite.setColor(new GLColor(1f, 0, 0, 1f));
                }

                // create a joint
                mTouchedBody.setAwake(true);
                final MouseJointDef mouseJointDef = new MouseJointDef();
                mouseJointDef.bodyA = mWorld.getGroundBody();
                mouseJointDef.bodyB = mTouchedBody;
                mouseJointDef.target.set(mWorld.p2M(x), mWorld.p2M(y));
                mouseJointDef.collideConnected = true;
                mouseJointDef.dampingRatio = 0.1f;
                mouseJointDef.maxForce = 300f * mTouchedBody.getMass();
                mWorld.post(new Runnable() {
                    @Override
                    public void run() {
                        mMouseJoint = (MouseJoint) mWorld.createJoint(mouseJointDef);
                    }
                });
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP && mTouchedBody != null) {
            // find the sprite
            final Sprite sprite = (Sprite) mTouchedBody.getUserData();
            if (sprite != null) {
                // change color
                sprite.setColor(null);
            }

            // clean up
            mTouchedBody = null;
            mWorld.post(new Runnable() {
                @Override
                public void run() {
                    mWorld.destroyJoint(mMouseJoint);
                    mMouseJoint = null;
                }
            });
        } else if (event.getAction() == MotionEvent.ACTION_MOVE && mMouseJoint != null) {
            // drag it
            mMouseJoint.getTarget().set(mWorld.p2M(x), mWorld.p2M(y));
        }

        return true;
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        float x = event.values[0] * 10;
        float y = event.values[1] * 10;
        mWorld.getGravity().set(-x, -y);
    }

    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {

    };
}

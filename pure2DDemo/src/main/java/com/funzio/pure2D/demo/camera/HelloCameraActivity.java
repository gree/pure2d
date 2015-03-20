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
package com.funzio.pure2D.demo.camera;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Camera;
import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.demo.objects.UniBouncer;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.uni.UniGroup;

public class HelloCameraActivity extends StageActivity {
    private Camera mCamera;
    private UniGroup mUniGroup;
    protected PointF mRegisteredVector;
    protected PointF mRegisteredCenter;
    protected float mRegisteredZoom = 1;
    protected float mRegisteredRotation = 0;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Pure2D.setAutoUpdateBounds(true); // for camera clipping
        mCamera = new Camera(new PointF(mDisplaySizeDiv2), new PointF(mDisplaySize));
        mCamera.setClipping(true);
        mScene.setCamera(mCamera);

        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    mUniGroup = new UniGroup();
                    mUniGroup.setSize(mDisplaySize.x, mDisplaySize.y);
                    mScene.addChild(mUniGroup);
                    // generate a lot of squares
                    addObjects(OBJ_INIT_NUM * 2);
                }
            }
        });

    }

    private void addObjects(final int num) {
        // generate a lot of squares
        for (int i = 0; i < num; i++) {
            // create object
            UniBouncer sq = new UniBouncer();
            // sq.setAutoUpdateBounds(true);// for camera clipping
            sq.setSize(30, 30);
            sq.setColor(new GLColor(1f, mRandom.nextFloat(), mRandom.nextFloat(), 0.7f));
            sq.setBoundary(mDisplaySize.y * 3, mDisplaySize.y * 3);

            // random positions
            sq.setPosition(mRandom.nextInt(mDisplaySize.x * 3), mRandom.nextInt(mDisplaySize.y * 3));

            // add to scene
            mUniGroup.addChild(sq);
        }
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {

        // find the vector
        PointF p1 = null;
        PointF p2 = null;
        PointF vector = null;
        if (event.getPointerCount() == 2) {
            p1 = new PointF(event.getX(0), mDisplaySize.y - event.getY(0));
            p2 = new PointF(event.getX(1), mDisplaySize.y - event.getY(1));
            vector = new PointF(p2.x - p1.x, p2.y - p1.y);
        } else if (event.getPointerCount() < 2) {
            mRegisteredVector = null;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (event.getPointerCount() == 1) {
                if (mRegisteredCenter == null) {
                    mRegisteredCenter = mCamera.getPosition();
                } else {
                    float deltaX = event.getX() - mRegisteredCenter.x;
                    float deltaY = mDisplaySize.y - event.getY() - mRegisteredCenter.y;
                    mCamera.moveTo(mRegisteredCenter.x + deltaX, mRegisteredCenter.y + deltaY);
                }
            } else if (event.getPointerCount() == 2) {

                if (mRegisteredVector == null) {
                    mRegisteredVector = new PointF(vector.x, vector.y);
                    mRegisteredZoom = mCamera.getZoom().x;
                    mRegisteredRotation = mCamera.getRotation();
                }

                // focus on the center of the vector
                mCamera.setPosition(p1.x + vector.x / 2, p1.y + vector.y / 2);

                // zoom it
                float scale = vector.length() / mRegisteredVector.length();
                if (scale > 0) {
                    mCamera.setZoom(mRegisteredZoom * scale);
                }

                // rotate it
                float cos = (mRegisteredVector.x * vector.x + mRegisteredVector.y * vector.y) / (mRegisteredVector.length() * vector.length());
                float rotation = -(float) (Math.acos(cos) * 180 / Math.PI);
                mCamera.setRotation(mRegisteredRotation + rotation);
            }
        }

        return true;
    }
}

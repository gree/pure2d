package com.funzio.pure2D.samples.activities.camera;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Camera;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.samples.Bouncer;
import com.funzio.pure2D.samples.activities.StageActivity;

public class HelloCameraActivity extends StageActivity {
    private Camera mCamera;
    private PointF mRegisteredVector;
    private PointF mRegisteredCenter;
    private float mRegisteredZoom = 1;
    private float mRegisteredRotation = 0;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCamera = new Camera(new PointF(mDisplaySizeDiv2), new PointF(mDisplaySize));
        mCamera.setClipping(true);
        mScene.setCamera(mCamera);

        // generate a lot of squares
        addObjects(OBJ_INIT_NUM * 2);
    }

    private void addObjects(final int num) {
        // generate a lot of squares
        for (int i = 0; i < num; i++) {
            // create object
            Bouncer sq = new Bouncer();
            sq.setAutoUpdateBounds(true);// for camera clipping
            sq.setSize(30, 30);
            sq.setColor(new GLColor(1f, mRandom.nextFloat(), mRandom.nextFloat(), 0.7f));
            sq.setBoundary(mDisplaySize.y * 3, mDisplaySize.y * 3);

            // random positions
            sq.setPosition(mRandom.nextInt(mDisplaySize.x * 3), mRandom.nextInt(mDisplaySize.y * 3));

            // add to scene
            mScene.addChild(sq);
        }
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (event.getPointerCount() == 1) {
                if (mRegisteredVector == null || mRegisteredCenter == null) {
                    mRegisteredVector = new PointF(event.getX(), mDisplaySize.y - event.getY());
                    mRegisteredCenter = mCamera.getPosition();
                } else {
                    float deltaX = event.getX() - mRegisteredVector.x;
                    float deltaY = mDisplaySize.y - event.getY() - mRegisteredVector.y;
                    mCamera.moveTo(mRegisteredCenter.x + deltaX, mRegisteredCenter.y + deltaY);
                    mRegisteredVector.x = event.getX();
                    mRegisteredVector.y = mDisplaySize.y - event.getY();
                }
            } else if (event.getPointerCount() == 2) {
                PointF temp1 = new PointF(event.getX(0), mDisplaySize.y - event.getY(0));
                PointF temp2 = new PointF(event.getX(1), mDisplaySize.y - event.getY(1));
                PointF vector = new PointF(temp2.x - temp1.x, temp2.y - temp1.y);

                if (mRegisteredVector == null) {
                    mRegisteredVector = new PointF(vector.x, vector.y);
                    mRegisteredZoom = mCamera.getZoom().x;
                    mRegisteredRotation = mCamera.getRotation();
                } else {
                    // focus on the center of the vector
                    mCamera.setPosition(temp1.x + vector.x / 2, temp1.y + vector.y / 2);
                    // zoom it
                    float scale = vector.length() / mRegisteredVector.length();
                    mCamera.setZoom(mRegisteredZoom * scale);
                    // rotate it
                    // float cos = (mRegisteredVector.x * vector.x + mRegisteredVector.y * vector.y) / (mRegisteredVector.length() * vector.length());
                    // float rotation = -(float) (Math.acos(cos) * 180 / Math.PI);
                    // mCamera.setRotation(mRegisteredRotation + rotation);
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mRegisteredVector = null;
        }
        return true;
    }
}

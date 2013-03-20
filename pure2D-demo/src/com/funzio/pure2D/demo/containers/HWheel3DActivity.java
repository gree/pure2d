package com.funzio.pure2D.demo.containers;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.atlas.FunzioAtlas;
import com.funzio.pure2D.containers.Wheel3D;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Clip;

public class HWheel3DActivity extends StageActivity {
    private static final int WHEEL_HEIGHT = 150;
    private int NUM_WHEELS;
    private PointF mRegisteredPoint = new PointF();
    private Wheel3D[] mWheels;
    private FunzioAtlas mAtlas;
    private String[] mFrameSetNames;
    private Texture mTexture;
    private Wheel3D mSelectedWheel;
    private float mDelta;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NUM_WHEELS = Math.round(mDisplaySize.y / WHEEL_HEIGHT);
        mWheels = new Wheel3D[NUM_WHEELS];

        // need to get the GL reference first
        // mScene.setCamera(new PerspectiveCamera(new PointF(mDisplaySize)));
        mScene.setListener(new BaseScene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {
                // mScene.setDepthRange(-100, 100);
                // load the textures
                loadTextures();

                // generate a lot of squares
                addWheels(mRandom.nextInt(mDisplaySize.x), mRandom.nextInt(mDisplaySize.y));
            }
        });

        Resources res = getResources();
        mAtlas = new FunzioAtlas(res.getXml(R.xml.atlas));
        mFrameSetNames = mAtlas.getSubFrameSets().keySet().toArray(new String[mAtlas.getNumSubFrameSets()]);
    }

    private void loadTextures() {
        // create texture
        mTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.atlas, null);
    }

    private void addWheels(final float x, final float y) {
        float itemSize = -1;
        for (int i = 0; i < NUM_WHEELS; i++) {
            Wheel3D wheel = new Wheel3D();

            for (int n = 0; n < mFrameSetNames.length; n++) {
                // create object
                Clip obj = new Clip(mAtlas.getSubFrameSet(mFrameSetNames[n % mFrameSetNames.length]));
                obj.setTexture(mTexture);
                obj.setOriginAtCenter();
                obj.setAlphaTestEnabled(true);

                // add to container
                wheel.addChild(obj);
                if (itemSize < 0) {
                    itemSize = obj.getWidth();
                }
            }
            // wheel.setGapAngle(30);
            wheel.setRadius(mDisplaySizeDiv2.x - itemSize / 2);
            wheel.setPosition(mDisplaySizeDiv2.x, i * WHEEL_HEIGHT + (itemSize / 2f));
            // wheel.setDepthRange(-100, 100);

            // add to scene
            mScene.addChild(wheel);
            mWheels[i] = wheel;
        }
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    // private int index = 0;

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        if (action == MotionEvent.ACTION_DOWN) {
            int index = (int) ((mDisplaySize.y - y) / WHEEL_HEIGHT);
            if (index < NUM_WHEELS) {
                mSelectedWheel = mWheels[index];
                mRegisteredPoint.x = x;
                mRegisteredPoint.y = y;
                if (mSelectedWheel != null) {
                    mSelectedWheel.stop();
                }
            } else {
                mSelectedWheel = null;
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (mSelectedWheel != null) {
                mDelta = x - mRegisteredPoint.x;
                mSelectedWheel.scrollByDistance(mDelta);
                mRegisteredPoint.x = x;
                mRegisteredPoint.y = y;
            }
        } else if (action == MotionEvent.ACTION_UP) {
            if (mSelectedWheel != null) {
                mSelectedWheel.spin(-mDelta / BaseScene.DEFAULT_MSPF, mDelta > 0 ? 0.002f : -0.002f);

                // mSelectedWheel.spinToChild(mSelectedWheel.getNumChildren() - 1 - (++index) % mSelectedWheel.getNumChildren(), -0.0001f, 500, false);
            }

        }

        return true;
    }
}

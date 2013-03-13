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
import com.funzio.pure2D.containers.HWheel;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Clip;

public class HWheelActivity extends StageActivity {
    private static final int WHEEL_HEIGHT = 150;
    private int NUM_WHEELS;
    private PointF mRegisteredPoint = new PointF();
    private HWheel[] mWheels;
    private FunzioAtlas mAtlas;
    private String[] mFrameSetNames;
    private Texture mTexture;
    private HWheel mSelectedWheel;
    private float mDelta;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NUM_WHEELS = Math.round(mDisplaySize.y / WHEEL_HEIGHT);
        mWheels = new HWheel[NUM_WHEELS];

        // need to get the GL reference first
        mScene.setListener(new BaseScene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {

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
        for (int i = 0; i < NUM_WHEELS; i++) {
            HWheel wheel = new HWheel();
            wheel.setGap(10);
            wheel.setSize(mDisplaySize.x, WHEEL_HEIGHT);

            for (int n = 0; n < mFrameSetNames.length; n++) {
                // create object
                Clip obj = new Clip(mAtlas.getSubFrameSet(mFrameSetNames[n % mFrameSetNames.length]));
                obj.setTexture(mTexture);

                // add to container
                wheel.addChild(obj);
            }
            wheel.setPosition(0, i * WHEEL_HEIGHT);

            // add to scene
            mScene.addChild(wheel);
            mWheels[i] = wheel;
        }
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

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
                mSelectedWheel.scrollBy(-mDelta, 0);
                mRegisteredPoint.x = x;
                mRegisteredPoint.y = y;
            }
        } else if (action == MotionEvent.ACTION_UP) {
            if (mSelectedWheel != null) {
                mSelectedWheel.spin(mDelta / BaseScene.DEFAULT_MSPF, mDelta > 0 ? -0.002f : 0.002f);
            }
        }

        return true;
    }
}

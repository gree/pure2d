package com.funzio.pure2D.demo.containers;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.atlas.FunzioAtlas;
import com.funzio.pure2D.containers.Wheel3D;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Clip;

public class HWheel3DActivity extends StageActivity {
    private static final int WHEEL_HEIGHT = 150;
    private int NUM_WHEELS;

    private FunzioAtlas mAtlas;
    private String[] mFrameSetNames;
    private Texture mTexture;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NUM_WHEELS = Math.round(mDisplaySize.y / (float) WHEEL_HEIGHT);

        // need to get the GL reference first
        // mScene.setCamera(new PerspectiveCamera(new PointF(mDisplaySize)));
        mScene.setUIEnabled(true);
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
            wheel.setSwipeEnabled(true);
            wheel.setSize(mDisplaySize.x, WHEEL_HEIGHT);

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
        }
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        // forward the event to scene
        mScene.onTouchEvent(event);

        return true;
    }
}

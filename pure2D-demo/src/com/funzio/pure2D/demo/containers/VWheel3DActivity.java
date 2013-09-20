package com.funzio.pure2D.demo.containers;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.atlas.FunzioAtlas;
import com.funzio.pure2D.containers.Wheel3D;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Clip;

public class VWheel3DActivity extends StageActivity {
    private static final int WHEEL_WIDTH = 150;

    private int NUM_WHEELS;
    private FunzioAtlas mAtlas;
    private String[] mFrameSetNames;
    private Texture mTexture;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NUM_WHEELS = Math.round(mDisplaySize.x / (float) WHEEL_WIDTH);

        // for swiping
        mScene.setUIEnabled(true);
        // need to get the GL reference first
        mScene.setListener(new BaseScene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {

                    // load the textures
                    loadTextures();

                    // generate a lot of wheels
                    addWheels();
                }
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

    private void addWheels() {
        float itemSize = -1;

        for (int i = 0; i < NUM_WHEELS; i++) {
            Wheel3D wheel = new Wheel3D();
            wheel.setOrientation(Wheel3D.ORIENTATION_Y);
            wheel.setSwipeEnabled(true);
            wheel.setSize(WHEEL_WIDTH, mDisplaySize.y);

            for (int n = 0; n < mFrameSetNames.length * 2; n++) {
                // create object
                Clip obj = new Clip(mAtlas.getSubFrameSet(mFrameSetNames[n % mFrameSetNames.length]));
                obj.setTexture(mTexture);
                obj.setOriginAtCenter();
                obj.setAlphaTestEnabled(true);

                // add to container
                wheel.addChild(obj);

                // add to container
                wheel.addChild(obj);
                if (itemSize < 0) {
                    itemSize = obj.getHeight();
                }
            }
            // wheel.setGapAngle(30);
            wheel.setRadius(mDisplaySizeDiv2.y - itemSize / 2f);
            wheel.setPosition(i * WHEEL_WIDTH + (itemSize / 2f), mDisplaySizeDiv2.y);
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

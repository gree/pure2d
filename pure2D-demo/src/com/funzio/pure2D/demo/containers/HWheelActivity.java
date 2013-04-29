package com.funzio.pure2D.demo.containers;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.atlas.FunzioAtlas;
import com.funzio.pure2D.containers.HWheel;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Clip;

public class HWheelActivity extends StageActivity {
    private static final int WHEEL_HEIGHT = 150;
    private int NUM_WHEELS;

    private FunzioAtlas mAtlas;
    private String[] mFrameSetNames;
    private Texture mTexture;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NUM_WHEELS = Math.round(mDisplaySize.y / (float) WHEEL_HEIGHT);

        // to allow swiping
        mScene.setUIEnabled(true);
        // need to get the GL reference first
        mScene.setListener(new BaseScene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {

                // load the textures
                loadTextures();

                // generate a lot of wheels
                addWheels();
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
        for (int i = 0; i < NUM_WHEELS; i++) {
            HWheel wheel = new HWheel();
            wheel.setGap(10);
            wheel.setSwipeEnabled(true);
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

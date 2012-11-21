package com.funzio.pure2D.samples.activities.containers;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.R;
import com.funzio.pure2D.atlas.FunzioAtlas;
import com.funzio.pure2D.containers.Wheel3D;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.samples.activities.StageActivity;
import com.funzio.pure2D.shapes.Clip;

public class VWheel3DActivity extends StageActivity {
    private PointF mRegisteredPoint = new PointF();
    private Wheel3D mWheel;
    private FunzioAtlas mAtlas;
    private String[] mFrameSetNames;
    private Texture mTexture;
    private float mDelta;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to get the GL reference first
        mScene.setListener(new BaseScene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {

                // load the textures
                loadTextures();

                // generate a lot of squares
                addWheel(mRandom.nextInt(mDisplaySize.x), mRandom.nextInt(mDisplaySize.y));
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

    private void addWheel(final float x, final float y) {
        mWheel = new Wheel3D();
        float itemSize = -1;

        for (int n = 0; n < mFrameSetNames.length * 2; n++) {
            // create object
            Clip obj = new Clip(mAtlas.getSubFrameSet(mFrameSetNames[n % mFrameSetNames.length]));
            obj.setTexture(mTexture);
            obj.setOriginAtCenter();
            obj.setAlphaTestEnabled(true);
            // obj.stop();

            // add to container
            mWheel.addChild(obj);
            if (itemSize < 0) {
                itemSize = obj.getWidth();
            }
        }
        mWheel.setOrientation(Wheel3D.ORIENTATION_Y);
        mWheel.setRadius(mDisplaySizeDiv2.y - itemSize / 2);
        mWheel.setPosition(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);

        // add to scene
        mScene.addChild(mWheel);
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (mWheel == null) {
            return false;
        }

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        if (action == MotionEvent.ACTION_DOWN) {
            mRegisteredPoint.x = x;
            mRegisteredPoint.y = y;
            mWheel.stop();
        } else if (action == MotionEvent.ACTION_MOVE) {
            mDelta = -(y - mRegisteredPoint.y);
            mWheel.scrollByDistance(-mDelta);
            mRegisteredPoint.x = x;
            mRegisteredPoint.y = y;
        } else if (action == MotionEvent.ACTION_UP) {
            mWheel.spin(mDelta / BaseScene.DEFAULT_MSPF, mDelta > 0 ? -0.002f : 0.002f);
        }

        return true;
    }
}

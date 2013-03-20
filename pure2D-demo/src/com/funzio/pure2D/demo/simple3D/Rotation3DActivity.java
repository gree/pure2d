package com.funzio.pure2D.demo.simple3D;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Playable;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animators.RotateAnimator;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.demo.objects.Bouncer;
import com.funzio.pure2D.geom.Matrix;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Sprite;

public class Rotation3DActivity extends StageActivity {
    private Texture mTexture;
    private float[] mTranformation;

    @Override
    protected int getLayout() {
        return R.layout.stage_simple;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTranformation = new float[16];
        Matrix.perspectiveM(mTranformation, 0, 60, (float) mDisplaySize.x / (float) mDisplaySize.y, 0.001f, 1f);
        // Matrix.frustumM(mTranformation, 0, 0, mDisplaySize.x, 0, mDisplaySize.y, 0.001f, 1000f);
        Matrix.setLookAtM(mTranformation, 0, 0, 0, 1000, 0, 0, 0, 0, 1, 0);
        // Matrix.setRotateM(mTranformation, 0, 45, 0, 0, 1);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {
                // load the textures
                loadTexture();

                // create first obj
                addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
            }
        });
    }

    private void loadTexture() {
        // create texture
        mTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.cc_175, null);
    }

    private void addObject(final float x, final float y) {
        // create object
        Sprite obj = new Bouncer();
        // obj.setColor(new GLColor(1f, mRandom.nextFloat(), mRandom.nextFloat(), mRandom.nextFloat() + 0.5f));
        obj.setTexture(mTexture);

        // center origin
        obj.setOriginAtCenter();

        // set positions
        obj.setPosition(x, y);

        // add to scene
        mScene.addChild(obj);

        // animation
        final RotateAnimator animator = new RotateAnimator(null);
        animator.setDuration(3000);
        animator.setLoop(Playable.LOOP_REPEAT);
        obj.setExtraTransformation(mTranformation);
        obj.setRotationVector(0, 1, 0);
        obj.addManipulator(animator);
        animator.start(360);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    addObject(event.getX(), mDisplaySize.y - event.getY());
                }
            });
        }

        return true;
    }
}

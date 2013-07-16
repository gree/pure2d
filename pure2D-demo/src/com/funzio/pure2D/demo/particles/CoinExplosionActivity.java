package com.funzio.pure2D.demo.particles;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.Animator.AnimatorListener;
import com.funzio.pure2D.animators.TrajectoryAnimator;
import com.funzio.pure2D.atlas.JsonAtlas;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Clip;

public class CoinExplosionActivity extends StageActivity implements AnimatorListener {
    private Texture mTexture;
    private JsonAtlas mAtlas;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {

                // load the textures
                loadTexture();

                // generate a lot of squares
                addSome(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
            }
        });

        try {
            mAtlas = new JsonAtlas(mScene.getAxisSystem());
            mAtlas.load(getAssets(), "atlas/coin_01_60.json", 1);
        } catch (Exception e) {
            Log.e("JsonAtlasActivity", Log.getStackTraceString(e));
        }
    }

    private void loadTexture() {
        // create texture
        mTexture = mScene.getTextureManager().createAssetTexture("atlas/coin_01_60.png", null);
    }

    private void addObject(final float x, final float y) {
        // create object
        Clip obj = new Clip(mAtlas.getMasterFrameSet());
        obj.setTexture(mTexture);
        obj.playAt(mRandom.nextInt(obj.getNumFrames()));
        // obj.setRotation(mRandom.nextInt(360));
        // obj.setFps(30);

        // center origin
        obj.setOriginAtCenter();

        // position
        obj.setPosition(x, y);

        // add to scene
        mScene.addChild(obj);

        // animation
        final TrajectoryAnimator animator = new TrajectoryAnimator(0);
        // animator.setTargetAngleFixed(false);
        // animator.setTargetAngleOffset(-90);
        obj.addManipulator(animator);
        animator.start(mRandom.nextInt(100), (float) (mRandom.nextInt(360) * Math.PI / 180));
        animator.setListener(this);
    }

    private void addSome(final float x, final float y) {
        for (int i = 0; i < 10; i++) {
            addObject(x, y);
        }
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            mStage.queueEvent(new Runnable() {
                @Override
                public void run() {
                    addSome(event.getX(), mDisplaySize.y - event.getY());
                }
            });
        }

        return true;
    }

    @Override
    public void onAnimationEnd(final Animator animator) {
        mStage.queueEvent(new Runnable() {

            @Override
            public void run() {
                ((DisplayObject) animator.getTarget()).removeFromParent();
            }
        });
    }

    @Override
    public void onAnimationUpdate(final Animator animator, final float value) {
        // TODO Auto-generated method stub

    }

}

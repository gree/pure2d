package com.funzio.pure2D.samples.activities.animations;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.funzio.pure2D.Playable;
import com.funzio.pure2D.R;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animators.PathAnimator;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.samples.activities.StageActivity;
import com.funzio.pure2D.shapes.Sprite;

public class PathAnimationActivity extends StageActivity {
    private static final AccelerateInterpolator ACCELERATE = new AccelerateInterpolator();
    private static final DecelerateInterpolator DECELERATE = new DecelerateInterpolator();
    private static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();
    private static final BounceInterpolator BOUNCE = new BounceInterpolator();

    private Texture mTexture;
    private PathAnimator mAnimator = new PathAnimator(null);

    @Override
    protected int getLayout() {
        return R.layout.stage_tween_animations;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAnimator.setDuration(4000);
        mAnimator.setValues(new PointF(), new PointF(0, mDisplaySize.y), new PointF(mDisplaySize.x, 0), new PointF(mDisplaySize.x, mDisplaySize.y), new PointF());

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {

                // load the textures
                loadTexture();

                // generate a lot of squares
                addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
            }
        });
    }

    private void loadTexture() {
        // create texture
        mTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.cc_128, null);
    }

    private void addObject(final float x, final float y) {
        // create object
        Sprite obj = new Sprite();
        obj.setTexture(mTexture);
        // center origin
        obj.setOriginAtCenter();
        // random positions
        obj.setPosition(x, y);

        // animation
        obj.addManipulator(mAnimator);

        // add to scene
        mScene.addChild(obj);
    }

    public void onClickRadio(final View view) {

        switch (view.getId()) {
            case R.id.radio_linear:
                mAnimator.setInterpolator(null);
                break;

            case R.id.radio_accelarate:
                mAnimator.setInterpolator(ACCELERATE);
                break;

            case R.id.radio_decelarate:
                mAnimator.setInterpolator(DECELERATE);
                break;

            case R.id.radio_accelerate_decelarate:
                mAnimator.setInterpolator(ACCELERATE_DECELERATE);
                break;

            case R.id.radio_bounce:
                mAnimator.setInterpolator(BOUNCE);
                break;

            case R.id.radio_once:
                mAnimator.setLoop(Playable.LOOP_NONE);
                break;

            case R.id.radio_repeat:
                mAnimator.setLoop(Playable.LOOP_REPEAT);
                break;

            case R.id.radio_reverse:
                mAnimator.setLoop(Playable.LOOP_REVERSE);
                break;
        }

        mAnimator.stop();
        mAnimator.start();
    }
}

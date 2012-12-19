package com.funzio.pure2D.samples.activities.animations;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;

import com.funzio.pure2D.Playable;
import com.funzio.pure2D.R;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animators.TweenAnimator;
import com.funzio.pure2D.effects.trails.MotionTrailShape;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.samples.activities.StageActivity;
import com.funzio.pure2D.shapes.Sprite;

public abstract class AnimationActivity extends StageActivity {
    protected static final AccelerateInterpolator ACCELERATE = new AccelerateInterpolator();
    protected static final DecelerateInterpolator DECELERATE = new DecelerateInterpolator();
    protected static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();
    protected static final BounceInterpolator BOUNCE = new BounceInterpolator();
    protected static final int OBJ_SIZE = 128;

    protected Texture mTexture;
    protected TweenAnimator mAnimator;
    protected MotionTrailShape mMotionTrail;

    @Override
    protected int getLayout() {
        return R.layout.stage_tween_animations;
    }

    abstract protected TweenAnimator createAnimator();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAnimator = createAnimator();

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

        // motion trail
        mMotionTrail = new MotionTrailShape();
        mMotionTrail.setNumPoints(10);
        mMotionTrail.setStrokeRange(10, 1);
        mMotionTrail.setTarget(obj);
        mScene.addChild(mMotionTrail);
    }

    public void onClickRadio(final View view) {

        boolean restart = true;

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

            case R.id.cb_motion_trail:
                mMotionTrail.setVisible(((CheckBox) view).isChecked());
                restart = false;
                break;
        }

        if (restart) {
            mAnimator.start();
        }
    }
}

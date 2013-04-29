package com.funzio.pure2D.demo.animations;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;

import com.funzio.pure2D.Playable;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.TweenAnimator;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.effects.trails.MotionTrailShape;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Sprite;

public abstract class AnimationActivity extends StageActivity {
    protected static final AccelerateInterpolator ACCELERATE = new AccelerateInterpolator();
    protected static final DecelerateInterpolator DECELERATE = new DecelerateInterpolator();
    protected static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();
    protected static final BounceInterpolator BOUNCE = new BounceInterpolator();
    protected static final int OBJ_SIZE = 128;

    protected Texture mTexture;
    protected Sprite mSprite;
    protected Animator mAnimator;
    protected MotionTrailShape mMotionTrail;

    @Override
    protected int getLayout() {
        return R.layout.stage_tween_animations;
    }

    abstract protected Animator createAnimator();

    protected void setAnimator(final Animator animator) {

        mAnimator = animator;

        if (mSprite != null) {
            mScene.queueEvent(new Runnable() {

                @Override
                public void run() {
                    mSprite.removeAllManipulators();
                    mSprite.addManipulator(mAnimator);
                }
            });
        }
    }

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
                mSprite = addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
            }
        });
    }

    private void loadTexture() {
        // create texture
        mTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.cc_128, null);
    }

    private Sprite addObject(final float x, final float y) {
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
        mMotionTrail.setNumPoints(15);
        mMotionTrail.setStrokeRange(10, 1);
        mMotionTrail.setStrokeColors(new GLColor(1f, 0, 0, 1f), new GLColor(1f, 0, 0, 0.5f));
        mMotionTrail.setTarget(obj);
        mScene.addChild(mMotionTrail);

        return obj;
    }

    public void onClickRadio(final View view) {

        boolean restart = true;

        if (mAnimator instanceof TweenAnimator) {
            final TweenAnimator tween = (TweenAnimator) mAnimator;

            // tween specific switches
            switch (view.getId()) {
                case R.id.radio_linear:
                    tween.setInterpolator(null);
                    break;

                case R.id.radio_accelarate:
                    tween.setInterpolator(ACCELERATE);
                    break;

                case R.id.radio_decelarate:
                    tween.setInterpolator(DECELERATE);
                    break;

                case R.id.radio_accelerate_decelarate:
                    tween.setInterpolator(ACCELERATE_DECELERATE);
                    break;

                case R.id.radio_bounce:
                    tween.setInterpolator(BOUNCE);
                    break;

                case R.id.radio_once:
                    tween.setLoop(Playable.LOOP_NONE);
                    break;

                case R.id.radio_repeat:
                    tween.setLoop(Playable.LOOP_REPEAT);
                    break;

                case R.id.radio_reverse:
                    tween.setLoop(Playable.LOOP_REVERSE);
                    break;
            }
        }

        // general switches
        switch (view.getId()) {
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

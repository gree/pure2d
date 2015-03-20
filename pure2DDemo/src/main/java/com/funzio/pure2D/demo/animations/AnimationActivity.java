/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package com.funzio.pure2D.demo.animations;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;

import com.funzio.pure2D.Playable;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.TweenAnimator;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.effects.trails.MotionTrailShape;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Sprite;
import com.longo.pure2D.demo.R;

public abstract class AnimationActivity extends StageActivity {
    public static final AccelerateInterpolator ACCELERATE = new AccelerateInterpolator();
    public static final DecelerateInterpolator DECELERATE = new DecelerateInterpolator();
    public static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();
    public static final BounceInterpolator BOUNCE = new BounceInterpolator();
    public static final AnticipateInterpolator ANTICIPATE = new AnticipateInterpolator();
    public static final AnticipateOvershootInterpolator ANTICIPATE_OVERSHOOT = new AnticipateOvershootInterpolator();
    public static final OvershootInterpolator OVERSHOOT = new OvershootInterpolator();
    public static final CycleInterpolator CYCLE = new CycleInterpolator(1);
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
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {

                if (firstTime) {
                    // load the textures
                    loadTexture();

                    // generate a lot of squares
                    mSprite = addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                }
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

                case R.id.radio_anticipate:
                    tween.setInterpolator(ANTICIPATE);
                    break;

                case R.id.radio_anticipate_overshoot:
                    tween.setInterpolator(ANTICIPATE_OVERSHOOT);
                    break;

                case R.id.radio_overshoot:
                    tween.setInterpolator(OVERSHOOT);
                    break;

                case R.id.radio_cycle:
                    tween.setInterpolator(CYCLE);
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

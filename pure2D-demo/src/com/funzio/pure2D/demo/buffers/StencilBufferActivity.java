package com.funzio.pure2D.demo.buffers;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.Playable;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animators.RotateAnimator;
import com.funzio.pure2D.containers.MaskGroup;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.StencilEGLConfig;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Rectangular;
import com.funzio.pure2D.shapes.Sprite;

public class StencilBufferActivity extends StageActivity {
    private Texture mTexture;
    private MaskGroup mMask;
    private CheckBox mCBMasking;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCBMasking = (CheckBox) findViewById(R.id.cb_masking);

        mScene.setColor(COLOR_GREEN);
        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {
                loadTexture();
                createMask();
                // addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                for (int i = 0; i < 500; i++) {
                    addObject(RANDOM.nextInt(mDisplaySize.x), RANDOM.nextInt(mDisplaySize.y));
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.demo.activities.StageActivity#getLayout()
     */
    @Override
    protected int getLayout() {
        return R.layout.stage_masking;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.samples.activities.StageActivity#createScene()
     */
    @Override
    protected BaseScene createScene() {
        // NOTE: this is needed for some certain devices such as Galaxy Tab
        // and need to be set before Scene assignment
        mStage.setEGLConfigChooser(new StencilEGLConfig());
        return super.createScene();
    }

    private void createMask() {
        mMask = new MaskGroup();
        mMask.setRenderChildren(true); // for testing only
        mMask.setPosition(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
        mScene.addChild(mMask);

        // create a rect for the mask group
        Rectangular rect1 = new Rectangular();
        rect1.setColor(new GLColor(0, 0, 0, 0.5f));
        rect1.setSize(mDisplaySize.x, mDisplaySize.x);
        rect1.setOriginAtCenter();
        mMask.addChild(rect1);

        Rectangular rect2 = new Rectangular();
        rect2.setColor(new GLColor(0, 0, 0, 0.5f));
        rect2.setSize(mDisplaySize.x, mDisplaySize.x);
        rect2.setOriginAtCenter();
        rect2.rotate(45);
        mMask.addChild(rect2);

        // rotating
        RotateAnimator animator = new RotateAnimator(null);
        animator.setLoop(Playable.LOOP_REPEAT);
        animator.setDuration(5000);
        mMask.addManipulator(animator);
        animator.start(360);
    }

    private void loadTexture() {
        // create texture
        mTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.cc_175, null);
    }

    private void addObject(final float x, final float y) {
        // create object
        final Sprite obj = new Sprite();
        obj.setTexture(mTexture);
        obj.setMask(mCBMasking.isChecked() ? mMask : null);

        // center origin
        obj.setOriginAtCenter();

        // set positions
        obj.setPosition(x, y);

        // add to scene
        mScene.addChild(obj);
    }

    public void onClickMasking(final View view) {
        // need to queue the in GL Thread!
        mScene.queueEvent(new Runnable() {

            @Override
            public void run() {
                final int num = mScene.getNumChildren();
                for (int n = 0; n < num; n++) {
                    if (mScene.getChildAt(n) instanceof Sprite) {
                        Sprite obj = (Sprite) mScene.getChildAt(n);
                        obj.setMask(mCBMasking.isChecked() ? mMask : null);
                    }
                }
            }
        });
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

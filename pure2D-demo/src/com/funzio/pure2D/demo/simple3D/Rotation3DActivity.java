package com.funzio.pure2D.demo.simple3D;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.Playable;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animators.RotateAnimator;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Sprite;

public class Rotation3DActivity extends StageActivity {
    private Texture mTexture;
    private CheckBox mCBPerspective;

    @Override
    protected int getLayout() {
        return R.layout.stage_perspective;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCBPerspective = (CheckBox) findViewById(R.id.cb_perspective);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {
                // need more depth for perspective projection
                mScene.setDepthRange(1, 100);

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
        Sprite obj = new Sprite();
        obj.setTexture(mTexture);

        // center origin
        obj.setOriginAtCenter();
        // obj.setPivotAtCenter();
        obj.setPerspectiveEnabled(mCBPerspective.isChecked());

        // set positions
        obj.setPosition(x, y);

        // add to scene
        mScene.addChild(obj);

        // debug
        // obj.setAutoUpdateBounds(true);
        // obj.setDebugFlags(Pure2D.DEBUG_FLAG_GLOBAL_BOUNDS);

        // animation
        final RotateAnimator animator = new RotateAnimator(null);
        animator.setDuration(3000);
        animator.setLoop(Playable.LOOP_REPEAT);
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

    public void onClickPerspective(final View view) {
        final int num = mScene.getNumChildren();
        for (int i = 0; i < num; i++) {
            mScene.getChildAt(i).setPerspectiveEnabled(mCBPerspective.isChecked());
        }
    }
}

package com.funzio.pure2D.demo.simple3D;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.PerspectiveCamera;
import com.funzio.pure2D.Playable;
import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animators.RotateAnimator;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Sprite;

public class Rotation3DActivity extends StageActivity {
    private Texture mTexture;
    private CheckBox mCBPerspective;
    private PerspectiveCamera mCamera;

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
                mCamera = new PerspectiveCamera(new PointF(mDisplaySizeDiv2), new PointF(mDisplaySize));
                mScene.setCamera(mCamera);
                // need more depth for better z-sorting
                mScene.setDepthRange(1, mCamera.getZFar());

                // load the textures
                loadTexture();

                // create first obj
                addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                // testTiles();
            }
        });
    }

    private void loadTexture() {
        // create texture
        mTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.cc_175, null);
    }

    protected void testTiles() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                // create object
                Sprite obj = new Sprite();
                obj.setPosition(j * 101, i * 101);
                // obj.setPerspectiveEnabled(mCBPerspective.isChecked());
                obj.setSize(100, 100);
                // obj.setOriginAtCenter();
                mScene.addChild(obj);

                // animation
                final RotateAnimator animator = new RotateAnimator(null);
                animator.setDuration(3000);
                animator.setLoop(Playable.LOOP_REPEAT);
                obj.setRotationVector(0, 1, 0);
                obj.addManipulator(animator);
                animator.start(360);
            }
        }
    }

    private void addObject(final float x, final float y) {
        // create object
        Sprite obj = new Sprite();
        obj.setTexture(mTexture);

        obj.setOriginAtCenter();
        // obj.setPivotAtCenter();
        // obj.setPerspectiveEnabled(mCBPerspective.isChecked());

        // set positions
        obj.setPosition(x, y);

        // add to scene
        mScene.addChild(obj);

        // debug
        obj.setAutoUpdateBounds(true);
        obj.setDebugFlags(Pure2D.DEBUG_FLAG_GLOBAL_BOUNDS);

        // animation
        final RotateAnimator animator = new RotateAnimator(null);
        animator.setDuration(3000);
        animator.setLoop(Playable.LOOP_REPEAT);
        obj.setRotationVector(0, 1, 0);
        obj.addManipulator(animator);
        animator.start(360);

        // TornadoAnimator ta = new TornadoAnimator(null);
        // ta.setZEnabled(true);
        // ta.setLoop(LoopModes.LOOP_REPEAT);
        // ta.setDuration(10000);
        // ta.setCircles(300, 10, TornadoAnimator.DEFAULT_CIRCLE_RATIO, null);
        // ta.start(x, y - 200, x, y + 300);
        // MotionTrailShape3D trail = new MotionTrailShape3D();
        // trail.setStrokeRange(20, 20);
        // trail.addManipulator(ta);
        // trail.setPerspectiveEnabled(true);
        // trail.setPointsAt(x, y, 0);
        // mScene.addChild(trail);
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
        // final int num = mScene.getNumChildren();
        // for (int i = 0; i < num; i++) {
        // mScene.getChildAt(i).setPerspectiveEnabled(mCBPerspective.isChecked());
        // }

        // need to queue the in GL Thread!
        mScene.queueEvent(new Runnable() {

            @Override
            public void run() {
                mScene.setCamera(mCBPerspective.isChecked() ? mCamera : null);
            }
        });
    }
}

package com.funzio.pure2D.demo.particles;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.Animator.AnimatorListener;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.particles.nova.NovaLoader;
import com.funzio.pure2D.particles.nova.vo.NovaVO;

public class NovaActivity extends StageActivity implements AnimatorListener {
    protected static final String TAG = NovaActivity.class.getSimpleName();

    private static final String NOVA_DIR = "nova/";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static {
        OBJECT_MAPPER.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private String mFilePath;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFilePath = NOVA_DIR + getIntent().getExtras().getString("text");
        NovaLoader loader = new NovaLoader();
        loader.setListener(new NovaLoader.Listener() {

            @Override
            public void onLoad(final NovaLoader loader, final NovaVO vo) {
                Log.d(TAG, vo.toString());
            }

            @Override
            public void onError(final NovaLoader loader) {
                // TODO Auto-generated method stub

            }
        });
        loader.load(getAssets(), mFilePath, OBJECT_MAPPER);

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
    }

    private void loadTexture() {
        // create texture
    }

    private void addObject(final float x, final float y) {
        // // create object
        // Clip obj = new Clip(mAtlas.getMasterFrameSet());
        // obj.setTexture(mTexture);
        // obj.playAt(mRandom.nextInt(obj.getNumFrames()));
        // // obj.setRotation(mRandom.nextInt(360));
        // // obj.setFps(30);
        //
        // // center origin
        // obj.setOriginAtCenter();
        //
        // // position
        // obj.setPosition(x, y);
        //
        // // add to scene
        // mScene.addChild(obj);
        //
        // // animation
        // final TrajectoryAnimator animator = new TrajectoryAnimator(0);
        // // animator.setTargetAngleFixed(false);
        // // animator.setTargetAngleOffset(-90);
        // obj.addManipulator(animator);
        // animator.start(mRandom.nextInt(100), (float) (mRandom.nextInt(360) * Math.PI / 180));
        // animator.setListener(this);
    }

    private void addSome(final float x, final float y) {
        for (int i = 0; i < 10; i++) {
            addObject(x, y);
        }
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
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

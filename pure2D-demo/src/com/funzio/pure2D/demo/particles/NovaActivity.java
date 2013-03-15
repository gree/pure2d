package com.funzio.pure2D.demo.particles;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.Animator.AnimatorListener;
import com.funzio.pure2D.atlas.AtlasFrameSet;
import com.funzio.pure2D.atlas.SingleFrameSet;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.textures.AssetTexture;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.particles.nova.NovaEmitter;
import com.funzio.pure2D.particles.nova.NovaFactory;
import com.funzio.pure2D.particles.nova.NovaFactory.SpriteDelegator;
import com.funzio.pure2D.particles.nova.NovaLoader;
import com.funzio.pure2D.particles.nova.vo.NovaVO;

public class NovaActivity extends StageActivity implements AnimatorListener {
    private static final String TAG = NovaActivity.class.getSimpleName();
    private static final String NOVA_DIR = "nova/";

    private String mFilePath;

    private SingleFrameSet mSmokeFrame;
    private SingleFrameSet mFireFrame;
    private NovaFactory mNovaFactory;

    private SpriteDelegator mSpriteDelegator = new SpriteDelegator() {

        @Override
        public AtlasFrameSet getFrameSet(final String name) {
            // null check
            if (name == null) {
                return null;
            }

            if (name.equals("smoke")) {
                return mSmokeFrame;
            } else {
                return mFireFrame;
            }
        }
    };

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFilePath = NOVA_DIR + getIntent().getExtras().getString("text");
        mScene.setColor(new GLColor(0, 0.7f, 0, 1));

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {

                // load the textures
                loadTextures();

                // generate a lot of squares
                // addSome(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);

                NovaLoader loader = new NovaLoader(new NovaLoader.Listener() {

                    @Override
                    public void onLoad(final NovaLoader loader, final NovaVO vo) {
                        Log.d(TAG, vo.toString());
                        mNovaFactory = new NovaFactory(vo, mSpriteDelegator, 500);
                        addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                    }

                    @Override
                    public void onError(final NovaLoader loader) {
                        // TODO Auto-generated method stub

                    }
                });

                // load the file
                loader.load(getAssets(), mFilePath);
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.samples.activities.StageActivity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();

        if (mNovaFactory != null) {
            mNovaFactory.dispose();
        }
    }

    private void loadTextures() {
        TextureOptions options = TextureOptions.getDefault();
        options.inMipmaps = 1;

        // create textures
        AssetTexture smokeTexture = new AssetTexture(mScene.getGLState(), getAssets(), "nova/smoke.png", options);
        mSmokeFrame = new SingleFrameSet("smoke", smokeTexture);

        AssetTexture fireTexture = new AssetTexture(mScene.getGLState(), getAssets(), "nova/fire.png", options);
        mFireFrame = new SingleFrameSet("fire", fireTexture);
    }

    private void addObject(final float x, final float y) {
        // null check
        if (mNovaFactory == null) {
            return;
        }

        List<NovaEmitter> emitters = mNovaFactory.createEmitters(new PointF(x, y));
        for (NovaEmitter emitter : emitters) {
            mScene.addChild(emitter);
        }
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {
                @Override
                public void run() {
                    addObject(event.getX(), mDisplaySize.y - event.getY());
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

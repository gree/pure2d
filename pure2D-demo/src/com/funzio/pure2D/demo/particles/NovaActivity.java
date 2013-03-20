package com.funzio.pure2D.demo.particles;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.atlas.AtlasFrameSet;
import com.funzio.pure2D.atlas.JsonAtlas;
import com.funzio.pure2D.atlas.SingleFrameSet;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.particles.nova.NovaEmitter;
import com.funzio.pure2D.particles.nova.NovaFactory;
import com.funzio.pure2D.particles.nova.NovaFactory.SpriteDelegator;
import com.funzio.pure2D.particles.nova.NovaLoader;
import com.funzio.pure2D.particles.nova.vo.NovaVO;

public class NovaActivity extends StageActivity {
    private static final String TAG = NovaActivity.class.getSimpleName();
    private static final String NOVA_DIR = "nova/";

    private SpriteDelegator mSpriteDelegator = new SpriteDelegator() {

        @Override
        public AtlasFrameSet getFrameSet(final String name) {
            // null check
            if (name == null) {
                return null;
            }

            if (name.equals("smoke")) {
                return mSmokeFrame;
            } else if (name.equalsIgnoreCase("fire")) {
                return mFireFrame;
            } else if (name.equalsIgnoreCase("side_platform_glow")) {
                return mSidePlatformGlow;
            } else if (name.equalsIgnoreCase("side_ground_flare")) {
                return mSideGroundFlare;
            } else if (name.equalsIgnoreCase("middle_flare_static")) {
                return mMiddleFlareStatic;
            } else if (name.equalsIgnoreCase("glitter")) {
                return mGlitter;
            } else if (name.equalsIgnoreCase("star")) {
                return mStarAtlas.getMasterFrameSet();
            } else {
                return null;
            }
        }
    };

    private String mFilePath;

    private JsonAtlas mStarAtlas;
    private SingleFrameSet mSmokeFrame;
    private SingleFrameSet mFireFrame;
    private SingleFrameSet mSidePlatformGlow;
    private SingleFrameSet mSideGroundFlare;
    private NovaFactory mNovaFactory;
    private SingleFrameSet mMiddleFlareStatic;
    private SingleFrameSet mGlitter;

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
                loader.loadAsync(getAssets(), mFilePath);
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

    /*
     * (non-Javadoc)
     * @see android.app.Activity#finish()
     */
    @Override
    public void finish() {
        super.finish();

        Log.d(TAG, mScene.getTrace());
    }

    private void loadTextures() {
        TextureOptions options = TextureOptions.getDefault();
        options.inMipmaps = 1;

        // create textures
        mSmokeFrame = new SingleFrameSet("smoke", mScene.getTextureManager().createAssetTexture("nova/smoke.png", options));

        mFireFrame = new SingleFrameSet("fire", mScene.getTextureManager().createAssetTexture("nova/fire.png", options));

        mSideGroundFlare = new SingleFrameSet("a", mScene.getTextureManager().createAssetTexture("nova/side_ground_flare.png", options));
        mSidePlatformGlow = new SingleFrameSet("b", mScene.getTextureManager().createAssetTexture("nova/side_platform_glow.png", options));
        mMiddleFlareStatic = new SingleFrameSet("c", mScene.getTextureManager().createAssetTexture("nova/middle_flare_static.png", options));
        mGlitter = new SingleFrameSet("c", mScene.getTextureManager().createAssetTexture("nova/glitter.png", options));

        // star texture and atlas
        try {
            mStarAtlas = new JsonAtlas(getAssets(), "atlas/star_03_60.json", 1);
            mStarAtlas.getMasterFrameSet().setTexture(mScene.getTextureManager().createAssetTexture("atlas/star_03_60.png", options));
        } catch (Exception e) {
            Log.e(TAG, "Load Error: ", e);
        }
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
        final int action = event.getAction() & MotionEvent.ACTION_MASK;

        if (action == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {
                @Override
                public void run() {
                    final int pointerCount = event.getPointerCount();
                    for (int i = 0; i < pointerCount; i++) {
                        addObject(event.getX(i), mDisplaySize.y - event.getY(i));
                    }
                }
            });
        }

        return true;
    }

}

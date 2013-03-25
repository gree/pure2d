package com.funzio.pure2D.demo.particles;

import java.io.IOException;
import java.util.HashMap;
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
    private static final String NOVA_DIR = "nova";

    private SpriteDelegator mSpriteDelegator = new SpriteDelegator() {

        @Override
        public AtlasFrameSet getFrameSet(final String name) {
            return name == null ? null : mFileToFrameMap.get(name);
        }

    };

    private HashMap<String, AtlasFrameSet> mFileToFrameMap = new HashMap<String, AtlasFrameSet>();

    private NovaFactory mNovaFactory;

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

                // load the json file
                loader.loadAsync(getAssets(), NOVA_DIR + "/" + getIntent().getExtras().getString("text"));
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

        try {
            String[] files = getAssets().list(NOVA_DIR);
            for (String file : files) {
                if (file.contains(".png")) {
                    final SingleFrameSet frameSet = new SingleFrameSet(file, mScene.getTextureManager().createAssetTexture(NOVA_DIR + "/" + file, options));

                    // map it
                    mFileToFrameMap.put(file, frameSet);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "File Error:", e);
        }

        try {
            // star texture and atlas
            final JsonAtlas atlas = new JsonAtlas(getAssets(), "atlas/star_03_60.json", 1);
            atlas.getMasterFrameSet().setTexture(mScene.getTextureManager().createAssetTexture("atlas/star_03_60.png", options));

            // map it
            mFileToFrameMap.put("star.json", atlas.getMasterFrameSet());
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

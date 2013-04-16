package com.funzio.pure2D.demo.particles;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
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

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.demo.activities.StageActivity#getLayout()
     */
    @Override
    protected int getLayout() {
        return R.layout.stage_bg_colors;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {

                final NovaLoader loader = new NovaLoader(new NovaLoader.Listener() {

                    @Override
                    public void onLoad(final NovaLoader loader, final String filePath, final NovaVO vo) {
                        Log.d(TAG, vo.toString());
                        mNovaFactory = new NovaFactory(vo, mSpriteDelegator, 500);

                        // load textures on GL thread
                        mScene.queueEvent(new Runnable() {

                            @Override
                            public void run() {
                                // load the textures
                                loadTextures();

                                // sample object
                                addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                            }
                        });
                    }

                    @Override
                    public void onError(final NovaLoader loader, final String filePath) {
                        Log.e(TAG, "Nova Loading Error! " + filePath);
                    }
                });

                // load asynchronously the json file, some old Android requires this to run on UI Thread
                runOnUiThread(new Runnable() {
                    public void run() {
                        loader.loadAsync(getAssets(), NOVA_DIR + "/" + getIntent().getExtras().getString("text"));
                    }
                });

                // Or load synchronously
                // loader.load(getAssets(), NOVA_DIR + "/" + getIntent().getExtras().getString("text"));
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
        final TextureOptions options = TextureOptions.getDefault();
        options.inMipmaps = 1;

        // find and load the textures being used by the json file
        Set<String> files = mNovaFactory.getNovaVO().getUsedSprites();
        for (String file : files) {
            Log.v(TAG, "Loading sprite: " + file);

            if (file.contains(".json")) {
                // load json atlas and texture
                try {
                    final JsonAtlas atlas = new JsonAtlas(getAssets(), file, 1);
                    atlas.getMasterFrameSet().setTexture(mScene.getTextureManager().createAssetTexture(file.replace(".json", ".png"), options));

                    // map it
                    mFileToFrameMap.put(file, atlas.getMasterFrameSet());
                } catch (Exception e) {
                    Log.e(TAG, "Load Error: ", e);
                }
            } else {
                // just load a single frame texture
                final SingleFrameSet frameSet = new SingleFrameSet(file, mScene.getTextureManager().createAssetTexture(NOVA_DIR + "/" + file, options));
                // map it
                mFileToFrameMap.put(file, frameSet);
            }
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

    public void onClickRadio(final View view) {

        mScene.queueEvent(new Runnable() {

            @Override
            public void run() {
                switch (view.getId()) {
                    case R.id.radio_black:
                        mScene.setColor(COLOR_BLACK);
                        break;

                    case R.id.radio_gray:
                        mScene.setColor(COLOR_GRAY);
                        break;

                    case R.id.radio_white:
                        mScene.setColor(COLOR_WHITE);
                        break;

                    case R.id.radio_red:
                        mScene.setColor(COLOR_RED);
                        break;

                    case R.id.radio_green:
                        mScene.setColor(COLOR_GREEN);
                        break;

                    case R.id.radio_blue:
                        mScene.setColor(COLOR_BLUE);
                        break;
                }
            }
        });

    }

}

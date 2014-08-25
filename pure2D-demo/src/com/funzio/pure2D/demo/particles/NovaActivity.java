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
package com.funzio.pure2D.demo.particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONException;

import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.atlas.AtlasFrameSet;
import com.funzio.pure2D.atlas.JsonAtlas;
import com.funzio.pure2D.atlas.SingleFrameSet;
import com.funzio.pure2D.demo.activities.MenuActivity;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.particles.nova.NovaConfig;
import com.funzio.pure2D.particles.nova.NovaDelegator;
import com.funzio.pure2D.particles.nova.NovaEmitter;
import com.funzio.pure2D.particles.nova.NovaFactory;
import com.funzio.pure2D.particles.nova.NovaLoader;
import com.funzio.pure2D.particles.nova.NovaParticle;
import com.funzio.pure2D.particles.nova.vo.NovaVO;
import com.longo.pure2D.demo.R;

public class NovaActivity extends StageActivity {
    private static final String TAG = NovaActivity.class.getSimpleName();
    private static final String NOVA_DIR = "nova";

    private NovaDelegator mNovaDelegator = new NovaDelegator() {

        @Override
        public void delegateEmitter(final NovaEmitter emitter, final Object... params) {
            // Nothing now
        }

        @Override
        public void delegateParticle(final NovaParticle particle, final Object... params) {
            final String sprite = NovaConfig.getString(particle.getParticleVO().sprite, -1);

            // apply the frameset
            particle.setAtlasFrameSet(sprite == null ? null : mFileToFrameMap.get(sprite));
        }

    };

    private HashMap<String, AtlasFrameSet> mFileToFrameMap = new HashMap<String, AtlasFrameSet>();

    private NovaFactory mNovaFactory;
    private ScrollView mScrollView;
    private TextView mTextView;

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    @Override
    protected int getLayout() {
        return R.layout.stage_nova;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScrollView = (ScrollView) findViewById(R.id.sv_code);
        mTextView = (TextView) mScrollView.findViewById(R.id.tv_code);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {

                    final NovaLoader loader = new NovaLoader(new NovaLoader.Listener() {

                        @Override
                        public void onLoad(final NovaLoader loader, final String filePath, final NovaVO vo) {
                            Log.d(TAG, vo.toString());
                            mNovaFactory = new NovaFactory(vo, mNovaDelegator, 500);

                            // display the code
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        mTextView.setText(vo.getSource().toString(3));
                                    } catch (JSONException e) {
                                    }
                                }
                            });

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
                            loader.loadAsync(getAssets(), NOVA_DIR + "/" + getIntent().getExtras().getString(MenuActivity.EXTRA_TAG));
                        }
                    });

                    // Or load synchronously
                    // loader.load(getAssets(), NOVA_DIR + "/" + getIntent().getExtras().getString("text"));
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mNovaFactory != null) {
            mNovaFactory.dispose();
        }
    }

    @Override
    public void finish() {
        super.finish();

        // Log.d(TAG, mScene.getObjectCounts());
    }

    private void loadTextures() {
        final TextureOptions options = TextureOptions.getDefault();
        options.inMipmaps = 1; // better performance for scaling

        // find and load the textures being used by the json file
        Set<String> files = mNovaFactory.getNovaVO().getUsedSprites();
        for (String file : files) {
            Log.v(TAG, "Loading sprite: " + file);

            if (file.contains(".json")) {
                // load json atlas and texture
                try {
                    final JsonAtlas atlas = new JsonAtlas(mScene.getAxisSystem());
                    atlas.load(getAssets(), file, 1);
                    atlas.getMasterFrameSet().setTexture(mScene.getTextureManager().createAssetTexture(file.replace(".json", ".png"), options));

                    // map it
                    mFileToFrameMap.put(file, atlas.getMasterFrameSet());
                } catch (Exception e) {
                    Log.e(TAG, "Load Error: ", e);
                }
            } else {
                // just load a single frame texture
                final SingleFrameSet frameSet = new SingleFrameSet(file, mScene.getTextureManager().createAssetTexture(file, options));
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

        ArrayList<NovaEmitter> emitters = mNovaFactory.createEmitters(new PointF(x, y));
        for (NovaEmitter emitter : emitters) {
            mScene.addChild(emitter);
        }
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction() & MotionEvent.ACTION_MASK;

        // null check
        if (mNovaFactory == null) {
            return false;
        }

        if (action == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {
                @Override
                public void run() {
                    final int pointerCount = event.getPointerCount();
                    for (int i = 0; i < pointerCount; i++) {
                        // for demo, limit the number of emitters
                        if (mScene.getNumGrandChildren() < mNovaFactory.getPoolSize()) {
                            addObject(event.getX(i), mDisplaySize.y - event.getY(i));
                        }
                    }
                }
            });
        }

        return true;
    }

    public void onClickCode(final View view) {
        final int visibility = mScrollView.getVisibility();
        mScrollView.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
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

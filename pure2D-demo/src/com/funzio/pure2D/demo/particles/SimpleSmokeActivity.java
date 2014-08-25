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
import java.util.List;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.longo.pure2D.demo.R;

public class SimpleSmokeActivity extends StageActivity {

    private List<SimpleSmoke> mEmitters = new ArrayList<SimpleSmoke>();
    private Texture mTexture;
    private boolean mUseTexture = true;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScene.setColor(COLOR_GREEN);
        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    loadTexture();
                    addEmitter(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#finish()
     */
    @Override
    public void finish() {
        super.finish();

        // clear the pool
        SimpleSmokeParticle.clearPool();
    }

    @Override
    protected int getLayout() {
        return R.layout.stage_textures;
    }

    private void loadTexture() {
        TextureOptions options = TextureOptions.getDefault();
        options.inMipmaps = 1;
        // smoke
        mTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.smoke_small, options);
    }

    private void addEmitter(final float x, final float y) {
        SimpleSmoke emitter = new SimpleSmoke(50, 100);
        emitter.setParticleTexture(mTexture);
        emitter.setParticleTextureEnabled(mUseTexture);
        emitter.setPosition(x, y);

        // add to scene
        mScene.addChild(emitter);

        // for ref
        mEmitters.add(emitter);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    if (event.getPointerCount() == 1) {
                        // add
                        addEmitter(event.getX(), mDisplaySize.y - event.getY());
                    } else if (event.getPointerCount() == 2 && mEmitters.size() > 0) {
                        // remove
                        SimpleSmoke emitter = mEmitters.remove(mEmitters.size() - 1);
                        mScene.removeChild(emitter);
                    }
                }
            });
        }

        return true;
    }

    public void onClickTextures(final View view) {
        if (view.getId() == R.id.cb_textures) {
            mUseTexture = ((CheckBox) findViewById(R.id.cb_textures)).isChecked();
            for (SimpleSmoke emitter : mEmitters) {
                emitter.setParticleTextureEnabled(mUseTexture);
            }
        }
    }
}

package com.funzio.pure2D.samples.activities.particles;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.R;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.samples.activities.StageActivity;

public class SimpleSmokeActivity extends StageActivity {

    private List<SimpleSmoke> mEmitters = new ArrayList<SimpleSmoke>();
    private Texture mTexture;
    private boolean mUseTexture = true;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScene.setColor(new GLColor(0, 0.7f, 0, 1));
        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {
                loadTexture();
                addEmitter(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.stage_textures;
    }

    private void loadTexture() {
        TextureOptions options = TextureOptions.getDefault();
        options.inMipmaps = 1;
        // smoke
        mTexture = mScene.getTextureManager().createTexture(R.drawable.smoke_small, options);
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

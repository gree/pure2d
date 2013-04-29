package com.funzio.pure2D.demo.particles;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;

public class DynamicEmitterActivity extends StageActivity {
    public static final int NUM = 3;

    private Texture mSmokeTexture;
    private Texture mFireTexture;
    private ArrayList<DynamicEmitter> mEmitters = new ArrayList<DynamicEmitter>();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // mScene.setColor(COLOR_GREEN);
        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {
                loadTextures();

                // create emitters
                DynamicEmitter emitter = new DynamicEmitter(mDisplaySize, mFireTexture, mSmokeTexture);
                mScene.addChild(emitter);
                mEmitters.add(emitter);

                emitter = new DynamicEmitter(mDisplaySize, mFireTexture, mSmokeTexture);
                emitter.setType(2);
                mScene.addChild(emitter);
                mEmitters.add(emitter);

                emitter = new DynamicEmitter(mDisplaySize, mFireTexture, mSmokeTexture);
                emitter.setType(3);
                mScene.addChild(emitter);
                mEmitters.add(emitter);
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
        Particle1.clearPool();
    }

    @Override
    protected int getLayout() {
        return R.layout.stage_bg_colors;
    }

    private void loadTextures() {
        TextureOptions options = TextureOptions.getDefault();
        options.inMipmaps = 1;

        // smoke
        mSmokeTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.smoke_small, options);

        // fire
        mFireTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.fireball_small, options);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            for (int i = 0; i < NUM; i++) {
                DynamicEmitter emitter = mEmitters.get(i);
                emitter.setDestination(event.getX(), mDisplaySize.y - event.getY());
                emitter.lockDestination(true);
            }
        } else if (action == MotionEvent.ACTION_UP) {
            for (int i = 0; i < NUM; i++) {
                DynamicEmitter emitter = mEmitters.get(i);
                emitter.lockDestination(false);
            }
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

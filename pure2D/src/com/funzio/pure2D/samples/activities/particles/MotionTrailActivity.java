package com.funzio.pure2D.samples.activities.particles;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.R;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.gl10.BlendFunc;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.samples.activities.StageActivity;

public class MotionTrailActivity extends StageActivity {
    private static final BlendFunc BLEND_FUNC_1 = new BlendFunc(GL10.GL_ONE, GL10.GL_ONE);
    private static final BlendFunc BLEND_FUNC_2 = new BlendFunc(GL10.GL_SRC_COLOR, GL10.GL_ONE_MINUS_SRC_ALPHA);

    private Texture mSmokeTexture;
    private Texture mFireTexture;
    private int mType = 0;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // mScene.setColor(new GLColor(0, 0.7f, 0, 1));
        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {
                loadTextures();
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
        return R.layout.stage_particle_trails;
    }

    private void loadTextures() {
        TextureOptions options = TextureOptions.getDefault();
        options.inMipmaps = 1;

        // smoke
        mSmokeTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.smoke_small, options);

        // fire
        mFireTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.fireball_small, options);
    }

    private void addParticles(final float x, final float y) {
        Particle1 particle = null;
        if (mType <= 1) {
            particle = Particle1.create();
            particle.setTexture(mSmokeTexture);
            particle.setBlendFunc(BLEND_FUNC_1);
            particle.setSize(Particle1.SIZE_BIG, Particle1.SIZE_BIG);
            particle.setColor(Particle1.RED);
            particle.start1(x, y);
            mScene.addChild(particle);

            particle = Particle1.create();
            particle.setTexture(mFireTexture);
            particle.setBlendFunc(BLEND_FUNC_1);
            particle.start1(x, y);
            mScene.addChild(particle);

            particle = Particle1.create();
            particle.setTexture(mFireTexture);
            particle.setBlendFunc(BLEND_FUNC_1);
            particle.start1(x, y);
            mScene.addChild(particle);
        } else if (mType == 2) {
            particle = Particle1.create();
            particle.setTexture(mSmokeTexture);
            particle.setBlendFunc(BLEND_FUNC_1);
            particle.start1(x, y);
            particle.setSize(Particle1.SIZE_BIG, Particle1.SIZE_BIG);
            particle.setColor(Particle1.GREEN);
            mScene.addChild(particle);

            particle = Particle1.create();
            particle.setTexture(mFireTexture);
            particle.setBlendFunc(BLEND_FUNC_1);
            particle.start1(x, y);
            mScene.addChild(particle);

            particle = Particle1.create();
            particle.setTexture(mFireTexture);
            particle.setBlendFunc(BLEND_FUNC_1);
            particle.start1(x, y);
            mScene.addChild(particle);
        } else {
            particle = Particle1.create();
            particle.setTexture(mSmokeTexture);
            particle.setBlendFunc(BLEND_FUNC_2);
            particle.start1(x, y);
            particle.setSize(Particle1.SIZE_BIG, Particle1.SIZE_BIG);
            mScene.addChild(particle);

            particle = Particle1.create();
            particle.setTexture(mFireTexture);
            particle.setBlendFunc(BLEND_FUNC_1);
            particle.start1(x, y);
            mScene.addChild(particle);

            particle = Particle1.create();
            particle.setTexture(mFireTexture);
            particle.setBlendFunc(BLEND_FUNC_1);
            particle.start1(x, y);
            mScene.addChild(particle);
        }
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            mStage.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int len = event.getPointerCount();
                    for (int i = 0; i < len; i++) {
                        addParticles(event.getX(i), mDisplaySize.y - event.getY(i));
                    }
                }
            });
        }

        return true;
    }

    public void onClickRadio(final View view) {
        switch (view.getId()) {
            case R.id.radio_1:
                mType = 1;
                break;

            case R.id.radio_2:
                mType = 2;
                break;

            case R.id.radio_3:
                mType = 3;
                break;
        }
    }
}

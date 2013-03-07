/**
 * 
 */
package com.funzio.pure2D.demo.particles;

import android.graphics.PointF;

import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.RectangularEmitter;

/**
 * @author long
 */
public class SimpleSmoke extends RectangularEmitter {
    private int mInitParticles = 0;
    private int mRetainParticles = 0;
    private Texture mParticleTexture;
    private boolean mParticleTextureEnabled = false;

    private Runnable mInitRunnable = new Runnable() {

        @Override
        public void run() {
            for (int n = 0; n < mInitParticles; n++) {
                createParticle();
            }
        }
    };

    private Runnable mUpdateRunnable = new Runnable() {

        @Override
        public void run() {
            final int more = Math.min(mRetainParticles - getNumParticles(), 2);
            for (int n = 0; n < more; n++) {
                createParticle();
            }
        }
    };

    /**
     * 
     */
    public SimpleSmoke(final int initParticles, final int retainParticles) {
        mInitParticles = initParticles;
        mRetainParticles = retainParticles;
        setSize(50, 50);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.DisplayObject#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        queueEvent(mUpdateRunnable);

        return true;
    }

    private void createParticle() {
        SimpleSmokeParticle particle = SimpleSmokeParticle.newInstance();
        particle.setTexture(mParticleTextureEnabled ? mParticleTexture : null);

        particle.setSize(40, 40);
        particle.setAlpha(0.3f);
        // random position
        particle.setPosition(getNextPosition());
        // random velocity
        particle.setVelocity(new PointF(mRandom.nextInt(7) - 3, 5 + mRandom.nextInt(10)));

        // add to scene
        addParticle(particle);
    }

    /**
     * @return the particleTexture
     */
    public Texture getParticleTexture() {
        return mParticleTexture;
    }

    /**
     * @param particleTexture the particleTexture to set
     */
    public void setParticleTexture(final Texture particleTexture) {
        mParticleTexture = particleTexture;
    }

    /**
     * @return the useTexture
     */
    public boolean isParticleTextureEnabled() {
        return mParticleTextureEnabled;
    }

    /**
     * @param useTexture the useTexture to set
     */
    public void setParticleTextureEnabled(final boolean useTexture) {
        mParticleTextureEnabled = useTexture;
    }

    @Override
    public void onAdded(final Container parent) {
        super.onAdded(parent);

        queueEvent(mInitRunnable);
    };

}
